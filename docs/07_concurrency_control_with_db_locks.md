# 동시성 제어 분석 보고서

이커머스 서비스에서 발생 가능한 동시성 문제를 분석하고, DB를 활용한 적절한 해결 방안을 제시합니다.

<br/>

## 📚 목차

- [1. 배경](#1-배경)
- [2. 동시성 이슈 분석](#2-동시성-이슈-분석)
- [3. 동시성 제어 전략 결정 기준](#3-동시성-제어-전략-결정-기준)
- [4. DB Lock을 활용한 동시성 제어](#4-DB-Lock을-활용한-동시성-제어)
- [5. 동시성 제어 테스트](#5-동시성-제어-테스트)
- [6. DB Lock 기반의 동시성 제어 한계점](#6-DB-Lock-기반의-동시성-제어-한계점)
- [7. 결론](#7-결론)

<br/>

<br/>

## 1. 배경

이커머스 시스템에서는 동시에 다수의 유저가 요청을 보낼 수 있는 구조적 특성상, 동시성 문제가 빈번하게 발생한다.
특히 주문 처리 과정에서 발생하는 재고 차감, 포인트 잔액 변경, 선착순 쿠폰 발급과 같은 로직은 정합성이 중요하기 때문에, 적절한 동시성 제어가 필요하다.
이 보고서에서는 다음 세 가지 대표적인 케이스를 중심으로, 문제 발생 조건을 분석하고 이를 데이터베이스 트랜잭션 및 락을 통해 해결하는 전략을 제시한다.

- 상품 재고 차감 및 복원
- 유저 포인트 잔액
- 선착순 쿠폰 발급

<br/>

<br/>

## 2. 동시성 이슈 분석

### 2.1 상품 재고 차감 및 복원

- **상황**: 재고가 1개 남은 상품에 대해 A와 B 사용자가 동시에 구매 요청
- **결과**: 두 요청이 모두 재고를 1로 조회한 후 차감 → 재고가 음수로 내려감

### 2.2 유저 포인트 잔액 차감

- **상황**: 유저가 결제 시 보유 포인트를 사용함. 동시에 두 개의 주문을 진행.
- **결과**: 포인트 중복 차감 → 잔액이 음수로 내려감

### 2.3 선착순 쿠폰 발급

- **상황**: 100개 한정 쿠폰 이벤트, 수천 명 동시 요청
- **결과**: 쿠폰 발급 수량 초과, 100개 이상 발급됨

<br/>

<br/>


## 3. 동시성 제어 전략 결정 기준

> 동시성 문제가 발생할 수 있는 상황에서 어떤 제어 전략(낙관적 락 vs 비관적 락)을 선택할지는 단순히 기술적 구현의 문제가 아니라,
> **비즈니스 요구사항**과 **서비스의 특성**을 기반으로 결정되어야 한다.

### 3.1 자원 기준의 일관성

동일한 자원에 대해서는 **일관된 락 전략**을 유지해야 한다.

- 예를 들어, `사용자 잔액`이라는 자원에 대해 충전에는 낙관적 락을, 차감에는 비관적 락을 적용하는 것은 위험하다.
- 락 전략이 혼용되면 충돌 발생 시 예측 불가능한 정합성 문제로 이어질 수 있다.
- 따라서 자원 단위로 하나의 전략을 선택해 일관되게 적용해야 한다.

### 3.2 경쟁의 강도

- **경쟁이 심한 자원**에 대해서는 **비관적 락**을 사용하는 것이 유리하다.
- 낙관적 락은 충돌 발생 시 트랜잭션이 롤백되고, **재시도**가 필요하다.
- 재시도가 많아질수록 DB에 열리는 트랜잭션 수가 증가하여 부하가 커진다.

### 3.3 트래픽의 양

트래픽이 많은 시스템일수록 **락 전략에 따른 DB 부하**를 고려해야 한다.

- **비관적 락**
  - 모든 요청이 DB 락을 걸고 실행되므로, 락 경합으로 인한 지연과 DB 처리량 저하가 발생할 수 있다.
- **낙관적 락**
  - 높은 트래픽에서 충돌률이 높아지면 다수의 요청이 실패하거나 재시도되면서 전체적인 효율이 낮아진다.
  - 재시도 로직이 없는 경우, 실패 요청이 많아 사용자 경험에 부정적 영향을 줄 수 있다.

### 3.4 실패 감내 가능성

기능의 특성에 따라 **충돌 발생 시 실패를 수용할 수 있는지** 여부를 고려한다.

- **실패를 감내할 수 없는 기능**
  - 재고 차감, 포인트 사용, 쿠폰 발급 등
  - 요청이 실패했을 때 사용자 불만이 크고, **데이터 정합성이 중요**

    ⇒ `비관적 락이 적합 (무조건 성공해야 함)`

- **실패를 감내할 수 있는 기능**
  - 장바구니 수량 수정, 마이페이지 프로필 변경 등
  - 한두 번 실패해도 사용자 경험에 큰 영향을 주지 않음

    ⇒ `낙관적 락 사용 가능 (충돌 시 재시도 or 무시 가능)`


<br/>

<br/>

## 4. DB Lock을 활용한 동시성 제어

### 4-1. 상품 재고 차감

- **락 전략**: 비관적 락 (`SELECT ... FOR UPDATE`)
- **락 범위**: 상품 테이블의 `product_id` 단위 (상품 1개에만 락)
- **예제**:

```java
@Transactional
public void deductStock(List<OrderCommand.Item> items) {
    items.forEach(item -> {
      Product product = productRepository.findByIdWithPessimisticLock(item.productId())  // SELECT ... FOR UPDATE
                .orElseThrow(() -> new ApiException(PRODUCT_NOT_FOUND));

        product.deductStock(item.quantity());
        productRepository.save(product);
    });
}
```

- **이유**:
  - 재고는 중복 차감이 치명적 → 반드시 성공해야 함
  - 실패 시 사용자가 재시도하기 어렵고 불만이 큼
  - DB 락으로 선점하고 차감해야 안정성 확보

---

### 4-2. 유저 포인트 잔액

- **락 전략**: 낙관적 락 (`@Version`)
- **락 범위**: 유저 포인트 테이블의 `user_id` 단위
- **예제**:

```java
@Entity
public class UserPointJpaEntity {
    @Id private Long userId;
    private int balance;

    @Version
    private Long version; // 도메인에도 동일하게 version 추가
}
```

```java
@Transactional
@Retryable(    // 재시도 로직
        value = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 5,
        backoff = @Backoff(delay = 100, multiplier = 2.0)
)
public void use(PointCommand.Use command) {
  UserPoint userPoint = pointRepository.findByIdWithOptimisticLock(command.userId())
          .orElseThrow(() -> new ApiException(USER_NOT_FOUND));

  userPoint.use(command.amount());
  pointRepository.save(userPoint);
}
```

- **이유**:
  - 대부분의 사용자는 동시에 포인트 차감할 일이 많지 않음
  - 재시도 로직 (`@Retryable`)으로 자동 복구 가능
  - 락 대기 없이 성능 유리, DB 부하 감소

---

### 4-3. 선착순 쿠폰 발급

- **락 전략**: 비관적 락 (`SELECT ... FOR UPDATE`)
- **락 범위**: 쿠폰 정책 테이블의 `coupon_policy_id` 단위 (쿠폰 1종당 하나의 row)
- **예제**:

```java
@Transactional
public void issue(Long userId, Long couponPolicyId) {
  // 1. 쿠폰 정책 조회 (없으면 예외 발생)
  CouponPolicy couponPolicy = couponPolicyRepository.findByIdWithPessimisticLock(couponPolicyId)  // SELECT ... FOR UPDATE
          .orElseThrow(() -> new ApiException(COUPON_POLICY_NOT_FOUND));

  // 2. 쿠폰 발급 수량 초과 여부 확인
  if (!couponPolicy.isIssuable()) {
    throw new ApiException(COUPON_SOLD_OUT);
  }

  // 3. 이미 해당 유저가 이 쿠폰 정책으로 쿠폰을 발급받았는지 확인 (중복 발급 방지)
  boolean alreadyUsed = couponRepository.existsByUserIdAndCouponPolicyId(userId, couponPolicy.getId());
  if (alreadyUsed) {
    throw new ApiException(ALREADY_ISSUED_COUPON);
  }

  // 4. 쿠폰 생성 및 저장
  Coupon coupon = Coupon.create(userId, couponPolicyId, CouponStatus.ISSUED);
  couponRepository.save(coupon);

  // 5. 쿠폰 발급 수량 1 증가
  couponPolicy.increaseIssuedCount();
  couponPolicyRepository.save(couponPolicy);
}
```

- **이유**:
  - 극심한 동시 요청이 몰리는 상황 (이벤트 등)
  - 낙관적 락은 충돌 발생 시 초과 발급 위험 있음
  - 초과 발급은 정합성, 서비스 신뢰도에 문제 발생
  - 비관적 락으로 **정확하게 100명만 발급**되도록 보장

<br/>

<br/>

## 5. 동시성 제어 테스트
- 실질적인 race condition 테스트를 위해 `ExcutorService`, `CountDownLatch` 활용
- `AtomicInteger`를 사용하여 성공/실패 요청 수 기록

### 5-1. 상품 재고 차감

- **테스트 시나리오**: 동시에 100개의 요청으로 재고를 감소시킨다
- **테스트 구현**: link

### 5-2. 유저 포인트 잔액

- **테스트 시나리오**: 동시에 10개의 요청으로 잔액을 충전시킨다 (재시도 로직 포함)
- **테스트 구현**: link

### 5-3. 선착순 쿠폰

- **테스트 시나리오**: 동시에 100개의 요청으로 쿠폰이 정상 발급된다
- **테스트 구현**: link

<br/>

<br/>

## 6. DB Lock 기반의 동시성 제어 한계점

- **성능 저하 및 병목 발생**
  - 비관적 락은 트랜잭션이 끝날 때까지 자원을 독점해서 다수 동시 요청 시 락 대기 시간이 길어지고, 처리량이 급격히 떨어짐
  - 트랜잭션 대기와 락 경합으로 DB 서버 부하가 커져 전체 시스템 응답 속도 저하
- **확장성 한계**
  - DB가 모든 동시성 제어를 책임지면서, 대규모 트래픽 처리에 한계가 있음
  - 단일 DB에 집중되는 부하 때문에 수평 확장 어려움
- **공정성 및 순서 보장 어려움**
  - DB 락은 선착순 같은 엄격한 순서를 보장하지 않음
  - 락 획득 순서가 DB 커넥션 풀 상황에 따라 비예측적임
- **복잡한 비즈니스 로직 처리 제한**
  - 여러 유형의 상이한 요청(예: 충전 vs 차감)이 복합적으로 동작하는 경우 락만으로는 정합성 유지 어려움
  - 멱등 처리, 비동기 처리 등 추가 메커니즘 필요

<br/>

<br/>

## 7. 결론

- 재고 차감, 포인트 잔액, 선착순 쿠폰 발급 등 이커머스 주요 기능에서 발생할 수 있는 동시성 문제를 다루고, 낙관적 락과 비관적 락을 활용한 DB 수준의 동시성 제어 전략을 살펴 보았다.
- DB Lock 만으로도 정합성을 확보할 수 있지만, 트래픽 증가나 처리 순서 보장과 같은 요구사항 앞에서는 한계가 존재한다. 이를 보완하기 위해 Redis 분산 락, Kafka 기반 직렬 처리 등 외부 시스템과의 연계가 필요하다.
- 결국, 안정성과 성능을 모두 만족시키기 위해서는 기능별 특성과 트래픽 수준을 고려한 복합적인 동시성 제어 전략이 요구된다.