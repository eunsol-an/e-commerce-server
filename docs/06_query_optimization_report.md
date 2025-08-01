# 조회 성능 개선 보고서

조회 성능 저하가 발생할 수 있는 기능을 식별하고, 해당 원인을 분석하여 쿼리 재설계 / 인덱스 설계 등 최적화 방안을 제안합니다.

<br/>

## 1. 기능 선정: 판매량 상위 상품 조회

- 최근 3일간 가장 많이 팔린 상위 5개 상품 정보
- 관련 테이블: `product`, `order`, `order_item`

```sql
-- 상품 테이블
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    stock_quantity INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 주문 테이블
CREATE TABLE `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NULL,
    total_item_price INT NOT NULL,
    discount_amount INT NOT NULL DEFAULT 0,
    paid_amount INT NOT NULL,
    ordered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_user_id (user_id),
    INDEX idx_order_coupon_id (coupon_id)
);

-- 주문 항목 테이블
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price INT NOT NULL,
    ordered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

- 예상 쿼리:

```sql
SELECT oi.product_id, SUM(oi.quantity) AS total_quantity
FROM order_item oi
JOIN `order` o ON oi.order_id = o.id
WHERE o.ordered_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
GROUP BY oi.product_id
ORDER BY total_quantity DESC
LIMIT 5;
```

<br/>

<br/>



## 2. 성능 저하 원인 분석

<img width="771" height="576" alt="Image" src="https://github.com/user-attachments/assets/4e15488d-0190-4696-afc4-6b35da9ef694" />

### 쿼리 실행 계획 (EXPLAIN)

| id | select_type | table | type | possible_keys | key | rows | filtered | Extra |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | SIMPLE | oi | ALL |  |  | 7356 | 33.33 | Using temporary; Using filesort |
| 1 | SIMPLE | o | eq_ref | PRIMARY | PRIMARY | 1 | 100.00 | Using where |

1. 주문 항목 테이블(oi)
    - **type: ALL**
        - Full Table Scan 발생. `WHERE` 절에 인덱스가 효과적으로 작동하지 않음을 의미함.
    - **filtered: 33.33**
        - 전체 데이터 중 33.33% 정도만 조건에 일치함. (최근 3일 조건 등의 필터로 인해)
    - **Extra: Using where; Using temporary; Using filesort**
        - **Using temporary**: 정렬 또는 그룹핑을 위해 임시 테이블 사용.
        - **Using filesort**: 정렬을 위해 별도 정렬 작업을 수행 (index를 활용한 정렬이 아님).
          → 이 조합은 **성능이 안 좋은 편임.**

2. 주문 테이블(o)
    - **type: eq_ref**
      - 조건 컬럼에 대해 **인덱스를 활용한 조회**

<br/>

<br/>

## 3. 최적화 방안

### 1) 주문 항목 테이블 (order_item) 반정규화

- `order_item` 테이블에 `ordered_at` 컬럼 추가

```sql
ALTER TABLE order_item ADD COLUMN ordered_at DATETIME;
```

### 2) 인덱스 추가

- 주문 ID(`order_id`), 상품 ID(`product_id`) 각각 인덱스 추가

```sql
CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_order_item_product_id ON order_item (product_id);
```

- 주문 날짜(`ordered_at`)와 상품 ID(`product_id`)의 복합 인덱스 추가

```sql
CREATE INDEX idx_order_item_ordered_at_product_id ON order_item (ordered_at, product_id);
```


<br/>

<br/>

## 4. 성능 테스트 준비

### 1) 더미 데이터 생성

- 상품 테이블 더미 데이터 (1,000개)
- 주문 테이블 더미 데이터 (10만 건, 최근 30일)
- 주문 아이템 더미 데이터 (20만 건, 반정규화 컬럼 포함)

```sql
-- 1. 상품 테이블 더미 데이터 (1,000개)
INSERT INTO product (name, price, stock_quantity)
SELECT CONCAT('상품_', seq), FLOOR(RAND() * 9000 + 1000), FLOOR(RAND() * 100 + 1)
FROM (
    SELECT @row := @row + 1 AS seq
    FROM information_schema.columns a, information_schema.columns b, (SELECT @row := 0) r
    LIMIT 1000
) t;

-- 2. 주문 테이블 더미 데이터 (10만 건, 최근 30일)
INSERT INTO `order` (user_id, coupon_id, total_item_price, discount_amount, paid_amount, ordered_at)
SELECT
    FLOOR(RAND() * 10000) + 1 AS user_id,
    NULL AS coupon_id,
    0 AS total_item_price,
    0 AS discount_amount,
    0 AS paid_amount,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) AS ordered_at
FROM (
    SELECT 1 FROM information_schema.columns LIMIT 100000
) t;

-- 3. 주문 아이템 더미 데이터 (20만 건, 반정규화 컬럼 포함)
INSERT INTO order_item (order_id, product_id, quantity, price)
SELECT 
    o.id,
    FLOOR(RAND() * 1000) + 1 AS product_id,
    FLOOR(RAND() * 5) + 1 AS quantity,
    FLOOR(RAND() * 9000) + 1000 AS price
FROM `order` o
JOIN (
    SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
) t ON RAND() < 0.5  -- 평균 2개 아이템 per 주문
LIMIT 200000;

-- 4. 반정규환 ordered_at 채우기
UPDATE order_item oi
JOIN `order` o ON oi.order_id = o.id
SET oi.ordered_at = o.ordered_at;

```

### 2) 테스트용 쿼리

- 비최적화 버전 (join 필요 + 인덱스 없음)

```sql
SELECT oi.product_id, SUM(oi.quantity) AS total_quantity
FROM order_item oi
JOIN `order` o ON oi.order_id = o.id
WHERE o.ordered_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
GROUP BY oi.product_id
ORDER BY total_quantity DESC
LIMIT 5;
```

- 최적화 버전 (반정규화 + 인덱스 활용)

```sql
SELECT oi.product_id, SUM(oi.quantity) AS total_quantity
FROM order_item oi
WHERE oi.ordered_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
GROUP BY oi.product_id
ORDER BY total_quantity DESC
LIMIT 5;
```

<br/>

<br/>

## 5. 최적화 후 성능 비교

### 1) 최적화 전 (Query cost: 3334.45)

<img width="771" height="576" alt="Image" src="https://github.com/user-attachments/assets/4e15488d-0190-4696-afc4-6b35da9ef694" />

| id | select_type | table | type | possible_keys | key | rows | filtered | Extra |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | SIMPLE | oi | ALL |  |  | 7356 | 33.33 | Using temporary; Using filesort |
| 1 | SIMPLE | o | eq_ref | PRIMARY | PRIMARY | 1 | 100.00 | Using where |
- **병목 원인**:
    - `order.ordered_at` 조건 필터링을 위해 `order` 테이블을 **전수 조회**해야 함.
    - 이후 `order_item`과 조인을 수행해야 하므로 **조인 비용이 매우 높음**.
- **성능 지표**:
    - `order` 테이블: `type: ALL`, 즉 **풀 스캔** (7,356 rows)
    - `Extra`: `Using where; Using temporary; Using filesort` → **비효율적 정렬 및 임시 테이블 사용**

### 2) **반정규화 적용 후** (Query cost: 759.85)

<img width="461" height="371" alt="Image" src="https://github.com/user-attachments/assets/83192363-6f9e-48d5-8834-ed3765d5b5e3" />

| id | select_type | table | type | possible_keys | key | rows | filtered | Extra |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | SIMPLE | oi | ALL |  |  | 7356 | 33.33 | Using where; Using temporary; Using filesort |
- `order_item`에서 직접 `ordered_at`으로 필터링 가능 → **조인 제거**
- 하지만 인덱스가 없다면 여전히 **풀 테이블 스캔** 발생 가능성 존재

### 3) **반정규화 및 인덱스 적용 후** (Query cost: 329.21)

<img width="496" height="391" alt="Image" src="https://github.com/user-attachments/assets/574d54d0-69e1-454c-a250-004b4344adbf" />

| id | select_type | table | type | possible_keys | key | rows | filtered | Extra |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | SIMPLE | oi | range | idx_order_item_ordered_at_product_id | idx_order_item_ordered_at_product_id | 731 | 100.00 | Using index condition; Using temporary; Using filesort |
- **개선 효과:**
    - `order_item.ordered_at` 필드를 활용하여 **단일 테이블에서 직접 필터링** 가능해짐.
    - **복합 인덱스**(`ordered_at`, `product_id`) 덕분에 효율적인 range scan 수행.
    - `row: 731`만으로 결과 추출 → **필터 효율 100%** 유지.
    - `Using index condition` → 인덱스 범위에서 **조건 필터링 가능**.
    - 여전히 `filesort`는 존재하나, **조인 제거로 I/O 및 CPU 부담 감소**.

<br/>

<br/>

## 6. 결론

| 항목 | 평가 |
| --- | --- |
| **반정규화 효과** | 조인 제거 → 단일 테이블에서 직접 필터링 가능해져 성능 대폭 개선 |
| **복합 인덱스 효과** | `ordered_at` + `product_id` 조합으로 **범위 조건 + 그룹 연산 최적화** |
| **비용 절감 측면** | 읽어야 할 row 수 대폭 감소 (3,619 → 731), CPU 연산량 감소 |
| **추가 제안** | `LIMIT`이 있는 쿼리이므로 `ORDER BY`에 적합한 인덱스를 추가 설계 고려 가능 |