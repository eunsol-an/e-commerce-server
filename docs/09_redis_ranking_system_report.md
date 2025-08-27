# Redis 기반 랭킹 시스템

가장 많이 주문한 상품 랭킹을 Redis 기반으로 개발하고 설계 및 구현합니다.

<br/>

## 📚 목차

- [1. 배경](#1-배경)
- [2. 문제 해결](#2-문제-해결)
- [3. 설계](#3-설계)
- [4. 구현](#4-구현)
- [5. 한계점](#5-한계점)
- [6. 개선 사항](#6-개선-사항)
- [7. 결론](#7-결론)

<br/>


## 1. 배경

이커머스 서비스에서는 **실시간으로 인기 상품 랭킹**을 제공하는 기능이 필요합니다.

사용자 경험을 위해 최신 주문 데이터를 기반으로 상위 상품을 빠르게 조회할 수 있어야 하며,
**대량 트래픽 환경에서 DB 부하를 최소화**하면서 안정적인 랭킹 정보를 제공하는 것이 목표입니다.

기존 시스템은 RDBMS 기반으로 주문 데이터 조회 후 정렬하는 방식이었으나,
트래픽 증가 시 성능 저하와 지연이 발생할 수 있는 문제점이 있습니다.

<br/>

<br/>

## 2. 문제 해결

- **실시간 집계**: 매번 DB에서 집계하지 않고 Redis를 활용하여 주문 발생 시 즉시 랭킹 점수를 증가
- **최근 3일 기준 집계**: 하루 단위로 ZSet을 나누고, 배치를 통해 최근 3일 데이터를 합산
- **조회 최적화**: 캐시(look-aside)를 적용하여 반복 조회 시 Redis 집계를 재사용
- **트랜잭션 및 원자성 보장**: Redis의 ZINCRBY 명령어를 이용해 단일 연산으로 점수 증가 → 원자성 확보

<br/>

<br/>

## 3. 설계

### 3.1 주문 시 처리

- 주문 발생 시 `ZINCRBY`로 점수 증가
- 점수 기반으로 순위를 계산하기 위해 Redis **Sorted Set** 사용
- 하루 단위로 상품별 주문 수를 집계
- 과거 데이터를 관리하기 위해 TTL 7일 설정

**Redis 구성**

항목 | 값
-- | --
자료구조 | Sorted Set
Key 전략 | ranking:product:yyyy-MM-dd
TTL | 7일

<br/>

### 3.2 배치 처리 (5분 단위)

- 최근 3일치 주문 데이터를 합산하여 조회용 랭킹 생성
- `ZUNIONSTORE` 명령어 사용
- TTL 30분으로 집계 주기에 맞춰 최신 상태 유지

**Redis 구성**
항목 | 값
-- | --
자료구조 | Sorted Set
Key 전략 | ranking:product:3days
TTL | 30분

<br/>

### 3.3 조회 시

- 먼저 캐시(`cache:ranking:product:3days`) 확인
- 캐시가 없으면 `ranking:product:3days` 기준 집계 후 캐싱
- 조회 결과는 DTO 변환 후 반환

**Redis 구성**
항목 | 값
-- | --
자료구조 | Key-Value 캐시
Key 전략 | cache:ranking:product:3days
TTL | 30분

<br/>

### 3.4 자정 처리

- 하루가 바뀌면 캐시 초기화
- 신규 집계를 반영할 수 있도록 준비

<br/>

<br/>

## 4. 구현

- **Redis 연동과 Repository 추상화**
    - `RankingRepository` 인터페이스를 통해 Redis 구현체(`RankingRedisRepository`)를 추상화.
    - 이렇게 구현함으로써 **서비스 레이어에서는 Redis 세부 구현에 강결합되지 않고**, 추후 다른 캐시/메모리 기반 저장소로 전환 가능.
    - DIP(Dependency Inversion Principle)를 적용하여, **RankingService**는 `RankingRepository` 인터페이스에만 의존하며, 인프라 변경에도 비즈니스 로직 보호.
- **Service 레이어 역할**
    - 주문 발생: `increaseScore(productId, count)`
        - Redis Sorted Set 기반 `ranking:product:YYYY-MM-DD` Key 생성
        - Member: `productId`, Score: 주문 수량
        - 신규 Key 생성 시 TTL 7일 설정 → 과거 데이터 유지
    - 최근 3일 랭킹 조회: `getTop5ProductsLast3Days()`
        - 캐시(`cache:ranking:product:3days`) 확인 후 없으면 Redis 집계 및 DTO 변환
        - ZUNIONSTORE로 최근 3일 점수 합산 후, ZREVRANGE로 TOP 5 추출
- **Batch 처리**
    - `RankingBatchJob`
        - 5분 단위 → 최근 3일 집계 및 캐시 갱신
        - 매일 자정 → 캐시 초기화 및 신규 집계 반영
    - 배치 로직은 서비스 레이어와 Repository 인터페이스를 통해 구현되어, **비즈니스 로직과 인프라 구현을 분리**.
- **설계적 장점**
    1. Redis 자료구조 활용 최적화: Sorted Set과 ZUNIONSTORE로 **실시간 랭킹 집계** 효율화
    2. Repository 추상화로 **서비스 레이어 재사용성 확보**
    3. DIP 적용으로 **인프라 변경에도 안전한 비즈니스 로직** 유지
    4. 캐시와 배치를 연계해 **DB 부하 최소화** 및 고가용성 구현

<br/>

<br/>

## 5. 한계점

- TTL과 배치 주기에 따라 **실시간 반영과 조회 시점 간 시차** 발생 가능
- 3일치 합산 배치 시, 한 번에 많은 데이터가 ZUNIONSTORE로 처리될 경우 **CPU 부하** 발생 가능성
- Redis 단일 인스턴스 장애 시 랭킹 데이터 일부 손실 가능

<br/>

<br/>

## 6. 개선 사항

- Redis Cluster, Replica 구성으로 **장애 대응 및 데이터 안정성 강화**
- ZSet 기반 집계 외에, **데이터 샤딩**이나 **Stream/Queue**를 통해 배치 부하 분산
- 조회 캐시 TTL 조정 및 **Event-driven 캐시 갱신** 도입 가능

<br/>

<br/>

## 7. 결론

- Redis Sorted Set과 ZINCRBY, ZUNIONSTORE를 활용하여 **대량 트래픽 환경에서도 실시간 인기 상품 랭킹** 제공 가능
- Look-aside 캐시 전략으로 **반복 조회 시 DB 부하 최소화**
- 배치 및 자정 캐시 초기화 전략으로 **최신 집계 반영**과 **TTL 관리** 가능
- Redis 기반 설계로 기존 RDBMS 중심 설계 대비 **속도, 확장성, 유연성**을 확보