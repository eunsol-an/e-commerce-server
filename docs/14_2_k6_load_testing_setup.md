# 선착순 쿠폰 발급 테스트 환경 세팅

k6 + InfluxDB + Grafana의 테스트 환경을 구성합니다.


<br/>

## 📚 목차

- [1. InfluxDB + Grafana / k6 설치](#1-InfluxDB-+-Grafana-/-k6-설치)
- [2. InfluxDB 연결 확인](#2-InfluxDB-연결-확인)
- [3. Grafana 접속](#3-Grafana-접속)
- [4. k6 스크립트 작성](#4-k6-스크립트-작성)
- [5. k6 실행 (InfluxDB로 결과 저장)](#5-k6-실행-(InfluxDB로-결과-저장))
- [6. Grafana에서 대시보드 생성](#6-Grafana에서-대시보드-생성)

<br/>

<br/>



## 1. InfluxDB + Grafana / k6 설치

```bash
docker run -d \
  --name docker-influxdb-grafana \
  --platform linux/amd64 \
  -p 3003:3003 \
  -p 3004:8083 \
  -p 8086:8086 \
  -v ./grafana/influxdb:/var/lib/influxdb \
  -v ./grafana/grafana:/var/lib/grafana \
  philhawthorne/docker-influxdb-grafana:latest
```

```bash
brew install k6
```

<br/>

<br/>

## 2. InfluxDB 연결 확인

컨테이너 안에서 InfluxDB가 실행되고 있는지 확인

```bash
docker ps | grep influx
```

InfluxDB 내부로 접속

```bash
docker exec -it docker-influxdb-grafana influx
```

Influx CLI에서 k6 DB 생성

```sql
CREATE DATABASE k6;
SHOW DATABASES;
```

* `k6` 라는 DB가 있어야 k6 결과를 쌓을 수 있음

<br/>

<br/>

## 3. Grafana 접속

브라우저에서 [http://localhost:3003/](http://localhost:3003/) 접속

(초기 계정은 보통 `root / root`)

- Data Source 추가 → **InfluxDB**
    - URL: `http://influxdb:8086` (도커 내부에서 접근 시)
    - Database: `k6`

<br/>

<br/>

## 4. k6 스크립트 작성

resoucre/k6 위치에 생성

예시 (`test.js`):

```jsx
import http from "k6/http";
import { sleep } from "k6";

export const options = {
  vus: 10, // 동시 사용자 수
  duration: "30s", // 실행 시간
};

export default function () {
  // 인기 상품 조회 API (예시)
  http.get("http://localhost:8080/api/products/popular");

  sleep(1);

  // 주문 API (예시)
  http.post("http://localhost:8080/api/orders", JSON.stringify({
    userId: Math.floor(Math.random() * 100) + 1, // 1~100
    productId: Math.floor(Math.random() * 10000) + 1, // 1~10000
    quantity: 1
  }), { headers: { "Content-Type": "application/json" } });
}

```

<br/>

<br/>

## 5. k6 실행 (InfluxDB로 결과 저장)

```bash
k6 run --out influxdb=http://localhost:8086/k6 src/main/resources/k6/test.js
```

이렇게 하면 k6 결과가 InfluxDB의 `k6` 데이터베이스에 들어감.

<br/>

<br/>

## 6. Grafana에서 대시보드 생성

- Grafana → Dashboards → Import
- k6 공식 대시보드 ID: **2587** (https://grafana.com/grafana/dashboards/2587)
- Import 후 Data Source를 `InfluxDB (k6)`로 지정

⇒ 이제 TPS, p95, p99 latency 같은 지표를 시각화해서 볼 수 있음!