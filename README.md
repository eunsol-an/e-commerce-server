# 🛒 대용량 트래픽 이커머스 서비스

주문, 결제, 쿠폰 등 커머스 핵심 기능을 Java/Spring 기반으로 설계·구현한 프로젝트입니다.<br/>
단순 기능 구현을 넘어서 대규모 트래픽, 동시성 제어, 성능 최적화까지 고려한 설계를 적용했습니다.


<br/>

<br/>

## 🚀 주요 기능

- **주문/결제**: 재고 차감, 결제 중복 방지 등을 포함한 **안정적인 주문 트랜잭션 처리**
- **쿠폰 발급**: Redis + Kafka 기반 **대규모 트래픽 환경에서도 안정적인 선착순 쿠폰 발급**
- **랭킹 시스템**: Redis Sorted Set을 활용한 **실시간 상품 랭킹 제**
- **통계/리포트**: k6 + InfluxDB + Grafana 기반 **성능 부하 테스트 및 실시간 모니터링**

<br/>

<br/>

## 🏗️ 아키텍처

- [요구사항 분석](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/01_requirements.md)
- [시퀀스 다이어그램](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/02_sequence_diagram.md)
- [ERD](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/03_entity_relationship_diagram.md)
- [플로우 차트](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/04_flow_chart.md)
- [상태 다이어그램](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/05_state_diagram.md)
- [아키텍처 설계](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/15_architecture.md)

<br/>

<br/>


## 📊 성능 최적화 & 기술 실험

- **DB 인덱싱 최적화**: [쿼리 성능 개선 보고서](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/06_query_optimization_report.md)
- **동시성 제어**: [DB 락 기반 동시성 제어](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/07_concurrency_control_with_db_locks.md)
- **캐싱 최적화**: [Redis 캐싱 성능 보고서](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/08_redis_caching_performance.md)
- **실시간 랭킹 시스템**: [Redis 랭킹 시스템 설계](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/09_redis_ranking_system_report.md)
- **쿠폰 발급 시스템**: [Redis 기반 선착순 쿠폰 발급 시스템](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/10_redis_coupon_system_report.md)
- **분산 트랜잭션 아키텍처**: [분산 환경의 트랜잭션 설계](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/11_distributed_transaction_diagnosis_and_design.md)
- **Kafka 이벤트 처리**: [Kafka 기본 & 실시간 이벤트](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/12_kafka_basics_and_real_time_events.md)
- **Kafka 쿠폰 발급**: [Kafka 기반 선착순 쿠폰 발급 시스템](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/13_kafka_first_come_coupon_issuance.md)
- **성능 비교 실험**: [Redis vs Kafka 부하 테스트](https://github.com/eunsol-an/e-commerce-server/blob/main/docs/14_1_redis_vs_kafka_load_testing_and_performance_analysis.md)


<br/>

<br/>

## ⚙️ 기술 스택

- **Backend**: Java 17, Spring Boot, Spring Data JPA
- **DB**: MySQL, Redis
- **Messaging**: Kafka
- **Infra**: Docker, Docker Compose
- **Monitoring**: k6, InfluxDB, Grafana
- **Docs**: Swagger, Mermaid
