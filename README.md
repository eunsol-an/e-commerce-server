# 🛒 e-커머스 서비스

Spring Boot 기반의 e-커머스 상품 주문 서비스입니다.

<br/>


<h2>1. 아키텍처 개요</h2>
<p>본 프로젝트는 <strong>레이어드 아키텍처(Layered Architecture)</strong>와 <strong>인터페이스 패턴</strong>을 결합하여 설계하였습니다.</p>
<p>이는 코드의 유지보수성과 확장성, 테스트 용이성을 높이기 위함입니다.</p>
<h3>주요 설계 의도</h3>
<ul>
<li><strong>관심사 분리</strong>: 프레젠테이션, 애플리케이션, 도메인, 인프라스트럭처 계층을 명확히 분리</li>
<li><strong>단방향 의존성</strong>: 상위 계층이 하위 계층에 의존하며, 하위 계층은 상위 계층을 몰라 의존 역전 원칙 준수</li>
<li><strong>인터페이스 도입</strong>: 리포지토리, 매퍼 등 외부 의존성은 인터페이스로 추상화해 구현체와 분리</li>
<li><strong>테스트 용이성 확보</strong>: Mocking과 단위 테스트가 쉽도록 계층별 분리 및 인터페이스 활용</li>
</ul>

<br/>

<h2>2. 계층별 책임 및 역할</h2>

계층 | 설명
-- | --
presentation | 외부 API 또는 UI에서 들어오는 요청을 받아 DTO/Command 형태로 변환 및 전달<br/>(컨트롤러 및 DTO 위치)
application | Use Case 구현, 비즈니스 로직 조율, 트랜잭션 관리 담당<br/>(서비스, 파사드, Command, Info 클래스 위치)
domain | 핵심 비즈니스 로직, 도메인 모델 정의(엔티티, 밸류 객체)<br/>(도메인 서비스 및 인터페이스로 추상화된 리포지토리 포함)
infrastructure | 데이터베이스 접근, 외부 API 연동 등 기술적 세부사항 구현<br/>(JPA 엔티티, 매퍼, 리포지토리 구현체 위치)
common/entity | 공통 엔티티, 혹은 기본 도메인 공통 모듈
exception | 공통 예외 및 API 예외 처리 관련 클래스


<br/>


<h2>3. 패키지 구조 (주요 폴더 위주)</h2>

```
kr.hhplus.be.server
├── common.entity                    # 공통 엔티티 정의
├── config                           # 환경 설정 관련
├── docs                             # 문서 관련 파일
├── domain                           # 핵심 도메인 및 애플리케이션 로직
│   ├── coupon                       # 쿠폰 도메인
│   ├── order                        # 쿠폰 도메인
│   │   ├── application              # 서비스, 파사드, Command, Info
│   │   │   ├── OrderCommand.java
│   │   │   ├── OrderFacade.java
│   │   │   ├── OrderInfo.java
│   │   │   └── OrderService.java
│   │   ├── domain
│   │   │   └── model                # 도메인 엔티티(Order, OrderItem)
│   │   │   └── repository           # 도메인 인터페이스(Repository)
│   │   ├── infrastructure.persistence
│   │   │   ├── entity               # JPA 엔티티
│   │   │   ├── mapper               # 매퍼 인터페이스
│   │   │   ├── repository           # JPA 리포지토리 인터페이스
│   │   │   └── repositoryImpl.java  # 리포지토리 구현체
│   │   └── presentation             # 컨트롤러 및 DTO
├── point                            # 포인트 도메인
├── product                          # 상품 도메인
└── exception                        # 공통 예외 처리
```

<br/>

<h2>4. 아키텍처와 패키지 구조 연계</h2>
<ul>
<li><code>presentation</code> 계층에서 외부 요청을 받고, 필요한 데이터 전송 객체(DTO)를 활용하여 <code>application</code> 계층으로 전달합니다.</li>
<li><code>application</code> 계층은 도메인 모델을 조작하는 비즈니스 로직을 담당하며, 여러 도메인 서비스와 리포지토리를 호출해 유스케이스를 수행합니다.</li>
<li><code>domain</code> 패키지 내의 <code>model</code>은 핵심 비즈니스 규칙과 엔티티를 담고 있고, <code>repository</code> 인터페이스를 통해 인프라스트럭처와 분리합니다.</li>
<li><code>infrastructure.persistence</code> 내에서는 실제 데이터베이스 연동을 담당하는 JPA 엔티티, 매퍼, 리포지토리 구현체를 위치시켜 도메인과의 의존성을 역전합니다.</li>
<li><code>common.entity</code> 및 <code>exception</code> 등 공통 모듈은 모든 계층에서 공용으로 활용됩니다.</li>
</ul>
<!-- notionvc: b14e12d6-ce74-41dc-9a25-ee683369558a -->