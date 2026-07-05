# 03. ADR — Architecture Decision Record

> 소로소로(SOROSORO)의 주요 기술 선택과 설계 결정을 기록하는 문서

---

# 1. 문서 개요

## 1.1 문서 목적

본 문서는 소로소로(SOROSORO) 프로젝트에서 내린 주요 기술적·구조적 의사결정을 기록하기 위한 Architecture Decision Record이다.

PRD와 SRS가 각각 “왜 이 서비스를 만드는가”, “무엇을 만들어야 하는가”를 정의한다면, ADR은 “왜 이렇게 만들기로 결정했는가”를 설명한다.

소로소로는 개인 재봉 기록 서비스이지만, 단순 CRUD 서비스로만 설계하지 않는다.  
Project, DailyLog, Fabric, Photo, Calendar 등 각 도메인의 책임을 분리하고, 향후 AI 기반 원단 정보 자동 기록 기능까지 확장할 수 있는 구조를 목표로 한다.

따라서 본 문서는 다음 내용을 명확히 기록한다.

- 기술 스택 선택 이유
    
- 도메인 분리 기준
    
- 데이터 모델링 결정 배경
    
- 인증 및 권한 구조 선택 이유
    
- 이미지 업로드 및 처리 방식 결정 이유
    
- MVP에서 제외한 기능과 그 근거
    
- 향후 확장을 고려한 설계 방향
    

---

## 1.2 문서 범위

본 문서는 소로소로 MVP를 기준으로 한 주요 아키텍처 의사결정을 다룬다.

포함 범위는 다음과 같다.

- Backend Framework 선택
    
- Database 선택
    
- 인증 방식 선택
    
- 도메인 모델 분리 기준
    
- Project와 Fabric 관계 설계
    
- DailyLog 임시저장 구조
    
- 작업 시간 계산 방식
    
- Photo 공통 엔티티 설계
    
- Presigned URL 기반 이미지 업로드 구조
    
- 이미지 리사이징 비동기 처리 구조
    
- Calendar와 Contribution 계산 방식
    
- Docker, Docker Compose, EC2, Nginx 기반 배포 구조
    
- MVP 제외 기능 판단
    

본 문서에서 다루지 않는 내용은 다음과 같다.

- API Endpoint 상세 명세
    
- 데이터베이스 컬럼 타입 상세
    
- 인덱스 상세 설계
    
- 클래스 단위 구현 설계
    
- 프론트엔드 컴포넌트 구조
    
- UI 와이어프레임
    
- 실제 배포 스크립트 전문
    
- 테스트 코드 상세
    

위 항목은 각각 다음 문서에서 다룬다.

|문서|역할|
|---|---|
|01_PRD.md|서비스 기획과 제품 목표 정의|
|02_SRS.md|기능 요구사항과 비기능 요구사항 정의|
|03_ADR.md|주요 기술 선택과 설계 결정 기록|
|04_ERD.md|데이터 모델과 관계 상세 정의|
|05_Backend_Design.md|백엔드 구현 구조와 계층 설계|
|06_Architecture.md|시스템 및 인프라 아키텍처 정의|
|07_API.md|API Endpoint, Request, Response 상세 명세|
|08_Wireframe.md|주요 화면 흐름과 UI 구조 정의|

---

## 1.3 작성 기준

ADR은 단순히 “무엇을 선택했다”가 아니라, 다음 항목을 중심으로 작성한다.

|항목|설명|
|---|---|
|Context|해당 결정을 내려야 했던 배경|
|Decision|최종 선택한 방식|
|Alternatives|고려했던 다른 선택지|
|Rationale|현재 선택을 한 이유|
|Trade-off|선택으로 인해 얻는 장점과 감수해야 할 단점|
|Consequences|이후 설계와 구현에 미치는 영향|

각 ADR은 가능한 한 독립적으로 읽을 수 있도록 작성한다.  
다만 도메인 간 관계가 있는 경우, 관련 ADR을 참조할 수 있다.

---

## 1.4 ADR 작성 형식

각 의사결정은 다음 형식을 따른다.

```markdown
# ADR-000. 제목

## Status

Accepted

## Context

이 결정을 내려야 했던 배경을 설명한다.

## Decision

최종적으로 선택한 방식을 설명한다.

## Alternatives

고려했던 대안을 설명한다.

## Rationale

현재 선택을 한 이유를 설명한다.

## Trade-off

선택으로 인한 장점과 단점을 설명한다.

## Consequences

이 결정이 이후 설계와 구현에 미치는 영향을 설명한다.
```

---

## 1.5 ADR Status 정의

ADR의 상태는 다음 값을 사용한다.

|Status|설명|
|---|---|
|Proposed|검토 중인 결정|
|Accepted|현재 프로젝트에서 채택한 결정|
|Deprecated|더 이상 권장하지 않는 결정|
|Superseded|다른 ADR로 대체된 결정|

MVP 설계 단계에서 확정된 결정은 `Accepted` 상태로 작성한다.

---

## 1.6 의사결정 원칙

소로소로의 기술 및 구조 선택은 다음 원칙을 기준으로 판단한다.

---

### 1.6.1 MVP 구현 가능성을 우선한다

소로소로는 1인 개발 프로젝트이므로, 지나치게 복잡한 구조보다 MVP를 안정적으로 완성할 수 있는 구조를 우선한다.

따라서 초기 단계에서는 다음을 피한다.

- 과도한 마이크로서비스 분리
    
- 복잡한 이벤트 기반 아키텍처
    
- 운영 부담이 큰 인프라 구성
    
- 검증되지 않은 AI 자동화 기능의 MVP 포함
    

---

### 1.6.2 도메인 책임을 명확히 분리한다

MVP 구현 가능성을 우선하되, 모든 기능을 하나의 거대한 CRUD 구조로 만들지는 않는다.

소로소로는 다음 도메인을 분리한다.

- Authentication
    
- Project
    
- DailyLog
    
- Fabric
    
- Photo
    
- Calendar
    

도메인 분리의 목적은 다음과 같다.

- 기능 변경 영향 범위 축소
    
- 테스트 단위 명확화
    
- 향후 기능 확장 용이성 확보
    
- 포트폴리오에서 설계 의도 설명 가능
    

---

### 1.6.3 개인 기록 서비스의 권한 모델을 단순하게 유지한다

MVP에서는 모든 데이터가 User 소유 데이터이다.

공개 프로젝트, 커뮤니티, 팔로우, 댓글 기능은 MVP에서 제외한다.  
따라서 권한 정책은 “현재 로그인한 사용자는 자신의 데이터만 접근할 수 있다”는 원칙을 따른다.

이 결정은 MVP 구현을 단순화하고, 개인정보와 개인 기록이 실수로 노출될 위험을 줄이기 위한 것이다.

---

### 1.6.4 향후 확장을 막지 않는 구조를 선택한다

MVP에서는 AI 기반 원단 정보 자동 기록, 통계 대시보드, 커뮤니티 기능을 제외한다.

하지만 현재 설계가 향후 확장을 막지 않도록 다음 구조를 선택한다.

- Fabric을 Project와 독립 도메인으로 분리
    
- Project와 Fabric을 ProjectFabric으로 연결
    
- Photo를 Project, DailyLog, Fabric 공통 엔티티로 설계
    
- Calendar와 Contribution을 DailyLog 기반 파생 데이터로 설계
    
- AI 추출 결과를 Fabric 생성 전 단계로 확장 가능하게 고려
    

---

### 1.6.5 데이터 정합성을 우선한다

소로소로는 개인 기록 서비스이므로, 사용자가 작성한 기록이 일관되게 유지되어야 한다.

특히 다음 데이터 정합성을 중요하게 본다.

- DailyLog는 반드시 Project에 속해야 한다.
    
- Fabric은 Project와 독립적으로 존재할 수 있어야 한다.
    
- 동일 Project에 동일 Fabric은 중복 연결되지 않아야 한다.
    
- PUBLISHED DailyLog만 통계에 반영되어야 한다.
    
- DailyLog.durationMinutes는 TimeEntry 합산값과 일치해야 한다.
    
- Photo는 반드시 ownerType과 ownerId를 통해 연결 대상을 가져야 한다.
    

---

## 1.7 주요 ADR 목록

본 문서에서 작성할 주요 ADR은 다음과 같다.

|ID|제목|핵심 결정|
|---|---|---|
|ADR-001|Backend Framework로 Spring Boot를 선택한 이유|Java/Spring 기반 백엔드 구현|
|ADR-002|Database로 PostgreSQL을 선택한 이유|관계형 데이터와 확장 가능성 고려|
|ADR-003|Fabric을 Project와 독립 도메인으로 분리한 이유|원단 구매 기록과 작품 제작 기록 분리|
|ADR-004|Project-Fabric 관계를 다대다 + 연결 엔티티로 설계한 이유|원단 재사용과 연결 메모 관리|
|ADR-005|DailyLog에 DRAFT/PUBLISHED 상태를 둔 이유|작성 중 기록과 정식 기록 분리|
|ADR-006|작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유|여러 작업 구간 기록과 통계 정확성 확보|
|ADR-007|Photo를 공통 이미지 엔티티로 설계한 이유|Project, DailyLog, Fabric 이미지 관리 통합|
|ADR-008|Presigned URL 기반 이미지 업로드를 선택한 이유|서버 부하 감소와 이미지 업로드 안정성 확보|
|ADR-009|이미지 리사이징을 비동기로 처리하는 이유|업로드 응답 속도와 이미지 조회 성능 분리|
|ADR-010|Calendar/Contribution을 저장하지 않고 조회 시 계산하는 이유|원본 DailyLog 기준 정합성 유지|
|ADR-011|MVP에서 AI 원단 자동 기록을 제외한 이유|MVP 범위 관리와 추출 정확도 검증 필요|
|ADR-012|Docker Compose + EC2 + Nginx 배포를 선택한 이유|1인 개발 MVP에 적합한 단순 배포 구조|

---

## 1.8 문서 활용 방식

본 문서는 개발 과정에서 다음 기준으로 활용한다.

- 기술 선택에 대한 근거를 확인할 때
    
- 구현 중 설계 방향이 흔들릴 때
    
- 기능 확장 시 기존 결정의 배경을 확인할 때
    
- 포트폴리오에서 프로젝트 설계 의도를 설명할 때
    
- README나 기술 면접 답변을 정리할 때
    

ADR은 구현이 진행되며 변경될 수 있다.  
결정이 바뀌는 경우 기존 ADR을 삭제하지 않고, `Deprecated` 또는 `Superseded` 상태로 변경한 뒤 새로운 ADR을 추가한다.

---

# 1장 요약

본 문서는 소로소로의 주요 기술 선택과 설계 결정을 기록하는 ADR 문서이다.

핵심 목적은 다음과 같다.

- 어떤 기술과 구조를 선택했는지 기록한다.
    
- 왜 해당 선택을 했는지 설명한다.
    
- 고려한 대안과 트레이드오프를 남긴다.
    
- MVP 구현 범위와 향후 확장 가능성의 균형을 유지한다.
    
- 포트폴리오에서 설계 의사결정을 설명할 수 있는 근거를 만든다.
    

소로소로 ADR은 단순 구현 기록이 아니라, “개인 재봉 기록 서비스라는 문제를 어떤 구조로 풀 것인가”에 대한 의사결정 문서이다.


# ADR-001. Backend Framework로 Spring Boot를 선택한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 개인 재봉 기록 서비스이다.

MVP에서 제공해야 하는 핵심 기능은 다음과 같다.

- 카카오 로그인
    
- JWT 기반 인증
    
- Project 관리
    
- DailyLog 작성 및 임시저장
    
- DailyLogTimeEntry 기반 작업 시간 계산
    
- Fabric 관리
    
- Project와 Fabric 연결
    
- Photo 업로드 및 이미지 처리 상태 관리
    
- Calendar와 Contribution 조회
    

소로소로는 단순히 화면에서 입력한 데이터를 저장하는 CRUD 서비스처럼 보일 수 있지만, 실제로는 여러 도메인 간의 관계와 상태 규칙이 중요하다.

예를 들어 다음과 같은 비즈니스 규칙이 존재한다.

- DailyLog는 반드시 Project에 속해야 한다.
    
- Fabric은 Project에 종속되지 않고 독립적으로 존재해야 한다.
    
- Project와 Fabric은 다대다 관계를 가진다.
    
- 동일 Project에 동일 Fabric은 중복 연결될 수 없다.
    
- DailyLog는 DRAFT와 PUBLISHED 상태를 가진다.
    
- DRAFT DailyLog는 통계에 반영하지 않는다.
    
- PUBLISHED DailyLog만 Calendar와 Contribution에 반영한다.
    
- 작업 시간은 DailyLogTimeEntry 합산값으로 계산한다.
    
- Photo는 Project, DailyLog, Fabric에서 공통으로 사용된다.
    
- 사용자는 자신의 데이터만 접근할 수 있다.
    

따라서 Backend Framework는 다음 조건을 만족해야 한다.

- 도메인 중심의 계층 분리가 쉬워야 한다.
    
- 인증과 권한 처리를 안정적으로 구현할 수 있어야 한다.
    
- 관계형 데이터베이스와의 연동이 자연스러워야 한다.
    
- 트랜잭션 처리가 명확해야 한다.
    
- 테스트와 유지보수가 쉬워야 한다.
    
- 포트폴리오에서 백엔드 설계 역량을 설명하기 좋아야 한다.
    
- 향후 AI 원단 자동 기록, 통계, 공개 프로젝트 등으로 확장할 수 있어야 한다.
    

---

## Decision

소로소로의 Backend Framework로 **Java 17 + Spring Boot**를 선택한다.

주요 구성은 다음을 기준으로 한다.

|영역|선택|
|---|---|
|Language|Java 17|
|Framework|Spring Boot|
|Security|Spring Security|
|ORM|Spring Data JPA|
|Query|QueryDSL|
|Database Migration|Flyway|
|Test|JUnit 5|
|API Documentation|Springdoc OpenAPI|
|Build Tool|Gradle|

---

## Alternatives

### 1. NestJS

NestJS는 TypeScript 기반 백엔드 프레임워크이다.

장점은 다음과 같다.

- TypeScript 기반으로 프론트엔드와 언어 통일성이 좋다.
    
- 모듈 구조가 명확하다.
    
- Controller, Service, Module 구조가 직관적이다.
    
- Node.js 기반이라 초기 개발 속도가 빠르다.
    
- 개인 프로젝트 MVP를 빠르게 만들기 좋다.
    

단점은 다음과 같다.

- 관계형 데이터 모델의 복잡한 연관관계와 트랜잭션 설계를 포트폴리오에서 깊게 보여주기에는 Spring/JPA보다 설득력이 약할 수 있다.
    
- 국내 Java/Spring 백엔드 채용 시장과 직접적인 연결성이 상대적으로 낮다.
    
- Spring Security, JPA, Transaction 기반 설계를 보여주기 어렵다.
    
- 이미 인턴 경험에서 NestJS를 사용했기 때문에, 새 프로젝트에서는 Java/Spring 역량을 보여주는 것이 더 유리하다.
    

---

### 2. Django

Django는 Python 기반 웹 프레임워크이다.

장점은 다음과 같다.

- Admin 기능이 강력하다.
    
- ORM과 인증 기능이 기본 제공된다.
    
- 빠른 CRUD 서비스 구현에 적합하다.
    
- Python 기반 AI 기능과 연결하기 쉽다.
    

단점은 다음과 같다.

- 사용자의 주요 취업 목표인 Java/Spring 백엔드 역량을 직접적으로 보여주기 어렵다.
    
- 국내 백엔드 신입 포트폴리오에서 Spring Boot 대비 직무 적합성이 낮을 수 있다.
    
- JPA 기반 도메인 모델링, 트랜잭션, 계층 설계를 보여주기 어렵다.
    

---

### 3. FastAPI

FastAPI는 Python 기반 API 프레임워크이다.

장점은 다음과 같다.

- API 서버를 빠르게 만들 수 있다.
    
- 비동기 처리와 문서화가 편리하다.
    
- 향후 AI 원단 자동 기록 기능과 연결하기 좋다.
    

단점은 다음과 같다.

- MVP의 핵심은 AI 추출이 아니라 개인 기록 도메인 설계이다.
    
- 관계형 도메인 모델과 트랜잭션을 중심으로 설계 역량을 보여주기에는 Spring Boot보다 약하다.
    
- Java/Spring 기반 백엔드 포트폴리오 목적과 맞지 않는다.
    

---

### 4. Express

Express는 Node.js 기반의 경량 웹 프레임워크이다.

장점은 다음과 같다.

- 구조가 단순하고 빠르게 구현할 수 있다.
    
- 초기 러닝커브가 낮다.
    
- 작은 API 서버 구현에 적합하다.
    

단점은 다음과 같다.

- 구조를 직접 설계해야 하므로 프로젝트가 커질수록 일관성을 유지하기 어렵다.
    
- 인증, 검증, 계층 분리, 테스트 구조를 직접 구성해야 한다.
    
- 도메인 중심 설계와 포트폴리오 설명 측면에서 Spring Boot보다 약하다.
    

---

## Rationale

Spring Boot를 선택한 이유는 다음과 같다.

---

### 1. 도메인 중심 구조를 설계하기 좋다

소로소로는 다음과 같은 여러 도메인을 가진다.

- Authentication
    
- Project
    
- DailyLog
    
- Fabric
    
- Photo
    
- Calendar
    

각 도메인은 서로 연결되어 있지만 책임이 다르다.

Spring Boot는 Controller, Service, Repository, Domain 계층을 분리하기 좋고, 도메인 단위 패키지 구조를 만들기 쉽다.

예를 들어 다음과 같이 구성할 수 있다.

```text
sorosoro
 ├── auth
 ├── user
 ├── project
 ├── dailylog
 ├── fabric
 ├── photo
 ├── calendar
 └── common
```

이 구조는 기능이 늘어나도 변경 범위를 도메인 단위로 제한할 수 있게 한다.

---

### 2. 관계형 데이터 모델과 잘 맞는다

소로소로의 핵심 데이터는 관계형 구조이다.

대표적인 관계는 다음과 같다.

- User 1 : N Project
    
- Project 1 : N DailyLog
    
- DailyLog 1 : N DailyLogTimeEntry
    
- User 1 : N Fabric
    
- Project N : M Fabric
    
- Project / DailyLog / Fabric 1 : N Photo
    

Spring Data JPA는 이러한 관계형 데이터 모델을 객체 중심으로 표현하기 좋다.

또한 QueryDSL을 함께 사용하면 Calendar, Contribution, 목록 조회, 조건 검색처럼 동적 쿼리가 필요한 기능도 타입 안정성 있게 작성할 수 있다.

---

### 3. 트랜잭션 처리가 명확하다

소로소로에는 하나의 요청 안에서 여러 데이터가 함께 변경되는 기능이 많다.

예를 들어 DailyLog 발행 시 다음 처리가 필요하다.

- DRAFT DailyLog 조회
    
- 필수값 검증
    
- TimeEntry 존재 여부 확인
    
- durationMinutes 재계산
    
- status를 PUBLISHED로 변경
    
- Calendar와 Contribution에 반영될 수 있는 상태로 저장
    

Project 삭제 시에도 다음 데이터가 함께 처리되어야 한다.

- Project 삭제
    
- ProjectSpecification 삭제
    
- ProjectReference 삭제
    
- DailyLog 삭제
    
- DailyLogTimeEntry 삭제
    
- ProjectFabric 삭제
    
- Project Photo 조회 제외 처리
    

이러한 작업은 중간에 실패하면 데이터 정합성이 깨질 수 있다.

Spring의 선언적 트랜잭션을 사용하면 서비스 계층에서 작업 단위를 명확히 묶을 수 있고, 실패 시 롤백 처리를 일관되게 적용할 수 있다.

---

### 4. 인증과 권한 처리를 안정적으로 구현할 수 있다

소로소로는 개인 기록 서비스이므로, 인증과 권한 제어가 중요하다.

MVP에서는 다음 권한 원칙을 따른다.

- 로그인한 사용자만 개인 데이터에 접근할 수 있다.
    
- 사용자는 자신의 데이터만 조회, 수정, 삭제할 수 있다.
    
- 탈퇴 상태의 User는 서비스 기능에 접근할 수 없다.
    
- ARCHIVED Project는 조회와 상태 변경 외 수정성 작업을 제한한다.
    

Spring Security는 JWT 기반 인증, 인증 필터, 인증 객체 관리, 예외 처리 등을 구현하기에 적합하다.

또한 서비스 계층에서 User 소유권 검증을 명시적으로 수행하면, “본인 데이터만 접근 가능”이라는 정책을 일관되게 적용할 수 있다.

---

### 5. 테스트와 유지보수에 유리하다

Spring Boot는 다음 테스트를 작성하기 좋다.

- Service 단위 테스트
    
- Repository 테스트
    
- Controller API 테스트
    
- 인증/권한 테스트
    
- 통합 테스트
    

소로소로에서 특히 테스트가 필요한 부분은 다음과 같다.

- DRAFT DailyLog는 통계에 반영되지 않는지
    
- PUBLISHED DailyLog만 Calendar에 반영되는지
    
- TimeEntry 수정 시 durationMinutes가 재계산되는지
    
- 다른 사용자의 Project에 접근할 수 없는지
    
- 동일 Project에 동일 Fabric을 중복 연결할 수 없는지
    
- READY 상태의 Photo만 대표 사진으로 설정할 수 있는지
    

Spring Boot는 이러한 비즈니스 규칙을 테스트 코드로 검증하기에 적합하다.

---

### 6. 포트폴리오 목적과 잘 맞는다

사용자의 취업 목표는 백엔드 개발 또는 IT 시스템 운영·보안 직무와 연결되어 있다.

Spring Boot를 사용하면 다음 역량을 포트폴리오에서 설명할 수 있다.

- Java 기반 백엔드 개발 역량
    
- Spring MVC 기반 API 설계
    
- Spring Security 기반 인증/권한 구현
    
- JPA 기반 도메인 모델링
    
- QueryDSL 기반 동적 조회
    
- 트랜잭션 기반 데이터 정합성 관리
    
- Docker 기반 배포
    
- 운영 환경을 고려한 구조 설계
    

이는 단순히 “CRUD를 만들었다”가 아니라, 실제 백엔드 서비스에서 중요한 구조적 의사결정을 경험했다는 근거가 된다.

---

## Trade-off

### 장점

Spring Boot를 선택하면 다음 장점이 있다.

- 도메인 계층을 명확히 분리할 수 있다.
    
- 인증과 권한 처리를 체계적으로 구현할 수 있다.
    
- 관계형 데이터베이스와의 연동이 안정적이다.
    
- 트랜잭션 처리가 명확하다.
    
- 테스트 코드 작성이 용이하다.
    
- Java/Spring 백엔드 포트폴리오로 활용하기 좋다.
    
- 향후 기능 확장 시 구조를 유지하기 쉽다.
    

---

### 단점

Spring Boot 선택으로 인해 다음 비용도 발생한다.

- 초기 설정과 보일러플레이트가 상대적으로 많다.
    
- 단순 CRUD만 구현한다면 NestJS나 FastAPI보다 개발 속도가 느릴 수 있다.
    
- JPA 연관관계와 영속성 컨텍스트에 대한 이해가 필요하다.
    
- Spring Security 설정이 복잡할 수 있다.
    
- 작은 개인 프로젝트에는 구조가 다소 무겁게 느껴질 수 있다.
    

---

### Trade-off 판단

소로소로는 아주 단순한 메모 앱이 아니라, Project, DailyLog, Fabric, Photo, Calendar가 연결되는 기록 서비스이다.

또한 포트폴리오 목적상 “빠르게 만든 서비스”보다 “설계 이유를 설명할 수 있는 서비스”가 중요하다.

따라서 초기 구현 비용이 조금 증가하더라도, 도메인 구조, 트랜잭션, 인증/권한, 테스트 가능성을 명확히 보여줄 수 있는 Spring Boot를 선택하는 것이 더 적절하다고 판단했다.

---

## Consequences

Spring Boot를 선택함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. 계층형 아키텍처를 사용한다

기본 구조는 다음과 같다.

```text
Controller
→ Service
→ Repository
→ Domain Entity
```

Controller는 HTTP 요청과 응답을 담당하고, Service는 비즈니스 규칙과 트랜잭션을 담당한다. Repository는 데이터 접근을 담당한다.

---

### 2. 도메인 단위 패키지 구조를 사용한다

기능별 책임을 명확히 하기 위해 도메인 단위 패키지 구조를 사용한다.

```text
auth
user
project
dailylog
fabric
photo
calendar
common
```

각 도메인 내부에는 필요에 따라 다음 구조를 둔다.

```text
controller
service
repository
entity
dto
exception
```

구체적인 패키지 구조는 `05_Backend_Design.md`에서 정의한다.

---

### 3. JPA와 QueryDSL을 함께 사용한다

기본 CRUD와 단순 조회는 Spring Data JPA를 사용한다.

동적 조건이 필요한 조회는 QueryDSL을 사용한다.

QueryDSL이 필요한 대표 기능은 다음과 같다.

- Project 목록 검색
    
- DailyLog 기간 조회
    
- Fabric 검색 및 필터
    
- Calendar 월별 집계
    
- Contribution 연도별 집계
    
- Fabric 사용 Project 조회
    

---

### 4. Spring Security 기반 JWT 인증을 구현한다

보호된 API는 Access Token을 통해 인증한다.

Refresh Token은 서버 저장소에서 관리한다.

인증 흐름은 다음과 같다.

```text
Kakao Login
→ User 생성 또는 조회
→ Access Token 발급
→ Refresh Token 저장
→ 보호된 API 요청 시 Access Token 검증
```

---

### 5. 트랜잭션 경계를 Service 계층에 둔다

데이터 변경 작업은 Service 계층에서 트랜잭션을 관리한다.

트랜잭션이 필요한 대표 기능은 다음과 같다.

- Project 생성 시 ProjectSpecification 함께 생성
    
- DailyLog 발행
    
- DailyLog 수정 시 TimeEntry 전체 교체 및 durationMinutes 재계산
    
- Project와 Fabric 연결
    
- Project 삭제
    
- Fabric 삭제
    
- Photo 대표 이미지 변경
    

---

### 6. 테스트 가능한 구조를 유지한다

비즈니스 규칙은 Controller가 아니라 Service 계층에서 검증한다.

이를 통해 핵심 규칙을 단위 테스트로 검증할 수 있도록 한다.

대표 테스트 대상은 다음과 같다.

- DailyLog 상태별 수정 규칙
    
- PUBLISHED DailyLog 통계 반영 규칙
    
- Project ARCHIVED 상태 제한
    
- Project-Fabric 중복 연결 방지
    
- Photo owner 권한 검증
    
- RefreshToken 무효화
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-002 Database로 PostgreSQL을 선택한 이유|Spring Data JPA, QueryDSL과 함께 사용|
|ADR-005 DailyLog에 DRAFT/PUBLISHED 상태를 둔 이유|상태 기반 비즈니스 규칙 구현|
|ADR-006 작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유|트랜잭션과 데이터 정합성 필요|
|ADR-008 Presigned URL 기반 이미지 업로드를 선택한 이유|Spring Boot 서버는 업로드 제어와 상태 관리 담당|
|ADR-012 Docker Compose + EC2 + Nginx 배포를 선택한 이유|Spring Boot 애플리케이션 배포 구조와 연결|

---

# ADR-001 요약

소로소로의 Backend Framework로 Java 17 + Spring Boot를 선택한다.

선택 이유는 다음과 같다.

- Project, DailyLog, Fabric, Photo 등 도메인 책임을 분리하기 좋다.
    
- 관계형 데이터 모델을 JPA로 표현하기 적합하다.
    
- QueryDSL을 통해 조건 조회와 통계성 조회를 구현하기 좋다.
    
- Spring Security로 인증과 권한 처리를 안정적으로 구현할 수 있다.
    
- 트랜잭션을 통해 데이터 정합성을 관리하기 좋다.
    
- 테스트 가능한 서비스 계층 구조를 만들 수 있다.
    
- Java/Spring 백엔드 포트폴리오로 설계 역량을 설명하기 좋다.
    

NestJS, Django, FastAPI, Express도 고려할 수 있지만, 소로소로의 핵심 목표가 “빠른 CRUD 구현”이 아니라 “도메인 관계와 비즈니스 규칙을 설명할 수 있는 백엔드 설계”에 있으므로 Spring Boot를 선택한다.


----
# ADR-002. Database로 PostgreSQL을 선택한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 개인 재봉 기록 서비스이다.

MVP에서 관리해야 하는 주요 데이터는 다음과 같다.

- User
    
- RefreshToken
    
- Project
    
- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- Fabric
    
- ProjectFabric
    
- Photo
    

이 데이터들은 대부분 명확한 관계를 가진다.

예를 들어 다음과 같은 관계가 존재한다.

- User는 여러 Project를 가진다.
    
- Project는 여러 DailyLog를 가진다.
    
- DailyLog는 여러 DailyLogTimeEntry를 가진다.
    
- User는 여러 Fabric을 가진다.
    
- Project와 Fabric은 ProjectFabric을 통해 다대다 관계를 가진다.
    
- Photo는 Project, DailyLog, Fabric 중 하나에 연결된다.
    

또한 소로소로는 단순한 데이터 저장뿐 아니라 다음과 같은 조회와 집계를 필요로 한다.

- Project별 DailyLog 목록 조회
    
- 날짜별 DailyLog 조회
    
- 월별 Calendar 집계
    
- 연도별 Contribution 집계
    
- Fabric 검색 및 필터
    
- Fabric이 사용된 Project 목록 조회
    
- Photo 상태 기반 조회
    
- PUBLISHED DailyLog만 통계에 반영
    

따라서 데이터베이스는 다음 조건을 만족해야 한다.

- 관계형 데이터 모델을 안정적으로 표현할 수 있어야 한다.
    
- 트랜잭션과 데이터 무결성을 보장할 수 있어야 한다.
    
- 조건 검색과 집계 조회를 처리할 수 있어야 한다.
    
- JPA, QueryDSL과 잘 연동되어야 한다.
    
- 향후 AI 기반 원단 정보 자동 기록 기능 확장을 고려할 수 있어야 한다.
    
- 1인 개발 MVP에서 운영 부담이 과도하지 않아야 한다.
    

---

## Decision

소로소로의 Database로 **PostgreSQL**을 선택한다.

MVP에서는 PostgreSQL을 관계형 데이터 저장소로 사용하고, Flyway를 통해 스키마 변경 이력을 관리한다.

주요 구성은 다음과 같다.

|영역|선택|
|---|---|
|Database|PostgreSQL|
|ORM|Spring Data JPA|
|Query|QueryDSL|
|Migration|Flyway|
|Data Model|Relational Model|
|Future Extension|JSONB, Full Text Search 검토 가능|

---

## Alternatives

### 1. MySQL

MySQL은 가장 널리 사용되는 관계형 데이터베이스 중 하나이다.

장점은 다음과 같다.

- 사용 경험이 많고 자료가 풍부하다.
    
- Spring Boot, JPA와 연동이 쉽다.
    
- 대부분의 CRUD 서비스에 충분하다.
    
- 운영 난이도가 낮다.
    
- AWS RDS 등 클라우드 환경에서 사용하기 좋다.
    

단점은 다음과 같다.

- PostgreSQL에 비해 JSONB 기반 확장성과 복합 데이터 처리 측면에서 상대적으로 매력이 낮다.
    
- 향후 AI 추출 결과처럼 구조가 유동적인 데이터를 저장할 때 PostgreSQL의 JSONB가 더 적합할 수 있다.
    
- 포트폴리오 관점에서 MySQL만 사용하는 것보다 PostgreSQL 선택 이유를 설명하는 것이 설계 의도를 드러내기 좋다.
    

MySQL도 소로소로 MVP를 구현하기에는 충분하다.  
하지만 소로소로는 향후 AI 원단 정보 자동 기록, 검색 고도화, 통계 기능 확장을 고려하기 때문에 PostgreSQL을 선택한다.

---

### 2. SQLite

SQLite는 파일 기반 경량 데이터베이스이다.

장점은 다음과 같다.

- 설치와 운영이 간단하다.
    
- 로컬 개발이나 개인용 앱에 적합하다.
    
- 별도 DB 서버가 필요 없다.
    
- 초기 MVP를 빠르게 구현하기 좋다.
    

단점은 다음과 같다.

- 서버 배포 환경에서 다중 사용자 요청을 처리하는 구조에는 적합하지 않다.
    
- 인증, 이미지 업로드, 웹 기반 서비스와 함께 운영하기에는 확장성이 부족하다.
    
- 포트폴리오에서 운영 가능한 백엔드 서비스 구조를 보여주기 어렵다.
    
- EC2, Docker Compose 기반 배포 환경에서는 PostgreSQL 컨테이너를 함께 사용하는 편이 더 자연스럽다.
    

소로소로가 완전한 로컬 개인 앱이라면 SQLite도 가능하다.  
하지만 본 프로젝트는 웹 기반 백엔드 포트폴리오이므로 SQLite는 선택하지 않는다.

---

### 3. MongoDB

MongoDB는 문서 기반 NoSQL 데이터베이스이다.

장점은 다음과 같다.

- 유연한 스키마를 사용할 수 있다.
    
- 원단 정보처럼 속성이 달라질 수 있는 데이터를 저장하기 편하다.
    
- JSON 형태 데이터 저장에 자연스럽다.
    
- AI 추출 결과 원문을 저장하기 좋다.
    

단점은 다음과 같다.

- 소로소로의 핵심 데이터는 관계가 명확한 구조이다.
    
- Project, DailyLog, Fabric, ProjectFabric 관계를 표현하기에는 관계형 DB가 더 적합하다.
    
- 트랜잭션과 데이터 무결성 설명 측면에서 PostgreSQL보다 포트폴리오 설득력이 떨어질 수 있다.
    
- JPA, QueryDSL 기반 백엔드 설계와 맞지 않는다.
    

소로소로는 “스키마가 자유로운 데이터 저장”보다 “관계가 명확한 기록 데이터의 정합성 유지”가 더 중요하므로 MongoDB는 선택하지 않는다.

---

### 4. PostgreSQL + JSONB 혼합 구조

PostgreSQL은 기본적으로 관계형 데이터베이스이지만 JSONB 타입을 지원한다.

장점은 다음과 같다.

- 핵심 도메인은 관계형 모델로 안정적으로 관리할 수 있다.
    
- 향후 AI 추출 결과처럼 구조가 유동적인 데이터는 JSONB로 저장할 수 있다.
    
- 관계형 모델과 반정형 데이터를 하나의 DB에서 함께 다룰 수 있다.
    
- 검색 고도화나 통계 확장에도 유리하다.
    

단점은 다음과 같다.

- JSONB를 남용하면 스키마가 불명확해질 수 있다.
    
- JPA 매핑과 검증 로직이 복잡해질 수 있다.
    
- MVP에서 필요하지 않은 유연성을 과도하게 먼저 적용할 위험이 있다.
    

따라서 MVP에서는 핵심 데이터를 명확한 정규 필드로 설계하고, JSONB는 향후 AI 추출 결과 저장이 필요해질 때 검토한다.

---

## Rationale

PostgreSQL을 선택한 이유는 다음과 같다.

---

### 1. 소로소로의 핵심 데이터는 관계형 모델에 적합하다

소로소로의 핵심 데이터는 명확한 관계를 가진다.

대표 관계는 다음과 같다.

```text
User
 ├── Project
 │    ├── ProjectSpecification
 │    ├── DailyLog
 │    │    └── DailyLogTimeEntry
 │    └── ProjectFabric
 │         └── Fabric
 ├── Fabric
 └── Photo
```

이 구조에서는 데이터 간 관계와 무결성이 중요하다.

예를 들어 다음 제약이 필요하다.

- DailyLog는 반드시 Project에 속해야 한다.
    
- DailyLogTimeEntry는 반드시 DailyLog에 속해야 한다.
    
- ProjectFabric은 Project와 Fabric을 연결해야 한다.
    
- 동일 Project에 동일 Fabric은 중복 연결될 수 없다.
    
- Photo는 반드시 User와 owner 정보를 가져야 한다.
    

PostgreSQL은 이러한 관계형 데이터와 제약조건을 안정적으로 관리하기에 적합하다.

---

### 2. 데이터 정합성을 유지하기 좋다

소로소로는 개인 기록 서비스이므로 사용자의 기록이 일관되게 유지되어야 한다.

특히 다음 규칙은 데이터 정합성과 직접 연결된다.

- PUBLISHED DailyLog는 workedDate와 TimeEntry를 가져야 한다.
    
- DailyLog.durationMinutes는 TimeEntry 합산값과 일치해야 한다.
    
- DRAFT DailyLog는 통계에 포함되지 않아야 한다.
    
- PUBLISHED DailyLog가 수정되면 통계 기준도 변경되어야 한다.
    
- Project 삭제 시 DailyLog와 ProjectFabric의 처리 범위가 명확해야 한다.
    
- Fabric 삭제 시 Project는 유지되어야 한다.
    

PostgreSQL은 트랜잭션, 제약조건, 인덱스, 집계 쿼리를 통해 이러한 데이터 정합성을 유지하기 좋다.

---

### 3. Calendar와 Contribution 집계에 적합하다

소로소로는 DailyLog 데이터를 기반으로 Calendar와 Contribution을 제공한다.

이 기능은 별도 원본 데이터를 저장하는 것이 아니라, PUBLISHED DailyLog의 workedDate와 durationMinutes를 기준으로 계산한다.

필요한 집계는 다음과 같다.

- 특정 월의 날짜별 DailyLog 수
    
- 특정 월의 날짜별 총 작업 시간
    
- 특정 연도의 날짜별 작업 시간
    
- 특정 연도의 activeDays
    
- Project별 총 작업 시간
    
- Fabric이 사용된 Project 목록
    

PostgreSQL은 날짜 조건, 그룹화, 합산, 정렬 같은 집계 조회에 적합하다.

QueryDSL과 함께 사용하면 이러한 조회를 타입 안정성 있게 구현할 수 있다.

---

### 4. 향후 AI 원단 자동 기록 기능 확장을 고려할 수 있다

MVP에서는 AI 기반 원단 정보 자동 기록 기능을 제외한다.

하지만 향후에는 다음과 같은 기능을 확장할 수 있다.

```text
상품 페이지 URL 또는 주문 내역 캡처
→ OCR / Vision LLM / DOM Extraction
→ 원단 정보 후보 추출
→ 사용자 검수
→ Fabric 생성
```

AI 추출 결과는 항상 정형화되어 있지 않을 수 있다.

예를 들어 쇼핑몰마다 다음 정보가 다르게 표현될 수 있다.

- 소재
    
- 혼용률
    
- 원단폭
    
- 구매 단위
    
- 옵션명
    
- 색상
    
- 상품명
    
- 품번
    

PostgreSQL을 사용하면 MVP에서는 Fabric의 주요 정보를 정규 필드로 관리하고, 향후 필요 시 AI 추출 원문이나 후보 데이터를 JSONB로 확장할 수 있다.

즉, PostgreSQL은 관계형 데이터의 정합성과 반정형 데이터의 확장 가능성을 동시에 제공한다.

---

### 5. 검색 고도화 가능성이 있다

MVP에서는 기본적인 Fabric 목록 조회와 검색만 제공한다.

향후에는 다음과 같은 검색 고도화가 가능하다.

- 원단명 검색
    
- 쇼핑몰 상품명 검색
    
- 품번 검색
    
- 구매처 검색
    
- 색상 검색
    
- 소재 검색
    
- 메모 검색
    
- 재구매 의사 필터
    
- 사용 Project 기준 필터
    

PostgreSQL은 기본적인 LIKE 검색뿐 아니라, 향후 Full Text Search나 별도 검색 최적화 전략을 검토할 수 있다.

MVP에서 검색 엔진을 별도로 도입하지 않아도, PostgreSQL 기반으로 확장 여지를 남길 수 있다.

---

### 6. 포트폴리오에서 설계 의도를 설명하기 좋다

MySQL은 충분히 좋은 선택지이지만, 소로소로에서는 PostgreSQL을 선택함으로써 다음 설계 의도를 설명할 수 있다.

- 단순 CRUD가 아니라 관계형 데이터 정합성을 고려했다.
    
- Project와 Fabric의 다대다 관계를 명확히 관리한다.
    
- DailyLog 기반 집계 기능을 고려했다.
    
- 향후 AI 추출 결과처럼 유동적인 데이터를 JSONB로 확장할 수 있다.
    
- 검색 고도화 가능성을 고려했다.
    
- Flyway를 통해 스키마 변경 이력을 관리한다.
    

이는 포트폴리오에서 “데이터베이스를 왜 선택했는가”에 대한 답변으로 활용하기 좋다.

---

## Trade-off

### 장점

PostgreSQL을 선택하면 다음 장점이 있다.

- 관계형 데이터 모델을 안정적으로 표현할 수 있다.
    
- 트랜잭션과 데이터 무결성 관리에 적합하다.
    
- Calendar와 Contribution 같은 집계 조회에 적합하다.
    
- JPA, QueryDSL과 연동할 수 있다.
    
- 향후 JSONB를 통해 AI 추출 결과를 유연하게 저장할 수 있다.
    
- Full Text Search 등 검색 고도화 가능성이 있다.
    
- 포트폴리오에서 기술 선택 이유를 설명하기 좋다.
    

---

### 단점

PostgreSQL 선택으로 인해 다음 비용도 발생한다.

- MySQL보다 사용자에게 익숙하지 않을 수 있다.
    
- 일부 JPA 매핑이나 JSONB 활용은 추가 학습이 필요하다.
    
- 단순 CRUD 서비스라면 MySQL이나 SQLite보다 선택 이유를 더 명확히 설명해야 한다.
    
- 운영 환경에서 PostgreSQL 설정과 백업 전략을 별도로 고려해야 한다.
    
- JSONB를 사용할 경우 스키마 관리가 흐려질 수 있다.
    

---

### Trade-off 판단

소로소로 MVP만 놓고 보면 MySQL도 충분히 구현 가능하다.

하지만 소로소로는 다음 특징을 가진다.

- Project, DailyLog, Fabric, Photo 간 관계가 명확하다.
    
- Calendar와 Contribution처럼 날짜 기반 집계가 필요하다.
    
- Fabric 검색과 필터가 중요해질 가능성이 있다.
    
- 향후 AI 원단 자동 기록 기능에서 유동적인 추출 결과를 다룰 수 있다.
    
- 포트폴리오에서 데이터 모델링 선택 이유를 설명해야 한다.
    

따라서 관계형 데이터 정합성과 향후 확장 가능성을 함께 고려하여 PostgreSQL을 선택한다.

---

## Consequences

PostgreSQL을 선택함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. 핵심 도메인은 정규화된 관계형 모델로 설계한다

MVP에서는 다음 데이터를 정규화된 테이블로 관리한다.

- User
    
- Project
    
- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- Fabric
    
- ProjectFabric
    
- Photo
    
- RefreshToken
    

AI 추출 결과처럼 유동적인 데이터는 MVP에 포함하지 않는다.

---

### 2. JSONB는 MVP에서 남용하지 않는다

PostgreSQL은 JSONB를 지원하지만, MVP에서는 Fabric의 핵심 정보를 명확한 필드로 관리한다.

예를 들어 다음 정보는 Fabric의 정규 필드로 둔다.

- productName
    
- productCode
    
- productUrl
    
- storeName
    
- purchasePrice
    
- color
    
- size
    
- width
    
- materialComposition
    

향후 AI 추출 기능을 도입할 때 `rawExtractedData`, `extractedAttributes`, `confidence` 등은 JSONB 또는 별도 추출 테이블로 설계할 수 있다.

---

### 3. Flyway로 스키마 변경 이력을 관리한다

데이터베이스 스키마는 Flyway를 통해 버전 관리한다.

이를 통해 다음을 얻을 수 있다.

- 로컬과 운영 환경의 스키마 일관성 유지
    
- 변경 이력 추적
    
- 배포 시 DB 변경 자동 적용
    
- 포트폴리오에서 운영 친화적인 설계 설명 가능
    

---

### 4. QueryDSL로 복잡한 조회를 처리한다

다음 기능은 QueryDSL 기반 조회를 고려한다.

- Project 목록 조회
    
- DailyLog 기간별 조회
    
- Calendar 월별 집계
    
- Contribution 연도별 집계
    
- Fabric 검색 및 필터
    
- Fabric 사용 Project 조회
    
- Photo ownerType별 조회
    

단순 CRUD는 Spring Data JPA Repository를 사용하고, 조건 조합이 필요한 조회는 QueryDSL을 사용한다.

---

### 5. 제약조건을 데이터베이스와 애플리케이션 양쪽에서 관리한다

데이터 무결성은 애플리케이션 검증만으로 처리하지 않는다.

다음 규칙은 DB 제약조건 또는 유니크 제약으로도 관리할 수 있다.

- User.kakaoId unique
    
- 동일 Project에 동일 Fabric 중복 연결 금지
    
- 필수 FK 무결성
    
- Enum 값 제한
    
- Not Null 제약
    
- createdAt, updatedAt 관리
    

구체적인 제약조건은 `04_ERD.md`에서 정의한다.

---

### 6. 날짜 기반 조회를 고려한 인덱스를 설계한다

Calendar와 Contribution 성능을 위해 다음 기준의 인덱스를 고려한다.

- DailyLog.userId
    
- DailyLog.projectId
    
- DailyLog.status
    
- DailyLog.workedDate
    
- DailyLog.userId + status + workedDate
    
- ProjectFabric.projectId
    
- ProjectFabric.fabricId
    
- Photo.ownerType + ownerId
    

구체적인 인덱스 설계는 `04_ERD.md`와 `05_Backend_Design.md`에서 정의한다.

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-001 Backend Framework로 Spring Boot를 선택한 이유|Spring Data JPA, QueryDSL과 함께 사용|
|ADR-003 Fabric을 Project와 독립 도메인으로 분리한 이유|Fabric을 독립 테이블로 관리|
|ADR-004 Project-Fabric 관계를 다대다 + 연결 엔티티로 설계한 이유|ProjectFabric 관계 모델 필요|
|ADR-006 작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유|날짜·시간 기반 집계 필요|
|ADR-010 Calendar/Contribution을 저장하지 않고 조회 시 계산하는 이유|PostgreSQL 집계 조회 활용|
|ADR-011 MVP에서 AI 원단 자동 기록을 제외한 이유|향후 JSONB 또는 Extraction Job 확장 가능|

---

# ADR-002 요약

소로소로의 Database로 PostgreSQL을 선택한다.

선택 이유는 다음과 같다.

- 소로소로의 핵심 데이터는 관계형 모델에 적합하다.
    
- Project, DailyLog, Fabric, Photo 간 관계와 무결성을 안정적으로 관리할 수 있다.
    
- Calendar와 Contribution 같은 날짜 기반 집계 조회에 적합하다.
    
- JPA, QueryDSL과 함께 사용하기 좋다.
    
- 향후 AI 원단 자동 기록 기능에서 JSONB 기반 확장을 고려할 수 있다.
    
- Fabric 검색과 통계 기능 고도화 가능성이 있다.
    
- Flyway를 통해 스키마 변경 이력을 관리하기 좋다.
    

MySQL도 MVP 구현에는 충분하지만, 소로소로는 관계형 정합성과 향후 반정형 데이터 확장 가능성을 모두 고려하기 때문에 PostgreSQL을 선택한다.


---
# ADR-002. Database로 PostgreSQL을 선택한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 개인 재봉 기록 서비스이다.

MVP에서 관리해야 하는 주요 데이터는 다음과 같다.

- User
    
- RefreshToken
    
- Project
    
- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- Fabric
    
- ProjectFabric
    
- Photo
    

이 데이터들은 대부분 명확한 관계를 가진다.

예를 들어 다음과 같은 관계가 존재한다.

- User는 여러 Project를 가진다.
    
- Project는 여러 DailyLog를 가진다.
    
- DailyLog는 여러 DailyLogTimeEntry를 가진다.
    
- User는 여러 Fabric을 가진다.
    
- Project와 Fabric은 ProjectFabric을 통해 다대다 관계를 가진다.
    
- Photo는 Project, DailyLog, Fabric 중 하나에 연결된다.
    

또한 소로소로는 단순한 데이터 저장뿐 아니라 다음과 같은 조회와 집계를 필요로 한다.

- Project별 DailyLog 목록 조회
    
- 날짜별 DailyLog 조회
    
- 월별 Calendar 집계
    
- 연도별 Contribution 집계
    
- Fabric 검색 및 필터
    
- Fabric이 사용된 Project 목록 조회
    
- Photo 상태 기반 조회
    
- PUBLISHED DailyLog만 통계에 반영
    

따라서 데이터베이스는 다음 조건을 만족해야 한다.

- 관계형 데이터 모델을 안정적으로 표현할 수 있어야 한다.
    
- 트랜잭션과 데이터 무결성을 보장할 수 있어야 한다.
    
- 조건 검색과 집계 조회를 처리할 수 있어야 한다.
    
- JPA, QueryDSL과 잘 연동되어야 한다.
    
- 향후 AI 기반 원단 정보 자동 기록 기능 확장을 고려할 수 있어야 한다.
    
- 1인 개발 MVP에서 운영 부담이 과도하지 않아야 한다.
    

---

## Decision

소로소로의 Database로 **PostgreSQL**을 선택한다.

MVP에서는 PostgreSQL을 관계형 데이터 저장소로 사용하고, Flyway를 통해 스키마 변경 이력을 관리한다.

주요 구성은 다음과 같다.

|영역|선택|
|---|---|
|Database|PostgreSQL|
|ORM|Spring Data JPA|
|Query|QueryDSL|
|Migration|Flyway|
|Data Model|Relational Model|
|Future Extension|JSONB, Full Text Search 검토 가능|

---

## Alternatives

### 1. MySQL

MySQL은 가장 널리 사용되는 관계형 데이터베이스 중 하나이다.

장점은 다음과 같다.

- 사용 경험이 많고 자료가 풍부하다.
    
- Spring Boot, JPA와 연동이 쉽다.
    
- 대부분의 CRUD 서비스에 충분하다.
    
- 운영 난이도가 낮다.
    
- AWS RDS 등 클라우드 환경에서 사용하기 좋다.
    

단점은 다음과 같다.

- PostgreSQL에 비해 JSONB 기반 확장성과 복합 데이터 처리 측면에서 상대적으로 매력이 낮다.
    
- 향후 AI 추출 결과처럼 구조가 유동적인 데이터를 저장할 때 PostgreSQL의 JSONB가 더 적합할 수 있다.
    
- 포트폴리오 관점에서 MySQL만 사용하는 것보다 PostgreSQL 선택 이유를 설명하는 것이 설계 의도를 드러내기 좋다.
    

MySQL도 소로소로 MVP를 구현하기에는 충분하다.  
하지만 소로소로는 향후 AI 원단 정보 자동 기록, 검색 고도화, 통계 기능 확장을 고려하기 때문에 PostgreSQL을 선택한다.

---

### 2. SQLite

SQLite는 파일 기반 경량 데이터베이스이다.

장점은 다음과 같다.

- 설치와 운영이 간단하다.
    
- 로컬 개발이나 개인용 앱에 적합하다.
    
- 별도 DB 서버가 필요 없다.
    
- 초기 MVP를 빠르게 구현하기 좋다.
    

단점은 다음과 같다.

- 서버 배포 환경에서 다중 사용자 요청을 처리하는 구조에는 적합하지 않다.
    
- 인증, 이미지 업로드, 웹 기반 서비스와 함께 운영하기에는 확장성이 부족하다.
    
- 포트폴리오에서 운영 가능한 백엔드 서비스 구조를 보여주기 어렵다.
    
- EC2, Docker Compose 기반 배포 환경에서는 PostgreSQL 컨테이너를 함께 사용하는 편이 더 자연스럽다.
    

소로소로가 완전한 로컬 개인 앱이라면 SQLite도 가능하다.  
하지만 본 프로젝트는 웹 기반 백엔드 포트폴리오이므로 SQLite는 선택하지 않는다.

---

### 3. MongoDB

MongoDB는 문서 기반 NoSQL 데이터베이스이다.

장점은 다음과 같다.

- 유연한 스키마를 사용할 수 있다.
    
- 원단 정보처럼 속성이 달라질 수 있는 데이터를 저장하기 편하다.
    
- JSON 형태 데이터 저장에 자연스럽다.
    
- AI 추출 결과 원문을 저장하기 좋다.
    

단점은 다음과 같다.

- 소로소로의 핵심 데이터는 관계가 명확한 구조이다.
    
- Project, DailyLog, Fabric, ProjectFabric 관계를 표현하기에는 관계형 DB가 더 적합하다.
    
- 트랜잭션과 데이터 무결성 설명 측면에서 PostgreSQL보다 포트폴리오 설득력이 떨어질 수 있다.
    
- JPA, QueryDSL 기반 백엔드 설계와 맞지 않는다.
    

소로소로는 “스키마가 자유로운 데이터 저장”보다 “관계가 명확한 기록 데이터의 정합성 유지”가 더 중요하므로 MongoDB는 선택하지 않는다.

---

### 4. PostgreSQL + JSONB 혼합 구조

PostgreSQL은 기본적으로 관계형 데이터베이스이지만 JSONB 타입을 지원한다.

장점은 다음과 같다.

- 핵심 도메인은 관계형 모델로 안정적으로 관리할 수 있다.
    
- 향후 AI 추출 결과처럼 구조가 유동적인 데이터는 JSONB로 저장할 수 있다.
    
- 관계형 모델과 반정형 데이터를 하나의 DB에서 함께 다룰 수 있다.
    
- 검색 고도화나 통계 확장에도 유리하다.
    

단점은 다음과 같다.

- JSONB를 남용하면 스키마가 불명확해질 수 있다.
    
- JPA 매핑과 검증 로직이 복잡해질 수 있다.
    
- MVP에서 필요하지 않은 유연성을 과도하게 먼저 적용할 위험이 있다.
    

따라서 MVP에서는 핵심 데이터를 명확한 정규 필드로 설계하고, JSONB는 향후 AI 추출 결과 저장이 필요해질 때 검토한다.

---

## Rationale

PostgreSQL을 선택한 이유는 다음과 같다.

---

### 1. 소로소로의 핵심 데이터는 관계형 모델에 적합하다

소로소로의 핵심 데이터는 명확한 관계를 가진다.

대표 관계는 다음과 같다.

```text
User
 ├── Project
 │    ├── ProjectSpecification
 │    ├── DailyLog
 │    │    └── DailyLogTimeEntry
 │    └── ProjectFabric
 │         └── Fabric
 ├── Fabric
 └── Photo
```

이 구조에서는 데이터 간 관계와 무결성이 중요하다.

예를 들어 다음 제약이 필요하다.

- DailyLog는 반드시 Project에 속해야 한다.
    
- DailyLogTimeEntry는 반드시 DailyLog에 속해야 한다.
    
- ProjectFabric은 Project와 Fabric을 연결해야 한다.
    
- 동일 Project에 동일 Fabric은 중복 연결될 수 없다.
    
- Photo는 반드시 User와 owner 정보를 가져야 한다.
    

PostgreSQL은 이러한 관계형 데이터와 제약조건을 안정적으로 관리하기에 적합하다.

---

### 2. 데이터 정합성을 유지하기 좋다

소로소로는 개인 기록 서비스이므로 사용자의 기록이 일관되게 유지되어야 한다.

특히 다음 규칙은 데이터 정합성과 직접 연결된다.

- PUBLISHED DailyLog는 workedDate와 TimeEntry를 가져야 한다.
    
- DailyLog.durationMinutes는 TimeEntry 합산값과 일치해야 한다.
    
- DRAFT DailyLog는 통계에 포함되지 않아야 한다.
    
- PUBLISHED DailyLog가 수정되면 통계 기준도 변경되어야 한다.
    
- Project 삭제 시 DailyLog와 ProjectFabric의 처리 범위가 명확해야 한다.
    
- Fabric 삭제 시 Project는 유지되어야 한다.
    

PostgreSQL은 트랜잭션, 제약조건, 인덱스, 집계 쿼리를 통해 이러한 데이터 정합성을 유지하기 좋다.

---

### 3. Calendar와 Contribution 집계에 적합하다

소로소로는 DailyLog 데이터를 기반으로 Calendar와 Contribution을 제공한다.

이 기능은 별도 원본 데이터를 저장하는 것이 아니라, PUBLISHED DailyLog의 workedDate와 durationMinutes를 기준으로 계산한다.

필요한 집계는 다음과 같다.

- 특정 월의 날짜별 DailyLog 수
    
- 특정 월의 날짜별 총 작업 시간
    
- 특정 연도의 날짜별 작업 시간
    
- 특정 연도의 activeDays
    
- Project별 총 작업 시간
    
- Fabric이 사용된 Project 목록
    

PostgreSQL은 날짜 조건, 그룹화, 합산, 정렬 같은 집계 조회에 적합하다.

QueryDSL과 함께 사용하면 이러한 조회를 타입 안정성 있게 구현할 수 있다.

---

### 4. 향후 AI 원단 자동 기록 기능 확장을 고려할 수 있다

MVP에서는 AI 기반 원단 정보 자동 기록 기능을 제외한다.

하지만 향후에는 다음과 같은 기능을 확장할 수 있다.

```text
상품 페이지 URL 또는 주문 내역 캡처
→ OCR / Vision LLM / DOM Extraction
→ 원단 정보 후보 추출
→ 사용자 검수
→ Fabric 생성
```

AI 추출 결과는 항상 정형화되어 있지 않을 수 있다.

예를 들어 쇼핑몰마다 다음 정보가 다르게 표현될 수 있다.

- 소재
    
- 혼용률
    
- 원단폭
    
- 구매 단위
    
- 옵션명
    
- 색상
    
- 상품명
    
- 품번
    

PostgreSQL을 사용하면 MVP에서는 Fabric의 주요 정보를 정규 필드로 관리하고, 향후 필요 시 AI 추출 원문이나 후보 데이터를 JSONB로 확장할 수 있다.

즉, PostgreSQL은 관계형 데이터의 정합성과 반정형 데이터의 확장 가능성을 동시에 제공한다.

---

### 5. 검색 고도화 가능성이 있다

MVP에서는 기본적인 Fabric 목록 조회와 검색만 제공한다.

향후에는 다음과 같은 검색 고도화가 가능하다.

- 원단명 검색
    
- 쇼핑몰 상품명 검색
    
- 품번 검색
    
- 구매처 검색
    
- 색상 검색
    
- 소재 검색
    
- 메모 검색
    
- 재구매 의사 필터
    
- 사용 Project 기준 필터
    

PostgreSQL은 기본적인 LIKE 검색뿐 아니라, 향후 Full Text Search나 별도 검색 최적화 전략을 검토할 수 있다.

MVP에서 검색 엔진을 별도로 도입하지 않아도, PostgreSQL 기반으로 확장 여지를 남길 수 있다.

---

### 6. 포트폴리오에서 설계 의도를 설명하기 좋다

MySQL은 충분히 좋은 선택지이지만, 소로소로에서는 PostgreSQL을 선택함으로써 다음 설계 의도를 설명할 수 있다.

- 단순 CRUD가 아니라 관계형 데이터 정합성을 고려했다.
    
- Project와 Fabric의 다대다 관계를 명확히 관리한다.
    
- DailyLog 기반 집계 기능을 고려했다.
    
- 향후 AI 추출 결과처럼 유동적인 데이터를 JSONB로 확장할 수 있다.
    
- 검색 고도화 가능성을 고려했다.
    
- Flyway를 통해 스키마 변경 이력을 관리한다.
    

이는 포트폴리오에서 “데이터베이스를 왜 선택했는가”에 대한 답변으로 활용하기 좋다.

---

## Trade-off

### 장점

PostgreSQL을 선택하면 다음 장점이 있다.

- 관계형 데이터 모델을 안정적으로 표현할 수 있다.
    
- 트랜잭션과 데이터 무결성 관리에 적합하다.
    
- Calendar와 Contribution 같은 집계 조회에 적합하다.
    
- JPA, QueryDSL과 연동할 수 있다.
    
- 향후 JSONB를 통해 AI 추출 결과를 유연하게 저장할 수 있다.
    
- Full Text Search 등 검색 고도화 가능성이 있다.
    
- 포트폴리오에서 기술 선택 이유를 설명하기 좋다.
    

---

### 단점

PostgreSQL 선택으로 인해 다음 비용도 발생한다.

- MySQL보다 사용자에게 익숙하지 않을 수 있다.
    
- 일부 JPA 매핑이나 JSONB 활용은 추가 학습이 필요하다.
    
- 단순 CRUD 서비스라면 MySQL이나 SQLite보다 선택 이유를 더 명확히 설명해야 한다.
    
- 운영 환경에서 PostgreSQL 설정과 백업 전략을 별도로 고려해야 한다.
    
- JSONB를 사용할 경우 스키마 관리가 흐려질 수 있다.
    

---

### Trade-off 판단

소로소로 MVP만 놓고 보면 MySQL도 충분히 구현 가능하다.

하지만 소로소로는 다음 특징을 가진다.

- Project, DailyLog, Fabric, Photo 간 관계가 명확하다.
    
- Calendar와 Contribution처럼 날짜 기반 집계가 필요하다.
    
- Fabric 검색과 필터가 중요해질 가능성이 있다.
    
- 향후 AI 원단 자동 기록 기능에서 유동적인 추출 결과를 다룰 수 있다.
    
- 포트폴리오에서 데이터 모델링 선택 이유를 설명해야 한다.
    

따라서 관계형 데이터 정합성과 향후 확장 가능성을 함께 고려하여 PostgreSQL을 선택한다.

---

## Consequences

PostgreSQL을 선택함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. 핵심 도메인은 정규화된 관계형 모델로 설계한다

MVP에서는 다음 데이터를 정규화된 테이블로 관리한다.

- User
    
- Project
    
- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- Fabric
    
- ProjectFabric
    
- Photo
    
- RefreshToken
    

AI 추출 결과처럼 유동적인 데이터는 MVP에 포함하지 않는다.

---

### 2. JSONB는 MVP에서 남용하지 않는다

PostgreSQL은 JSONB를 지원하지만, MVP에서는 Fabric의 핵심 정보를 명확한 필드로 관리한다.

예를 들어 다음 정보는 Fabric의 정규 필드로 둔다.

- productName
    
- productCode
    
- productUrl
    
- storeName
    
- purchasePrice
    
- color
    
- size
    
- width
    
- materialComposition
    

향후 AI 추출 기능을 도입할 때 `rawExtractedData`, `extractedAttributes`, `confidence` 등은 JSONB 또는 별도 추출 테이블로 설계할 수 있다.

---

### 3. Flyway로 스키마 변경 이력을 관리한다

데이터베이스 스키마는 Flyway를 통해 버전 관리한다.

이를 통해 다음을 얻을 수 있다.

- 로컬과 운영 환경의 스키마 일관성 유지
    
- 변경 이력 추적
    
- 배포 시 DB 변경 자동 적용
    
- 포트폴리오에서 운영 친화적인 설계 설명 가능
    

---

### 4. QueryDSL로 복잡한 조회를 처리한다

다음 기능은 QueryDSL 기반 조회를 고려한다.

- Project 목록 조회
    
- DailyLog 기간별 조회
    
- Calendar 월별 집계
    
- Contribution 연도별 집계
    
- Fabric 검색 및 필터
    
- Fabric 사용 Project 조회
    
- Photo ownerType별 조회
    

단순 CRUD는 Spring Data JPA Repository를 사용하고, 조건 조합이 필요한 조회는 QueryDSL을 사용한다.

---

### 5. 제약조건을 데이터베이스와 애플리케이션 양쪽에서 관리한다

데이터 무결성은 애플리케이션 검증만으로 처리하지 않는다.

다음 규칙은 DB 제약조건 또는 유니크 제약으로도 관리할 수 있다.

- User.kakaoId unique
    
- 동일 Project에 동일 Fabric 중복 연결 금지
    
- 필수 FK 무결성
    
- Enum 값 제한
    
- Not Null 제약
    
- createdAt, updatedAt 관리
    

구체적인 제약조건은 `04_ERD.md`에서 정의한다.

---

### 6. 날짜 기반 조회를 고려한 인덱스를 설계한다

Calendar와 Contribution 성능을 위해 다음 기준의 인덱스를 고려한다.

- DailyLog.userId
    
- DailyLog.projectId
    
- DailyLog.status
    
- DailyLog.workedDate
    
- DailyLog.userId + status + workedDate
    
- ProjectFabric.projectId
    
- ProjectFabric.fabricId
    
- Photo.ownerType + ownerId
    

구체적인 인덱스 설계는 `04_ERD.md`와 `05_Backend_Design.md`에서 정의한다.

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-001 Backend Framework로 Spring Boot를 선택한 이유|Spring Data JPA, QueryDSL과 함께 사용|
|ADR-003 Fabric을 Project와 독립 도메인으로 분리한 이유|Fabric을 독립 테이블로 관리|
|ADR-004 Project-Fabric 관계를 다대다 + 연결 엔티티로 설계한 이유|ProjectFabric 관계 모델 필요|
|ADR-006 작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유|날짜·시간 기반 집계 필요|
|ADR-010 Calendar/Contribution을 저장하지 않고 조회 시 계산하는 이유|PostgreSQL 집계 조회 활용|
|ADR-011 MVP에서 AI 원단 자동 기록을 제외한 이유|향후 JSONB 또는 Extraction Job 확장 가능|

---

# ADR-002 요약

소로소로의 Database로 PostgreSQL을 선택한다.

선택 이유는 다음과 같다.

- 소로소로의 핵심 데이터는 관계형 모델에 적합하다.
    
- Project, DailyLog, Fabric, Photo 간 관계와 무결성을 안정적으로 관리할 수 있다.
    
- Calendar와 Contribution 같은 날짜 기반 집계 조회에 적합하다.
    
- JPA, QueryDSL과 함께 사용하기 좋다.
    
- 향후 AI 원단 자동 기록 기능에서 JSONB 기반 확장을 고려할 수 있다.
    
- Fabric 검색과 통계 기능 고도화 가능성이 있다.
    
- Flyway를 통해 스키마 변경 이력을 관리하기 좋다.
    

MySQL도 MVP 구현에는 충분하지만, 소로소로는 관계형 정합성과 향후 반정형 데이터 확장 가능성을 모두 고려하기 때문에 PostgreSQL을 선택한다.

---
# ADR-004. Project-Fabric 관계를 다대다 + 연결 엔티티로 설계한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)에서 Project는 하나의 재봉 작품을 의미하고, Fabric은 사용자가 구매한 원단 기록을 의미한다.

ADR-003에서 Fabric은 Project의 하위 속성이 아니라 독립 도메인으로 분리하기로 결정했다.

따라서 다음 관계를 표현해야 한다.

- 하나의 Project는 여러 Fabric을 사용할 수 있다.
    
- 하나의 Fabric은 여러 Project에 사용될 수 있다.
    
- Project와 Fabric은 서로 독립적으로 생성, 수정, 삭제될 수 있다.
    
- Project를 삭제해도 Fabric 자체는 삭제되지 않아야 한다.
    
- Fabric을 삭제해도 Project 자체는 삭제되지 않아야 한다.
    

예를 들어 하나의 Project가 다음 원단을 사용할 수 있다.

```text
Project: 딸기 파우치
 ├── Fabric: 딸기 면 원단
 ├── Fabric: 흰색 안감 원단
 └── Fabric: 레이스 원단
```

반대로 하나의 Fabric이 여러 Project에 사용될 수도 있다.

```text
Fabric: 딸기 면 원단
 ├── Project: 딸기 파우치
 ├── Project: 카드지갑
 └── Project: 헤어리본
```

이 관계는 단순한 1:N 관계가 아니라 N:M 관계이다.

또한 Project와 Fabric의 연결 자체에도 의미가 있다.

예를 들어 사용자는 특정 Project에서 해당 Fabric을 다음과 같이 기록하고 싶을 수 있다.

- 겉감으로 사용
    
- 안감으로 사용
    
- 포켓 부분에 사용
    
- 배색용으로 사용
    
- 테스트용으로 조금 사용
    
- 생각보다 얇아서 심지를 함께 사용
    
- 다음에는 이 조합을 쓰지 않을 예정
    

따라서 단순히 Project와 Fabric을 연결하는 것만으로는 부족하고, 연결 관계에 메모를 남길 수 있어야 한다.

---

## Decision

Project와 Fabric은 **다대다 관계**로 설계하되, JPA의 직접 `@ManyToMany` 매핑을 사용하지 않고 **ProjectFabric 연결 엔티티**를 둔다.

결정된 구조는 다음과 같다.

```text
Project
 └── ProjectFabric
      └── Fabric
```

즉, 관계는 다음과 같이 표현한다.

```text
Project 1 : N ProjectFabric
Fabric  1 : N ProjectFabric
Project N : M Fabric
```

MVP에서 ProjectFabric은 다음 정보를 가진다.

|속성|설명|
|---|---|
|id|ProjectFabric 식별자|
|projectId|연결된 Project 식별자|
|fabricId|연결된 Fabric 식별자|
|memo|해당 Project에서 Fabric을 사용한 맥락|
|createdAt|생성 일시|
|updatedAt|수정 일시|

또한 동일 Project에 동일 Fabric을 중복 연결하지 않기 위해 다음 제약을 둔다.

```text
unique(projectId, fabricId)
```

---

## Alternatives

### 1. Project가 Fabric을 단일 참조하는 1:N 구조

첫 번째 대안은 하나의 Project가 하나의 Fabric만 참조하는 구조이다.

```text
Project
 └── Fabric
```

또는 Project에 `fabricId`를 두는 방식이다.

```text
Project
 └── fabricId
```

#### 장점

- 구현이 가장 단순하다.
    
- Project 상세에서 Fabric을 바로 조회할 수 있다.
    
- 별도 연결 엔티티가 필요 없다.
    

#### 단점

- 하나의 Project에 여러 Fabric을 연결할 수 없다.
    
- 겉감, 안감, 배색 원단처럼 여러 원단을 쓰는 재봉 작업을 표현하기 어렵다.
    
- 하나의 Project가 하나의 원단만 사용하는 것으로 모델이 제한된다.
    
- 실제 재봉 작업 흐름과 맞지 않는다.
    

이 방식은 소로소로의 원단 사용 구조를 표현하기에 부족하다.

---

### 2. Fabric이 Project를 단일 참조하는 N:1 구조

두 번째 대안은 Fabric이 특정 Project에 속하도록 만드는 구조이다.

```text
Project
 └── Fabric
```

즉, Fabric 테이블에 `projectId`를 두는 방식이다.

#### 장점

- Project별 사용 원단 목록을 조회하기 쉽다.
    
- Project 삭제 시 관련 Fabric을 함께 처리하기 쉽다.
    
- 구현이 비교적 단순하다.
    

#### 단점

- Fabric이 Project 없이 존재할 수 없다.
    
- 하나의 Fabric을 여러 Project에 연결할 수 없다.
    
- 같은 원단을 여러 Project에서 사용하려면 중복 등록해야 한다.
    
- 원단 구매 기록을 독립적으로 관리하기 어렵다.
    
- Fabric 삭제와 Project 삭제의 영향 범위가 모호해진다.
    

이 방식은 ADR-003에서 결정한 “Fabric은 독립 도메인”이라는 방향과 맞지 않는다.

---

### 3. JPA `@ManyToMany` 직접 매핑

세 번째 대안은 Project와 Fabric을 JPA의 `@ManyToMany`로 직접 연결하는 방식이다.

예시는 다음과 같다.

```java
@ManyToMany
private List<Fabric> fabrics;
```

#### 장점

- 코드가 간단해 보인다.
    
- 연결 테이블을 별도 엔티티로 직접 다루지 않아도 된다.
    
- Project에서 Fabric 목록을 쉽게 접근할 수 있다.
    
- Fabric에서 Project 목록을 쉽게 접근할 수 있다.
    

#### 단점

- 연결 관계에 속성을 추가하기 어렵다.
    
- memo, createdAt, updatedAt 같은 연결 정보를 저장하기 어렵다.
    
- 향후 사용량, 용도, 재단 부위 등을 추가하려면 구조를 변경해야 한다.
    
- 연결 테이블을 도메인으로 다루기 어렵다.
    
- 삭제, 중복 방지, 권한 검증을 명확히 표현하기 어렵다.
    
- 실무적 JPA 설계에서는 확장 가능성이 낮다.
    

소로소로에서는 Project와 Fabric의 연결 관계 자체가 의미를 가지므로, 직접 `@ManyToMany`는 사용하지 않는다.

---

### 4. ProjectFabric 연결 엔티티 사용

네 번째 대안은 Project와 Fabric 사이에 연결 엔티티를 두는 방식이다.

```text
Project
 └── ProjectFabric
      └── Fabric
```

#### 장점

- Project와 Fabric의 다대다 관계를 명확히 표현할 수 있다.
    
- 연결 관계에 memo를 저장할 수 있다.
    
- 동일 Project-Fabric 중복 연결을 제어할 수 있다.
    
- 향후 usageRole, usedLength, usedArea 등으로 확장할 수 있다.
    
- 삭제 영향 범위를 명확히 관리할 수 있다.
    
- JPA에서 관계를 더 명확하게 다룰 수 있다.
    
- 권한 검증 시 Project와 Fabric 양쪽 소유권을 확인하기 쉽다.
    

#### 단점

- 엔티티가 하나 더 늘어난다.
    
- 연결/해제 API를 별도로 구현해야 한다.
    
- Project 상세 조회 시 ProjectFabric을 함께 조회해야 한다.
    
- Fabric 사용 Project 조회 시 ProjectFabric을 기준으로 역조회해야 한다.
    
- UI에서 “원단 등록”과 “프로젝트에 원단 연결” 흐름을 분리해야 한다.
    

소로소로는 연결 관계에 의미가 있고 향후 확장 가능성을 고려해야 하므로, 이 방식을 선택한다.

---

## Rationale

Project-Fabric 관계를 다대다 + 연결 엔티티로 설계한 이유는 다음과 같다.

---

### 1. 실제 재봉 작업은 Project와 Fabric이 다대다 관계에 가깝다

하나의 작품은 여러 원단으로 구성될 수 있다.

예를 들어 파우치를 만들 때 다음 원단이 함께 사용될 수 있다.

- 겉감
    
- 안감
    
- 배색 원단
    
- 포켓 원단
    
- 장식용 원단
    

반대로 하나의 원단은 여러 작품에 나누어 사용될 수 있다.

예를 들어 딸기 면 원단을 다음 작품에 사용할 수 있다.

- 파우치
    
- 카드지갑
    
- 헤어리본
    
- 키링
    
- 작은 주머니
    

따라서 Project와 Fabric의 관계는 1:N이나 N:1로 고정하기 어렵다.

다대다 관계가 실제 사용 흐름을 가장 자연스럽게 표현한다.

---

### 2. 연결 관계 자체에 메모가 필요하다

Fabric 자체 메모와 Project에서의 사용 메모는 다르다.

예를 들어 Fabric 자체 메모는 다음과 같다.

```text
얇고 부드러운 면 원단. 색감은 예쁘지만 구김이 잘 생김.
```

반면 특정 Project에서의 사용 메모는 다음과 같을 수 있다.

```text
겉감으로 사용. 힘이 부족해서 다음에는 심지를 함께 붙이는 것이 좋을 듯.
```

이 두 메모는 성격이 다르다.

Fabric.memo는 원단 자체에 대한 평가이고, ProjectFabric.memo는 해당 Project에서 그 원단을 어떻게 사용했는지에 대한 기록이다.

따라서 연결 관계에도 별도 속성이 필요하다.

---

### 3. JPA 직접 ManyToMany보다 확장에 유리하다

JPA의 `@ManyToMany`는 단순 연결만 표현할 때는 편리하다.

하지만 연결 테이블에 속성이 추가되는 순간 별도 엔티티가 필요하다.

소로소로의 ProjectFabric은 MVP에서는 memo만 가지지만, 향후 다음 속성으로 확장될 가능성이 있다.

|속성|설명|
|---|---|
|usageRole|겉감, 안감, 배색 등 사용 용도|
|usedLength|사용한 원단 길이|
|usedArea|사용 부위|
|isMainFabric|주요 원단 여부|
|sortOrder|Project 내 원단 표시 순서|

직접 ManyToMany로 시작하면 나중에 연결 엔티티로 리팩터링해야 한다.

따라서 처음부터 ProjectFabric을 명시적 엔티티로 두는 것이 더 안전하다.

---

### 4. 중복 연결을 명확히 막을 수 있다

하나의 Project에 동일 Fabric이 여러 번 연결되면 데이터가 중복된다.

예를 들어 다음과 같은 데이터는 불필요한 중복이다.

```text
Project: 딸기 파우치
 ├── Fabric: 딸기 면 원단
 └── Fabric: 딸기 면 원단
```

따라서 다음 제약이 필요하다.

```text
unique(projectId, fabricId)
```

ProjectFabric을 명시적 엔티티로 두면 이 제약을 데이터 모델에서 분명히 표현할 수 있다.

---

### 5. 삭제 영향 범위를 명확히 관리할 수 있다

Project와 Fabric은 독립 도메인이다.

따라서 삭제 정책은 다음처럼 분리되어야 한다.

|삭제 대상|함께 삭제 또는 제거되는 것|유지되는 것|
|---|---|---|
|Project|ProjectFabric 연결|Fabric|
|Fabric|ProjectFabric 연결|Project|
|ProjectFabric|연결 정보|Project, Fabric|

ProjectFabric을 명시적으로 두면 삭제 영향 범위를 명확히 정의할 수 있다.

Project를 삭제하더라도 Fabric 구매 기록은 보존되고, Fabric을 삭제하더라도 Project 기록은 보존된다.

---

### 6. 권한 검증이 명확해진다

소로소로는 개인 기록 서비스이므로 모든 데이터는 User 소유권을 기준으로 접근 제어한다.

ProjectFabric 작업 시에는 다음 조건을 확인해야 한다.

- Project가 현재 User 소유인지
    
- Fabric이 현재 User 소유인지
    
- Project가 ARCHIVED 상태는 아닌지
    

ProjectFabric을 별도 엔티티로 두면 연결 생성, 수정, 삭제 시 이러한 권한 검증을 명확히 수행할 수 있다.

---

## Trade-off

### 장점

ProjectFabric 연결 엔티티를 사용하면 다음 장점이 있다.

- Project와 Fabric의 다대다 관계를 명확히 표현할 수 있다.
    
- Project 없이 Fabric을 관리할 수 있다.
    
- 하나의 Project에 여러 Fabric을 연결할 수 있다.
    
- 하나의 Fabric을 여러 Project에 연결할 수 있다.
    
- 연결 관계에 memo를 저장할 수 있다.
    
- 동일 Project-Fabric 중복 연결을 방지할 수 있다.
    
- 삭제 영향 범위를 명확히 분리할 수 있다.
    
- 권한 검증을 명확히 수행할 수 있다.
    
- 향후 사용량, 용도, 정렬 등으로 확장할 수 있다.
    
- JPA에서 직접 ManyToMany를 피하고 명시적 도메인 모델을 만들 수 있다.
    

---

### 단점

이 결정으로 인해 다음 비용도 발생한다.

- ProjectFabric 엔티티가 추가된다.
    
- 연결 생성, 수정, 삭제 기능이 별도로 필요하다.
    
- Project 상세 조회 시 연결된 Fabric 조회 로직이 필요하다.
    
- Fabric 상세 조회 시 사용된 Project 조회 로직이 필요하다.
    
- 단순한 Project-Fabric 직접 참조보다 구현량이 늘어난다.
    
- 사용자가 Fabric을 등록한 뒤 Project에 연결하는 흐름을 이해할 수 있도록 UI를 설계해야 한다.
    

---

### Trade-off 판단

Project와 Fabric 관계를 단순하게 처리하면 초기 구현은 쉬워진다.

하지만 소로소로에서 원단은 작품의 단일 부속 정보가 아니라, 여러 작품에 재사용될 수 있는 독립 구매 기록이다.

또한 Project와 Fabric의 연결 자체에도 “이 작품에서 이 원단을 어떻게 사용했는가”라는 의미가 있다.

따라서 단순 `@ManyToMany`나 단일 FK보다, ProjectFabric 연결 엔티티를 두는 편이 실제 도메인 의미를 더 정확히 표현한다.

초기 구현량이 늘어나더라도, 데이터 정합성, 확장성, 권한 검증, 포트폴리오 설명력을 고려하면 ProjectFabric 연결 엔티티를 사용하는 것이 더 적절하다.

---

## Consequences

ProjectFabric 연결 엔티티를 사용함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. ProjectFabric 전용 기능을 제공한다

MVP에서는 다음 기능을 제공한다.

- Project에 Fabric 연결
    
- ProjectFabric 메모 수정
    
- ProjectFabric 연결 삭제
    
- Project 상세에서 연결된 Fabric 목록 조회
    
- Fabric 상세에서 사용 Project 목록 조회
    

---

### 2. Project와 Fabric 소유권을 모두 검증한다

ProjectFabric 생성, 수정, 삭제 시 다음을 검증한다.

```text
currentUser.id == project.userId
currentUser.id == fabric.userId
```

둘 중 하나라도 현재 User 소유가 아니면 요청은 실패해야 한다.

---

### 3. Project 상태 제약을 따른다

Project가 `ARCHIVED` 상태이면 ProjectFabric 수정성 작업을 제한한다.

제한되는 작업은 다음과 같다.

- Project에 Fabric 연결
    
- ProjectFabric 메모 수정
    
- ProjectFabric 연결 삭제
    

단, 조회는 가능하다.

---

### 4. 동일 Project-Fabric 중복 연결을 방지한다

동일 Project에 동일 Fabric을 두 번 연결할 수 없다.

애플리케이션 계층에서 중복 여부를 검증하고, DB에서도 unique 제약을 둘 수 있다.

```text
unique(projectId, fabricId)
```

---

### 5. 삭제 정책을 명확히 적용한다

삭제 정책은 다음과 같다.

- Project 삭제 시 연결된 ProjectFabric은 제거한다.
    
- Project 삭제 시 Fabric은 삭제하지 않는다.
    
- Fabric 삭제 시 연결된 ProjectFabric은 제거한다.
    
- Fabric 삭제 시 Project는 삭제하지 않는다.
    
- ProjectFabric 삭제 시 Project와 Fabric은 모두 유지한다.
    

---

### 6. 향후 확장 필드를 추가할 수 있다

MVP에서는 ProjectFabric에 memo만 둔다.

향후 다음 필드를 추가할 수 있다.

|필드|설명|
|---|---|
|usageRole|겉감, 안감, 배색 등 사용 용도|
|usedLength|사용한 원단 길이|
|usedArea|사용 부위|
|isMainFabric|주요 원단 여부|
|sortOrder|Project 내 원단 표시 순서|

다만 MVP에서는 usageRole enum을 두지 않고, 사용 맥락은 memo로 자유롭게 기록한다.

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-002 Database로 PostgreSQL을 선택한 이유|ProjectFabric 관계와 unique 제약 관리|
|ADR-003 Fabric을 Project와 독립 도메인으로 분리한 이유|Project와 Fabric을 연결하는 방식 결정|
|ADR-006 작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유|연결 엔티티를 통해 의미 있는 하위 데이터를 분리하는 설계 방향과 유사|
|ADR-011 MVP에서 AI 원단 자동 기록을 제외한 이유|향후 Fabric 확장 시 Project 연결과 독립적으로 동작 가능|

---

# ADR-004 요약

Project와 Fabric은 다대다 관계로 설계하되, 직접 `@ManyToMany`를 사용하지 않고 ProjectFabric 연결 엔티티를 둔다.

선택 이유는 다음과 같다.

- 하나의 Project는 여러 Fabric을 사용할 수 있다.
    
- 하나의 Fabric은 여러 Project에 사용될 수 있다.
    
- Project와 Fabric의 연결 관계 자체에 memo가 필요하다.
    
- 직접 ManyToMany는 연결 속성 추가와 확장에 불리하다.
    
- 동일 Project-Fabric 중복 연결을 막아야 한다.
    
- Project 삭제와 Fabric 삭제의 영향 범위를 분리해야 한다.
    
- ProjectFabric 작업 시 Project와 Fabric 양쪽 소유권을 검증하기 쉽다.
    
- 향후 usageRole, usedLength, sortOrder 등으로 확장할 수 있다.
    

이 결정으로 인해 Project와 Fabric 사이에는 ProjectFabric 엔티티가 존재하며, 연결 생성·수정·삭제 기능을 별도로 제공한다.

---


# ADR-005. DailyLog에 DRAFT/PUBLISHED 상태를 둔 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)에서 DailyLog는 특정 Project에 대해 사용자가 날짜별로 작성하는 작업 기록이다.

DailyLog에는 다음 정보가 포함된다.

- 작업 날짜
    
- 작업 제목
    
- 작업 종류
    
- 작업 시간 구간
    
- 작업 메모
    
- 작업 사진
    

DailyLog는 Project의 제작 과정을 기록하는 핵심 데이터이며, Calendar와 Contribution의 집계 기준이 된다.

MVP에서 DailyLog는 다음 기능을 지원해야 한다.

- DailyLog 생성
    
- DailyLog 임시저장
    
- DailyLog 수정
    
- DailyLog 삭제
    
- DRAFT DailyLog 발행
    
- Project별 DailyLog 조회
    
- 날짜별 DailyLog 조회
    
- Calendar 집계
    
- Contribution 집계
    

초기에는 DailyLog를 단순히 생성하면 바로 정식 기록으로 저장하는 방식도 고려할 수 있었다.

하지만 실제 사용 흐름을 보면 사용자가 항상 완성된 일지를 한 번에 작성하는 것은 아니다.

예를 들어 사용자는 다음과 같은 상황에 놓일 수 있다.

- 작업 중간에 사진만 먼저 올리고 싶다.
    
- 작업 시간은 나중에 정리하고 싶다.
    
- 메모를 쓰다가 잠시 중단할 수 있다.
    
- 작업 날짜를 아직 확정하지 않은 상태로 내용을 적어둘 수 있다.
    
- TimeEntry를 아직 입력하지 않았지만 기록을 잃고 싶지 않다.
    
- 모바일에서 작성 중 앱을 이탈할 수 있다.
    
- 작업 과정은 저장하고 싶지만 아직 통계에는 반영하고 싶지 않다.
    

이러한 사용 흐름을 지원하려면 DailyLog에 임시저장 상태가 필요하다.

동시에 Calendar와 Contribution은 정확한 작업 기록만 반영해야 하므로, 작성 중인 임시 데이터가 통계에 섞이면 안 된다.

따라서 DailyLog에 `DRAFT`와 `PUBLISHED` 상태를 두어 작성 중 기록과 정식 기록을 구분한다.

---

## Decision

DailyLog에 다음 두 가지 상태를 둔다.

|상태|설명|
|---|---|
|DRAFT|임시저장 상태|
|PUBLISHED|정식 기록 상태|

DailyLog는 `DRAFT`, `PUBLISHED` 상태와 관계없이 수정할 수 있다.

다만 상태에 따라 필수값과 통계 반영 여부가 달라진다.

---

### DRAFT

DRAFT는 사용자가 작성 중인 임시저장 상태이다.

DRAFT 상태에서는 다음 값이 없어도 저장하거나 수정할 수 있다.

- workedDate
    
- workTypes
    
- timeEntries
    
- memo
    
- photos
    

단, DRAFT 상태에서도 다음 값은 필요하다.

- User
    
- Project
    

DRAFT DailyLog는 통계에 포함하지 않는다.

---

### PUBLISHED

PUBLISHED는 정식 기록 상태이다.

PUBLISHED 상태에서는 다음 값이 필요하다.

- workedDate
    
- timeEntries 1개 이상
    

PUBLISHED DailyLog는 다음 집계에 반영된다.

- Project 총 작업 시간
    
- Calendar 날짜별 작업 시간
    
- Contribution 재봉 잔디
    
- 향후 작업 통계
    

PUBLISHED 상태의 DailyLog도 수정할 수 있다.  
수정 시 workedDate와 timeEntries 필수 조건은 유지되어야 하며, 수정된 값을 기준으로 통계가 다시 계산되어야 한다.

---

### 상태 전환

MVP에서 허용하는 상태 전환은 다음과 같다.

|전환|허용 여부|설명|
|---|--:|---|
|DRAFT → PUBLISHED|허용|필수값 검증 후 정식 기록으로 발행|
|PUBLISHED → DRAFT|불허|정식 기록을 다시 임시저장 상태로 되돌리지는 않음|

---

## Alternatives

### 1. DailyLog에 상태를 두지 않는 방식

첫 번째 대안은 DailyLog에 상태를 두지 않고, 생성된 DailyLog를 모두 정식 기록으로 취급하는 방식이다.

#### 장점

- 데이터 모델이 단순하다.
    
- DailyLog 상태 전환 로직이 필요 없다.
    
- Calendar와 Contribution 집계 기준이 단순하다.
    
- 구현 속도가 빠르다.
    

#### 단점

- 작성 중인 DailyLog를 임시로 저장할 수 없다.
    
- workedDate나 TimeEntry가 없는 기록을 저장하기 어렵다.
    
- 사용자가 작성 중 이탈하면 기록이 사라질 수 있다.
    
- 불완전한 DailyLog가 통계에 반영될 위험이 있다.
    
- 모바일 작성 흐름과 맞지 않는다.
    

소로소로는 모바일 웹 중심의 기록 서비스이므로, 임시저장 없이 정식 기록만 두는 방식은 사용자 경험이 좋지 않다.

---

### 2. 모든 DailyLog를 저장하되, 필수값이 없어도 통계에 포함하지 않는 방식

두 번째 대안은 DailyLog에 명시적인 상태를 두지 않고, workedDate나 TimeEntry 존재 여부로 통계 반영 여부를 판단하는 방식이다.

예시는 다음과 같다.

```text
workedDate 없음 → 통계 제외
timeEntries 없음 → 통계 제외
workedDate 있음 + timeEntries 있음 → 통계 포함
```

#### 장점

- 별도 status 필드가 필요 없다.
    
- 상태 전환 API가 필요 없다.
    
- 데이터 저장은 유연하게 할 수 있다.
    

#### 단점

- DailyLog가 작성 중인지 정식 기록인지 명확하지 않다.
    
- 통계 포함 조건이 암묵적이다.
    
- 클라이언트가 DailyLog 상태를 직관적으로 표시하기 어렵다.
    
- “발행”이라는 사용자 행동을 표현하기 어렵다.
    
- 향후 검증 로직이 복잡해질 수 있다.
    

이 방식은 데이터 상태를 필드 조합으로 추론해야 하므로, 서비스 정책이 불명확해진다.

---

### 3. DRAFT/PUBLISHED/ARCHIVED 등 여러 상태를 두는 방식

세 번째 대안은 DailyLog에 더 많은 상태를 두는 방식이다.

예시는 다음과 같다.

|상태|설명|
|---|---|
|DRAFT|임시저장|
|PUBLISHED|정식 기록|
|ARCHIVED|숨김 또는 보관|
|DELETED|삭제|

#### 장점

- DailyLog 상태를 더 세분화할 수 있다.
    
- 향후 숨김, 보관, 복구 기능을 확장하기 쉽다.
    
- 삭제 정책을 상태로 관리할 수 있다.
    

#### 단점

- MVP에 비해 상태가 과도하게 많다.
    
- 상태 전환 규칙이 복잡해진다.
    
- 사용자에게 상태를 설명하기 어렵다.
    
- DailyLog보다 Project의 ARCHIVED 상태가 이미 존재하므로 중복 개념이 생길 수 있다.
    

MVP에서는 DailyLog에 보관 상태까지 둘 필요가 없다.  
DailyLog는 Project의 제작 과정 기록이므로, 보관 여부는 Project 상태로 충분히 관리할 수 있다.

---

### 4. DRAFT/PUBLISHED 두 상태만 두는 방식

네 번째 대안은 DailyLog에 DRAFT와 PUBLISHED 두 상태만 두는 방식이다.

#### 장점

- 작성 중 기록과 정식 기록을 명확히 구분할 수 있다.
    
- 임시저장을 지원할 수 있다.
    
- 통계 집계 기준을 명확히 할 수 있다.
    
- 클라이언트가 상태에 따라 UI를 다르게 표시할 수 있다.
    
- 상태 전환 규칙이 단순하다.
    
- MVP 구현 범위에 적합하다.
    

#### 단점

- 상태 필드와 발행 로직이 추가된다.
    
- DRAFT와 PUBLISHED의 검증 조건을 분리해야 한다.
    
- PUBLISHED 수정 시 통계 재계산 기준을 신경 써야 한다.
    
- PUBLISHED를 DRAFT로 되돌릴 수 없다는 정책을 명확히 해야 한다.
    

소로소로는 임시저장과 정확한 통계 기준이 모두 필요하므로, DRAFT/PUBLISHED 두 상태를 선택한다.

---

## Rationale

DailyLog에 DRAFT/PUBLISHED 상태를 둔 이유는 다음과 같다.

---

### 1. 모바일 작성 흐름에서 임시저장이 필요하다

소로소로는 모바일 웹 중심 서비스이다.

사용자는 작업 중간에 다음과 같은 방식으로 기록할 수 있다.

- 사진만 먼저 올리기
    
- 메모만 먼저 작성하기
    
- 작업 시간은 나중에 입력하기
    
- 작업 날짜를 나중에 정하기
    
- 작성 중 잠시 이탈하기
    

이때 모든 필수 정보를 한 번에 입력해야만 저장할 수 있다면 기록 경험이 불편해진다.

DRAFT 상태를 두면 사용자는 불완전한 DailyLog도 안전하게 저장할 수 있다.

---

### 2. 작성 중 데이터와 정식 기록을 구분해야 한다

임시저장된 DailyLog는 사용자가 아직 확정하지 않은 기록이다.

이 데이터가 Calendar나 Contribution에 반영되면 사용자의 작업 습관 통계가 부정확해진다.

예를 들어 다음과 같은 DRAFT가 있다고 가정한다.

```text
DailyLog status = DRAFT
workedDate = 2026-07-02
timeEntries = 없음
memo = "안감 재단하다가 중단"
```

이 기록은 사용자가 아직 작업 시간을 확정하지 않은 상태이다.

따라서 Calendar나 Contribution에 반영하면 안 된다.

DRAFT/PUBLISHED 상태를 두면 통계 반영 기준이 명확해진다.

---

### 3. 통계 기준을 명확히 할 수 있다

소로소로의 Calendar와 Contribution은 DailyLog를 기반으로 계산된다.

이때 집계 기준은 다음과 같다.

```text
status = PUBLISHED
workedDate 존재
durationMinutes 존재
```

DRAFT 상태의 DailyLog는 다음 통계에서 제외한다.

- Project 총 작업 시간
    
- Calendar 날짜별 총 작업 시간
    
- Contribution 날짜별 작업 시간
    
- activeDays
    
- 향후 통계 대시보드
    

이 기준을 명시하면 구현, 테스트, API 응답이 모두 명확해진다.

---

### 4. PUBLISHED도 수정 가능해야 한다

정식 기록이라고 해서 이후 수정이 불가능하면 사용성이 떨어진다.

사용자는 발행 후에도 다음 정보를 고칠 수 있어야 한다.

- 작업 날짜를 잘못 입력한 경우
    
- 작업 시간을 잘못 입력한 경우
    
- 작업 종류를 추가하고 싶은 경우
    
- 메모를 보완하고 싶은 경우
    
- 사진을 추가하거나 대표 사진을 바꾸고 싶은 경우
    

따라서 PUBLISHED 상태의 DailyLog도 수정 가능해야 한다.

다만 PUBLISHED 상태를 유지하려면 다음 필수 조건은 유지해야 한다.

- workedDate 존재
    
- timeEntries 1개 이상
    

PUBLISHED DailyLog를 수정하면 수정된 workedDate와 durationMinutes를 기준으로 통계가 다시 계산되어야 한다.

---

### 5. PUBLISHED를 DRAFT로 되돌리는 기능은 MVP에서 제외한다

PUBLISHED DailyLog를 DRAFT로 되돌리는 기능도 이론적으로는 가능하다.

하지만 MVP에서는 제외한다.

이유는 다음과 같다.

- 정식 기록의 통계 반영 여부가 갑자기 바뀌어 사용자 혼란이 생길 수 있다.
    
- Calendar와 Contribution에서 해당 기록을 제거해야 한다.
    
- 발행 취소 UI와 정책이 별도로 필요하다.
    
- 단순 수정 기능만으로 대부분의 사용 시나리오를 해결할 수 있다.
    

따라서 MVP에서는 DRAFT → PUBLISHED 전환만 허용하고, PUBLISHED → DRAFT 전환은 제공하지 않는다.

---

### 6. Project 상태와 DailyLog 상태의 책임을 분리할 수 있다

Project에는 다음 상태가 있다.

- IN_PROGRESS
    
- ON_HOLD
    
- COMPLETED
    
- ARCHIVED
    

DailyLog에는 다음 상태가 있다.

- DRAFT
    
- PUBLISHED
    

Project 상태는 작품 전체의 진행 상태를 의미한다.  
DailyLog 상태는 개별 일지의 작성 완료 여부를 의미한다.

두 상태의 책임은 다르다.

예를 들어 Project가 ON_HOLD여도 DailyLog는 작성할 수 있다.  
반면 Project가 ARCHIVED이면 DailyLog 생성, 수정, 삭제, 발행이 제한된다.

이처럼 Project 상태와 DailyLog 상태를 분리하면 도메인 규칙을 더 명확히 표현할 수 있다.

---

## Trade-off

### 장점

DailyLog에 DRAFT/PUBLISHED 상태를 두면 다음 장점이 있다.

- 임시저장을 지원할 수 있다.
    
- 작성 중 기록과 정식 기록을 명확히 구분할 수 있다.
    
- Calendar와 Contribution 집계 기준이 명확해진다.
    
- DRAFT DailyLog를 통계에서 제외할 수 있다.
    
- PUBLISHED DailyLog만 Project 총 작업 시간에 반영할 수 있다.
    
- 사용자가 불완전한 기록을 안전하게 저장할 수 있다.
    
- 상태별 검증 규칙을 명확히 정의할 수 있다.
    
- 테스트 케이스를 작성하기 쉽다.
    

---

### 단점

이 결정으로 인해 다음 비용도 발생한다.

- DailyLog에 status 필드가 필요하다.
    
- DRAFT와 PUBLISHED의 검증 조건을 분리해야 한다.
    
- 발행 API 또는 발행 로직이 필요하다.
    
- PUBLISHED 수정 시 통계 재계산 기준을 고려해야 한다.
    
- PUBLISHED → DRAFT 전환을 제공하지 않는 이유를 명확히 해야 한다.
    
- 클라이언트에서 DRAFT와 PUBLISHED 상태를 구분해 표시해야 한다.
    

---

### Trade-off 판단

상태를 두지 않으면 구현은 단순해진다.

하지만 소로소로는 사용자가 작업 중간에 기록을 작성하고, 나중에 보완하는 흐름이 자연스러운 서비스이다.

또한 Calendar와 Contribution은 정확한 정식 기록만 반영해야 한다.

따라서 DRAFT/PUBLISHED 상태를 두는 것이 사용자 경험과 데이터 정합성 모두에 유리하다.

MVP에서는 상태를 두 개로만 제한하여 복잡도를 관리한다.

---

## Consequences

DailyLog에 DRAFT/PUBLISHED 상태를 둠에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. DailyLog는 status 필드를 가진다

DailyLog는 다음 상태를 가진다.

```text
DRAFT
PUBLISHED
```

기본 생성 방식은 API 설계에서 구분할 수 있다.

예를 들어 다음 두 흐름을 제공할 수 있다.

- 임시저장 생성
    
- 정식 기록 생성
    
- DRAFT 발행
    

구체적인 API는 `07_API.md`에서 정의한다.

---

### 2. DRAFT와 PUBLISHED의 필수값 검증을 분리한다

DRAFT 상태에서는 다음 값이 없어도 된다.

- workedDate
    
- workTypes
    
- timeEntries
    
- memo
    
- photos
    

PUBLISHED 상태에서는 다음 값이 필수이다.

- workedDate
    
- timeEntries 1개 이상
    

이 검증은 Service 계층에서 명확히 수행한다.

---

### 3. Calendar와 Contribution은 PUBLISHED만 집계한다

Calendar와 Contribution 조회 시 다음 조건을 적용한다.

```text
DailyLog.status = PUBLISHED
```

DRAFT DailyLog는 조회 결과와 통계에 포함하지 않는다.

---

### 4. PUBLISHED DailyLog 수정 시 통계 기준이 변경된다

PUBLISHED DailyLog는 수정 가능하다.

수정 가능한 항목은 다음과 같다.

- workedDate
    
- title
    
- workTypes
    
- memo
    
- timeEntries
    
- photos
    

PUBLISHED DailyLog 수정 시 다음 처리가 필요하다.

- timeEntries가 변경되면 durationMinutes 재계산
    
- workedDate가 변경되면 Calendar와 Contribution 기준 날짜 변경
    
- 수정 후에도 workedDate와 timeEntries 1개 이상 조건 유지
    

---

### 5. PUBLISHED → DRAFT 전환은 제공하지 않는다

MVP에서는 PUBLISHED DailyLog를 DRAFT로 되돌리는 기능을 제공하지 않는다.

사용자가 정식 기록을 수정하고 싶다면 PUBLISHED 상태에서 내용을 수정한다.

발행 자체를 취소하는 개념은 MVP 범위에서 제외한다.

---

### 6. 테스트 케이스를 상태별로 작성한다

DailyLog 상태에 따라 다음 테스트가 필요하다.

- DRAFT는 workedDate 없이 저장 가능하다.
    
- DRAFT는 TimeEntry 없이 저장 가능하다.
    
- DRAFT는 Calendar에 반영되지 않는다.
    
- DRAFT는 Contribution에 반영되지 않는다.
    
- PUBLISHED는 workedDate 없이 저장할 수 없다.
    
- PUBLISHED는 TimeEntry 없이 저장할 수 없다.
    
- PUBLISHED는 Calendar에 반영된다.
    
- PUBLISHED는 Contribution에 반영된다.
    
- PUBLISHED 수정 시 durationMinutes가 재계산된다.
    
- PUBLISHED 수정 시 workedDate 변경이 집계 기준에 반영된다.
    
- PUBLISHED를 DRAFT로 되돌릴 수 없다.
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-001 Backend Framework로 Spring Boot를 선택한 이유|상태별 검증과 트랜잭션을 Service 계층에서 처리|
|ADR-002 Database로 PostgreSQL을 선택한 이유|DailyLog.status, workedDate 기반 조회와 집계|
|ADR-006 작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유|PUBLISHED 필수 조건과 durationMinutes 계산|
|ADR-010 Calendar/Contribution을 저장하지 않고 조회 시 계산하는 이유|PUBLISHED DailyLog만 집계 기준으로 사용|

---

# ADR-005 요약

DailyLog에 DRAFT와 PUBLISHED 상태를 둔다.

선택 이유는 다음과 같다.

- 모바일 작성 흐름에서 임시저장이 필요하다.
    
- 작성 중 데이터와 정식 기록을 구분해야 한다.
    
- DRAFT DailyLog는 통계에 포함하지 않아야 한다.
    
- PUBLISHED DailyLog만 Calendar, Contribution, Project 총 작업 시간에 반영해야 한다.
    
- PUBLISHED DailyLog도 수정 가능해야 한다.
    
- PUBLISHED 수정 시 수정된 workedDate와 durationMinutes를 기준으로 통계를 다시 계산해야 한다.
    
- Project 상태와 DailyLog 상태의 책임을 분리할 수 있다.
    

MVP에서는 DRAFT → PUBLISHED 전환만 허용하고, PUBLISHED → DRAFT 전환은 제공하지 않는다.

----
# ADR-006. 작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)에서 DailyLog는 특정 날짜에 특정 Project에서 수행한 작업 기록이다.

DailyLog에는 작업 시간 정보가 필요하다.  
이 작업 시간은 다음 기능에서 사용된다.

- DailyLog 상세 조회
    
- Project 총 작업 시간 계산
    
- Calendar 날짜별 작업 시간 표시
    
- Contribution 재봉 잔디 표시
    
- 향후 통계 대시보드
    

초기에는 DailyLog에 `durationMinutes`를 사용자가 직접 입력하는 방식을 고려할 수 있었다.

예를 들어 다음과 같은 구조이다.

```text
DailyLog
 ├── workedDate
 ├── title
 ├── workTypes
 ├── durationMinutes
 └── memo
```

이 방식은 단순하지만, 실제 재봉 작업 흐름과는 맞지 않는 부분이 있다.

재봉 작업은 한 번에 연속적으로 끝나는 경우보다 여러 구간으로 나뉘는 경우가 많다.

예를 들어 하루 작업이 다음처럼 나뉠 수 있다.

```text
15:00 ~ 15:40 재단
20:00 ~ 21:10 재봉
22:30 ~ 23:00 마감 정리
```

또는 작업 중간에 휴식, 식사, 외출, 재료 확인 등이 들어갈 수 있다.

이 경우 사용자가 총 작업 시간만 직접 입력하면 다음 문제가 생긴다.

- 실제 작업 흐름을 세부적으로 기록하기 어렵다.
    
- 중간에 나뉜 작업 구간을 표현할 수 없다.
    
- 사용자가 총 시간을 직접 계산해야 한다.
    
- 작업 시간을 잘못 입력해도 검증하기 어렵다.
    
- 향후 시간대별 작업 습관 분석이 어렵다.
    
- 작업 구간별 메모를 남기기 어렵다.
    

따라서 DailyLog의 작업 시간은 사용자가 직접 총합을 입력하는 방식이 아니라, 여러 DailyLogTimeEntry의 합산값으로 계산하는 구조가 필요하다.

---

## Decision

DailyLog의 작업 시간은 `DailyLogTimeEntry`의 `durationMinutes` 합산값으로 계산한다.

결정된 구조는 다음과 같다.

```text
DailyLog
 └── DailyLogTimeEntry
```

DailyLogTimeEntry는 하나의 작업 시간 구간을 의미한다.

```text
DailyLogTimeEntry
 ├── startedAt
 ├── endedAt
 ├── durationMinutes
 └── memo
```

DailyLog의 `durationMinutes`는 사용자가 직접 입력하지 않는다.

대신 다음 방식으로 계산한다.

```text
DailyLog.durationMinutes
= SUM(DailyLogTimeEntry.durationMinutes)
```

각 TimeEntry의 `durationMinutes`는 다음 방식으로 계산한다.

```text
DailyLogTimeEntry.durationMinutes
= endedAt - startedAt
```

---

## 상태별 적용 기준

DailyLog 상태에 따라 TimeEntry 요구사항은 다르다.

|DailyLog 상태|TimeEntry 필요 여부|durationMinutes 계산|통계 반영|
|---|--:|--:|--:|
|DRAFT|선택|TimeEntry가 있으면 계산 가능|미반영|
|PUBLISHED|필수, 1개 이상|필수 계산|반영|

DRAFT 상태에서는 TimeEntry가 없어도 저장할 수 있다.  
PUBLISHED 상태에서는 TimeEntry가 1개 이상 있어야 한다.

---

## Alternatives

### 1. DailyLog에 durationMinutes를 직접 입력

첫 번째 대안은 사용자가 DailyLog에 총 작업 시간을 직접 입력하는 방식이다.

```text
DailyLog.durationMinutes = 사용자가 입력한 총 작업 시간
```

#### 장점

- 데이터 모델이 단순하다.
    
- TimeEntry 엔티티가 필요 없다.
    
- 구현 속도가 빠르다.
    
- Calendar와 Contribution 집계가 쉽다.
    

#### 단점

- 사용자가 총 작업 시간을 직접 계산해야 한다.
    
- 하루 작업이 여러 구간으로 나뉘는 경우 표현하기 어렵다.
    
- 작업 구간별 메모를 남길 수 없다.
    
- 시간 입력 오류를 검증하기 어렵다.
    
- 실제 작업 흐름을 자세히 되돌아보기 어렵다.
    
- 향후 시간대별 작업 분석으로 확장하기 어렵다.
    

소로소로는 “작업 결과”뿐 아니라 “작업 과정”을 기록하는 서비스이므로, 단순 총 시간 입력만으로는 부족하다.

---

### 2. DailyLog에 startedAt, endedAt만 저장

두 번째 대안은 DailyLog에 작업 시작 시각과 종료 시각만 저장하는 방식이다.

```text
DailyLog
 ├── startedAt
 └── endedAt
```

#### 장점

- 총 작업 시간을 자동 계산할 수 있다.
    
- 사용자가 durationMinutes를 직접 계산하지 않아도 된다.
    
- 데이터 구조가 비교적 단순하다.
    

#### 단점

- 하루에 여러 번 나누어 작업한 경우 표현하기 어렵다.
    
- 중간 휴식 시간이 작업 시간에 포함될 수 있다.
    
- 여러 작업 구간별 메모를 남길 수 없다.
    
- 실제 작업 패턴을 정확히 기록하기 어렵다.
    

재봉 작업은 한 번에 연속으로 진행되지 않을 수 있으므로, 단일 startedAt/endedAt 구조는 적합하지 않다.

---

### 3. TimeEntry 없이 workSessions를 JSON으로 저장

세 번째 대안은 DailyLog 안에 작업 구간을 JSON 형태로 저장하는 방식이다.

```json
[
  {
    "startedAt": "15:00",
    "endedAt": "15:40",
    "memo": "재단"
  },
  {
    "startedAt": "20:00",
    "endedAt": "21:10",
    "memo": "재봉"
  }
]
```

#### 장점

- 여러 작업 구간을 표현할 수 있다.
    
- 별도 테이블이 필요 없다.
    
- 초기 구현이 단순해 보일 수 있다.
    

#### 단점

- 각 작업 구간에 대한 검증이 어려워진다.
    
- QueryDSL이나 SQL 집계에서 다루기 어렵다.
    
- TimeEntry 단위의 검색, 수정, 통계 확장이 어렵다.
    
- 관계형 데이터 모델의 장점을 활용하기 어렵다.
    
- JPA 매핑과 변경 감지가 복잡해질 수 있다.
    

작업 시간은 Calendar와 Contribution의 핵심 집계 기준이므로, JSON보다 명시적인 엔티티로 관리하는 것이 적절하다.

---

### 4. DailyLogTimeEntry 엔티티로 작업 구간 관리

네 번째 대안은 작업 구간을 별도 엔티티로 분리하는 방식이다.

```text
DailyLog
 ├── DailyLogTimeEntry
 ├── DailyLogTimeEntry
 └── DailyLogTimeEntry
```

#### 장점

- 하루 여러 작업 구간을 표현할 수 있다.
    
- 각 작업 구간별 메모를 남길 수 있다.
    
- 총 작업 시간을 자동 계산할 수 있다.
    
- 사용자가 직접 총 시간을 계산하지 않아도 된다.
    
- Calendar와 Contribution 집계 기준이 명확해진다.
    
- 향후 시간대별 통계나 작업 패턴 분석으로 확장할 수 있다.
    
- 관계형 데이터 모델로 검증과 제약을 관리하기 좋다.
    

#### 단점

- 엔티티가 하나 더 필요하다.
    
- DailyLog 생성/수정 시 TimeEntry 처리 로직이 필요하다.
    
- TimeEntry 수정 시 durationMinutes 재계산이 필요하다.
    
- 단순 총 시간 입력 방식보다 구현이 복잡하다.
    

소로소로는 작업 과정을 날짜별로 기록하는 서비스이므로, DailyLogTimeEntry를 두는 방식을 선택한다.

---

## Rationale

DailyLogTimeEntry 합산 방식을 선택한 이유는 다음과 같다.

---

### 1. 실제 재봉 작업은 여러 시간 구간으로 나뉠 수 있다

재봉 작업은 한 번에 연속적으로 끝나지 않는 경우가 많다.

예를 들어 하루 작업은 다음처럼 나뉠 수 있다.

```text
오후 3시 ~ 4시: 패턴 수정
저녁 8시 ~ 9시: 재단
밤 10시 ~ 10시 40분: 재봉
```

이러한 작업을 하나의 총 시간으로만 저장하면 작업 흐름을 자세히 되돌아보기 어렵다.

DailyLogTimeEntry를 사용하면 여러 작업 구간을 자연스럽게 기록할 수 있다.

---

### 2. 사용자가 총 작업 시간을 직접 계산하지 않아도 된다

사용자가 하루 작업 시간을 직접 계산하는 것은 번거롭고 오류가 발생하기 쉽다.

예를 들어 다음 작업 구간이 있을 때:

```text
15:10 ~ 15:55
20:30 ~ 21:20
22:00 ~ 22:35
```

사용자가 직접 총합을 계산하면 실수할 수 있다.

시스템이 각 TimeEntry의 시간 차이를 계산하고 DailyLog.durationMinutes에 합산하면 입력 오류를 줄일 수 있다.

---

### 3. Calendar와 Contribution 집계 기준이 명확해진다

소로소로의 Calendar와 Contribution은 작업 시간을 기반으로 한다.

집계 기준은 다음과 같다.

```text
PUBLISHED DailyLog의 durationMinutes 합산
```

DailyLog.durationMinutes가 TimeEntry 합산값으로 계산되면, Calendar와 Contribution은 DailyLog의 총 작업 시간을 기준으로 단순하고 일관되게 집계할 수 있다.

즉, 세부 작업 구간은 TimeEntry가 관리하고, 통계는 DailyLog.durationMinutes를 기준으로 계산한다.

---

### 4. 작업 구간별 메모를 남길 수 있다

하루의 작업은 여러 성격을 가질 수 있다.

예를 들어 다음처럼 기록할 수 있다.

```text
15:00 ~ 15:40: 안감 재단
20:00 ~ 21:10: 지퍼 달기
22:30 ~ 23:00: 실밥 정리
```

각 시간 구간에 memo를 둘 수 있으면, 사용자는 단순히 총 작업 시간뿐 아니라 구체적인 작업 흐름도 되돌아볼 수 있다.

이는 소로소로가 “완성 결과”뿐 아니라 “제작 과정”을 기록하는 서비스라는 방향과 맞다.

---

### 5. 향후 통계 확장에 유리하다

MVP에서는 Calendar와 Contribution만 제공하지만, 향후 통계 대시보드로 확장할 수 있다.

예를 들어 다음 통계가 가능하다.

- 하루 평균 작업 시간
    
- 시간대별 작업 패턴
    
- 야간 작업 비율
    
- Project별 누적 작업 시간
    
- WorkType별 작업 시간
    
- 가장 오래 작업한 날짜
    
- 연속 작업일
    

TimeEntry가 있으면 작업 시간 데이터를 더 세밀하게 활용할 수 있다.

반면 DailyLog에 총 시간만 직접 저장하면 시간대 분석이나 작업 구간 분석이 어렵다.

---

### 6. 데이터 정합성을 테스트하기 쉽다

TimeEntry 합산 방식을 사용하면 다음 규칙을 테스트할 수 있다.

- endedAt은 startedAt보다 늦어야 한다.
    
- TimeEntry durationMinutes는 startedAt과 endedAt의 차이와 일치해야 한다.
    
- DailyLog.durationMinutes는 TimeEntry durationMinutes 합산값과 일치해야 한다.
    
- TimeEntry가 수정되면 DailyLog.durationMinutes가 다시 계산되어야 한다.
    
- PUBLISHED DailyLog는 TimeEntry가 1개 이상 있어야 한다.
    
- DRAFT DailyLog는 TimeEntry 없이 저장할 수 있다.
    

이러한 규칙은 Service 계층 테스트로 명확히 검증할 수 있다.

---

## Trade-off

### 장점

DailyLogTimeEntry 합산 방식을 선택하면 다음 장점이 있다.

- 하루 여러 작업 구간을 표현할 수 있다.
    
- 사용자가 총 작업 시간을 직접 계산하지 않아도 된다.
    
- 작업 구간별 메모를 남길 수 있다.
    
- 작업 시간 계산 기준이 명확하다.
    
- Calendar와 Contribution 집계 기준이 일관된다.
    
- 향후 시간대별 통계와 작업 패턴 분석으로 확장할 수 있다.
    
- 데이터 정합성을 테스트하기 쉽다.
    
- PUBLISHED DailyLog의 필수 조건을 명확히 정의할 수 있다.
    

---

### 단점

이 결정으로 인해 다음 비용도 발생한다.

- DailyLogTimeEntry 엔티티가 추가된다.
    
- DailyLog 생성/수정 로직이 복잡해진다.
    
- TimeEntry 전체 교체 방식 또는 개별 수정 방식 중 하나를 결정해야 한다.
    
- TimeEntry 변경 시 DailyLog.durationMinutes를 재계산해야 한다.
    
- 단순 작업 시간 입력보다 UI가 복잡해질 수 있다.
    
- 사용자가 “총 시간만 빠르게 입력하고 싶다”고 느낄 수 있다.
    

---

### Trade-off 판단

DailyLog에 총 작업 시간만 직접 입력하면 구현은 쉬워진다.

하지만 소로소로는 단순한 시간 기록 앱이 아니라, 재봉 과정과 작업 습관을 함께 기록하는 서비스이다.

재봉 작업은 여러 시간 구간으로 나뉘기 쉽고, 작업 구간별로 다른 의미를 가질 수 있다.

따라서 초기 구현 복잡도가 늘어나더라도 DailyLogTimeEntry를 사용해 작업 구간을 명시적으로 관리하는 것이 더 적절하다.

MVP에서는 복잡도를 줄이기 위해 TimeEntry 개별 수정 API는 제공하지 않고, DailyLog 수정 요청 안에서 TimeEntry 목록을 전체 교체하는 방식으로 처리한다.

---

## Consequences

DailyLogTimeEntry 합산 방식을 선택함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. DailyLogTimeEntry 엔티티를 둔다

DailyLogTimeEntry는 다음 정보를 가진다.

|속성|설명|
|---|---|
|id|TimeEntry 식별자|
|dailyLogId|연결된 DailyLog 식별자|
|startedAt|작업 시작 시각|
|endedAt|작업 종료 시각|
|durationMinutes|작업 시간|
|memo|시간 구간 메모|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

### 2. DailyLog.durationMinutes는 직접 입력하지 않는다

DailyLog의 durationMinutes는 사용자가 직접 입력하는 값이 아니다.

다음 규칙으로 계산한다.

```text
DailyLog.durationMinutes = SUM(TimeEntry.durationMinutes)
```

TimeEntry가 없는 DRAFT DailyLog의 durationMinutes는 0으로 처리할 수 있다.

---

### 3. PUBLISHED DailyLog는 TimeEntry가 필수이다

PUBLISHED 상태로 저장 또는 발행하려면 TimeEntry가 1개 이상 필요하다.

```text
PUBLISHED DailyLog
→ workedDate 필수
→ timeEntries 1개 이상 필수
```

DRAFT 상태에서는 TimeEntry 없이 저장할 수 있다.

---

### 4. DailyLog 수정 시 TimeEntry는 전체 교체 방식으로 처리한다

MVP에서는 TimeEntry 개별 수정 API를 제공하지 않는다.

DailyLog 수정 요청에 포함된 TimeEntry 목록을 기준으로 기존 TimeEntry를 전체 교체한다.

이 방식은 다음 장점이 있다.

- API가 단순해진다.
    
- 클라이언트가 현재 화면 상태 전체를 서버에 전달하기 쉽다.
    
- TimeEntry 삭제, 추가, 수정의 구분을 복잡하게 처리하지 않아도 된다.
    
- 수정 후 durationMinutes를 한 번에 재계산할 수 있다.
    

개별 TimeEntry 수정 API는 향후 필요해지면 확장한다.

---

### 5. Calendar와 Contribution은 DailyLog.durationMinutes를 기준으로 집계한다

Calendar와 Contribution은 TimeEntry를 직접 집계하지 않고, PUBLISHED DailyLog의 durationMinutes를 기준으로 계산한다.

```text
Calendar.totalDurationMinutes
= SUM(PUBLISHED DailyLog.durationMinutes)

Contribution.totalDurationMinutes
= SUM(PUBLISHED DailyLog.durationMinutes)
```

이렇게 하면 조회 로직이 단순해지고, TimeEntry 상세 구조 변경의 영향을 줄일 수 있다.

---

### 6. TimeEntry 검증 규칙을 둔다

TimeEntry는 다음 검증을 통과해야 한다.

- startedAt은 필수이다.
    
- endedAt은 필수이다.
    
- endedAt은 startedAt보다 늦어야 한다.
    
- durationMinutes는 startedAt과 endedAt의 차이로 계산한다.
    
- durationMinutes는 0보다 커야 한다.
    

구체적인 시간대 처리 기준은 `05_Backend_Design.md`에서 정의한다.

---

### 7. 테스트 케이스를 작성한다

다음 테스트가 필요하다.

- TimeEntry durationMinutes가 정상 계산되는지
    
- 여러 TimeEntry의 합산값이 DailyLog.durationMinutes에 반영되는지
    
- PUBLISHED DailyLog가 TimeEntry 없이 저장되지 않는지
    
- DRAFT DailyLog는 TimeEntry 없이 저장 가능한지
    
- TimeEntry 수정 시 DailyLog.durationMinutes가 재계산되는지
    
- Calendar가 PUBLISHED DailyLog.durationMinutes를 기준으로 집계되는지
    
- Contribution이 PUBLISHED DailyLog.durationMinutes를 기준으로 집계되는지
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-001 Backend Framework로 Spring Boot를 선택한 이유|Service 계층에서 TimeEntry 계산과 트랜잭션 처리|
|ADR-002 Database로 PostgreSQL을 선택한 이유|DailyLogTimeEntry 관계형 모델과 집계 조회|
|ADR-005 DailyLog에 DRAFT/PUBLISHED 상태를 둔 이유|PUBLISHED DailyLog의 TimeEntry 필수 조건|
|ADR-010 Calendar/Contribution을 저장하지 않고 조회 시 계산하는 이유|DailyLog.durationMinutes 기반 집계|

---

# ADR-006 요약

DailyLog의 작업 시간은 사용자가 직접 입력하지 않고, DailyLogTimeEntry의 합산값으로 계산한다.

선택 이유는 다음과 같다.

- 실제 재봉 작업은 여러 시간 구간으로 나뉠 수 있다.
    
- 사용자가 총 작업 시간을 직접 계산하지 않아도 된다.
    
- 작업 구간별 메모를 남길 수 있다.
    
- Calendar와 Contribution의 집계 기준을 명확히 할 수 있다.
    
- 향후 시간대별 작업 패턴 분석으로 확장할 수 있다.
    
- 데이터 정합성을 테스트하기 쉽다.
    

MVP에서는 TimeEntry 개별 수정 API를 제공하지 않고, DailyLog 수정 요청 안에서 TimeEntry 목록을 전체 교체하는 방식으로 처리한다.

---
# ADR-007. Photo를 공통 이미지 엔티티로 설계한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 재봉 과정을 이미지 중심으로 기록하는 서비스이다.

사용자는 다음 대상에 사진을 첨부할 수 있어야 한다.

- Project
    
- DailyLog
    
- Fabric
    

각 도메인에서 사진이 사용되는 목적은 조금씩 다르다.

|대상|사진 사용 목적|
|---|---|
|Project|작품 대표 사진, 완성 사진, 디테일 사진|
|DailyLog|작업 과정 사진, 중간 상태, 실패 기록, 작업 결과|
|Fabric|원단 실물 사진, 쇼핑몰 상품 이미지, 질감/색상 기록|

초기 설계에서는 각 도메인마다 별도의 이미지 테이블을 두는 방식도 고려할 수 있었다.

예시는 다음과 같다.

```text
ProjectPhoto
DailyLogPhoto
FabricPhoto
```

하지만 이렇게 설계하면 다음 문제가 발생한다.

- 이미지 업로드 로직이 도메인마다 중복된다.
    
- Presigned URL 발급 로직이 중복된다.
    
- 이미지 처리 상태 관리가 중복된다.
    
- Original, Medium, Thumbnail 이미지 관리 방식이 중복된다.
    
- 대표 사진 정책이 중복된다.
    
- 정렬 순서 관리가 중복된다.
    
- 이미지 삭제 정책이 중복된다.
    
- 향후 이미지 리사이징, CDN URL 생성, 실패 처리 로직을 여러 곳에서 반복해야 한다.
    

반대로 Photo를 공통 엔티티로 설계하면 Project, DailyLog, Fabric의 이미지 관리 정책을 하나의 구조로 통합할 수 있다.

---

## Decision

Photo를 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 엔티티로 설계한다.

Photo는 `ownerType`과 `ownerId`를 통해 연결 대상을 구분한다.

```text
Photo
 ├── ownerType
 └── ownerId
```

ownerType은 다음 값을 가진다.

|ownerType|설명|
|---|---|
|PROJECT|Project에 연결된 사진|
|DAILY_LOG|DailyLog에 연결된 사진|
|FABRIC|Fabric에 연결된 사진|

결정된 구조는 다음과 같다.

```text
Project  ─┐
DailyLog ─┼── Photo
Fabric   ─┘
```

Photo는 다음 정보를 공통으로 가진다.

|속성|설명|
|---|---|
|userId|Photo 소유 User|
|ownerType|연결 대상 타입|
|ownerId|연결 대상 식별자|
|isThumbnail|대표 사진 여부|
|originalKey|원본 이미지 저장 Key|
|mediumKey|Medium 이미지 저장 Key|
|thumbnailKey|Thumbnail 이미지 저장 Key|
|width|이미지 너비|
|height|이미지 높이|
|sizeBytes|이미지 파일 크기|
|sortOrder|표시 순서|
|status|이미지 업로드 및 처리 상태|
|processedAt|이미지 처리 완료 일시|

---

## Alternatives

### 1. 도메인별 이미지 테이블 분리

첫 번째 대안은 ProjectPhoto, DailyLogPhoto, FabricPhoto를 각각 두는 방식이다.

```text
Project
 └── ProjectPhoto

DailyLog
 └── DailyLogPhoto

Fabric
 └── FabricPhoto
```

#### 장점

- 각 도메인의 이미지 정책을 독립적으로 관리할 수 있다.
    
- FK 관계를 명확하게 설정하기 쉽다.
    
- ownerType, ownerId 같은 다형적 연결 구조가 필요 없다.
    
- 도메인별 요구사항이 크게 달라질 경우 유연하다.
    

#### 단점

- 이미지 업로드 로직이 중복된다.
    
- Presigned URL 발급 로직이 중복된다.
    
- 이미지 처리 상태 관리가 중복된다.
    
- 이미지 리사이징 결과 필드가 중복된다.
    
- 대표 사진 설정 로직이 중복된다.
    
- 정렬 순서 관리 로직이 중복된다.
    
- 이미지 삭제 정책이 중복된다.
    
- API와 테스트 코드가 반복된다.
    
- 향후 이미지 정책 변경 시 여러 테이블과 서비스 로직을 수정해야 한다.
    

소로소로의 MVP에서는 Project, DailyLog, Fabric의 이미지 정책이 크게 다르지 않으므로, 도메인별 이미지 테이블을 분리하는 것은 중복이 크다.

---

### 2. 이미지 URL을 각 도메인 테이블에 직접 저장

두 번째 대안은 각 도메인에 이미지 URL 필드를 직접 두는 방식이다.

예시는 다음과 같다.

```text
Project
 ├── thumbnailUrl
 └── imageUrls

DailyLog
 ├── thumbnailUrl
 └── imageUrls

Fabric
 ├── imageUrl1
 └── imageUrl2
```

#### 장점

- 데이터 구조가 단순해 보인다.
    
- 별도 Photo 엔티티가 필요 없다.
    
- 간단한 대표 이미지 정도는 빠르게 구현할 수 있다.
    

#### 단점

- 여러 장의 이미지 관리가 어렵다.
    
- 이미지별 상태를 관리할 수 없다.
    
- 업로드 중, 처리 중, 실패 상태를 표현하기 어렵다.
    
- 이미지 정렬 순서 관리가 어렵다.
    
- Original, Medium, Thumbnail을 체계적으로 관리하기 어렵다.
    
- 이미지 삭제와 재처리 정책을 표현하기 어렵다.
    
- 향후 이미지 수 제한이나 대표 사진 정책을 적용하기 어렵다.
    

소로소로는 이미지 업로드, 리사이징, 상태 관리가 필요한 서비스이므로 단순 URL 필드 방식은 적합하지 않다.

---

### 3. 공통 Photo 엔티티 + nullable FK 방식

세 번째 대안은 공통 Photo 엔티티를 두되, Project, DailyLog, Fabric에 대한 FK를 각각 nullable로 두는 방식이다.

```text
Photo
 ├── projectId nullable
 ├── dailyLogId nullable
 └── fabricId nullable
```

#### 장점

- 공통 Photo 엔티티를 사용할 수 있다.
    
- FK 관계를 명시적으로 표현할 수 있다.
    
- ownerType/ownerId보다 관계가 눈에 잘 보인다.
    

#### 단점

- 세 FK 중 하나만 값이 있어야 한다는 제약을 별도로 관리해야 한다.
    
- 새로운 owner가 추가되면 Photo 테이블 컬럼을 변경해야 한다.
    
- null 컬럼이 많아진다.
    
- 도메인 확장성이 낮다.
    
- 코드에서 어떤 owner에 속한 Photo인지 매번 분기해야 한다.
    

이 방식은 현재 owner가 3개로 고정되어 있을 때는 가능하지만, 확장성과 제약 관리 측면에서 애매하다.

---

### 4. 공통 Photo 엔티티 + ownerType/ownerId 방식

네 번째 대안은 Photo에 ownerType과 ownerId를 두는 방식이다.

```text
Photo
 ├── ownerType
 └── ownerId
```

#### 장점

- 하나의 Photo 엔티티로 이미지 정책을 통합할 수 있다.
    
- Project, DailyLog, Fabric에서 같은 업로드/처리/조회 로직을 사용할 수 있다.
    
- 새로운 이미지 owner가 추가되어도 Photo 테이블 구조 변경이 적다.
    
- Presigned URL 발급, 업로드 완료 처리, 이미지 리사이징 상태 관리가 일관된다.
    
- Original, Medium, Thumbnail 구조를 공통으로 관리할 수 있다.
    
- Photo 상태를 공통으로 관리할 수 있다.
    
- 정렬, 삭제, 대표 사진 정책을 공통 로직으로 처리할 수 있다.
    

#### 단점

- DB 레벨에서 ownerId의 FK를 직접 강제하기 어렵다.
    
- ownerType별 권한 검증 로직이 필요하다.
    
- ownerType과 ownerId 조합이 실제 존재하는지 애플리케이션에서 검증해야 한다.
    
- 쿼리 작성 시 ownerType 조건을 함께 사용해야 한다.
    
- 도메인별 사진 수 제한을 애플리케이션에서 관리해야 한다.
    

소로소로에서는 이미지 정책의 공통성이 크고, 향후 확장 가능성도 고려해야 하므로 이 방식을 선택한다.

---

## Rationale

Photo를 공통 이미지 엔티티로 설계한 이유는 다음과 같다.

---

### 1. 이미지 업로드 흐름을 통합할 수 있다

Project, DailyLog, Fabric 모두 이미지 업로드 방식은 동일하다.

공통 흐름은 다음과 같다.

```text
Client
→ Server: Presigned URL 요청
→ Server: Photo 생성, status = UPLOADING
→ Client
→ S3: 원본 이미지 업로드
→ Client
→ Server: 업로드 완료 처리
→ Server: status = PROCESSING
→ Worker: 이미지 리사이징
→ Server: status = READY
```

도메인별 이미지 테이블을 나누면 이 흐름을 ProjectPhoto, DailyLogPhoto, FabricPhoto마다 반복해야 한다.

Photo를 공통 엔티티로 두면 업로드 흐름을 하나의 서비스 정책으로 관리할 수 있다.

---

### 2. 이미지 상태를 일관되게 관리할 수 있다

Photo는 다음 상태를 가진다.

|상태|설명|
|---|---|
|UPLOADING|Presigned URL 발급 후 원본 업로드 대기 또는 진행 중|
|PROCESSING|원본 업로드 완료 후 이미지 변환 중|
|READY|이미지 변환 완료 후 조회 가능|
|FAILED|업로드 또는 이미지 처리 실패|

이 상태는 Project, DailyLog, Fabric 모두에 동일하게 적용된다.

공통 Photo 엔티티를 사용하면 상태 관리와 실패 처리 정책을 일관되게 유지할 수 있다.

---

### 3. 이미지 버전을 공통으로 관리할 수 있다

소로소로는 이미지 조회 성능을 위해 원본 이미지와 조회용 이미지를 분리한다.

Photo는 다음 이미지 Key를 가진다.

|이미지|사용 목적|
|---|---|
|originalKey|사용자가 업로드한 원본 이미지|
|mediumKey|상세 화면용 이미지|
|thumbnailKey|목록, 카드, 캘린더용 이미지|

이 구조는 Project, DailyLog, Fabric 모두에 동일하게 필요하다.

따라서 이미지 버전 정보를 각 도메인에 중복해서 두기보다, Photo에서 공통으로 관리하는 것이 적절하다.

---

### 4. 대표 사진과 정렬 정책을 공통화할 수 있다

Project와 DailyLog는 대표 사진을 1장 가질 수 있다.

Fabric은 별도 대표 사진 설정 기능을 제공하지 않지만, 정렬 순서가 가장 앞선 이미지를 목록 이미지처럼 사용할 수 있다.

공통 Photo 엔티티는 다음 정보를 통해 표시 정책을 관리할 수 있다.

- isThumbnail
    
- sortOrder
    

도메인마다 대표 사진 정책은 조금 다르지만, Photo의 공통 속성을 활용하면 대부분의 로직을 재사용할 수 있다.

---

### 5. 이미지 수 제한을 ownerType별로 관리할 수 있다

소로소로 MVP의 사진 수 제한은 다음과 같다.

|ownerType|최대 사진 수|
|---|--:|
|PROJECT|15장|
|DAILY_LOG|15장|
|FABRIC|2장|

Photo를 공통 엔티티로 두면 ownerType과 ownerId를 기준으로 현재 사진 수를 조회하고 제한을 적용할 수 있다.

예를 들어 다음 조건으로 사진 수를 계산할 수 있다.

```text
ownerType = PROJECT
ownerId = 1
status != deleted
```

이 방식은 도메인별 이미지 테이블을 따로 두는 것보다 정책을 한 곳에서 관리하기 쉽다.

---

### 6. 향후 이미지 owner 확장에 유리하다

MVP에서는 Photo owner가 다음 세 가지이다.

- Project
    
- DailyLog
    
- Fabric
    

향후 다음 owner가 추가될 수 있다.

- PublicProject
    
- PublicDailyLog
    
- Profile
    
- Comment
    
- AI Extraction Result
    

ownerType/ownerId 구조를 사용하면 새로운 owner가 추가되어도 Photo 테이블 구조를 크게 바꾸지 않고 확장할 수 있다.

다만 새로운 ownerType이 추가될 때는 권한 검증과 owner 존재 검증 로직을 추가해야 한다.

---

### 7. 이미지 처리 Worker와 연결하기 쉽다

이미지 리사이징 Worker는 특정 도메인을 알 필요가 없다.

Worker는 photoId만 받아 다음 작업을 수행하면 된다.

```text
photoId 수신
→ Photo 조회
→ originalKey 확인
→ S3에서 원본 다운로드
→ medium.webp 생성
→ thumbnail.webp 생성
→ S3 업로드
→ Photo 상태 READY로 변경
```

Photo가 공통 엔티티이면 Worker는 ProjectPhoto, DailyLogPhoto, FabricPhoto를 구분할 필요가 없다.

이는 비동기 처리 구조를 단순하게 만든다.

---

## Trade-off

### 장점

Photo를 공통 엔티티로 설계하면 다음 장점이 있다.

- 이미지 업로드 로직을 통합할 수 있다.
    
- Presigned URL 발급 로직을 재사용할 수 있다.
    
- 이미지 처리 상태를 일관되게 관리할 수 있다.
    
- Original, Medium, Thumbnail 구조를 공통으로 사용할 수 있다.
    
- 이미지 리사이징 Worker가 도메인별 테이블을 알 필요가 없다.
    
- 대표 사진, 정렬, 삭제 정책을 공통화할 수 있다.
    
- 새로운 이미지 owner가 추가되어도 확장하기 쉽다.
    
- 중복 코드와 중복 테이블을 줄일 수 있다.
    

---

### 단점

이 결정으로 인해 다음 비용도 발생한다.

- ownerType/ownerId 구조는 DB 레벨 FK를 직접 강제하기 어렵다.
    
- owner 존재 여부를 애플리케이션에서 검증해야 한다.
    
- ownerType별 권한 검증 로직이 필요하다.
    
- ownerType별 사진 수 제한 로직이 필요하다.
    
- Project, DailyLog, Fabric의 대표 사진 정책 차이를 코드에서 분기해야 한다.
    
- 잘못된 ownerType/ownerId 조합이 저장되지 않도록 테스트가 필요하다.
    

---

### Trade-off 판단

도메인별 이미지 테이블을 두면 FK는 명확해지지만, 이미지 업로드와 처리 로직의 중복이 커진다.

소로소로에서 Project, DailyLog, Fabric의 이미지 정책은 대부분 동일하다.

- Presigned URL로 업로드한다.
    
- 업로드 완료 후 PROCESSING 상태가 된다.
    
- Worker가 이미지를 리사이징한다.
    
- READY 상태가 되어야 조회 가능하다.
    
- Original, Medium, Thumbnail을 가진다.
    
- 정렬 순서를 가진다.
    
- owner별 사진 수 제한이 있다.
    

따라서 이미지 정책을 공통화하는 이점이 FK 강제의 단점보다 크다고 판단했다.

다만 ownerType/ownerId 구조에서 발생할 수 있는 무결성 문제는 Service 계층의 owner 검증과 테스트로 보완한다.

---

## Consequences

Photo를 공통 이미지 엔티티로 설계함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. Photo는 ownerType과 ownerId를 가진다

Photo는 다음 ownerType 중 하나를 가진다.

```text
PROJECT
DAILY_LOG
FABRIC
```

Photo 생성 시 ownerType과 ownerId를 기준으로 실제 owner 존재 여부와 소유권을 검증한다.

---

### 2. Photo 업로드 API는 공통으로 제공한다

도메인별로 별도 업로드 API를 만들지 않고, 공통 Photo API에서 ownerType과 ownerId를 받는다.

예시는 다음과 같다.

```text
POST /api/v1/photos/presigned-url
POST /api/v1/photos/{photoId}/complete-upload
GET  /api/v1/photos?ownerType=PROJECT&ownerId=1
```

구체적인 API는 `07_API.md`에서 정의한다.

---

### 3. ownerType별 권한 검증을 수행한다

Photo 생성, 조회, 수정, 삭제 시 ownerType에 따라 소유권을 검증한다.

|ownerType|검증 기준|
|---|---|
|PROJECT|Project.userId가 현재 User와 일치해야 한다.|
|DAILY_LOG|DailyLog.userId와 Project.userId가 현재 User와 일치해야 한다.|
|FABRIC|Fabric.userId가 현재 User와 일치해야 한다.|

---

### 4. ownerType별 사진 수 제한을 적용한다

Photo 생성 시 ownerType별 최대 사진 수를 검증한다.

|ownerType|최대 사진 수|
|---|--:|
|PROJECT|15장|
|DAILY_LOG|15장|
|FABRIC|2장|

사진 수 제한을 초과하면 Presigned URL을 발급하지 않는다.

---

### 5. 대표 사진 정책을 ownerType별로 적용한다

Project와 DailyLog는 대표 사진을 1장 가질 수 있다.

Fabric은 별도의 대표 사진 설정 기능을 제공하지 않는다.

대표 사진 설정 시 다음 규칙을 적용한다.

- ownerType이 PROJECT 또는 DAILY_LOG여야 한다.
    
- Photo는 READY 상태여야 한다.
    
- 같은 owner의 기존 대표 사진은 해제한다.
    
- Fabric Photo는 대표 사진으로 설정할 수 없다.
    

---

### 6. Worker는 Photo만 기준으로 이미지 처리를 수행한다

이미지 처리 Worker는 photoId만 받아 처리한다.

Worker는 ownerType별 비즈니스 로직을 수행하지 않는다.

Worker의 책임은 다음으로 제한한다.

- Photo 조회
    
- originalKey 기반 원본 다운로드
    
- Medium 이미지 생성
    
- Thumbnail 이미지 생성
    
- 변환 이미지 업로드
    
- Photo 상태 변경
    

---

### 7. 테스트 케이스를 ownerType별로 작성한다

공통 Photo 엔티티는 ownerType에 따라 검증 로직이 달라진다.

따라서 다음 테스트가 필요하다.

- Project 소유자는 Project Photo를 추가할 수 있다.
    
- 다른 사용자의 Project에는 Photo를 추가할 수 없다.
    
- DailyLog 소유자는 DailyLog Photo를 추가할 수 있다.
    
- 다른 사용자의 DailyLog에는 Photo를 추가할 수 없다.
    
- Fabric 소유자는 Fabric Photo를 추가할 수 있다.
    
- 다른 사용자의 Fabric에는 Photo를 추가할 수 없다.
    
- Project와 DailyLog는 대표 사진을 1장만 가질 수 있다.
    
- Fabric은 대표 사진을 설정할 수 없다.
    
- READY 상태가 아닌 Photo는 대표 사진으로 설정할 수 없다.
    
- owner별 사진 수 제한을 초과할 수 없다.
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-001 Backend Framework로 Spring Boot를 선택한 이유|Photo Service에서 owner 검증과 상태 관리를 수행|
|ADR-002 Database로 PostgreSQL을 선택한 이유|ownerType/ownerId 기반 조회와 인덱스 설계|
|ADR-003 Fabric을 Project와 독립 도메인으로 분리한 이유|Fabric Photo를 공통 Photo로 관리|
|ADR-005 DailyLog에 DRAFT/PUBLISHED 상태를 둔 이유|DRAFT/PUBLISHED DailyLog 모두 Photo를 가질 수 있음|
|ADR-008 Presigned URL 기반 이미지 업로드를 선택한 이유|Photo의 업로드 흐름과 직접 연결|
|ADR-009 이미지 리사이징을 비동기로 처리하는 이유|Photo 상태와 Worker 처리 구조에 연결|

---

# ADR-007 요약

Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 엔티티로 설계한다.

선택 이유는 다음과 같다.

- 이미지 업로드 흐름을 통합할 수 있다.
    
- Presigned URL 발급 로직을 재사용할 수 있다.
    
- UPLOADING, PROCESSING, READY, FAILED 상태를 일관되게 관리할 수 있다.
    
- Original, Medium, Thumbnail 이미지 구조를 공통으로 사용할 수 있다.
    
- 이미지 리사이징 Worker가 도메인별 테이블을 알 필요가 없다.
    
- Project, DailyLog, Fabric의 사진 수 제한과 대표 사진 정책을 공통 구조에서 관리할 수 있다.
    
- 향후 새로운 이미지 owner가 추가되어도 확장하기 쉽다.
    

단, ownerType/ownerId 구조는 DB 레벨 FK 강제가 어렵기 때문에, owner 존재 여부와 소유권 검증은 Service 계층에서 명확히 수행한다.

---
# ADR-008. Presigned URL 기반 이미지 업로드를 선택한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 재봉 과정을 이미지 중심으로 기록하는 서비스이다.

사용자는 다음 대상에 사진을 업로드할 수 있다.

- Project
    
- DailyLog
    
- Fabric
    

사진은 서비스의 핵심 기록 데이터이다.

예를 들어 사용자는 다음 이미지를 저장할 수 있어야 한다.

- 작품 완성 사진
    
- 제작 과정 사진
    
- 실패하거나 수정한 부분 사진
    
- 원단 실물 사진
    
- 쇼핑몰 상품 이미지
    

MVP에서 사진 수 제한은 다음과 같다.

|대상|최대 사진 수|
|---|--:|
|Project|15장|
|DailyLog|15장|
|Fabric|2장|

이미지 업로드는 단순 텍스트 데이터보다 다음 부담이 크다.

- 파일 크기가 크다.
    
- 업로드 시간이 길 수 있다.
    
- 서버 메모리와 네트워크 사용량이 증가한다.
    
- 동시 업로드가 발생할 수 있다.
    
- 이미지 리사이징 처리가 필요하다.
    
- 업로드 중, 처리 중, 실패 상태를 관리해야 한다.
    

초기에는 클라이언트가 이미지를 백엔드 서버로 직접 업로드하고, 서버가 다시 S3에 저장하는 방식도 고려할 수 있었다.

하지만 이 방식은 백엔드 서버가 이미지 파일 전송의 중간 경로가 되므로, 서버 부하가 커지고 업로드 실패 처리도 복잡해진다.

따라서 이미지 파일은 클라이언트가 S3에 직접 업로드하고, 백엔드 서버는 업로드 권한 발급과 Photo 상태 관리만 담당하는 구조가 필요하다.

---

## Decision

소로소로의 이미지 업로드는 **Presigned URL 기반 S3 직접 업로드 방식**을 사용한다.

백엔드 서버는 이미지 파일을 직접 저장하지 않는다.

업로드 흐름은 다음과 같다.

```text
1. Client
   → Server: Presigned URL 요청

2. Server
   → owner 권한 확인
   → 사진 수 제한 확인
   → Photo row 생성
   → status = UPLOADING
   → S3 업로드용 Presigned URL 발급

3. Server
   → Client: photoId, uploadUrl, originalKey 반환

4. Client
   → S3: Presigned URL로 원본 이미지 직접 업로드

5. Client
   → Server: 업로드 완료 처리 요청

6. Server
   → S3 object 존재 확인
   → Photo status = PROCESSING
   → Queue에 photoId 발행

7. Worker
   → 이미지 리사이징
   → medium, thumbnail 생성
   → Photo status = READY 또는 FAILED
```

이 구조에서 백엔드 서버의 책임은 다음과 같다.

- 사용자 인증 확인
    
- ownerType, ownerId 검증
    
- owner 소유권 검증
    
- owner별 사진 수 제한 검증
    
- Photo 상태 생성 및 변경
    
- Presigned URL 발급
    
- 업로드 완료 처리
    
- 이미지 처리 작업 등록
    

실제 이미지 파일 업로드는 클라이언트가 S3에 직접 수행한다.

---

## Alternatives

### 1. 백엔드 서버가 Multipart 파일을 직접 수신하는 방식

첫 번째 대안은 클라이언트가 이미지를 백엔드 서버로 직접 업로드하는 방식이다.

```text
Client
→ Server: multipart/form-data 이미지 업로드
→ Server: 이미지 파일 수신
→ Server: S3에 업로드
```

#### 장점

- 클라이언트 구현이 직관적이다.
    
- 서버에서 파일 검증과 저장을 한 번에 처리할 수 있다.
    
- 업로드 완료 여부를 서버가 즉시 알 수 있다.
    
- S3 Presigned URL 발급 과정이 필요 없다.
    

#### 단점

- 이미지 파일이 백엔드 서버를 거치므로 서버 부하가 커진다.
    
- 서버 네트워크 사용량이 증가한다.
    
- 큰 파일 업로드 시 요청 시간이 길어진다.
    
- 서버 메모리 또는 임시 저장소 관리가 필요할 수 있다.
    
- 동시 업로드가 많아지면 서버 안정성에 영향을 줄 수 있다.
    
- 서버 확장 시 파일 업로드 처리 부담이 커진다.
    

소로소로는 이미지 중심 서비스이므로, 백엔드 서버가 모든 이미지 파일을 직접 받는 구조는 확장성과 안정성 측면에서 부담이 크다.

---

### 2. Base64 인코딩 이미지를 JSON으로 전송하는 방식

두 번째 대안은 이미지를 Base64 문자열로 변환해 JSON Body에 포함하는 방식이다.

```json
{
  "ownerType": "PROJECT",
  "ownerId": 1,
  "fileName": "image.png",
  "base64": "..."
}
```

#### 장점

- 단일 JSON 요청으로 처리할 수 있다.
    
- Multipart 처리 로직이 필요 없다.
    
- 작은 이미지 업로드에는 단순해 보일 수 있다.
    

#### 단점

- Base64 인코딩으로 인해 원본보다 데이터 크기가 증가한다.
    
- 서버 요청 Body가 커진다.
    
- 대용량 이미지 처리에 부적합하다.
    
- 메모리 사용량이 커질 수 있다.
    
- 이미지 중심 서비스에는 적합하지 않다.
    

소로소로는 여러 장의 사진을 업로드할 수 있으므로 Base64 방식은 선택하지 않는다.

---

### 3. 서버 로컬 디스크에 이미지 저장

세 번째 대안은 이미지를 서버 로컬 디스크에 저장하는 방식이다.

```text
Client
→ Server
→ EC2 local disk 저장
```

#### 장점

- S3 설정 없이 구현할 수 있다.
    
- 초기 개발이 단순하다.
    
- 로컬 테스트가 쉽다.
    

#### 단점

- 서버 재배포 또는 장애 시 파일 유실 위험이 있다.
    
- 여러 서버로 확장하기 어렵다.
    
- 백업과 복구가 어렵다.
    
- CDN 연결이 어렵다.
    
- 이미지 파일과 애플리케이션 서버가 강하게 결합된다.
    
- 포트폴리오 관점에서 운영 친화적 구조로 보기 어렵다.
    

소로소로는 Docker, EC2, Nginx 기반 배포를 고려하므로, 이미지 파일을 애플리케이션 서버 로컬 디스크에 영구 저장하지 않는다.

---

### 4. Presigned URL 기반 S3 직접 업로드

네 번째 대안은 서버가 Presigned URL을 발급하고, 클라이언트가 S3에 직접 업로드하는 방식이다.

```text
Client
→ Server: 업로드 권한 요청
→ Server: Presigned URL 발급
→ Client
→ S3: 직접 업로드
```

#### 장점

- 이미지 파일이 백엔드 서버를 거치지 않는다.
    
- 서버 네트워크와 메모리 부담이 줄어든다.
    
- S3를 이미지 원본 저장소로 사용할 수 있다.
    
- 업로드 권한과 만료 시간을 서버가 통제할 수 있다.
    
- owner 권한 검증 후에만 업로드 URL을 발급할 수 있다.
    
- Photo 상태를 통해 업로드 흐름을 관리할 수 있다.
    
- CloudFront와 연결해 이미지 조회 성능을 개선할 수 있다.
    
- 이미지 처리 Worker와 자연스럽게 연결된다.
    

#### 단점

- 업로드 흐름이 2단계 이상으로 나뉜다.
    
- 클라이언트가 S3 업로드와 서버 완료 요청을 모두 처리해야 한다.
    
- Presigned URL 발급 후 실제 업로드가 완료되지 않은 Photo가 남을 수 있다.
    
- S3 object 존재 확인이 필요하다.
    
- 업로드 실패, 중단, 만료 처리 정책이 필요하다.
    

이미지 중심 서비스인 소로소로에서는 서버 부하를 줄이고 업로드 책임을 분리하는 장점이 더 크므로 Presigned URL 방식을 선택한다.

---

## Rationale

Presigned URL 기반 이미지 업로드를 선택한 이유는 다음과 같다.

---

### 1. 백엔드 서버의 파일 처리 부담을 줄일 수 있다

이미지 파일은 일반 JSON 데이터보다 크다.

백엔드 서버가 이미지 파일을 직접 수신하면 다음 부담이 생긴다.

- multipart 요청 처리
    
- 파일 임시 저장
    
- S3 업로드
    
- 업로드 중 예외 처리
    
- 네트워크 사용량 증가
    
- 동시 업로드 처리 부담
    

Presigned URL 방식을 사용하면 이미지 파일은 클라이언트에서 S3로 직접 전송된다.

백엔드 서버는 이미지 데이터 자체를 받지 않고, 업로드 권한과 상태만 관리한다.

---

### 2. 업로드 권한을 서버가 통제할 수 있다

Presigned URL은 아무 사용자에게나 발급하지 않는다.

서버는 Presigned URL 발급 전에 다음을 검증한다.

- 사용자가 로그인했는지
    
- ownerType이 유효한지
    
- ownerId가 실제 존재하는지
    
- owner가 현재 User 소유인지
    
- owner 상태가 사진 추가 가능한 상태인지
    
- owner별 사진 수 제한을 초과하지 않았는지
    
- 파일 형식과 크기가 허용 범위인지
    

즉, 파일은 S3에 직접 업로드되지만, 업로드 권한은 서버가 통제한다.

---

### 3. Photo 상태 관리와 잘 맞는다

소로소로의 Photo는 다음 상태를 가진다.

|상태|설명|
|---|---|
|UPLOADING|Presigned URL 발급 후 원본 업로드 대기 또는 진행 중|
|PROCESSING|원본 업로드 완료 후 이미지 변환 중|
|READY|이미지 변환 완료 후 조회 가능|
|FAILED|업로드 또는 이미지 처리 실패|

Presigned URL 발급 시 Photo는 `UPLOADING` 상태로 생성된다.

클라이언트가 S3 업로드를 완료한 뒤 서버에 완료 요청을 보내면 Photo는 `PROCESSING` 상태가 된다.

이후 Worker가 이미지 리사이징에 성공하면 `READY`, 실패하면 `FAILED` 상태가 된다.

이 상태 흐름은 사용자에게 업로드 진행 상황을 보여주기에도 적합하다.

---

### 4. 이미지 리사이징 비동기 처리와 자연스럽게 연결된다

Presigned URL 방식에서는 원본 이미지가 먼저 S3에 저장된다.

그 후 서버는 업로드 완료 요청을 받은 시점에 이미지 처리 작업을 등록할 수 있다.

흐름은 다음과 같다.

```text
S3 원본 업로드 완료
→ Server complete-upload 호출
→ Photo status = PROCESSING
→ Queue에 photoId 발행
→ Worker가 S3 원본 다운로드
→ medium, thumbnail 생성
→ S3 업로드
→ Photo status = READY
```

이 구조에서는 이미지 업로드와 이미지 리사이징을 분리할 수 있다.

사용자 요청은 빠르게 완료되고, 무거운 이미지 변환은 Worker가 처리한다.

---

### 5. CloudFront 기반 이미지 조회와 연결하기 쉽다

소로소로는 이미지 조회 시 Original, Medium, Thumbnail을 구분한다.

|이미지|사용 위치|
|---|---|
|Original|원본 확인 또는 확대|
|Medium|상세 화면|
|Thumbnail|목록, 카드, 캘린더|

이미지를 S3에 저장하면 CloudFront를 통해 조회할 수 있다.

백엔드 서버는 이미지 파일을 직접 제공하지 않고, Photo의 key를 기반으로 조회 URL을 구성하거나 반환할 수 있다.

이 구조는 이미지 중심 서비스에서 조회 성능을 확보하는 데 유리하다.

---

### 6. 서버 로컬 저장소에 의존하지 않는다

Docker 기반 배포 환경에서는 애플리케이션 컨테이너가 언제든 재시작되거나 교체될 수 있다.

이미지를 서버 로컬 디스크에 저장하면 다음 문제가 생길 수 있다.

- 컨테이너 재시작 시 파일 유실
    
- 배포 시 파일 관리 복잡성 증가
    
- 서버 교체 시 파일 이전 필요
    
- 여러 서버로 확장 시 이미지 공유 문제 발생
    

Presigned URL 기반 S3 업로드를 사용하면 이미지 파일은 애플리케이션 서버와 분리된 저장소에 보관된다.

이는 Docker, EC2, Nginx 기반 배포 구조와 잘 맞는다.

---

### 7. 포트폴리오에서 운영 관점을 설명하기 좋다

Presigned URL 방식은 단순히 이미지를 저장하는 구현이 아니라, 다음 운영 관점을 보여줄 수 있다.

- 서버 부하 분산
    
- 파일 업로드 책임 분리
    
- S3 기반 정적 파일 저장
    
- CloudFront 기반 이미지 전달
    
- 비동기 이미지 처리
    
- 상태 기반 업로드 흐름 관리
    
- 실패 상태 처리
    
- 권한 검증 후 업로드 허용
    

이는 포트폴리오에서 “이미지 업로드를 어떻게 안정적으로 설계했는가”를 설명하기 좋은 의사결정이다.

---

## Trade-off

### 장점

Presigned URL 방식을 선택하면 다음 장점이 있다.

- 이미지 파일이 백엔드 서버를 거치지 않는다.
    
- 서버 네트워크와 메모리 부담을 줄일 수 있다.
    
- S3를 이미지 원본 저장소로 사용할 수 있다.
    
- 업로드 권한을 서버가 통제할 수 있다.
    
- Presigned URL 만료 시간을 설정할 수 있다.
    
- Photo 상태를 통해 업로드 흐름을 관리할 수 있다.
    
- 이미지 리사이징 Worker와 자연스럽게 연결된다.
    
- CloudFront 기반 이미지 제공 구조와 잘 맞는다.
    
- Docker 기반 배포 환경에서 로컬 파일 저장 문제를 피할 수 있다.
    

---

### 단점

이 결정으로 인해 다음 비용도 발생한다.

- 업로드 흐름이 복잡해진다.
    
- 클라이언트가 S3 업로드를 직접 처리해야 한다.
    
- Presigned URL 발급 후 실제 업로드가 완료되지 않은 Photo가 남을 수 있다.
    
- 업로드 완료 API가 별도로 필요하다.
    
- S3 object 존재 여부 검증이 필요하다.
    
- Presigned URL 만료 처리 정책이 필요하다.
    
- 업로드 중단 또는 실패 상태 정리 정책이 필요하다.
    
- 로컬 개발 환경에서 S3 연동 설정이 필요하다.
    

---

### Trade-off 판단

백엔드 서버가 이미지를 직접 받는 방식은 구현이 단순하지만, 이미지 중심 서비스에서는 서버 부하와 운영 부담이 커질 수 있다.

소로소로는 Project와 DailyLog에 각각 최대 15장의 사진을 업로드할 수 있으므로, 이미지 업로드가 주요 트래픽이 될 가능성이 있다.

따라서 초기 구현 복잡도가 늘어나더라도, 서버가 파일 전송의 중간 경로가 되지 않는 Presigned URL 방식을 선택하는 것이 더 적절하다.

---

## Consequences

Presigned URL 기반 이미지 업로드를 선택함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. Photo 생성과 파일 업로드가 분리된다

Presigned URL 발급 시점에 Photo row를 먼저 생성한다.

이때 Photo 상태는 `UPLOADING`이다.

```text
Photo.status = UPLOADING
```

하지만 이 시점에는 S3에 실제 원본 이미지가 아직 존재하지 않을 수 있다.

따라서 READY 상태가 되기 전까지는 정상 이미지로 표시하지 않는다.

---

### 2. 업로드 완료 API가 필요하다

클라이언트는 S3 업로드 완료 후 서버에 업로드 완료를 알려야 한다.

```text
POST /api/v1/photos/{photoId}/complete-upload
```

서버는 이 요청에서 다음을 수행한다.

- Photo 존재 확인
    
- Photo 소유권 확인
    
- Photo 상태 확인
    
- S3 object 존재 확인
    
- Photo status를 PROCESSING으로 변경
    
- Queue에 photoId 발행
    

---

### 3. 업로드 중단 데이터 정리 정책이 필요하다

Presigned URL을 발급받고 실제 업로드를 완료하지 않는 경우가 발생할 수 있다.

이 경우 Photo는 `UPLOADING` 상태로 남을 수 있다.

MVP에서는 즉시 복잡한 정리 정책을 구현하지 않더라도, Backend Design에서 다음 정책을 정의해야 한다.

- 오래된 UPLOADING Photo 정리
    
- 만료된 Presigned URL 처리
    
- S3 object 없는 Photo 정리
    
- 재업로드 허용 여부
    

---

### 4. 클라이언트는 업로드 단계를 처리해야 한다

클라이언트는 다음 단계를 처리해야 한다.

```text
1. Presigned URL 요청
2. S3에 파일 업로드
3. 서버에 complete-upload 요청
4. Photo 상태 polling 또는 재조회
```

따라서 UI에서는 다음 상태를 표시할 수 있어야 한다.

- 업로드 준비 중
    
- 업로드 중
    
- 처리 중
    
- 업로드 완료
    
- 실패
    

---

### 5. 이미지 파일 검증이 필요하다

Presigned URL 발급 시 서버는 최소한 다음 정보를 검증한다.

- contentType
    
- sizeBytes
    
- ownerType
    
- ownerId
    
- owner별 사진 수 제한
    

지원 파일 형식과 최대 파일 크기는 `05_Backend_Design.md`에서 구체적으로 정의한다.

---

### 6. S3 Key 규칙을 정의해야 한다

Photo는 다음 Key를 가진다.

- originalKey
    
- mediumKey
    
- thumbnailKey
    

S3 Key는 User와 Photo를 기준으로 충돌 없이 생성되어야 한다.

예상 구조는 다음과 같다.

```text
users/{userId}/photos/{photoId}/original
users/{userId}/photos/{photoId}/medium.webp
users/{userId}/photos/{photoId}/thumbnail.webp
```

정확한 Key 규칙은 `06_Architecture.md`에서 정의한다.

---

### 7. 테스트 케이스가 필요하다

다음 테스트가 필요하다.

- owner 권한이 없으면 Presigned URL을 발급하지 않는다.
    
- 사진 수 제한을 초과하면 Presigned URL을 발급하지 않는다.
    
- Presigned URL 발급 시 Photo는 UPLOADING 상태가 된다.
    
- 업로드 완료 처리 시 Photo가 PROCESSING 상태가 된다.
    
- UPLOADING 상태가 아닌 Photo는 complete-upload 처리할 수 없다.
    
- 다른 사용자의 Photo는 complete-upload 할 수 없다.
    
- S3 object가 없으면 complete-upload에 실패한다.
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-001 Backend Framework로 Spring Boot를 선택한 이유|서버가 Presigned URL 발급과 Photo 상태 관리를 담당|
|ADR-007 Photo를 공통 이미지 엔티티로 설계한 이유|공통 Photo 엔티티의 업로드 흐름|
|ADR-009 이미지 리사이징을 비동기로 처리하는 이유|complete-upload 이후 Queue와 Worker 처리|
|ADR-012 Docker Compose + EC2 + Nginx 배포를 선택한 이유|이미지 파일을 서버 로컬에 저장하지 않음|

---

# ADR-008 요약

이미지 업로드는 Presigned URL 기반 S3 직접 업로드 방식을 사용한다.

선택 이유는 다음과 같다.

- 이미지 파일이 백엔드 서버를 거치지 않는다.
    
- 서버 네트워크와 메모리 부담을 줄일 수 있다.
    
- 업로드 권한은 서버가 통제할 수 있다.
    
- Photo 상태를 통해 업로드 흐름을 관리할 수 있다.
    
- 이미지 리사이징 비동기 처리와 자연스럽게 연결된다.
    
- S3와 CloudFront 기반 이미지 저장·조회 구조와 잘 맞는다.
    
- Docker 기반 배포 환경에서 서버 로컬 파일 저장 문제를 피할 수 있다.
    

이 결정으로 인해 Presigned URL 발급 API와 업로드 완료 API가 필요하며, 오래된 UPLOADING Photo 정리 정책은 Backend Design에서 별도로 정의한다.

---
# ADR-009. 이미지 리사이징을 비동기로 처리하는 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 이미지 중심의 개인 재봉 기록 서비스이다.

사용자는 다음 대상에 사진을 업로드할 수 있다.

- Project
    
- DailyLog
    
- Fabric
    

사진은 단순히 저장만 하는 것이 아니라, 화면 용도에 따라 다른 크기로 제공되어야 한다.

소로소로에서 사용하는 이미지 종류는 다음과 같다.

|이미지|사용 위치|
|---|---|
|Original|원본 확인 또는 확대 보기|
|Medium|Project, DailyLog, Fabric 상세 화면|
|Thumbnail|목록, 카드, 캘린더, 작은 미리보기|

이미지 리사이징이 필요한 이유는 다음과 같다.

- 원본 이미지는 파일 크기가 클 수 있다.
    
- 목록 화면에서 원본 이미지를 그대로 사용하면 로딩이 느려진다.
    
- 모바일 웹 환경에서는 이미지 용량이 사용자 경험에 큰 영향을 준다.
    
- Project와 DailyLog는 최대 15장의 사진을 가질 수 있다.
    
- 사용자는 여러 장의 이미지를 연속으로 업로드할 수 있다.
    
- 이미지 변환은 CPU와 메모리를 사용하는 작업이다.
    

초기에는 업로드 완료 API 안에서 이미지 리사이징까지 즉시 처리하는 방식도 고려할 수 있었다.

예를 들어 다음과 같은 흐름이다.

```text
Client
→ S3 원본 업로드
→ Server complete-upload 호출
→ Server가 원본 이미지 다운로드
→ Server가 Medium, Thumbnail 생성
→ Server가 S3 업로드
→ Server가 응답 반환
```

이 방식은 흐름이 단순해 보이지만, 이미지 변환이 오래 걸릴 경우 API 응답 시간이 길어지고 서버 부하가 커진다.

따라서 이미지 업로드 완료 처리와 이미지 변환 처리를 분리할 필요가 있다.

---

## Decision

이미지 리사이징은 업로드 완료 요청 안에서 동기적으로 처리하지 않고, **Queue와 Worker를 통해 비동기로 처리**한다.

업로드 완료 요청의 책임은 다음으로 제한한다.

- Photo 존재 확인
    
- Photo 소유권 확인
    
- S3 Original Object 존재 확인
    
- Photo 상태를 `PROCESSING`으로 변경
    
- Queue에 `photoId` 발행
    
- 클라이언트에 처리 시작 상태 반환
    

이미지 리사이징은 Worker가 수행한다.

Worker의 책임은 다음과 같다.

- Queue에서 `photoId` 수신
    
- Photo 조회
    
- Original 이미지 다운로드
    
- Medium 이미지 생성
    
- Thumbnail 이미지 생성
    
- 변환 이미지 S3 업로드
    
- Photo 상태를 `READY`로 변경
    
- 실패 시 Photo 상태를 `FAILED`로 변경
    

전체 흐름은 다음과 같다.

```text
Client
→ Server: complete-upload 요청
→ Server: Photo status = PROCESSING
→ Server: Queue에 photoId 발행
→ Client: PROCESSING 응답

Worker
→ Queue에서 photoId 수신
→ S3 Original 이미지 다운로드
→ Medium / Thumbnail 생성
→ S3 업로드
→ Photo status = READY

실패 시
→ Photo status = FAILED
```

---

## Alternatives

### 1. 업로드 완료 API에서 리사이징까지 동기 처리

첫 번째 대안은 complete-upload API에서 이미지 리사이징까지 모두 처리하는 방식이다.

#### 장점

- 흐름이 단순하다.
    
- 별도 Queue와 Worker가 필요 없다.
    
- 클라이언트는 API 응답 시점에 이미지 처리 완료 여부를 바로 알 수 있다.
    
- 구현해야 하는 컴포넌트 수가 적다.
    

#### 단점

- API 응답 시간이 길어진다.
    
- 이미지 파일 크기가 크면 요청 타임아웃이 발생할 수 있다.
    
- 여러 장 업로드 시 서버 부하가 커진다.
    
- 이미지 처리 실패가 사용자 요청 실패로 직접 이어진다.
    
- CPU와 메모리 사용량이 API 서버에 집중된다.
    
- 사용자가 업로드 완료 응답을 오래 기다려야 한다.
    

소로소로는 이미지 중심 서비스이므로, API 요청 안에서 리사이징까지 처리하는 방식은 사용자 경험과 서버 안정성 측면에서 부담이 크다.

---

### 2. 클라이언트에서 이미지 리사이징 후 업로드

두 번째 대안은 클라이언트가 업로드 전에 이미지를 직접 리사이징하는 방식이다.

#### 장점

- 서버의 이미지 처리 부담이 줄어든다.
    
- 원본보다 작은 이미지만 업로드할 수 있다.
    
- 서버 Worker가 필요 없다.
    

#### 단점

- 클라이언트 구현이 복잡해진다.
    
- 브라우저와 기기별 이미지 처리 결과가 달라질 수 있다.
    
- 모바일 기기에서 이미지 처리 성능이 낮을 수 있다.
    
- 원본 이미지 보존이 어려울 수 있다.
    
- 클라이언트를 신뢰해야 하므로 이미지 품질과 크기 정책을 일관되게 강제하기 어렵다.
    

소로소로는 Original, Medium, Thumbnail을 안정적으로 관리해야 하므로 클라이언트에 리사이징 책임을 두지 않는다.

---

### 3. S3 이벤트 기반 이미지 처리

세 번째 대안은 S3 ObjectCreated 이벤트를 통해 이미지 처리 Worker 또는 Lambda를 실행하는 방식이다.

```text
Client
→ S3 원본 업로드
→ S3 Event 발생
→ Worker/Lambda 실행
→ 이미지 변환
```

#### 장점

- 클라이언트가 complete-upload를 호출하지 않아도 자동 처리할 수 있다.
    
- S3 업로드 이벤트와 이미지 처리가 자연스럽게 연결된다.
    
- 서버 API의 책임이 줄어든다.
    

#### 단점

- MVP 단계에서는 인프라 구성이 복잡해진다.
    
- S3 이벤트와 DB Photo 상태를 동기화해야 한다.
    
- owner 권한 검증과 Photo 상태 관리 흐름이 분리될 수 있다.
    
- 로컬 개발과 테스트가 어려워질 수 있다.
    
- 1인 개발 MVP에 비해 운영 복잡도가 크다.
    

S3 이벤트 기반 처리는 확장성은 좋지만, MVP에서는 명시적인 complete-upload API와 Queue 발행 방식이 더 이해하기 쉽고 제어하기 쉽다.

---

### 4. Queue와 Worker 기반 비동기 처리

네 번째 대안은 complete-upload 이후 Queue에 photoId를 발행하고, Worker가 이미지 리사이징을 처리하는 방식이다.

#### 장점

- API 응답 시간을 줄일 수 있다.
    
- 이미지 처리 작업을 API 요청 흐름에서 분리할 수 있다.
    
- 이미지 처리 실패를 Photo 상태로 관리할 수 있다.
    
- Worker를 별도로 확장할 수 있다.
    
- 이미지 변환 로직을 독립적으로 테스트할 수 있다.
    
- 사용자는 처리 중 상태를 확인할 수 있다.
    
- 향후 재시도, 실패 처리, 배치 정리 작업으로 확장하기 쉽다.
    

#### 단점

- Queue와 Worker 구성이 필요하다.
    
- 비동기 상태 관리가 필요하다.
    
- 클라이언트는 PROCESSING 상태를 처리해야 한다.
    
- Worker 실패 시 재처리 정책이 필요하다.
    
- 로컬 개발 환경에서 Worker 실행 구성이 필요하다.
    

소로소로는 이미지 처리 실패 상태를 관리해야 하고, 업로드 응답 속도를 유지해야 하므로 Queue와 Worker 기반 비동기 처리를 선택한다.

---

## Rationale

이미지 리사이징을 비동기로 처리하는 이유는 다음과 같다.

---

### 1. API 응답 시간을 짧게 유지할 수 있다

이미지 리사이징은 파일 크기, 이미지 해상도, 서버 성능에 따라 처리 시간이 달라진다.

complete-upload 요청 안에서 리사이징을 처리하면 사용자는 이미지 변환이 끝날 때까지 기다려야 한다.

비동기로 처리하면 complete-upload API는 다음 작업만 수행한다.

```text
Photo status = PROCESSING
Queue에 photoId 발행
응답 반환
```

이후 실제 이미지 변환은 Worker가 처리한다.

따라서 사용자 요청 응답 시간을 짧게 유지할 수 있다.

---

### 2. API 서버와 이미지 처리 책임을 분리할 수 있다

API 서버의 주요 책임은 다음과 같다.

- 인증
    
- 권한 검증
    
- 비즈니스 규칙 검증
    
- Photo 상태 관리
    
- Queue 발행
    

Worker의 주요 책임은 다음과 같다.

- 원본 이미지 다운로드
    
- 이미지 변환
    
- 변환 이미지 업로드
    
- 처리 결과 상태 반영
    

이렇게 역할을 분리하면 API 서버가 무거운 이미지 처리까지 담당하지 않아도 된다.

---

### 3. 이미지 처리 실패를 상태로 관리할 수 있다

이미지 변환은 실패할 수 있다.

실패 원인은 다음과 같다.

- S3 원본 파일 없음
    
- 지원하지 않는 이미지 포맷
    
- 이미지 파일 손상
    
- 변환 라이브러리 오류
    
- S3 업로드 실패
    
- Worker 실행 오류
    

비동기 처리 구조에서는 실패 시 Photo 상태를 `FAILED`로 변경할 수 있다.

사용자는 해당 Photo가 실패 상태임을 확인할 수 있고, 향후 재업로드 또는 재처리 기능으로 확장할 수 있다.

---

### 4. 이미지 중심 서비스에서 서버 안정성을 높일 수 있다

Project와 DailyLog는 각각 최대 15장의 사진을 가질 수 있다.

사용자가 여러 장의 이미지를 연속으로 업로드하면 이미지 리사이징 작업이 몰릴 수 있다.

이 작업을 API 서버 요청 안에서 처리하면 서버 응답 지연이나 부하 증가로 이어질 수 있다.

Queue와 Worker를 사용하면 이미지 처리 작업을 순차적으로 처리하거나, 필요 시 Worker를 확장할 수 있다.

---

### 5. Worker는 Photo 도메인만 기준으로 처리할 수 있다

ADR-007에서 Photo를 공통 이미지 엔티티로 설계했다.

따라서 Worker는 Project, DailyLog, Fabric을 직접 알 필요가 없다.

Worker는 `photoId`만 받아 다음을 처리한다.

```text
Photo 조회
→ originalKey 확인
→ Medium 생성
→ Thumbnail 생성
→ Photo 상태 변경
```

이 구조는 Worker 로직을 단순하게 만들고, 도메인별 이미지 테이블이 없어도 일관된 이미지 처리를 가능하게 한다.

---

### 6. 향후 재시도와 재처리 기능으로 확장할 수 있다

MVP에서는 단순히 실패 시 `FAILED` 상태로 저장한다.

향후에는 다음 기능으로 확장할 수 있다.

- 실패한 Photo 재처리
    
- Worker 재시도 횟수 관리
    
- 실패 사유 저장
    
- 오래된 PROCESSING 상태 정리
    
- 관리자용 실패 작업 조회
    
- 이미지 변환 옵션 변경 후 재생성
    

비동기 구조는 이러한 확장에 유리하다.

---

## Trade-off

### 장점

이미지 리사이징을 비동기로 처리하면 다음 장점이 있다.

- complete-upload API 응답 시간을 짧게 유지할 수 있다.
    
- API 서버와 이미지 처리 Worker의 책임을 분리할 수 있다.
    
- 이미지 처리 실패를 Photo 상태로 관리할 수 있다.
    
- 여러 장의 이미지 업로드에도 서버 안정성을 유지하기 쉽다.
    
- Worker 확장을 통해 처리량을 조절할 수 있다.
    
- 향후 재시도, 재처리, 실패 관리 기능으로 확장하기 좋다.
    
- Photo 공통 엔티티 구조와 잘 맞는다.
    

---

### 단점

이 결정으로 인해 다음 비용도 발생한다.

- Queue와 Worker 구성이 필요하다.
    
- 비동기 처리 상태를 관리해야 한다.
    
- 클라이언트가 PROCESSING 상태를 처리해야 한다.
    
- 사용자는 업로드 직후 변환 이미지가 바로 보이지 않을 수 있다.
    
- Worker 실패 시 FAILED 상태 처리와 재시도 정책이 필요하다.
    
- 로컬 개발 환경에서 API 서버와 Worker를 함께 실행해야 한다.
    
- 배포 환경에서 Worker 프로세스 운영 방식을 정의해야 한다.
    

---

### Trade-off 판단

이미지 리사이징을 동기 처리하면 구현은 단순해진다.

하지만 소로소로는 이미지 중심 서비스이고, Project와 DailyLog에 여러 장의 사진을 업로드할 수 있다.

이미지 변환을 API 요청 안에서 처리하면 사용자 응답 시간이 길어지고 서버 부하가 커질 수 있다.

따라서 초기 구조가 조금 복잡해지더라도, 이미지 업로드 완료와 이미지 변환을 분리하는 비동기 처리 방식이 더 적절하다.

MVP에서는 복잡한 재시도 정책까지 구현하지 않더라도, Photo 상태를 통해 처리 성공과 실패를 명확히 관리한다.

---

## Consequences

이미지 리사이징을 비동기로 처리함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. Photo 상태 전이가 명확해야 한다

Photo 상태 전이는 다음과 같다.

```text
UPLOADING
→ PROCESSING
→ READY
```

실패 시 다음 상태가 된다.

```text
PROCESSING
→ FAILED
```

각 상태의 의미는 다음과 같다.

|상태|의미|
|---|---|
|UPLOADING|Presigned URL 발급 후 원본 업로드 대기 또는 진행 중|
|PROCESSING|원본 업로드 완료 후 이미지 변환 작업 대기 또는 진행 중|
|READY|Medium과 Thumbnail 생성 완료|
|FAILED|이미지 처리 실패|

---

### 2. complete-upload API는 Queue 발행까지만 담당한다

complete-upload API는 이미지를 직접 변환하지 않는다.

담당하는 작업은 다음과 같다.

- Photo 조회
    
- 소유권 확인
    
- Photo 상태 확인
    
- S3 originalKey 존재 확인
    
- Photo status를 PROCESSING으로 변경
    
- Queue에 photoId 발행
    
- 응답 반환
    

---

### 3. Worker는 독립 실행 단위로 구성한다

Worker는 API 서버와 분리된 실행 단위로 구성할 수 있어야 한다.

MVP 배포에서는 Docker Compose 안에서 API 서버와 Worker를 별도 서비스로 실행할 수 있다.

예상 구조는 다음과 같다.

```text
docker-compose
 ├── app
 ├── worker
 └── postgres
```

구체적인 실행 구조는 `06_Architecture.md`에서 정의한다.

---

### 4. 클라이언트는 PROCESSING 상태를 표시해야 한다

업로드 완료 직후 Photo는 READY가 아닐 수 있다.

따라서 클라이언트는 다음 상태를 구분해야 한다.

- UPLOADING
    
- PROCESSING
    
- READY
    
- FAILED
    

PROCESSING 상태에서는 로딩 표시나 처리 중 메시지를 보여줄 수 있다.

---

### 5. 이미지 URL은 READY 상태에서 제공한다

Medium과 Thumbnail URL은 Photo가 READY 상태가 된 이후 제공한다.

READY가 아닌 상태에서는 다음 중 하나로 처리할 수 있다.

- URL을 null로 반환
    
- status만 반환
    
- 기본 placeholder 이미지 표시
    
- 처리 중 UI 표시
    

구체적인 응답 형식은 `07_API.md`에서 정의한다.

---

### 6. 실패 처리 정책이 필요하다

MVP에서는 이미지 처리 실패 시 Photo 상태를 FAILED로 변경한다.

향후에는 다음 정책을 추가할 수 있다.

- 실패 사유 저장
    
- 재처리 API
    
- 자동 재시도
    
- 오래된 PROCESSING 상태 정리
    
- 오래된 UPLOADING 상태 정리
    

MVP에서 어디까지 구현할지는 `05_Backend_Design.md`에서 정의한다.

---

### 7. 테스트 케이스가 필요하다

다음 테스트가 필요하다.

- complete-upload 호출 시 Photo 상태가 PROCESSING으로 변경된다.
    
- complete-upload 호출 시 Queue에 photoId가 발행된다.
    
- Worker 처리 성공 시 Photo 상태가 READY로 변경된다.
    
- Worker 처리 성공 시 mediumKey와 thumbnailKey가 저장된다.
    
- Worker 처리 실패 시 Photo 상태가 FAILED로 변경된다.
    
- READY 상태의 Photo만 이미지 URL을 제공한다.
    
- PROCESSING 상태의 Photo는 처리 중 상태로 조회된다.
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-007 Photo를 공통 이미지 엔티티로 설계한 이유|Worker는 공통 Photo 엔티티를 기준으로 처리|
|ADR-008 Presigned URL 기반 이미지 업로드를 선택한 이유|complete-upload 이후 비동기 이미지 처리|
|ADR-012 Docker Compose + EC2 + Nginx 배포를 선택한 이유|API 서버와 Worker 실행 구조 정의|

---

# ADR-009 요약

이미지 리사이징은 complete-upload API 안에서 동기 처리하지 않고, Queue와 Worker를 통해 비동기로 처리한다.

선택 이유는 다음과 같다.

- API 응답 시간을 짧게 유지할 수 있다.
    
- API 서버와 이미지 처리 책임을 분리할 수 있다.
    
- 이미지 처리 실패를 Photo 상태로 관리할 수 있다.
    
- 여러 장의 이미지 업로드에도 서버 안정성을 유지하기 쉽다.
    
- Worker는 photoId만 기준으로 처리할 수 있다.
    
- 향후 재시도, 재처리, 실패 관리 기능으로 확장하기 좋다.
    

이 결정으로 인해 Photo는 UPLOADING, PROCESSING, READY, FAILED 상태를 가지며, 클라이언트는 PROCESSING 상태를 처리할 수 있어야 한다.

---
# ADR-010. Calendar/Contribution을 저장하지 않고 조회 시 계산하는 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 사용자의 재봉 작업 기록을 날짜 단위로 시각화한다.

MVP에서 제공하는 대표적인 시각화 기능은 다음과 같다.

- 월별 Calendar
    
- 특정 날짜 작업 요약
    
- 연도별 Contribution, 즉 재봉 잔디
    

이 기능들은 DailyLog 데이터를 기반으로 한다.

Calendar와 Contribution에서 필요한 데이터는 다음과 같다.

|데이터|설명|
|---|---|
|workedDate|작업 날짜|
|dailyLogCount|해당 날짜의 PUBLISHED DailyLog 수|
|totalDurationMinutes|해당 날짜의 총 작업 시간|
|level|작업 시간 기반 표시 단계|
|activeDays|작업 기록이 있는 날짜 수|
|totalDailyLogCount|기간 내 PUBLISHED DailyLog 수|
|yearlyTotalDurationMinutes|연도 전체 작업 시간|

초기에는 Calendar 또는 Contribution 전용 테이블을 만들어 날짜별 집계 결과를 저장하는 방식도 고려할 수 있었다.

예를 들어 다음과 같은 구조이다.

```text
CalendarDailySummary
 ├── userId
 ├── date
 ├── dailyLogCount
 ├── totalDurationMinutes
 └── level
```

또는 Contribution 전용 테이블을 둘 수도 있다.

```text
Contribution
 ├── userId
 ├── date
 ├── totalDurationMinutes
 ├── dailyLogCount
 └── level
```

하지만 Calendar와 Contribution은 사용자가 직접 입력하는 원본 데이터가 아니다.

이 값들은 DailyLog의 상태, workedDate, durationMinutes를 기준으로 계산되는 파생 데이터이다.

특히 소로소로에서는 다음 규칙이 중요하다.

- DRAFT DailyLog는 통계에 포함하지 않는다.
    
- PUBLISHED DailyLog만 Calendar와 Contribution에 포함한다.
    
- PUBLISHED DailyLog는 수정 가능하다.
    
- PUBLISHED DailyLog의 workedDate가 수정될 수 있다.
    
- PUBLISHED DailyLog의 TimeEntry가 수정되면 durationMinutes가 변경된다.
    
- PUBLISHED DailyLog가 삭제되면 통계에서 제외되어야 한다.
    

따라서 Calendar와 Contribution을 별도 테이블에 저장하면 원본 DailyLog와 파생 데이터 간 불일치가 발생할 수 있다.

---

## Decision

Calendar와 Contribution은 별도 원본 테이블로 저장하지 않는다.

대신 조회 시점에 `PUBLISHED` 상태의 DailyLog를 기준으로 계산한다.

집계 기준은 다음과 같다.

```text
DailyLog.status = PUBLISHED
DailyLog.workedDate IS NOT NULL
DailyLog.durationMinutes IS NOT NULL
```

Calendar의 날짜별 총 작업 시간은 다음과 같이 계산한다.

```text
Calendar.totalDurationMinutes
= SUM(PUBLISHED DailyLog.durationMinutes)
GROUP BY DailyLog.workedDate
```

Contribution도 동일하게 DailyLog를 기준으로 계산한다.

```text
Contribution.dailyLevel
= calculateLevel(SUM(PUBLISHED DailyLog.durationMinutes))
```

Calendar Level과 Contribution Level은 원본 데이터로 저장하지 않고 조회 시 계산한다.

---

## Alternatives

### 1. Calendar/Contribution 전용 테이블에 저장

첫 번째 대안은 날짜별 집계 결과를 별도 테이블에 저장하는 방식이다.

예시는 다음과 같다.

```text
DailyWorkSummary
 ├── userId
 ├── date
 ├── dailyLogCount
 ├── totalDurationMinutes
 └── level
```

#### 장점

- 조회 성능이 빠르다.
    
- Calendar와 Contribution 조회 시 DailyLog를 매번 집계하지 않아도 된다.
    
- 데이터가 많아졌을 때 응답 시간을 줄이기 쉽다.
    
- 통계 화면이 많아질 경우 활용하기 좋다.
    

#### 단점

- DailyLog 생성, 수정, 삭제 시 집계 테이블을 함께 갱신해야 한다.
    
- PUBLISHED DailyLog의 workedDate 변경 시 이전 날짜와 새 날짜를 모두 갱신해야 한다.
    
- TimeEntry 변경으로 durationMinutes가 바뀌면 집계도 갱신해야 한다.
    
- DRAFT → PUBLISHED 발행 시 집계에 추가해야 한다.
    
- Project 삭제 또는 DailyLog 삭제 시 집계에서 제거해야 한다.
    
- 원본 DailyLog와 집계 테이블 간 불일치 가능성이 생긴다.
    
- MVP에 비해 구현 복잡도가 높다.
    

MVP에서는 사용자 데이터 규모가 크지 않고, 정합성이 더 중요하므로 전용 집계 테이블은 사용하지 않는다.

---

### 2. DailyLog에 통계 표시용 필드를 추가로 저장

두 번째 대안은 DailyLog에 통계 표시용 level 또는 contribution 관련 필드를 추가하는 방식이다.

예시는 다음과 같다.

```text
DailyLog
 ├── durationMinutes
 ├── calendarLevel
 └── contributionLevel
```

#### 장점

- 별도 집계 테이블보다 구조가 단순해 보일 수 있다.
    
- DailyLog 조회 시 일부 표시값을 바로 사용할 수 있다.
    

#### 단점

- level은 DailyLog 하나의 값이 아니라 날짜별 합산 결과로 결정된다.
    
- 같은 날짜에 DailyLog가 여러 개 있으면 개별 DailyLog에 level을 저장할 수 없다.
    
- 날짜별 총합이 바뀌면 같은 날짜의 모든 DailyLog 표시값이 영향을 받는다.
    
- Calendar와 Contribution의 집계 의미를 DailyLog 단일 레코드에 저장하는 것은 부자연스럽다.
    

Calendar Level과 Contribution Level은 날짜 단위 파생 값이므로 DailyLog에 직접 저장하지 않는다.

---

### 3. 조회 시 DailyLog를 집계

세 번째 대안은 Calendar와 Contribution 조회 시 DailyLog를 기준으로 계산하는 방식이다.

#### 장점

- 원본 데이터와 파생 데이터 간 불일치가 없다.
    
- DRAFT/PUBLISHED 상태 기준을 명확히 적용할 수 있다.
    
- DailyLog 수정, 삭제, 발행 시 별도 집계 테이블 갱신이 필요 없다.
    
- 구현 복잡도가 낮다.
    
- MVP 데이터 규모에 적합하다.
    
- 테스트 기준이 명확하다.
    
- 통계 기준이 바뀌어도 조회 로직만 수정하면 된다.
    

#### 단점

- 데이터가 많아지면 조회 성능이 느려질 수 있다.
    
- 월별, 연도별 집계 쿼리를 최적화해야 한다.
    
- 인덱스 설계가 필요하다.
    
- 향후 통계 기능이 많아지면 캐시나 집계 테이블이 필요할 수 있다.
    

소로소로 MVP에서는 데이터 규모보다 정합성과 구현 단순성이 더 중요하므로 조회 시 계산 방식을 선택한다.

---

## Rationale

Calendar와 Contribution을 저장하지 않고 조회 시 계산하는 이유는 다음과 같다.

---

### 1. Calendar와 Contribution은 원본 데이터가 아니라 파생 데이터이다

사용자가 직접 작성하는 원본 데이터는 DailyLog이다.

Calendar와 Contribution은 DailyLog를 날짜 기준으로 모아 보여주는 결과이다.

즉, 다음 관계이다.

```text
원본 데이터: DailyLog
파생 데이터: Calendar, Contribution
```

따라서 Calendar와 Contribution을 별도 원본 데이터처럼 저장하면 중복 저장이 발생한다.

MVP에서는 원본 DailyLog를 신뢰하고, 필요한 화면에서 계산하는 방식이 더 적절하다.

---

### 2. DRAFT/PUBLISHED 상태 기준을 일관되게 적용할 수 있다

소로소로의 통계 기준은 명확하다.

```text
PUBLISHED DailyLog만 통계에 포함한다.
DRAFT DailyLog는 통계에 포함하지 않는다.
```

조회 시 계산하면 이 조건을 쿼리에서 직접 적용할 수 있다.

```text
WHERE status = PUBLISHED
```

반면 집계 테이블을 따로 두면 DRAFT가 PUBLISHED로 발행될 때, PUBLISHED가 수정될 때, 삭제될 때마다 집계 테이블을 정확히 갱신해야 한다.

이 과정에서 누락이 발생하면 통계가 원본과 불일치할 수 있다.

---

### 3. PUBLISHED DailyLog 수정에 대응하기 쉽다

소로소로에서는 PUBLISHED DailyLog도 수정 가능하다.

수정 가능한 항목에는 다음이 포함된다.

- workedDate
    
- timeEntries
    
- memo
    
- workTypes
    
- photos
    

이 중 Calendar와 Contribution에 영향을 주는 값은 다음이다.

- workedDate
    
- durationMinutes
    

예를 들어 사용자가 작업 날짜를 7월 1일에서 7월 2일로 수정하면, 통계 기준 날짜가 바뀐다.

집계 테이블을 사용하면 다음 처리가 필요하다.

```text
기존 날짜 7월 1일 집계 차감
새 날짜 7월 2일 집계 증가
```

하지만 조회 시 계산 방식에서는 DailyLog의 현재 값만 기준으로 집계하면 된다.

따라서 수정 로직이 단순해지고 정합성 위험이 줄어든다.

---

### 4. TimeEntry 변경에 따른 durationMinutes 재계산과 잘 맞는다

DailyLog.durationMinutes는 DailyLogTimeEntry의 합산값이다.

사용자가 PUBLISHED DailyLog의 TimeEntry를 수정하면 durationMinutes가 변경된다.

집계 테이블이 있다면 durationMinutes 변경 전후 차이를 계산해 집계 테이블에 반영해야 한다.

반면 조회 시 계산하면 DailyLog.durationMinutes의 현재 값만 집계하면 된다.

이 구조는 ADR-006의 TimeEntry 합산 방식과 잘 맞는다.

---

### 5. MVP 데이터 규모에서는 조회 시 계산이 충분하다

소로소로는 1인 사용자 중심으로 시작하는 개인 기록 서비스이다.

MVP 단계에서 한 사용자가 생성할 데이터는 다음 수준으로 예상할 수 있다.

- Project 수십 개
    
- DailyLog 수백 개
    
- Fabric 수십~수백 개
    
- Photo 수백 장
    

이 정도 규모에서는 DailyLog에 적절한 인덱스를 두고 월별·연도별 집계를 수행해도 충분하다.

특히 Calendar는 월 단위, Contribution은 연도 단위로 조회 범위가 제한된다.

---

### 6. 구현 복잡도를 줄일 수 있다

집계 테이블을 두면 다음 이벤트마다 동기화 로직이 필요하다.

- DailyLog 생성
    
- DRAFT DailyLog 발행
    
- PUBLISHED DailyLog 수정
    
- PUBLISHED DailyLog 삭제
    
- Project 삭제
    
- TimeEntry 수정
    
- workedDate 변경
    

MVP에서는 이 복잡도를 감수하기보다, 조회 시 계산 방식으로 단순하게 시작하는 것이 적절하다.

향후 데이터가 많아져 성능 문제가 발생하면 캐시나 집계 테이블을 도입할 수 있다.

---

### 7. 테스트 기준이 명확하다

조회 시 계산 방식에서는 테스트 기준이 단순하다.

다음만 검증하면 된다.

- DRAFT DailyLog는 Calendar에 포함되지 않는다.
    
- PUBLISHED DailyLog는 Calendar에 포함된다.
    
- DRAFT DailyLog는 Contribution에 포함되지 않는다.
    
- PUBLISHED DailyLog는 Contribution에 포함된다.
    
- 같은 날짜의 PUBLISHED DailyLog는 합산된다.
    
- workedDate가 바뀌면 집계 날짜도 바뀐다.
    
- durationMinutes가 바뀌면 집계 시간도 바뀐다.
    

원본 DailyLog만 기준으로 테스트할 수 있으므로, 별도 집계 테이블 동기화 테스트가 필요하지 않다.

---

## Trade-off

### 장점

Calendar와 Contribution을 조회 시 계산하면 다음 장점이 있다.

- 원본 DailyLog와 파생 데이터 간 불일치가 발생하지 않는다.
    
- DRAFT/PUBLISHED 상태 기준을 일관되게 적용할 수 있다.
    
- PUBLISHED DailyLog 수정에 쉽게 대응할 수 있다.
    
- TimeEntry 변경으로 인한 durationMinutes 변경을 자연스럽게 반영할 수 있다.
    
- 별도 집계 테이블 갱신 로직이 필요 없다.
    
- MVP 구현 복잡도가 낮다.
    
- 통계 기준 변경 시 조회 로직만 수정하면 된다.
    
- 테스트 기준이 명확하다.
    

---

### 단점

이 결정으로 인해 다음 비용도 발생한다.

- 조회 시마다 집계 쿼리를 수행해야 한다.
    
- 데이터가 많아지면 Calendar와 Contribution 조회가 느려질 수 있다.
    
- 적절한 인덱스 설계가 필요하다.
    
- 복잡한 통계 대시보드로 확장할 경우 성능 문제가 생길 수 있다.
    
- 향후 캐시 또는 집계 테이블 도입이 필요할 수 있다.
    

---

### Trade-off 판단

Calendar와 Contribution을 저장하면 조회 성능은 좋아질 수 있다.

하지만 MVP 단계에서는 데이터 규모가 작고, PUBLISHED DailyLog 수정 가능성 때문에 집계 데이터 동기화 복잡도가 더 큰 문제가 된다.

따라서 초기에는 조회 시 계산 방식을 선택한다.

향후 데이터 규모가 커지거나 통계 기능이 고도화되면, 다음 방식으로 확장할 수 있다.

- 날짜별 집계 캐시
    
- DailyWorkSummary 테이블
    
- 배치 기반 통계 갱신
    
- Redis 캐시
    
- Materialized View
    

현재는 정합성과 단순성을 우선한다.

---

## Consequences

Calendar와 Contribution을 조회 시 계산하기로 결정함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. Calendar 전용 원본 테이블을 만들지 않는다

MVP에서는 다음과 같은 테이블을 만들지 않는다.

```text
CalendarDailySummary
Contribution
DailyWorkSummary
```

Calendar와 Contribution은 DailyLog 조회 결과를 기반으로 계산한다.

---

### 2. DailyLog 조회 조건이 중요해진다

Calendar와 Contribution은 다음 조건을 반드시 사용한다.

```text
userId = currentUser.id
status = PUBLISHED
workedDate between startDate and endDate
```

DRAFT DailyLog는 집계 대상이 아니다.

---

### 3. DailyLog.durationMinutes 정합성이 중요하다

Calendar와 Contribution은 DailyLog.durationMinutes를 기준으로 집계한다.

따라서 DailyLog.durationMinutes는 TimeEntry 합산값과 항상 일치해야 한다.

DailyLog 수정 시 TimeEntry가 변경되면 durationMinutes를 다시 계산해야 한다.

---

### 4. 인덱스 설계가 필요하다

Calendar와 Contribution 성능을 위해 다음 인덱스를 고려한다.

```text
DailyLog(userId, status, workedDate)
DailyLog(projectId, status, workedDate)
```

구체적인 인덱스는 `04_ERD.md`에서 정의한다.

---

### 5. Calendar Level과 Contribution Level은 저장하지 않는다

Level은 날짜별 총 작업 시간을 기준으로 계산한다.

기준은 다음과 같다.

|Level|기준|
|---|---|
|0|0분|
|1|1분 이상 30분 이하|
|2|31분 이상 120분 이하|
|3|121분 이상 240분 이하|
|4|241분 이상|

Level은 원본 데이터로 저장하지 않고 API 응답에서 계산된 값으로 반환한다.

---

### 6. 향후 성능 문제가 생기면 캐시 또는 집계 테이블을 도입한다

MVP 이후 데이터가 많아지거나 통계 기능이 고도화되면 다음 확장을 검토한다.

|확장 방식|설명|
|---|---|
|Redis Cache|자주 조회하는 Calendar/Contribution 결과 캐싱|
|DailyWorkSummary|날짜별 작업 시간 집계 테이블|
|Materialized View|PostgreSQL 기반 집계 뷰|
|Batch Aggregation|야간 또는 주기적 집계 갱신|
|Event-driven Update|DailyLog 변경 이벤트 기반 집계 갱신|

이 확장은 MVP 이후 성능 병목이 확인되었을 때 적용한다.

---

### 7. 테스트 케이스가 필요하다

다음 테스트가 필요하다.

- DRAFT DailyLog는 Calendar에 포함되지 않는다.
    
- PUBLISHED DailyLog는 Calendar에 포함된다.
    
- DRAFT DailyLog는 Contribution에 포함되지 않는다.
    
- PUBLISHED DailyLog는 Contribution에 포함된다.
    
- 같은 날짜의 여러 PUBLISHED DailyLog는 합산된다.
    
- workedDate가 변경되면 Calendar 집계 날짜가 변경된다.
    
- durationMinutes가 변경되면 Calendar 집계 시간이 변경된다.
    
- 다른 사용자의 DailyLog는 집계에 포함되지 않는다.
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-002 Database로 PostgreSQL을 선택한 이유|날짜 기반 집계 조회와 인덱스 설계|
|ADR-005 DailyLog에 DRAFT/PUBLISHED 상태를 둔 이유|PUBLISHED DailyLog만 집계|
|ADR-006 작업 시간을 DailyLogTimeEntry 합산으로 계산한 이유|DailyLog.durationMinutes 기준 집계|

---

# ADR-010 요약

Calendar와 Contribution은 별도 테이블에 저장하지 않고, 조회 시점에 PUBLISHED DailyLog를 기준으로 계산한다.

선택 이유는 다음과 같다.

- Calendar와 Contribution은 원본 데이터가 아니라 DailyLog 기반 파생 데이터이다.
    
- DRAFT DailyLog는 통계에 포함하지 않아야 한다.
    
- PUBLISHED DailyLog만 통계에 포함해야 한다.
    
- PUBLISHED DailyLog는 수정 가능하므로 별도 집계 테이블을 두면 동기화 복잡도가 커진다.
    
- TimeEntry 변경에 따른 durationMinutes 변경을 자연스럽게 반영할 수 있다.
    
- MVP 데이터 규모에서는 조회 시 계산으로 충분하다.
    
- 정합성과 구현 단순성을 우선할 수 있다.
    

향후 데이터 규모가 커지거나 통계 기능이 고도화되면 캐시, 집계 테이블, Materialized View, 배치 집계 등을 검토한다.

---

# ADR-011. MVP에서 AI 원단 자동 기록을 제외한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)의 Future Scope에는 AI 기반 원단 정보 자동 기록 기능이 있다.

이 기능의 목표는 사용자가 원단 정보를 직접 입력하는 부담을 줄이는 것이다.

사용자는 원단 쇼핑몰 상품 URL 또는 주문 내역 캡처 이미지를 입력하고, 시스템은 해당 정보에서 원단 데이터를 자동으로 추출한다.

예상 흐름은 다음과 같다.

```text
상품 URL 또는 주문 내역 캡처 입력
→ OCR / Vision LLM / DOM Extraction
→ 원단 정보 후보 추출
→ 사용자 검수
→ Fabric 생성
```

자동 추출 대상 정보는 다음과 같다.

|항목|설명|
|---|---|
|productName|쇼핑몰 상품명|
|productCode|품번|
|productUrl|상품 URL|
|storeName|구매처|
|purchasePrice|구매 가격|
|color|색상 또는 옵션|
|size|구매 규격|
|width|원단폭|
|materialComposition|혼용률 또는 소재|
|rawText|원본 추출 텍스트|
|confidence|추출 신뢰도|

이 기능은 소로소로의 차별화 포인트가 될 수 있다.

하지만 MVP에 포함하기에는 다음 문제가 있다.

- 쇼핑몰마다 페이지 구조가 다르다.
    
- 상품명, 옵션, 원단폭, 혼용률 표기 방식이 일정하지 않다.
    
- 주문 내역 캡처 이미지는 레이아웃과 해상도가 다양하다.
    
- OCR 또는 Vision LLM 결과가 항상 정확하지 않다.
    
- 추출 결과 검수 UI가 필요하다.
    
- 잘못 추출된 값이 Fabric 데이터로 저장될 위험이 있다.
    
- AI 기능이 들어가면 MVP의 핵심 범위가 커진다.
    

소로소로의 MVP에서 우선 검증해야 하는 것은 AI 자동화가 아니라, 개인 재봉 기록 서비스의 핵심 구조이다.

즉, MVP의 핵심은 다음이다.

- Project를 만들 수 있는가
    
- DailyLog로 제작 과정을 기록할 수 있는가
    
- Fabric을 독립적으로 관리할 수 있는가
    
- Project와 Fabric을 연결할 수 있는가
    
- Photo를 통해 이미지 중심 기록이 가능한가
    
- Calendar와 Contribution으로 작업 습관을 볼 수 있는가
    

따라서 AI 기반 원단 자동 기록은 MVP에서 제외하고, 향후 확장 기능으로 둔다.

---

## Decision

AI 기반 원단 정보 자동 기록 기능은 MVP에 포함하지 않는다.

MVP에서는 Fabric 정보를 사용자가 직접 입력한다.

MVP Fabric 등록 기능은 다음 정보를 입력할 수 있도록 한다.

|항목|설명|
|---|---|
|name|사용자가 부르는 원단명|
|productName|쇼핑몰 상품명|
|productCode|품번|
|productUrl|상품 URL|
|storeName|구매처|
|purchasedAt|구매일|
|purchasePrice|구매 가격|
|color|색상|
|size|원단 규격|
|width|원단폭|
|materialComposition|혼용률 또는 재질|
|memo|원단 메모|
|rating|사용자 평점|
|repurchaseIntention|재구매 의사|

AI 자동 기록은 MVP 이후 `Future Scope`로 관리한다.

향후 도입 시에는 AI 결과를 바로 Fabric으로 저장하지 않고, 별도 추출 작업으로 관리한 뒤 사용자가 검수한 결과만 Fabric으로 저장한다.

예상 확장 구조는 다음과 같다.

```text
FabricExtractionJob
→ ExtractedFabricItem
→ 사용자 검수
→ Fabric 생성
```

---

## Alternatives

### 1. AI 원단 자동 기록을 MVP 핵심 기능으로 포함

첫 번째 대안은 AI 기반 원단 자동 기록을 MVP의 핵심 기능으로 포함하는 방식이다.

#### 장점

- 서비스 차별화 포인트가 강해진다.
    
- 사용자의 입력 부담을 크게 줄일 수 있다.
    
- 포트폴리오에서 AI 활용 경험을 강조할 수 있다.
    
- 원단 기록 서비스의 매력이 커질 수 있다.
    

#### 단점

- MVP 구현 범위가 크게 증가한다.
    
- OCR, Vision LLM, DOM Parsing 중 어떤 방식을 선택할지 검증이 필요하다.
    
- 쇼핑몰마다 데이터 구조가 달라 안정적인 추출이 어렵다.
    
- 추출 정확도가 낮으면 사용자 신뢰도가 떨어진다.
    
- AI 결과 검수 UI가 필요하다.
    
- 잘못 추출된 데이터가 Fabric에 저장될 수 있다.
    
- AI 기능에 집중하느라 핵심 기록 도메인 구현이 늦어질 수 있다.
    
- 로컬 LLM 또는 외부 API 사용 여부에 따라 운영 구조가 복잡해진다.
    

AI 자동 기록은 매력적인 기능이지만, MVP의 핵심 기능으로 넣기에는 위험과 범위가 크다.

---

### 2. URL 기반 DOM Parsing만 MVP에 포함

두 번째 대안은 AI를 사용하지 않고, 상품 URL을 입력하면 HTML에서 원단 정보를 추출하는 방식이다.

#### 장점

- LLM 비용이나 OCR 정확도 문제를 줄일 수 있다.
    
- 텍스트 기반 추출이라 이미지 OCR보다 빠를 수 있다.
    
- 일부 쇼핑몰에서는 정확도가 높을 수 있다.
    
- AI 기능보다 구현 범위가 작아 보일 수 있다.
    

#### 단점

- 쇼핑몰마다 DOM 구조가 다르다.
    
- 동적 렌더링 페이지는 Playwright 같은 브라우저 자동화가 필요할 수 있다.
    
- 사이트 구조가 바뀌면 파서가 깨진다.
    
- 크롤링 정책과 접근 제한을 고려해야 한다.
    
- 상품 옵션 정보는 DOM에서 일관되게 추출하기 어렵다.
    
- 지원 쇼핑몰 범위를 계속 관리해야 한다.
    

이 방식은 특정 쇼핑몰에 최적화된 자동화에는 가능하지만, MVP 범위에서는 여전히 부담이 크다.

---

### 3. 주문 내역 캡처 OCR만 MVP에 포함

세 번째 대안은 주문 내역 캡처 이미지를 업로드하고 OCR로 텍스트를 추출하는 방식이다.

#### 장점

- 사용자가 상품 URL을 찾지 않아도 된다.
    
- 주문 내역 캡처만으로 원단 기록을 시작할 수 있다.
    
- 쇼핑몰 로그인 페이지나 동적 페이지 문제를 피할 수 있다.
    

#### 단점

- 이미지 해상도와 캡처 방식에 따라 인식률이 달라진다.
    
- OCR 결과에서 어떤 텍스트가 상품명, 가격, 옵션인지 분류해야 한다.
    
- 여러 상품이 한 이미지에 포함될 수 있다.
    
- 개인정보나 주문번호 같은 민감 정보가 포함될 수 있다.
    
- 검수 UI가 필요하다.
    
- 잘못 인식된 데이터가 Fabric에 저장될 위험이 있다.
    

OCR 방식도 매력적이지만, MVP에서는 안정적으로 완성하기 어렵다.

---

### 4. MVP에서는 수동 입력만 제공하고 AI는 Future Scope로 분리

네 번째 대안은 MVP에서는 Fabric 수동 입력만 제공하고, AI 자동 기록은 이후 확장으로 두는 방식이다.

#### 장점

- MVP 범위를 명확하게 유지할 수 있다.
    
- 핵심 도메인 구조를 먼저 검증할 수 있다.
    
- Fabric 데이터 모델을 안정적으로 설계할 수 있다.
    
- AI 정확도 문제로 인한 사용자 신뢰도 저하를 피할 수 있다.
    
- 향후 AI 기능을 Fabric 생성 전 단계로 자연스럽게 확장할 수 있다.
    
- 포트폴리오에서 MVP 범위 조절 능력을 설명할 수 있다.
    

#### 단점

- 초기 서비스 차별화 포인트가 약해질 수 있다.
    
- 사용자가 원단 정보를 직접 입력해야 한다.
    
- “한 번 클릭으로 기록”이라는 이상적인 사용 경험은 MVP에서 제공하지 못한다.
    
- AI 활용 경험을 즉시 보여주기 어렵다.
    

소로소로 MVP에서는 핵심 기록 구조를 먼저 완성하는 것이 더 중요하므로 이 방식을 선택한다.

---

## Rationale

MVP에서 AI 원단 자동 기록을 제외한 이유는 다음과 같다.

---

### 1. MVP의 핵심은 AI가 아니라 기록 구조 검증이다

소로소로는 AI 도구가 아니라 개인 재봉 기록 서비스이다.

MVP에서 가장 먼저 검증해야 하는 것은 다음이다.

- 사용자가 Project를 만들고 관리할 수 있는가
    
- 사용자가 DailyLog를 작성하고 제작 과정을 남길 수 있는가
    
- 사용자가 Fabric을 독립적으로 관리할 수 있는가
    
- 사용자가 Project와 Fabric을 연결할 수 있는가
    
- 사용자가 Photo로 과정을 기록할 수 있는가
    
- Calendar와 Contribution으로 작업 습관을 확인할 수 있는가
    

AI 자동 기록은 Fabric 입력을 편하게 만드는 보조 기능이다.

핵심 기록 구조가 완성되지 않은 상태에서 AI 기능을 먼저 넣으면, 프로젝트의 중심이 흐려질 수 있다.

---

### 2. AI 추출 정확도를 보장하기 어렵다

원단 정보는 표기 방식이 다양하다.

예시는 다음과 같다.

```text
면 100%
cotton 100
20수 면 원단
폭 110cm
1마 기준
1/2마 단위 판매
옵션: 딸기 핑크
품번: F-2031
```

쇼핑몰마다 상품명, 옵션명, 원단폭, 혼용률, 가격, 품번을 표현하는 방식이 다르다.

LLM이나 OCR이 이 정보를 항상 정확하게 분리한다고 보장하기 어렵다.

잘못 추출된 값이 Fabric에 바로 저장되면, 사용자는 나중에 원단 기록을 신뢰하기 어려워진다.

---

### 3. 사용자 검수 UI가 필요하다

AI 추출 결과는 반드시 사용자 검수가 필요하다.

따라서 AI 기능을 넣으려면 단순 API만 필요한 것이 아니라 다음 UI가 필요하다.

- 원본 입력 화면
    
- 추출 진행 상태 화면
    
- 추출 결과 후보 목록
    
- 필드별 수정 화면
    
- 여러 원단이 추출된 경우 선택 화면
    
- 최종 Fabric 저장 확인 화면
    
- 추출 실패 처리 화면
    

이 UI는 MVP 범위를 크게 늘린다.

MVP에서는 사용자가 직접 Fabric 정보를 입력하는 방식으로 시작하고, AI 검수 UI는 이후 확장으로 둔다.

---

### 4. AI 기능은 별도 도메인으로 분리하는 것이 적절하다

AI 자동 기록은 단순히 Fabric 생성 API 안에 넣을 기능이 아니다.

AI 추출 과정은 다음과 같은 상태를 가질 수 있다.

- 요청됨
    
- 처리 중
    
- 성공
    
- 부분 성공
    
- 실패
    
- 사용자 검수 완료
    
- Fabric 생성 완료
    

따라서 향후 AI 기능은 Fabric과 분리된 별도 도메인으로 관리하는 것이 적절하다.

예상 도메인은 다음과 같다.

```text
FabricExtractionJob
ExtractedFabricItem
```

이 구조를 두면 AI 추출 결과와 실제 Fabric 데이터를 분리할 수 있다.

MVP에서는 이 도메인을 만들지 않고, Fabric 수동 입력만 제공한다.

---

### 5. 비용과 운영 복잡도를 줄일 수 있다

AI 기능은 선택하는 방식에 따라 비용과 운영 복잡도가 달라진다.

예를 들어 다음 선택지가 있다.

- 외부 LLM API 사용
    
- 로컬 LLM 사용
    
- OCR API 사용
    
- 브라우저 자동화 + DOM Parsing
    
- Vision LLM 사용
    

각 방식에는 다음 부담이 있다.

- API 비용
    
- 처리 속도
    
- 정확도 검증
    
- 개인정보 처리
    
- 실패 처리
    
- 재시도 정책
    
- 모델 변경 대응
    
- 인프라 리소스
    

MVP에서는 이러한 운영 부담을 피하고, 기본 기록 기능을 안정적으로 완성하는 것이 더 적절하다.

---

### 6. 현재 Fabric 데이터 모델만으로도 향후 AI 확장이 가능하다

MVP에서 AI를 제외하더라도, Fabric 데이터 모델은 AI 확장을 고려해 설계되어 있다.

Fabric은 다음 필드를 가진다.

- productName
    
- productCode
    
- productUrl
    
- storeName
    
- purchasePrice
    
- color
    
- size
    
- width
    
- materialComposition
    

이 필드들은 향후 AI가 추출할 주요 대상과 일치한다.

따라서 MVP에서 수동 입력으로 시작하더라도, 이후 AI 추출 결과를 사용자 검수 후 Fabric으로 저장하는 구조로 확장할 수 있다.

---

### 7. 포트폴리오에서 범위 조절 능력을 보여줄 수 있다

포트폴리오에서 중요한 것은 많은 기능을 무리하게 넣는 것이 아니다.

오히려 다음을 설명할 수 있어야 한다.

- MVP에서 무엇을 우선 구현했는가
    
- 무엇을 제외했는가
    
- 왜 제외했는가
    
- 제외한 기능을 나중에 어떻게 확장할 수 있는가
    

AI 원단 자동 기록을 Future Scope로 분리하면, 기능 욕심보다 제품 범위와 기술 리스크를 관리했다는 점을 보여줄 수 있다.

---

## Trade-off

### 장점

AI 원단 자동 기록을 MVP에서 제외하면 다음 장점이 있다.

- MVP 범위를 명확하게 유지할 수 있다.
    
- 핵심 기록 도메인 구현에 집중할 수 있다.
    
- AI 추출 정확도 문제를 피할 수 있다.
    
- 사용자 검수 UI 구현 부담을 줄일 수 있다.
    
- 외부 AI API 비용이나 로컬 모델 운영 부담을 줄일 수 있다.
    
- Fabric 데이터 모델을 먼저 안정화할 수 있다.
    
- 향후 AI 기능을 별도 도메인으로 더 안전하게 확장할 수 있다.
    
- 포트폴리오에서 MVP 범위 관리와 확장 설계를 설명하기 좋다.
    

---

### 단점

이 결정으로 인해 다음 아쉬움도 있다.

- 초기 서비스 차별화 요소가 줄어든다.
    
- 사용자가 원단 정보를 직접 입력해야 한다.
    
- 원단 기록 자동화라는 핵심 아이디어가 MVP에서 드러나지 않을 수 있다.
    
- AI 활용 경험을 바로 보여주기 어렵다.
    
- 입력 편의성이 낮아질 수 있다.
    

---

### Trade-off 판단

AI 자동 기록은 소로소로의 중요한 확장 방향이다.

하지만 MVP에서는 기록 구조를 먼저 완성하는 것이 우선이다.

AI 기능을 무리하게 포함하면 구현 범위가 커지고, 정확도와 검수 UI 문제로 인해 전체 프로젝트 완성도가 떨어질 수 있다.

따라서 MVP에서는 Fabric 수동 입력을 제공하고, AI 기반 원단 자동 기록은 Future Scope로 분리한다.

---

## Consequences

AI 원단 자동 기록을 MVP에서 제외함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. MVP Fabric 등록은 수동 입력을 기준으로 한다

MVP에서 사용자는 Fabric 정보를 직접 입력한다.

최소 필수값은 다음이다.

```text
name
```

나머지 정보는 선택 입력이다.

---

### 2. Fabric 데이터 모델은 AI 확장을 고려해 유지한다

MVP에서도 향후 AI 추출 결과를 받을 수 있도록 다음 필드를 Fabric에 둔다.

- productName
    
- productCode
    
- productUrl
    
- storeName
    
- purchasePrice
    
- color
    
- size
    
- width
    
- materialComposition
    

다만 MVP에서는 `extractedAttributes` 같은 AI 전용 JSONB 필드는 두지 않는다.

---

### 3. AI 관련 엔티티는 MVP에서 만들지 않는다

MVP에서는 다음 엔티티를 만들지 않는다.

```text
FabricExtractionJob
ExtractedFabricItem
AIExtractionResult
```

이 엔티티들은 AI 기능을 실제로 도입할 때 ADR과 ERD에서 별도로 정의한다.

---

### 4. AI 추출 결과는 향후 사용자 검수 후 저장한다

향후 AI 기능을 도입하더라도, 추출 결과를 바로 Fabric으로 저장하지 않는다.

반드시 다음 흐름을 따른다.

```text
AI 추출
→ 사용자 검수
→ 사용자 수정
→ Fabric 생성
```

이는 잘못된 원단 정보가 바로 저장되는 것을 막기 위한 정책이다.

---

### 5. README와 포트폴리오에서는 Future Scope로 설명한다

AI 원단 자동 기록은 MVP 구현 기능이 아니라 향후 확장 방향으로 설명한다.

포트폴리오에서는 다음처럼 설명할 수 있다.

```text
MVP에서는 재봉 기록과 원단 관리 도메인 구조를 먼저 검증하기 위해 AI 자동 기록 기능은 제외했습니다. 대신 Fabric 데이터 모델을 상품명, 품번, 구매처, 가격, 소재, 규격 등 AI 추출 결과를 수용할 수 있는 구조로 설계하여, 향후 FabricExtractionJob 기반으로 확장할 수 있도록 했습니다.
```

---

### 6. 향후 별도 ADR이 필요하다

AI 자동 기록 기능을 실제로 도입할 때는 별도 ADR을 작성한다.

예상 ADR은 다음과 같다.

- AI 추출 방식으로 OCR, DOM Parsing, Vision LLM 중 무엇을 선택할 것인가
    
- 외부 LLM API와 로컬 LLM 중 무엇을 사용할 것인가
    
- FabricExtractionJob 상태를 어떻게 관리할 것인가
    
- AI 추출 결과를 JSONB로 저장할 것인가 별도 테이블로 저장할 것인가
    
- 사용자 검수 UI를 어떤 흐름으로 설계할 것인가
    

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-002 Database로 PostgreSQL을 선택한 이유|향후 JSONB 또는 Extraction Job 확장 가능|
|ADR-003 Fabric을 Project와 독립 도메인으로 분리한 이유|AI 추출 결과의 최종 저장 대상은 Fabric|
|ADR-004 Project-Fabric 관계를 다대다 + 연결 엔티티로 설계한 이유|AI로 등록한 Fabric도 이후 Project에 연결 가능|
|ADR-012 Docker Compose + EC2 + Nginx 배포를 선택한 이유|MVP에서는 AI 처리 인프라를 배포 범위에 포함하지 않음|

---

# ADR-011 요약

AI 기반 원단 정보 자동 기록 기능은 MVP에서 제외한다.

선택 이유는 다음과 같다.

- MVP의 핵심은 AI가 아니라 Project, DailyLog, Fabric, Photo 기록 구조 검증이다.
    
- 쇼핑몰 URL, 주문 내역 캡처, OCR, Vision LLM의 정확도 검증이 필요하다.
    
- AI 추출 결과는 틀릴 수 있으므로 사용자 검수 UI가 필요하다.
    
- AI 기능을 MVP에 포함하면 구현 범위와 운영 복잡도가 커진다.
    
- Fabric 데이터 모델만으로도 향후 AI 추출 결과를 수용할 수 있다.
    
- 기능 제외와 향후 확장 계획을 명확히 설명할 수 있다.
    

MVP에서는 Fabric 수동 입력을 제공하고, AI 자동 기록은 Future Scope로 관리한다.

---
# ADR-012. Docker Compose + EC2 + Nginx 배포를 선택한 이유

## Status

Accepted

---

## Context

소로소로(SOROSORO)는 1인 개발로 진행하는 개인 재봉 기록 서비스이다.

MVP에서 필요한 주요 서버 구성 요소는 다음과 같다.

- Spring Boot API Server
    
- PostgreSQL Database
    
- Image Processing Worker
    
- Queue
    
- Nginx Reverse Proxy
    
- S3
    
- CloudFront
    

소로소로는 단순 로컬 실행 프로젝트가 아니라, 실제 사용 가능한 웹 서비스 형태로 배포하는 것을 목표로 한다.

하지만 MVP 단계에서 지나치게 복잡한 인프라를 도입하면 다음 문제가 생긴다.

- 구현보다 인프라 설정에 시간이 과도하게 소요된다.
    
- 운영 비용이 증가한다.
    
- 장애 원인 파악이 어려워진다.
    
- 1인 개발자가 관리하기 어려운 구조가 된다.
    
- MVP 기능 검증보다 배포 구조 관리가 더 커질 수 있다.
    

따라서 소로소로의 배포 구조는 다음 기준을 만족해야 한다.

- 실제 서버에 배포 가능해야 한다.
    
- Docker 기반으로 실행 환경을 재현할 수 있어야 한다.
    
- API Server와 Worker를 분리 실행할 수 있어야 한다.
    
- Nginx를 통해 외부 요청을 애플리케이션으로 전달할 수 있어야 한다.
    
- GitHub Actions를 통한 배포 자동화가 가능해야 한다.
    
- 1인 개발자가 이해하고 운영할 수 있을 만큼 단순해야 한다.
    
- 향후 AWS ECS, RDS, SQS 등으로 확장할 수 있는 여지를 남겨야 한다.
    

---

## Decision

소로소로 MVP 배포 구조로 **Docker Compose + EC2 + Nginx**를 선택한다.

기본 구성은 다음과 같다.

```text
Client
→ Nginx
→ Spring Boot API Server

Spring Boot API Server
→ PostgreSQL
→ Queue
→ S3

Worker
→ Queue
→ S3
→ PostgreSQL
```

MVP 배포 환경은 다음을 기준으로 한다.

|영역|선택|
|---|---|
|Server|AWS EC2|
|Reverse Proxy|Nginx|
|Container|Docker|
|Composition|Docker Compose|
|Application|Spring Boot API Server|
|Worker|Image Processing Worker|
|Database|PostgreSQL|
|Storage|AWS S3|
|CDN|CloudFront|
|CI/CD|GitHub Actions|

Docker Compose는 API Server, Worker, PostgreSQL, Queue를 하나의 서버 안에서 실행·관리하기 위한 도구로 사용한다.

Nginx는 외부 HTTP/HTTPS 요청을 Spring Boot API Server로 전달하는 Reverse Proxy 역할을 한다.

---

## Alternatives

### 1. 로컬 실행만 제공

첫 번째 대안은 배포 없이 로컬 환경에서만 실행하는 방식이다.

#### 장점

- 배포 설정이 필요 없다.
    
- 구현에만 집중할 수 있다.
    
- 초기 개발 속도가 빠르다.
    
- 비용이 거의 들지 않는다.
    

#### 단점

- 실제 서비스 운영 경험을 보여주기 어렵다.
    
- 포트폴리오에서 배포·운영 역량을 설명하기 어렵다.
    
- 이미지 업로드, S3, CloudFront, Worker 구조를 실제 환경처럼 검증하기 어렵다.
    
- 사용 가능한 웹 서비스 형태가 아니다.
    

소로소로는 포트폴리오 프로젝트이므로 실제 배포 가능한 구조가 필요하다.

---

### 2. EC2에 직접 Java 애플리케이션 실행

두 번째 대안은 EC2에 Java와 PostgreSQL을 직접 설치하고 Spring Boot jar를 실행하는 방식이다.

```text
EC2
 ├── java -jar app.jar
 ├── postgres
 └── nginx
```

#### 장점

- Docker를 사용하지 않아도 된다.
    
- 초기 서버 구성이 단순해 보일 수 있다.
    
- 작은 프로젝트에서는 빠르게 배포할 수 있다.
    

#### 단점

- 서버 환경에 강하게 의존한다.
    
- Java 버전, 환경 변수, 실행 스크립트 관리가 복잡해질 수 있다.
    
- 로컬과 운영 환경의 차이가 커질 수 있다.
    
- API Server와 Worker를 분리 실행하기 번거롭다.
    
- 재배포와 롤백이 불편하다.
    
- 포트폴리오에서 컨테이너 기반 운영 경험을 보여주기 어렵다.
    

소로소로는 API Server와 Worker를 분리 실행할 필요가 있으므로, 직접 jar 실행보다 Docker 기반 실행이 더 적절하다.

---

### 3. AWS ECS 기반 배포

세 번째 대안은 AWS ECS를 사용해 API Server와 Worker를 각각 Task 또는 Service로 운영하는 방식이다.

#### 장점

- 컨테이너 운영에 적합하다.
    
- API Server와 Worker를 독립적으로 배포하고 확장할 수 있다.
    
- 운영 환경에 가까운 구조를 만들 수 있다.
    
- 향후 트래픽 증가 시 확장성이 좋다.
    
- RDS, SQS, CloudWatch 등 AWS 서비스와 연결하기 좋다.
    

#### 단점

- MVP 단계에서는 설정이 복잡하다.
    
- Task Definition, Service, Cluster, IAM, Load Balancer 등 이해해야 할 요소가 많다.
    
- 비용이 증가할 수 있다.
    
- 1인 개발 프로젝트에서 운영 부담이 커질 수 있다.
    
- 기능 구현보다 인프라 구성에 시간이 많이 들어갈 수 있다.
    

ECS는 향후 확장 단계에서는 좋은 선택이지만, MVP에서는 Docker Compose + EC2가 더 단순하다.

---

### 4. Kubernetes 기반 배포

네 번째 대안은 Kubernetes를 사용하는 방식이다.

#### 장점

- 컨테이너 오케스트레이션에 강력하다.
    
- 확장성과 자동 복구 기능이 좋다.
    
- API Server, Worker, DB, Queue 등을 체계적으로 관리할 수 있다.
    
- 실무 대규모 인프라 경험을 보여줄 수 있다.
    

#### 단점

- MVP에 비해 지나치게 복잡하다.
    
- 학습 및 운영 부담이 크다.
    
- 비용이 증가한다.
    
- 1인 개발자가 관리하기 어렵다.
    
- 소로소로의 현재 규모에는 과한 선택이다.
    

소로소로 MVP에서는 Kubernetes를 사용하지 않는다.

---

### 5. Docker Compose + EC2 + Nginx

다섯 번째 대안은 EC2 서버에서 Docker Compose로 API Server, Worker, PostgreSQL, Queue 등을 실행하고, Nginx로 외부 요청을 프록시하는 방식이다.

#### 장점

- 실제 서버 배포가 가능하다.
    
- Docker 기반으로 실행 환경을 재현할 수 있다.
    
- API Server와 Worker를 별도 컨테이너로 분리할 수 있다.
    
- 운영 구조가 ECS나 Kubernetes보다 단순하다.
    
- 1인 개발자가 관리하기 쉽다.
    
- GitHub Actions와 연결해 배포 자동화를 구성할 수 있다.
    
- 향후 ECS로 이전할 때 컨테이너 기반 구조를 재사용할 수 있다.
    

#### 단점

- 단일 EC2 서버 장애에 취약하다.
    
- 오토스케일링이 어렵다.
    
- DB를 같은 서버에서 운영하면 백업과 장애 대응에 주의가 필요하다.
    
- Compose 기반 운영은 대규모 트래픽에 적합하지 않다.
    
- 서버 리소스가 API, Worker, DB에 공유된다.
    

MVP에서는 단순성과 실제 배포 가능성의 균형이 중요하므로 이 방식을 선택한다.

---

## Rationale

Docker Compose + EC2 + Nginx를 선택한 이유는 다음과 같다.

---

### 1. 1인 개발 MVP에 적합한 복잡도이다

소로소로는 1인 개발 프로젝트이다.

초기 목표는 대규모 트래픽 대응이 아니라 다음을 검증하는 것이다.

- Project 기록 구조
    
- DailyLog 작성 흐름
    
- Fabric 관리
    
- Photo 업로드
    
- 이미지 비동기 처리
    
- Calendar와 Contribution 조회
    
- 실제 서버 배포 가능성
    

ECS나 Kubernetes는 확장성은 좋지만, MVP 단계에서는 인프라 복잡도가 과도하다.

Docker Compose + EC2는 실제 서버 배포가 가능하면서도 1인 개발자가 이해하고 운영하기에 적절한 수준이다.

---

### 2. API Server와 Worker를 분리 실행할 수 있다

ADR-009에서 이미지 리사이징은 Worker를 통해 비동기로 처리하기로 결정했다.

따라서 배포 환경에서는 최소한 다음 두 실행 단위가 필요하다.

```text
app: Spring Boot API Server
worker: Image Processing Worker
```

Docker Compose를 사용하면 동일 코드베이스 또는 동일 이미지 기반으로도 실행 명령을 달리하여 app과 worker를 분리할 수 있다.

예상 구조는 다음과 같다.

```yaml
services:
  app:
    image: sorosoro-app
    command: java -jar app.jar --spring.profiles.active=prod

  worker:
    image: sorosoro-app
    command: java -jar app.jar --spring.profiles.active=worker
```

구체적인 실행 모드 분리는 Backend Design과 Architecture 문서에서 정의한다.

---

### 3. 로컬과 서버 실행 환경을 맞추기 쉽다

Docker Compose를 사용하면 로컬 개발 환경과 서버 실행 환경을 비슷하게 구성할 수 있다.

예를 들어 로컬에서도 다음 서비스를 함께 실행할 수 있다.

```text
app
worker
postgres
queue
```

이 구조는 다음 장점이 있다.

- 새 환경에서 실행하기 쉽다.
    
- 의존 서비스 설정을 문서화하기 좋다.
    
- 로컬과 서버의 환경 차이를 줄일 수 있다.
    
- 포트폴리오에서 실행 방법을 명확히 안내할 수 있다.
    

---

### 4. Nginx로 외부 요청을 안정적으로 전달할 수 있다

Nginx는 외부 요청을 Spring Boot 애플리케이션으로 전달하는 Reverse Proxy 역할을 한다.

Nginx를 사용하는 이유는 다음과 같다.

- 80/443 포트 요청을 애플리케이션 포트로 전달
    
- HTTPS 설정 가능
    
- 정적 파일 또는 헬스 체크 경로 분리 가능
    
- 프록시 헤더 관리
    
- 추후 프론트엔드 배포 구조와 연결 가능
    

Spring Boot를 외부에 직접 노출하기보다 Nginx를 앞단에 두면 운영 구조를 더 명확히 만들 수 있다.

---

### 5. GitHub Actions 기반 배포 자동화와 연결하기 쉽다

MVP에서도 수동 배포만 사용하는 것보다 간단한 CI/CD를 구성하는 것이 좋다.

GitHub Actions를 사용하면 다음 흐름을 구성할 수 있다.

```text
main branch push
→ Build
→ Test
→ Docker image build
→ EC2 접속
→ docker compose pull / up
```

이 구조는 복잡한 배포 플랫폼 없이도 배포 자동화를 경험할 수 있게 한다.

포트폴리오에서는 다음을 설명할 수 있다.

- 코드 변경 후 자동 빌드
    
- Docker 이미지 기반 배포
    
- EC2에서 Compose로 서비스 재시작
    
- 환경 변수 기반 운영 설정 분리
    

---

### 6. 향후 ECS로 이전할 수 있는 기반이 된다

Docker Compose는 MVP에는 적합하지만, 장기적으로는 한계가 있다.

트래픽이 증가하거나 안정적인 운영이 필요해지면 다음 구조로 확장할 수 있다.

- EC2 Compose → ECS Fargate
    
- EC2 내 PostgreSQL → RDS
    
- 로컬 Queue 컨테이너 → SQS
    
- 단일 EC2 Nginx → ALB
    
- 수동 서버 관리 → CloudWatch 기반 모니터링
    

Docker 기반으로 애플리케이션을 구성해두면, 향후 ECS로 이전할 때도 애플리케이션 실행 단위는 재사용할 수 있다.

---

### 7. 포트폴리오에서 운영 현실성을 보여줄 수 있다

Docker Compose + EC2 + Nginx 구조는 과하게 복잡하지 않으면서도 실제 백엔드 배포에 필요한 요소를 포함한다.

포트폴리오에서 다음 역량을 설명할 수 있다.

- Dockerfile 작성
    
- Docker Compose로 다중 컨테이너 실행
    
- API Server와 Worker 실행 단위 분리
    
- Nginx Reverse Proxy 설정
    
- EC2 기반 서버 운영
    
- 환경 변수 관리
    
- GitHub Actions 배포 자동화
    
- S3/CloudFront와 외부 저장소 연동
    

이는 단순히 “로컬에서 실행되는 프로젝트”보다 백엔드 운영 관점을 더 잘 보여준다.

---

## Trade-off

### 장점

Docker Compose + EC2 + Nginx를 선택하면 다음 장점이 있다.

- 실제 서버 배포가 가능하다.
    
- Docker 기반으로 실행 환경을 재현할 수 있다.
    
- API Server와 Worker를 분리 실행할 수 있다.
    
- 로컬과 서버 환경 차이를 줄일 수 있다.
    
- Nginx를 통해 외부 요청을 안정적으로 프록시할 수 있다.
    
- GitHub Actions 기반 배포 자동화와 연결하기 쉽다.
    
- ECS나 Kubernetes보다 운영 복잡도가 낮다.
    
- 향후 ECS, RDS, SQS로 확장할 수 있는 기반이 된다.
    
- 포트폴리오에서 배포와 운영 설계를 설명하기 좋다.
    

---

### 단점

이 결정으로 인해 다음 한계도 있다.

- 단일 EC2 서버 장애에 취약하다.
    
- 오토스케일링이 어렵다.
    
- EC2 리소스를 API Server, Worker, PostgreSQL, Queue가 공유한다.
    
- PostgreSQL을 같은 EC2에서 운영할 경우 백업과 복구 정책을 신경 써야 한다.
    
- Compose 기반 배포는 대규모 트래픽에 적합하지 않다.
    
- 서버 보안, 방화벽, 인증서 관리 등 운영 작업을 직접 해야 한다.
    
- 컨테이너 상태 모니터링과 로그 관리가 제한적일 수 있다.
    

---

### Trade-off 판단

ECS나 Kubernetes는 확장성과 운영 안정성 측면에서 더 좋은 선택일 수 있다.

하지만 소로소로 MVP의 목표는 대규모 운영이 아니라, 1인 개발자가 실제 배포 가능한 서비스를 완성하는 것이다.

따라서 다음 기준에서 Docker Compose + EC2 + Nginx가 가장 적절하다.

- 구현 가능성
    
- 운영 복잡도
    
- 비용
    
- 포트폴리오 설명력
    
- 향후 확장 가능성
    

MVP에서는 단순하고 이해 가능한 배포 구조를 선택하고, 이후 필요할 때 ECS, RDS, SQS 등으로 확장한다.

---

## Consequences

Docker Compose + EC2 + Nginx를 선택함에 따라 이후 설계와 구현은 다음 방향을 따른다.

---

### 1. Dockerfile을 작성한다

Spring Boot 애플리케이션을 Docker 이미지로 빌드할 수 있어야 한다.

Dockerfile은 다음을 지원해야 한다.

- Java 17 기반 실행
    
- Gradle build 결과물 실행
    
- 환경 변수 주입
    
- prod profile 실행
    
- worker profile 실행 가능성
    

---

### 2. Docker Compose로 실행 단위를 정의한다

MVP의 docker-compose 구성은 다음 서비스를 포함할 수 있다.

```text
app
worker
postgres
queue
nginx
```

단, S3와 CloudFront는 외부 AWS 서비스를 사용한다.

---

### 3. API Server와 Worker를 분리한다

API Server와 Worker는 역할이 다르다.

|실행 단위|역할|
|---|---|
|app|HTTP API 요청 처리|
|worker|이미지 리사이징 비동기 처리|

Worker는 외부 HTTP 요청을 받지 않아도 된다.

구체적인 실행 방식은 다음 중 하나로 결정할 수 있다.

- 같은 Docker image + 다른 Spring profile
    
- 같은 Docker image + 다른 command
    
- 별도 worker module
    
- 별도 worker application
    

MVP에서는 같은 코드베이스에서 실행 모드를 분리하는 방식을 우선 검토한다.

---

### 4. Nginx 설정이 필요하다

Nginx는 외부 요청을 app 컨테이너로 전달한다.

기본 역할은 다음과 같다.

- HTTP/HTTPS 요청 수신
    
- API Server로 Reverse Proxy
    
- 프록시 헤더 전달
    
- 요청 크기 제한 설정
    
- 헬스 체크 경로 관리
    

이미지 파일은 백엔드 서버가 직접 제공하지 않고, CloudFront를 통해 조회한다.

---

### 5. 환경 변수 관리를 분리한다

민감 정보와 환경별 설정은 코드에 하드코딩하지 않는다.

환경 변수로 관리해야 하는 값은 다음과 같다.

- DB 접속 정보
    
- JWT Secret
    
- Kakao OAuth Client 정보
    
- S3 Bucket 이름
    
- AWS Access Key
    
- AWS Secret Key
    
- CloudFront Domain
    
- Queue 설정
    
- Spring Profile
    

운영 환경의 `.env` 파일은 Git에 포함하지 않는다.

---

### 6. GitHub Actions 배포 흐름을 정의한다

배포 자동화는 다음 흐름을 고려한다.

```text
GitHub push
→ Test
→ Build
→ Docker image build
→ EC2 접속
→ docker compose pull
→ docker compose up -d
```

구체적인 배포 스크립트는 `06_Architecture.md` 또는 README에서 정의한다.

---

### 7. 백업과 장애 대응 정책을 별도로 정의한다

MVP에서 PostgreSQL을 EC2 내부 컨테이너로 운영할 경우, 데이터 백업 정책이 필요하다.

고려할 항목은 다음과 같다.

- PostgreSQL volume 관리
    
- 주기적 DB dump
    
- S3 또는 외부 저장소 백업
    
- 서버 장애 시 복구 절차
    
- 이미지 파일은 S3에 저장하여 EC2 로컬 디스크 의존 제거
    

구체적인 백업 정책은 Architecture 문서에서 정의한다.

---

### 8. 향후 확장 경로를 남긴다

MVP 이후 확장 경로는 다음과 같다.

|현재 구조|확장 구조|
|---|---|
|EC2 + Docker Compose|ECS Fargate|
|EC2 PostgreSQL Container|RDS PostgreSQL|
|Local Queue Container|SQS|
|Nginx on EC2|ALB|
|EC2 로그 확인|CloudWatch Logs|
|단일 서버|다중 인스턴스|

이 확장은 실제 사용자 증가, 장애 대응 필요성, 운영 비용을 기준으로 판단한다.

---

## Related Decisions

이 결정은 다음 ADR과 연결된다.

|ADR|관련성|
|---|---|
|ADR-001 Backend Framework로 Spring Boot를 선택한 이유|Spring Boot 애플리케이션을 Docker로 배포|
|ADR-002 Database로 PostgreSQL을 선택한 이유|PostgreSQL을 Docker Compose 또는 향후 RDS로 운영|
|ADR-008 Presigned URL 기반 이미지 업로드를 선택한 이유|이미지 파일은 서버 로컬이 아니라 S3에 저장|
|ADR-009 이미지 리사이징을 비동기로 처리하는 이유|Worker를 별도 실행 단위로 운영|
|ADR-011 MVP에서 AI 원단 자동 기록을 제외한 이유|MVP 배포 범위에 AI 처리 인프라를 포함하지 않음|

---

# ADR-012 요약

소로소로 MVP 배포 구조로 Docker Compose + EC2 + Nginx를 선택한다.

선택 이유는 다음과 같다.

- 1인 개발 MVP에 적합한 복잡도이다.
    
- 실제 서버 배포가 가능하다.
    
- Docker 기반으로 실행 환경을 재현할 수 있다.
    
- API Server와 Worker를 분리 실행할 수 있다.
    
- Nginx를 통해 외부 요청을 Spring Boot 애플리케이션으로 전달할 수 있다.
    
- GitHub Actions 기반 배포 자동화와 연결하기 쉽다.
    
- ECS나 Kubernetes보다 운영 부담이 낮다.
    
- 향후 ECS, RDS, SQS, ALB 구조로 확장할 수 있다.
    
- 포트폴리오에서 배포와 운영 설계를 설명하기 좋다.
    

MVP에서는 단일 EC2 기반의 단순 배포 구조를 사용하고, 운영 규모가 커지면 AWS 관리형 서비스로 확장한다.