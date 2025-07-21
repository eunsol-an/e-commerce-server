### 목차

- [요구사항 분석](01_requirements.md)
- [시퀀스 다이어그램](02_sequence_diagram.md)
- [ERD](03_entity_relationship_diagram.md)
- [플로우 차트](04_flow_chart.md)
- [상태 다이어그램](05_state_diagram.md)

<br/>

# 시퀀스 다이어그램


## 주문&결제 흐름 시퀀스 다이어그램

> UC-06: 상품 주문 및 결제 ~ UC-10: 주문 내역 외부 전송


```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant OrderFacade
    participant CouponService
    participant BalanceService
    participant InventoryService
    participant OrderService
    participant ExternalDataPlatform

    User->>OrderController: 주문 요청 (상품 ID + 수량 + 쿠폰 ID 선택적) (UC-06)
    OrderController->>OrderFacade: 주문 처리 요청

    alt 쿠폰 사용 요청 있음 (UC-07)
        OrderFacade->>CouponService: 쿠폰 유효성 확인 및 할인 적용
        CouponService-->>OrderFacade: 할인 금액 반환
    else 사용 쿠폰 없음
        Note right of OrderFacade: 할인 없이 주문 진행
    end

    OrderFacade->>InventoryService: 상품 재고 확인 및 차감 (UC-09)
    InventoryService-->>OrderFacade: 재고 차감 성공/실패

    OrderFacade->>BalanceService: 사용자 잔액 차감 (UC-08)
    BalanceService-->>OrderFacade: 잔액 차감 성공/실패

    OrderFacade->>OrderService: 주문 정보 저장

    OrderFacade->>ExternalDataPlatform: 주문 정보 전송 (UC-10)
    ExternalDataPlatform-->>OrderFacade: 수신 확인

    OrderFacade-->>OrderController: 주문 처리 결과 반환
    OrderController-->>User: 주문 성공 응답
```

1. 사용자가 상품과 수량을 선택해 주문을 요청한다. 쿠폰 ID는 선택적으로 전달된다.
2. 쿠폰이 있다면, 유효성을 확인하고 할인 금액을 계산한다.
3. 재고 서비스가 재고를 확인하고 차감한다.
4. 잔액 서비스가 사용자 잔액을 차감한다.
5. 주문 정보는 DB에 저장된다.
6. 주문 정보가 외부 데이터 플랫폼에 전송된다.
7. 최종 결과가 사용자에게 반환된다.

<br/>
<br/>

## 선착순 쿠폰 발급 시퀀스 다이어그램 (정상 + 예외 흐름 포함)

> UC-04: 선착순 쿠폰 발급

```mermaid
sequenceDiagram
    participant User
    participant CouponController
    participant CouponService
    participant ConcurrencyGuard as 동시성제어모듈
    participant CouponRepository
    participant UserCouponRepository

    User->>CouponController: 쿠폰 발급 요청 (UC-04)
    CouponController->>CouponService: 발급 처리 요청

    %% 동시성 처리 책임 추상화
    CouponService->>ConcurrencyGuard: 락/검증/선착순 제어

    alt 발급 가능
        CouponService->>CouponRepository: 쿠폰 수량 감소
        CouponService->>UserCouponRepository: 사용자에게 쿠폰 발급 저장
        CouponService-->>CouponController: 발급 성공
        CouponController-->>User: 쿠폰 발급 성공 응답
    else 발급 불가 (동시 시도/소진 등)
        CouponService-->>CouponController: 발급 실패 (사유 포함)
        CouponController-->>User: 쿠폰 발급 실패 응답
    end
```

1. 사용자가 쿠폰 발급을 요청한다.
2. 동시성 제어 모듈을 통해 선착순을 제어한다.
3. 쿠폰 발급이 가능하다면, 쿠폰 서비스가 수량을 확인하고 차감한다.
4. 쿠폰 발급 내역은 DB에 저장된다.
5. 쿠폰 발급이 불가능 하다면 (동시 시도, 쿠폰 소진 등), 실패 사유를 포함하여 사용자에게 응답한다.
6. 최종 결과가 사용자에게 반환된다.

<br/>
<br/>

## 포인트 충전 시퀀스 다이어그램

> UC-01: 잔액 충전하기

```mermaid
sequenceDiagram
    participant User
    participant PointController
    participant PointService
    participant UserDomain
    participant UserRepository
    participant UserPointHistoryRepository

    User->>PointController: 포인트 충전 요청 (유저ID + 포인트 수량) (UC-01)
    PointController->>PointService: 포인트 충전 처리 요청

    PointService->>UserRepository: 사용자 조회
    UserRepository-->>PointService: UserDomain 반환

    PointService->>UserDomain: 잔액 증가 (increaseBalance)
    PointService->>UserPointHistoryRepository: 포인트 충전 이력 저장

    PointService->>UserRepository: 사용자 정보 저장 (갱신된 잔액)

    PointService-->>PointController: 충전 결과 반환
    PointController-->>User: 충전 성공 응답
```

1. 사용자가 포인트 충전을 요청한다.
2. 포인트 서비스가 사용자를 조회한다.
3. 유저 도메인에서 해당 사용자의 잔액을 증가 시킨다.
4. 포인트 충전 이력은 DB에 저장된다.
5. 사용자의 갱신된 잔액은 DB에 저장된다.
6. 최종 결과가 사용자에게 반환된다.