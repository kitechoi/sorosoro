# 02. SRS — 소로소로(SOROSORO)

> 하루하루 재봉 과정을 기록하는 개인 재봉일기 서비스

---

# 1. Introduction

## 1.1 문서 목적

본 문서는 **소로소로(SOROSORO)** 서비스의 소프트웨어 요구사항을 정의하는 SRS(Software Requirements Specification) 문서이다.

이 문서는 서비스가 제공해야 하는 기능, 사용자 흐름, 입력 및 출력, 비즈니스 규칙, 예외 상황, 권한 정책, 비기능 요구사항을 명확히 정의하여 이후 설계와 구현의 기준으로 사용한다.

본 문서의 목적은 다음과 같다.

- 서비스가 구현해야 할 기능 요구사항을 명확히 정의한다.
    
- 각 기능의 입력, 출력, 처리 조건, 예외 상황을 정리한다.
    
- 도메인별 핵심 비즈니스 규칙을 문서화한다.
    
- MVP 범위에 포함되는 기능과 제외되는 기능을 구분한다.
    
- 이후 작성될 ERD, API Specification, Backend Design, Architecture 문서의 기준이 된다.
    

본 문서는 구현 방법 자체를 설명하지 않는다.  
예를 들어 패키지 구조, 트랜잭션 경계, JPA 연관관계 설계, QueryDSL 조회 전략, S3 업로드 상세 구현, Queue Worker 처리 방식 등은 별도 문서인 **Backend Design** 또는 **Architecture**에서 다룬다.

---

## 1.2 문서 범위

본 SRS는 소로소로 MVP에서 구현할 기능 요구사항을 대상으로 한다.

문서 범위에 포함되는 기능은 다음과 같다.

- 카카오 소셜 로그인
    
- JWT 기반 인증
    
- Refresh Token 기반 Access Token 재발급
    
- 프로젝트 관리
    
- 프로젝트 설정 관리
    
- 프로젝트 참고자료 관리
    
- 재봉 일지 관리
    
- 작업 시간 구간 관리
    
- 원단 관리
    
- 프로젝트와 원단 연결 관리
    
- 사진 업로드 및 관리
    
- 월별 캘린더 조회
    
- 재봉 잔디 조회
    
- 권한 정책
    
- 비기능 요구사항
    

문서 범위에서 제외되는 기능은 다음과 같다.

- AI 기반 원단 정보 자동 기록
    
- 커뮤니티
    
- 댓글
    
- 좋아요
    
- 팔로우
    
- 공개 프로젝트
    
- 원단 재고 관리
    
- 알림
    
- 검색 고도화
    
- 통계 대시보드
    

제외 기능은 향후 확장 범위로 관리하며, 본 SRS에서는 MVP 구현 대상 요구사항으로 정의하지 않는다.

---

## 1.3 제품 개요

소로소로는 재봉을 취미로 하는 사용자가 작품 제작 과정과 원단 구매 이력을 함께 기록할 수 있도록 돕는 개인 재봉일기 서비스이다.

사용자는 하나의 작품을 **Project**로 생성하고, 날짜별 작업 과정을 **DailyLog**로 기록한다. 또한 구매한 원단을 **Fabric**으로 등록하고, 특정 Project와 연결할 수 있다.

이를 통해 사용자는 시간이 지나도 다음 정보를 쉽게 확인할 수 있다.

- 어떤 작품을 만들었는지
    
- 어떤 날짜에 어떤 작업을 했는지
    
- 완성까지 얼마나 걸렸는지
    
- 어떤 원단을 사용했는지
    
- 특정 원단으로 어떤 작품을 만들었는지
    
- 어떤 원단을 다시 구매하고 싶은지
    

소로소로는 단순한 사진 저장 서비스가 아니라, **작품, 작업 과정, 원단 구매 이력**을 하나의 데이터 흐름으로 연결하는 개인 기록 플랫폼을 지향한다.

---

## 1.4 대상 사용자

본 서비스의 1차 대상 사용자는 재봉을 취미로 하며 자신의 작업 과정을 기록하고 싶은 개인 사용자이다.

대상 사용자의 특징은 다음과 같다.

- 하나의 작품을 여러 날에 걸쳐 제작한다.
    
- 작업 중간 과정 사진을 자주 촬영한다.
    
- 원단을 여러 쇼핑몰에서 구매한다.
    
- 구매한 원단 정보를 나중에 다시 확인하고 싶어 한다.
    
- 완성 작품뿐 아니라 제작 과정과 시행착오도 기록하고 싶어 한다.
    
- 원단과 작품의 관계를 체계적으로 관리하고 싶어 한다.
    

MVP는 개인 사용자 1인이 자신의 기록을 관리하는 흐름을 중심으로 한다.  
다른 사용자와의 공유, 커뮤니티, 팔로우, 댓글 기능은 MVP 범위에서 제외한다.

---

## 1.5 운영 환경

MVP는 모바일 웹 중심으로 사용되는 것을 전제로 한다.

사용자는 재봉 작업 중간에 휴대폰으로 사진을 촬영하고, 작업이 끝난 뒤 간단히 기록을 남길 가능성이 높다. 따라서 핵심 사용 흐름은 모바일 화면에서 불편 없이 동작해야 한다.

운영 환경은 다음을 기준으로 한다.

|구분|내용|
|---|---|
|Client|Mobile Web 우선|
|Backend|Java 17, Spring Boot|
|Database|PostgreSQL|
|Authentication|Kakao OAuth, JWT, Refresh Token|
|Storage|AWS S3, CloudFront|
|Deployment|Docker, Docker Compose, EC2, Nginx|

구체적인 인프라 구성과 배포 방식은 Architecture 문서에서 정의한다.

---

## 1.6 문서의 기준

본 SRS는 다음 문서들을 기준으로 작성된다.

|문서|역할|
|---|---|
|01_PRD.md|서비스 기획 방향, 문제 정의, 제품 목표|
|02_SRS.md|소프트웨어 요구사항 정의|
|03_ADR.md|주요 기술 및 설계 의사결정 기록|
|04_ERD.md|데이터 모델 및 테이블 구조|
|05_Backend_Design.md|백엔드 구현 설계|
|06_Architecture.md|시스템 아키텍처 및 인프라 구조|
|07_API.md|API Endpoint, Request, Response 명세|
|08_Wireframe.md|화면 흐름 및 사용자 인터페이스|

본 문서는 PRD의 제품 방향을 바탕으로 작성되며, 이후 ERD, API, Backend Design 문서의 기준 문서로 사용된다.

---

## 1.7 용어 정의

|용어|설명|
|---|---|
|User|소로소로 서비스를 이용하는 사용자|
|Project|사용자가 제작하는 하나의 재봉 작품|
|DailyLog|특정 날짜에 특정 Project에서 수행한 작업 기록|
|DailyLogTimeEntry|하나의 DailyLog 안에서 실제 작업한 시간 구간|
|Fabric|사용자가 구매한 원단 기록|
|ProjectFabric|Project와 Fabric의 연결 정보|
|Photo|Project, DailyLog, Fabric에 첨부되는 이미지|
|Thumbnail|목록 또는 카드 화면에서 사용하는 축소 이미지|
|Medium Image|상세 화면에서 사용하는 최적화 이미지|
|Original Image|사용자가 업로드한 원본 이미지|
|Presigned URL|클라이언트가 S3에 직접 파일을 업로드할 수 있도록 서버가 발급하는 임시 URL|
|Refresh Token|Access Token 재발급을 위해 저장되는 장기 토큰|
|MVP|최소 기능 제품. 1차 구현 범위|
|Archive|사용자가 더 이상 active하게 관리하지 않는 데이터를 보관 상태로 전환하는 것|
|ON_HOLD|프로젝트가 일시 중단된 상태|
|COMPLETED|프로젝트가 완성된 상태|
|IN_PROGRESS|프로젝트가 진행 중인 상태|

---

## 1.8 문서 작성 원칙

본 SRS는 다음 원칙에 따라 작성한다.

### 1. 요구사항과 구현 설계를 분리한다

SRS는 “무엇을 만들어야 하는가”를 정의한다.

다음 내용은 SRS에 포함하지 않는다.

- 패키지 구조
    
- Service 계층 설계
    
- Transaction 범위
    
- JPA 연관관계 구현 방식
    
- QueryDSL 상세 조회 전략
    
- Worker 내부 처리 구조
    
- 배포 파이프라인 상세
    

위 내용은 Backend Design 또는 Architecture 문서에서 다룬다.

---

### 2. MVP 기준으로 작성한다

본 문서는 1차 구현 범위인 MVP를 기준으로 한다.

향후 확장 기능은 Future Scope로 분리하며, MVP 요구사항과 혼합하지 않는다.

---

### 3. 도메인별로 기능을 구분한다

기능 요구사항은 다음 도메인 기준으로 구분한다.

- Authentication
    
- Project
    
- DailyLog
    
- Fabric
    
- Photo
    
- Calendar
    

각 기능은 고유한 요구사항 ID를 가진다.

예시:

- AUTH-001 카카오 로그인
    
- PRJ-001 프로젝트 생성
    
- DL-001 재봉 일지 생성
    
- FAB-001 원단 등록
    
- PHO-001 Presigned URL 발급
    
- CAL-001 월별 캘린더 조회
    

---

### 4. 각 기능 요구사항은 동일한 구조로 작성한다

각 기능 요구사항은 가능한 한 다음 구조를 따른다.

- 목적
    
- Actor
    
- Priority
    
- Preconditions
    
- Trigger
    
- Main Flow
    
- Alternate Flow
    
- Input
    
- Output
    
- Business Rules
    
- Exception
    
- Related Entity
    
- Acceptance Criteria
    

단, 기능 성격에 따라 불필요한 항목은 생략할 수 있다.

---

### 5. 권한 정책을 명확히 한다

소로소로는 개인 기록 서비스이므로, 모든 주요 데이터는 소유자 기준으로 접근을 제한한다.

기본 원칙은 다음과 같다.

- 사용자는 자신의 데이터만 조회할 수 있다.
    
- 사용자는 자신의 데이터만 수정할 수 있다.
    
- 사용자는 자신의 데이터만 삭제할 수 있다.
    
- 다른 사용자의 Project, DailyLog, Fabric, Photo에는 접근할 수 없다.
    

구체적인 권한 정책은 별도 장에서 정의한다.

---

## 1.9 요구사항 우선순위 기준

각 기능 요구사항은 다음 우선순위 기준을 사용한다.

|Priority|의미|
|---|---|
|High|MVP 구현에 반드시 필요한 핵심 기능|
|Medium|MVP에 포함되지만 핵심 흐름 이후 구현해도 되는 기능|
|Low|MVP 이후로 미룰 수 있는 기능|

MVP 포함 기능은 기본적으로 High 또는 Medium으로 분류한다.  
MVP 제외 기능은 본 SRS의 기능 요구사항으로 작성하지 않고 Future Scope에 정리한다.

---

## 1.10 요구사항 ID 규칙

요구사항 ID는 도메인 접두어와 일련번호로 구성한다.

|도메인|Prefix|예시|
|---|---|---|
|Authentication|AUTH|AUTH-001|
|Project|PRJ|PRJ-001|
|DailyLog|DL|DL-001|
|Fabric|FAB|FAB-001|
|Photo|PHO|PHO-001|
|Calendar|CAL|CAL-001|
|Non-functional Requirement|NFR|NFR-001|
|Global Business Rule|GBR|GBR-001|
|Permission Rule|PERM|PERM-001|

요구사항 ID는 이후 API 명세, 테스트 케이스, 이슈 관리에서 추적 가능한 기준으로 사용한다.

---

# 1장 요약

1장은 SRS의 목적, 범위, 기준, 용어, 작성 원칙을 정의한다.

이 장에서 확정한 핵심 기준은 다음과 같다.

- SRS는 구현 설계가 아니라 요구사항 정의 문서이다.
    
- MVP 범위에 포함되는 기능만 기능 요구사항으로 작성한다.
    
- AI 원단 자동 기록, 커뮤니티, 통계 대시보드는 향후 확장 기능으로 분리한다.
    
- 기능 요구사항은 도메인별 ID를 부여한다.
    
- 모든 사용자 데이터는 소유자 기준으로 접근 제어한다.
    
- 기술 스택 중 Deployment는 Docker, Docker Compose, EC2, Nginx를 기준으로 한다.



----

# 2. Overall Description

## 2.1 Product Perspective

소로소로(SOROSORO)는 재봉을 취미로 하는 사용자가 작품 제작 과정과 원단 구매 이력을 함께 관리할 수 있도록 돕는 개인 기록 서비스이다.

본 서비스는 단순한 이미지 저장 서비스나 메모 앱이 아니라, 다음 세 가지 데이터를 중심으로 사용자의 재봉 활동을 구조화한다.

- Project: 사용자가 제작하는 하나의 재봉 작품
    
- DailyLog: 특정 날짜에 특정 Project에서 수행한 작업 기록
    
- Fabric: 사용자가 구매한 원단 기록
    

소로소로의 핵심 관점은 **작품, 작업 과정, 원단 구매 이력의 연결**이다.

사용자는 Project를 생성하고, 날짜별 DailyLog를 작성하며, 구매한 Fabric을 Project에 연결한다. 이를 통해 하나의 작품이 어떤 과정으로 만들어졌는지, 어떤 원단이 사용되었는지, 특정 원단이 어떤 작품에 활용되었는지 추적할 수 있다.

서비스의 주요 데이터 관계는 다음과 같다.

```text
User
 ├── Project
 │    ├── ProjectSpecification
 │    │    └── ProjectReference
 │    ├── DailyLog
 │    │    └── DailyLogTimeEntry
 │    └── ProjectFabric
 │         └── Fabric
 │
 ├── Fabric
 ├── Photo
 └── RefreshToken
```

본 서비스는 개인 기록 서비스를 전제로 하므로, 모든 주요 데이터는 User를 기준으로 소유권을 가진다.

---

## 2.2 Product Functions

소로소로 MVP에서 제공하는 주요 기능은 다음과 같다.

|구분|주요 기능|
|---|---|
|Authentication|카카오 로그인, Access Token 재발급, 로그아웃, 내 정보 조회|
|Project|프로젝트 생성, 조회, 수정, 삭제, 상태 관리|
|ProjectSpecification|프로젝트 설정 조회 및 수정|
|ProjectReference|프로젝트 참고자료 등록, 수정, 삭제|
|DailyLog|날짜별 재봉 일지 생성, 조회, 수정, 삭제|
|DailyLogTimeEntry|일지 내 작업 시간 구간 관리|
|Fabric|원단 등록, 조회, 수정, 삭제|
|ProjectFabric|프로젝트와 원단 연결, 연결 메모 관리|
|Photo|사진 업로드, 조회, 삭제, 대표 사진 설정|
|Calendar|월별 작업 기록 조회|
|Contribution|연도별 재봉 잔디 조회|

각 기능은 SRS의 Functional Requirements 장에서 상세히 정의한다.

---

## 2.3 User Classes and Characteristics

### 2.3.1 Guest

Guest는 아직 로그인하지 않은 사용자이다.

Guest는 다음 기능만 사용할 수 있다.

- 서비스 소개 화면 조회
    
- 카카오 로그인 요청
    

Guest는 Project, DailyLog, Fabric, Photo 등 개인 데이터에 접근할 수 없다.

---

### 2.3.2 User

User는 카카오 로그인을 통해 인증된 사용자이다.

User는 자신의 재봉 기록을 생성하고 관리할 수 있다.

User는 다음 기능을 사용할 수 있다.

- 프로젝트 관리
    
- 프로젝트 설정 관리
    
- 프로젝트 참고자료 관리
    
- 재봉 일지 관리
    
- 작업 시간 구간 관리
    
- 원단 관리
    
- 프로젝트와 원단 연결
    
- 사진 업로드 및 관리
    
- 캘린더 조회
    
- 재봉 잔디 조회
    
- 내 정보 조회
    
- 로그아웃
    

User는 자신의 데이터에만 접근할 수 있다. 다른 사용자의 Project, DailyLog, Fabric, Photo에는 접근할 수 없다.

---

### 2.3.3 Admin

MVP에서는 별도의 관리자 기능을 제공하지 않는다.

Admin 기능은 MVP 범위에서 제외한다.

향후 서비스 운영이 필요해질 경우, 다음 기능을 별도 범위로 검토할 수 있다.

- 사용자 관리
    
- 신고 관리
    
- 공개 콘텐츠 관리
    
- 시스템 로그 조회
    
- AI Extraction Job 모니터링
    

---

## 2.4 Operating Environment

소로소로 MVP는 모바일 웹 중심으로 설계한다.

재봉 기록은 사용자가 작업 중간 또는 작업 직후 휴대폰으로 작성할 가능성이 높다. 따라서 핵심 기능은 모바일 화면에서 우선적으로 사용 가능해야 한다.

### 2.4.1 Client Environment

|구분|내용|
|---|---|
|주요 환경|Mobile Web|
|보조 환경|Desktop Web|
|핵심 입력|텍스트, 날짜, 시간, 이미지|
|핵심 조회|프로젝트 목록, 일지 목록, 캘린더, 원단 목록|

MVP에서는 Native App을 제공하지 않는다.

---

### 2.4.2 Server Environment

|구분|내용|
|---|---|
|Backend|Java 17, Spring Boot|
|Database|PostgreSQL|
|ORM|Spring Data JPA, QueryDSL|
|Migration|Flyway|
|Authentication|Spring Security, JWT, Refresh Token|
|Storage|AWS S3, CloudFront|
|Async|Queue + Worker|
|CI/CD|GitHub Actions|
|Deployment|Docker, Docker Compose, EC2, Nginx|

구체적인 인프라 구성, 네트워크 구조, 배포 방식은 Architecture 문서에서 정의한다.

---

## 2.5 Design and Implementation Constraints

본 서비스는 다음 제약사항을 가진다.

### 2.5.1 MVP 우선 개발

소로소로는 1차 구현 범위를 MVP로 제한한다.

MVP에서는 개인 사용자의 기록 관리 흐름에 집중한다.

MVP에 포함되는 핵심 흐름은 다음과 같다.

1. 카카오 로그인
    
2. 프로젝트 생성
    
3. DailyLog 작성
    
4. 원단 등록
    
5. 프로젝트와 원단 연결
    
6. 사진 업로드
    
7. 캘린더와 재봉 잔디 조회
    

커뮤니티, AI 자동 기록, 통계 대시보드, 알림 등은 MVP 범위에서 제외한다.

---

### 2.5.2 개인 기록 서비스

MVP는 개인 기록 서비스를 전제로 한다.

따라서 모든 데이터 접근은 소유자 기준으로 제한된다.

기본 원칙은 다음과 같다.

- 사용자는 자신의 Project만 조회할 수 있다.
    
- 사용자는 자신의 DailyLog만 조회할 수 있다.
    
- 사용자는 자신의 Fabric만 조회할 수 있다.
    
- 사용자는 자신의 Photo만 조회할 수 있다.
    
- 다른 사용자의 데이터에는 접근할 수 없다.
    

공개 프로젝트, 공유 링크, 팔로우 기반 조회는 MVP에서 제공하지 않는다.

---

### 2.5.3 카카오 로그인만 제공

MVP에서는 카카오 소셜 로그인만 제공한다.

이메일 회원가입, 자체 비밀번호 로그인, 다른 OAuth Provider 로그인은 제공하지 않는다.

인증 방식은 다음을 기준으로 한다.

- 카카오 OAuth 로그인
    
- JWT Access Token
    
- Refresh Token
    
- 기기별 Refresh Token 관리
    

---

### 2.5.4 이미지 중심 서비스

소로소로는 재봉 과정을 사진으로 기록하는 서비스이므로 이미지 업로드와 조회 성능이 중요하다.

MVP에서는 다음 이미지 정책을 따른다.

- Project는 최대 15장의 사진을 가질 수 있다.
    
- DailyLog는 최대 15장의 사진을 가질 수 있다.
    
- Fabric은 최대 2장의 사진을 가질 수 있다.
    
- Project와 DailyLog는 대표 사진을 1장 가질 수 있다.
    
- Fabric은 별도 사진 타입을 구분하지 않는다.
    

이미지는 원본, Medium, Thumbnail 형태로 관리한다.

이미지 상세 처리 방식은 Photo 요구사항 및 Architecture 문서에서 정의한다.

---

### 2.5.5 AI 기능은 2차 범위로 분리

AI 기반 원단 정보 자동 기록 기능은 MVP에서 제외한다.

해당 기능은 향후 다음 흐름으로 확장할 수 있다.

1. 주문 내역 캡처 이미지 업로드
    
2. Extraction Job 생성
    
3. AI 기반 상품 정보 추출
    
4. 사용자 검수
    
5. Fabric 생성
    

MVP에서는 Fabric을 사용자가 직접 등록하는 방식만 제공한다.

다만 향후 AI 기능 확장을 고려하여 Fabric 도메인은 상품명, 품번, 상품 URL, 구매처, 색상, 원단폭, 혼용률 등 명확한 원단 정보를 저장할 수 있도록 설계한다.

---

## 2.6 Assumptions and Dependencies

### 2.6.1 Assumptions

본 SRS는 다음 가정을 기반으로 한다.

- 사용자는 카카오 계정을 보유하고 있다.
    
- 사용자는 모바일 웹 환경에서 서비스를 주로 사용한다.
    
- 사용자는 자신의 재봉 기록을 개인적으로 관리한다.
    
- 하나의 Project는 하나 이상의 DailyLog를 가질 수 있다.
    
- 하나의 Project는 여러 Fabric과 연결될 수 있다.
    
- 하나의 Fabric은 여러 Project와 연결될 수 있다.
    
- DailyLog의 총 작업 시간은 사용자가 직접 입력하지 않고 TimeEntry 합산으로 계산한다.
    
- 사용자는 직접 원단 정보를 입력할 수 있다.
    
- MVP에서는 다른 사용자와 데이터를 공유하지 않는다.
    

---

### 2.6.2 Dependencies

본 서비스는 다음 외부 시스템 또는 기술 요소에 의존한다.

|구분|의존 요소|설명|
|---|---|---|
|OAuth|Kakao OAuth|사용자 인증|
|Storage|AWS S3|원본 이미지 및 변환 이미지 저장|
|CDN|CloudFront|이미지 조회 최적화|
|Queue|Queue System|이미지 리사이징 작업 비동기 처리|
|Database|PostgreSQL|서비스 데이터 저장|
|Deployment|EC2, Docker, Docker Compose, Nginx|서버 실행 및 배포|
|CI/CD|GitHub Actions|빌드 및 배포 자동화|

외부 시스템 장애 또는 설정 오류가 발생할 경우 일부 기능이 제한될 수 있다.

예를 들어 S3 장애 시 이미지 업로드 또는 조회가 실패할 수 있으며, 카카오 OAuth 장애 시 신규 로그인 또는 토큰 발급이 제한될 수 있다.

---

## 2.7 User Workflow Overview

소로소로 MVP의 주요 사용자 흐름은 다음과 같다.

### 2.7.1 최초 사용 흐름

```text
서비스 접속
→ 카카오 로그인
→ User 생성 또는 기존 User 조회
→ 홈 화면 진입
```

최초 로그인 사용자는 별도 회원가입 절차 없이 서비스를 시작할 수 있다.

---

### 2.7.2 프로젝트 기록 흐름

```text
프로젝트 생성
→ 프로젝트 설정 입력
→ 참고자료 등록
→ 날짜별 DailyLog 작성
→ 작업 시간 구간 입력
→ 작업 사진 업로드
→ 프로젝트 상세에서 진행 과정 확인
```

Project는 하나의 작품을 의미하며, DailyLog가 누적될수록 제작 과정이 기록된다.

---

### 2.7.3 원단 관리 흐름

```text
원단 등록
→ 원단 구매 정보 입력
→ 원단 사진 업로드
→ 프로젝트에 원단 연결
→ Fabric 상세에서 사용 프로젝트 확인
```

Fabric은 Project에 종속되지 않고 독립적으로 관리된다.

사용자는 원단을 먼저 등록하고, 이후 적절한 Project에 연결할 수 있다.

---

### 2.7.4 작업 습관 조회 흐름

```text
DailyLog 작성
→ 작업 시간 누적
→ 월별 캘린더 조회
→ 연도별 재봉 잔디 조회
```

캘린더와 재봉 잔디는 DailyLog와 DailyLogTimeEntry의 작업 시간 데이터를 기반으로 생성된다.

---

## 2.8 Domain Relationship Overview

### 2.8.1 User 중심 구조

소로소로의 모든 주요 데이터는 User를 기준으로 소유된다.

User는 다음 데이터를 가진다.

- Project
    
- DailyLog
    
- Fabric
    
- Photo
    
- RefreshToken
    

DailyLog는 Project에 속하지만, 권한 검증과 조회 편의를 위해 User 소유 데이터로도 관리한다.

---

### 2.8.2 Project와 DailyLog 관계

Project는 하나의 작품이다.

DailyLog는 특정 날짜에 특정 Project에서 수행한 작업 기록이다.

관계는 다음과 같다.

```text
Project 1 : N DailyLog
```

하나의 Project는 여러 DailyLog를 가질 수 있다.

하나의 DailyLog는 반드시 하나의 Project에 속한다.

---

### 2.8.3 DailyLog와 DailyLogTimeEntry 관계

DailyLogTimeEntry는 하나의 DailyLog 안에서 실제 작업한 시간 구간이다.

관계는 다음과 같다.

```text
DailyLog 1 : N DailyLogTimeEntry
```

하나의 DailyLog는 여러 작업 시간 구간을 가질 수 있다.

DailyLog의 총 작업 시간은 DailyLogTimeEntry의 duration 합산값으로 계산된다.

---

### 2.8.4 Project와 Fabric 관계

Project와 Fabric은 다대다 관계를 가진다.

관계는 ProjectFabric을 통해 연결한다.

```text
Project N : M Fabric
Project 1 : N ProjectFabric
Fabric 1 : N ProjectFabric
```

ProjectFabric은 단순 연결 정보가 아니라, 해당 Project에서 Fabric을 어떤 맥락으로 사용했는지 기록하는 연결 엔티티이다.

예시:

- 안감으로 사용
    
- 포인트 리본으로 사용
    
- 생각보다 두꺼워서 일부에만 사용
    

---

### 2.8.5 Photo 관계

Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 데이터이다.

Photo는 ownerType과 ownerId를 통해 연결 대상을 구분한다.

지원하는 ownerType은 다음과 같다.

- PROJECT
    
- DAILY_LOG
    
- FABRIC
    

Photo는 항상 User 소유 데이터를 기준으로 관리된다.

---

## 2.9 Data Retention and Deletion Overview

MVP에서는 사용자가 자신의 데이터를 삭제할 수 있어야 한다.

기본 삭제 정책은 다음과 같다.

- Project 삭제 시 해당 Project의 DailyLog, ProjectSpecification, ProjectReference, ProjectFabric 연결은 함께 삭제된다.
    
- Project 삭제 시 연결된 Fabric 자체는 삭제되지 않는다.
    
- Fabric 삭제 시 해당 Fabric과 연결된 ProjectFabric은 함께 삭제된다.
    
- Fabric 삭제 시 Project 자체는 삭제되지 않는다.
    
- DailyLog 삭제 시 해당 DailyLogTimeEntry는 함께 삭제된다.
    
- Photo 삭제 시 서비스에서 해당 Photo를 더 이상 조회할 수 없어야 한다.
    

물리 삭제, 소프트 삭제, S3 Object 삭제 시점 등 구체적인 삭제 정책은 Backend Design 및 Architecture 문서에서 정의한다.

본 SRS에서는 사용자가 삭제 요청을 했을 때 서비스 화면과 조회 결과에서 해당 데이터가 제거되어야 함을 요구사항으로 정의한다.

---

## 2.10 Overall Constraints Summary

소로소로 MVP의 전체 제약사항은 다음과 같다.

|구분|제약사항|
|---|---|
|인증|카카오 로그인만 제공|
|사용자 범위|개인 사용자 중심|
|공유|MVP에서 제공하지 않음|
|커뮤니티|MVP에서 제외|
|AI 자동 기록|MVP에서 제외|
|이미지|Project/DailyLog 최대 15장, Fabric 최대 2장|
|대표 이미지|Project/DailyLog 각 1장|
|원단 관리|Fabric은 Project와 독립적으로 관리|
|작업 시간|TimeEntry 합산으로 계산|
|접근 권한|소유자만 조회, 수정, 삭제 가능|
|운영 환경|모바일 웹 우선|
|배포|Docker, Docker Compose, EC2, Nginx 기준|

---

# 2장 요약

2장은 소로소로의 전체 제품 관점, 사용자 유형, 운영 환경, 도메인 관계, 주요 제약사항을 정의한다.

이 장에서 확정한 핵심 기준은 다음과 같다.

- 소로소로는 개인 재봉 기록 서비스이다.
    
- MVP는 모바일 웹 중심으로 설계한다.
    
- 사용자는 자신의 데이터만 접근할 수 있다.
    
- Project와 Fabric은 독립 도메인이다.
    
- Project와 Fabric은 ProjectFabric을 통해 연결된다.
    
- DailyLog는 Project의 날짜별 작업 기록이다.
    
- DailyLog의 작업 시간은 TimeEntry 합산으로 계산한다.
    
- Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 데이터이다.
    
- AI 자동 기록과 커뮤니티 기능은 MVP에서 제외한다.

# 3. Functional Requirements

본 장에서는 소로소로(SOROSORO) MVP에서 구현해야 하는 기능 요구사항을 정의한다.

기능 요구사항은 도메인별로 구분하며, 각 요구사항은 고유 ID를 가진다.

|도메인|Prefix|설명|
|---|---|---|
|Authentication|AUTH|로그인, 토큰 재발급, 로그아웃, 사용자 인증|
|Project|PRJ|프로젝트 생성, 조회, 수정, 삭제, 상태 관리|
|DailyLog|DL|재봉 일지 생성, 조회, 수정, 삭제|
|Fabric|FAB|원단 등록, 조회, 수정, 삭제|
|Photo|PHO|사진 업로드, 조회, 삭제, 대표 사진 관리|
|Calendar|CAL|월별 캘린더, 재봉 잔디 조회|

각 기능 요구사항은 가능한 한 다음 구조를 따른다.

- 목적
    
- Actor
    
- Priority
    
- Preconditions
    
- Trigger
    
- Main Flow
    
- Alternate Flow
    
- Input
    
- Output
    
- Business Rules
    
- Exceptions
    
- Related Entity
    
- Acceptance Criteria
    

구현 방식, 패키지 구조, 트랜잭션 범위, JPA 연관관계, QueryDSL 조회 전략 등은 본 문서에서 다루지 않는다. 해당 내용은 Backend Design 문서에서 정의한다.

---

# 3.1 Authentication

## 3.1.1 개요

Authentication 도메인은 사용자의 로그인 상태를 관리하고, 인증된 사용자가 자신의 데이터에 접근할 수 있도록 한다.

소로소로 MVP에서는 별도의 이메일 회원가입을 제공하지 않고, **카카오 소셜 로그인**만 제공한다.

인증 방식은 다음을 기준으로 한다.

- 카카오 OAuth 로그인
    
- JWT Access Token 발급
    
- Refresh Token 발급
    
- Refresh Token 기반 Access Token 재발급
    
- 기기별 Refresh Token 관리
    
- 로그아웃 시 현재 기기의 Refresh Token 무효화
    

Authentication 도메인의 주요 목적은 다음과 같다.

- 사용자가 별도 회원가입 없이 카카오 계정으로 서비스를 시작할 수 있도록 한다.
    
- 인증된 사용자만 개인 기록 데이터에 접근할 수 있도록 한다.
    
- Access Token 만료 후에도 Refresh Token을 통해 로그인 상태를 유지할 수 있도록 한다.
    
- 로그아웃 시 현재 기기의 로그인 세션만 종료할 수 있도록 한다.
    

---

## 3.1.2 기능 목록

|ID|기능명|Priority|설명|
|---|---|---|---|
|AUTH-001|카카오 로그인|High|카카오 계정으로 로그인하고 서비스 토큰을 발급한다.|
|AUTH-002|Access Token 재발급|High|Refresh Token을 이용해 새로운 Access Token을 발급한다.|
|AUTH-003|로그아웃|Medium|현재 기기의 Refresh Token을 무효화한다.|
|AUTH-004|내 정보 조회|Medium|현재 로그인한 사용자의 기본 정보를 조회한다.|
|AUTH-005|회원 탈퇴|Medium|현재 사용자의 계정을 탈퇴 상태로 전환한다.|

---

# AUTH-001 카카오 로그인

## 목적

사용자는 카카오 계정을 이용하여 별도의 이메일 회원가입 없이 소로소로 서비스를 이용할 수 있어야 한다.

---

## Actor

Guest

---

## Priority

High

---

## Preconditions

- 사용자는 카카오 계정을 보유해야 한다.
    
- 사용자는 카카오 인증을 완료할 수 있어야 한다.
    
- 클라이언트는 카카오 인증 후 Authorization Code를 획득할 수 있어야 한다.
    

---

## Trigger

사용자가 로그인 화면에서 **카카오로 시작하기** 버튼을 선택한다.

---

## Main Flow

1. 사용자가 카카오 로그인 버튼을 선택한다.
    
2. 클라이언트는 카카오 인증 화면으로 사용자를 이동시킨다.
    
3. 사용자는 카카오 인증을 완료한다.
    
4. 클라이언트는 카카오 Authorization Code를 획득한다.
    
5. 클라이언트는 Authorization Code를 서버에 전달한다.
    
6. 서버는 Authorization Code를 사용하여 카카오 Access Token을 요청한다.
    
7. 서버는 카카오 Access Token으로 카카오 사용자 정보를 조회한다.
    
8. 서버는 카카오 사용자 식별자를 기준으로 기존 User를 조회한다.
    
9. 기존 User가 없으면 신규 User를 생성한다.
    
10. 서버는 User에게 Access Token과 Refresh Token을 발급한다.
    
11. 서버는 로그인 결과를 클라이언트에 반환한다.
    
12. 클라이언트는 로그인 상태로 서비스를 이용할 수 있다.
    

---

## Alternate Flow

### AF-001 카카오 인증 실패

카카오 인증 과정에서 사용자가 인증을 취소하거나 인증에 실패한 경우, 로그인은 완료되지 않는다.

결과:

- User는 생성되지 않는다.
    
- Access Token과 Refresh Token은 발급되지 않는다.
    
- 클라이언트는 로그인 실패 메시지를 표시한다.
    

---

### AF-002 카카오 사용자 정보 조회 실패

서버가 카카오 사용자 정보를 조회하지 못한 경우, 로그인은 완료되지 않는다.

결과:

- User는 생성되지 않는다.
    
- Access Token과 Refresh Token은 발급되지 않는다.
    
- 클라이언트는 로그인 실패 메시지를 표시한다.
    

---

### AF-003 탈퇴 상태 사용자 로그인 시도

동일한 카카오 계정에 연결된 User가 탈퇴 상태인 경우, 기본 정책은 로그인을 허용하지 않는다.

결과:

- Access Token과 Refresh Token은 발급되지 않는다.
    
- 클라이언트는 탈퇴한 계정으로 로그인할 수 없다는 메시지를 표시한다.
    

단, 탈퇴 후 재가입 허용 정책은 향후 정책 변경에 따라 별도 정의할 수 있다.

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|authorizationCode|필수|카카오 인증 후 클라이언트가 획득한 Authorization Code|

---

## Output

|항목|설명|
|---|---|
|accessToken|서비스 API 접근에 사용하는 JWT Access Token|
|refreshToken|Access Token 재발급에 사용하는 Refresh Token|
|tokenType|토큰 타입. 기본값은 Bearer|
|expiresIn|Access Token 만료 시간|
|userId|로그인한 User의 식별자|
|nickname|사용자 닉네임|
|profileImageUrl|사용자 프로필 이미지 URL|
|isNewUser|신규 가입 여부|

---

## Business Rules

- 이메일 회원가입은 제공하지 않는다.
    
- 카카오 계정 하나는 하나의 User와 연결된다.
    
- 최초 로그인 시 User를 자동 생성한다.
    
- 기존 사용자 로그인 시 기존 User 정보를 사용한다.
    
- Access Token과 Refresh Token을 함께 발급한다.
    
- Refresh Token은 User에 대해 여러 개 존재할 수 있다.
    
- Refresh Token은 기기 또는 세션 단위로 관리한다.
    
- 탈퇴 상태의 User는 로그인할 수 없다.
    
- 로그인 성공 후 사용자는 자신의 데이터에만 접근할 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|AUTH-001|카카오 인증 실패|카카오 로그인에 실패했습니다.|
|AUTH-002|카카오 사용자 정보 조회 실패|카카오 사용자 정보를 조회할 수 없습니다.|
|AUTH-003|유효하지 않은 Authorization Code|유효하지 않은 인증 코드입니다.|
|AUTH-004|탈퇴한 사용자 로그인 시도|탈퇴한 계정으로는 로그인할 수 없습니다.|

---

## Related Entity

- User
    
- RefreshToken
    

---

## Acceptance Criteria

- 사용자는 카카오 계정으로 로그인할 수 있다.
    
- 최초 로그인 시 User가 생성된다.
    
- 기존 사용자는 중복 생성되지 않는다.
    
- 로그인 성공 시 Access Token과 Refresh Token이 발급된다.
    
- 탈퇴 상태의 사용자는 로그인할 수 없다.
    
- 로그인 실패 시 User와 RefreshToken이 생성되지 않는다.
    

---

# AUTH-002 Access Token 재발급

## 목적

Access Token이 만료된 경우, 사용자는 Refresh Token을 이용해 새로운 Access Token을 발급받을 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 이전에 로그인한 이력이 있어야 한다.
    
- 유효한 Refresh Token이 존재해야 한다.
    
- Refresh Token이 만료되지 않아야 한다.
    
- Refresh Token에 연결된 User가 활성 상태여야 한다.
    

---

## Trigger

클라이언트가 Access Token 만료를 감지하거나, API 요청에서 인증 만료 응답을 받은 뒤 토큰 재발급을 요청한다.

---

## Main Flow

1. 클라이언트는 Refresh Token을 서버에 전달한다.
    
2. 서버는 Refresh Token의 유효성을 검증한다.
    
3. 서버는 Refresh Token이 저장소에 존재하는지 확인한다.
    
4. 서버는 Refresh Token에 연결된 User를 조회한다.
    
5. 서버는 User 상태가 활성 상태인지 확인한다.
    
6. 서버는 새로운 Access Token을 발급한다.
    
7. 서버는 새로운 Access Token을 클라이언트에 반환한다.
    
8. 클라이언트는 새 Access Token으로 API 요청을 계속 수행한다.
    

---

## Alternate Flow

### AF-001 Refresh Token 만료

Refresh Token이 만료된 경우 Access Token을 재발급하지 않는다.

결과:

- 새로운 Access Token은 발급되지 않는다.
    
- 클라이언트는 재로그인을 유도한다.
    

---

### AF-002 Refresh Token 없음

전달된 Refresh Token이 저장소에 존재하지 않는 경우 Access Token을 재발급하지 않는다.

결과:

- 새로운 Access Token은 발급되지 않는다.
    
- 클라이언트는 재로그인을 유도한다.
    

---

### AF-003 탈퇴 또는 비활성 사용자

Refresh Token에 연결된 User가 탈퇴 상태인 경우 Access Token을 재발급하지 않는다.

결과:

- 새로운 Access Token은 발급되지 않는다.
    
- 클라이언트는 로그아웃 상태로 전환한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|refreshToken|필수|Access Token 재발급에 사용하는 Refresh Token|

---

## Output

|항목|설명|
|---|---|
|accessToken|새로 발급된 JWT Access Token|
|tokenType|토큰 타입. 기본값은 Bearer|
|expiresIn|Access Token 만료 시간|

---

## Business Rules

- Access Token만 새로 발급한다.
    
- Refresh Token은 기본적으로 재사용한다.
    
- Refresh Token Rotation은 MVP에서 적용하지 않는다.
    
- Refresh Token이 만료되었거나 저장소에 없으면 재로그인이 필요하다.
    
- 탈퇴 상태의 User에게는 Access Token을 발급하지 않는다.
    
- 하나의 User는 여러 Refresh Token을 가질 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|AUTH-005|Refresh Token 누락|Refresh Token이 필요합니다.|
|AUTH-006|Refresh Token 만료|로그인 세션이 만료되었습니다. 다시 로그인해주세요.|
|AUTH-007|유효하지 않은 Refresh Token|유효하지 않은 Refresh Token입니다.|
|AUTH-008|Refresh Token에 연결된 User 없음|사용자 정보를 찾을 수 없습니다.|
|AUTH-009|탈퇴한 사용자 토큰 재발급 시도|탈퇴한 사용자는 토큰을 재발급할 수 없습니다.|

---

## Related Entity

- User
    
- RefreshToken
    

---

## Acceptance Criteria

- 유효한 Refresh Token으로 새로운 Access Token을 발급받을 수 있다.
    
- 만료된 Refresh Token으로는 Access Token을 발급받을 수 없다.
    
- 저장소에 존재하지 않는 Refresh Token으로는 Access Token을 발급받을 수 없다.
    
- 탈퇴 상태의 User에게는 Access Token을 발급하지 않는다.
    
- Access Token 재발급 시 기존 Refresh Token은 변경되지 않는다.
    

---

# AUTH-003 로그아웃

## 목적

사용자는 현재 기기의 로그인 세션을 종료할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- 현재 기기에 발급된 Refresh Token이 존재해야 한다.
    

---

## Trigger

사용자가 설정 또는 메뉴 화면에서 로그아웃을 선택한다.

---

## Main Flow

1. 사용자가 로그아웃을 선택한다.
    
2. 클라이언트는 현재 Refresh Token을 서버에 전달한다.
    
3. 서버는 Refresh Token을 조회한다.
    
4. 서버는 해당 Refresh Token을 삭제하거나 무효화한다.
    
5. 서버는 로그아웃 성공 응답을 반환한다.
    
6. 클라이언트는 저장된 Access Token과 Refresh Token을 제거한다.
    
7. 클라이언트는 로그인 화면으로 이동한다.
    

---

## Alternate Flow

### AF-001 이미 무효화된 Refresh Token

전달된 Refresh Token이 이미 삭제되었거나 존재하지 않는 경우에도 로그아웃 요청은 성공으로 처리한다.

결과:

- 클라이언트는 로컬 토큰을 제거한다.
    
- 로그인 화면으로 이동한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|refreshToken|필수|현재 기기의 Refresh Token|

---

## Output

|항목|설명|
|---|---|
|success|로그아웃 처리 결과|

---

## Business Rules

- 로그아웃은 현재 기기의 Refresh Token만 무효화한다.
    
- 다른 기기의 Refresh Token은 유지한다.
    
- 로그아웃 요청은 멱등적으로 처리한다.
    
- 로그아웃 후 해당 Refresh Token으로는 Access Token을 재발급할 수 없다.
    

---

## Exceptions

로그아웃은 멱등적으로 처리하므로, Refresh Token이 존재하지 않는 경우에도 성공으로 응답한다.

단, 서버 오류가 발생한 경우에는 공통 서버 오류로 처리한다.

---

## Related Entity

- RefreshToken
    

---

## Acceptance Criteria

- 로그아웃 후 현재 기기의 Refresh Token은 더 이상 사용할 수 없다.
    
- 로그아웃 후 현재 기기에서는 Access Token 재발급이 불가능하다.
    
- 다른 기기의 로그인 상태는 유지된다.
    
- 이미 로그아웃된 Refresh Token으로 로그아웃 요청을 보내도 성공으로 처리된다.
    

---

# AUTH-004 내 정보 조회

## 목적

사용자는 현재 로그인한 자신의 기본 정보를 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 앱에 진입하거나, 마이페이지 또는 설정 화면에 접근한다.

---

## Main Flow

1. 클라이언트는 Access Token을 포함하여 내 정보 조회를 요청한다.
    
2. 서버는 Access Token을 검증한다.
    
3. 서버는 Access Token에서 User 식별자를 확인한다.
    
4. 서버는 현재 User 정보를 조회한다.
    
5. 서버는 User 기본 정보를 반환한다.
    

---

## Alternate Flow

### AF-001 Access Token 만료

Access Token이 만료된 경우 내 정보 조회에 실패한다.

결과:

- 클라이언트는 Access Token 재발급을 시도한다.
    
- 재발급에 실패하면 로그인 화면으로 이동한다.
    

---

### AF-002 탈퇴 상태 사용자

User가 탈퇴 상태인 경우 내 정보 조회에 실패한다.

결과:

- 클라이언트는 로그아웃 상태로 전환한다.
    

---

## Input

없음

단, 요청 Header에 Access Token이 포함되어야 한다.

---

## Output

|항목|설명|
|---|---|
|userId|User 식별자|
|nickname|사용자 닉네임|
|profileImageUrl|사용자 프로필 이미지 URL|
|status|사용자 상태|
|createdAt|가입 일시|

---

## Business Rules

- 사용자는 자신의 정보만 조회할 수 있다.
    
- 내 정보 조회는 인증된 사용자만 사용할 수 있다.
    
- 탈퇴 상태의 사용자는 조회할 수 없다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|AUTH-010|Access Token 없음|인증 정보가 필요합니다.|
|AUTH-011|Access Token 만료|인증이 만료되었습니다.|
|AUTH-012|유효하지 않은 Access Token|유효하지 않은 인증 정보입니다.|
|AUTH-013|User 없음|사용자 정보를 찾을 수 없습니다.|
|AUTH-014|탈퇴 상태 사용자 조회|탈퇴한 사용자입니다.|

---

## Related Entity

- User
    

---

## Acceptance Criteria

- 로그인한 사용자는 자신의 기본 정보를 조회할 수 있다.
    
- Access Token이 없으면 내 정보 조회가 불가능하다.
    
- 만료되거나 유효하지 않은 Access Token으로는 내 정보 조회가 불가능하다.
    
- 탈퇴 상태의 사용자는 내 정보 조회가 불가능하다.
    

---

# AUTH-005 회원 탈퇴

## 목적

사용자는 자신의 계정을 탈퇴 처리할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 설정 화면에서 회원 탈퇴를 선택하고, 탈퇴 확인 절차를 완료한다.

---

## Main Flow

1. 사용자가 회원 탈퇴를 선택한다.
    
2. 클라이언트는 탈퇴 확인 절차를 진행한다.
    
3. 클라이언트는 회원 탈퇴 요청을 서버에 보낸다.
    
4. 서버는 Access Token을 검증한다.
    
5. 서버는 현재 User를 조회한다.
    
6. 서버는 User 상태를 탈퇴 상태로 변경한다.
    
7. 서버는 User에 연결된 Refresh Token을 모두 무효화한다.
    
8. 서버는 탈퇴 완료 응답을 반환한다.
    
9. 클라이언트는 로컬 토큰을 제거한다.
    
10. 클라이언트는 로그인 화면으로 이동한다.
    

---

## Alternate Flow

### AF-001 이미 탈퇴한 사용자

이미 탈퇴 상태인 사용자가 탈퇴를 요청한 경우 성공으로 처리한다.

결과:

- 추가 상태 변경은 발생하지 않는다.
    
- 클라이언트는 로그인 화면으로 이동한다.
    

---

## Input

없음

단, 요청 Header에 Access Token이 포함되어야 한다.

---

## Output

|항목|설명|
|---|---|
|success|탈퇴 처리 결과|

---

## Business Rules

- 회원 탈퇴는 User를 즉시 물리 삭제하지 않고 탈퇴 상태로 변경한다.
    
- User 상태는 `DELETED`로 변경된다.
    
- 탈퇴 처리 후 해당 User의 모든 Refresh Token은 무효화된다.
    
- 탈퇴 상태의 User는 로그인할 수 없다.
    
- 탈퇴 상태의 User는 Access Token 재발급을 받을 수 없다.
    
- MVP에서는 탈퇴 후 재가입 복구 기능을 제공하지 않는다.
    
- 탈퇴 후 개인 데이터 처리 정책은 별도 삭제 정책에서 정의한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|AUTH-015|인증 정보 없음|인증 정보가 필요합니다.|
|AUTH-016|User 없음|사용자 정보를 찾을 수 없습니다.|

이미 탈퇴한 사용자의 탈퇴 요청은 예외로 처리하지 않고 성공으로 응답한다.

---

## Related Entity

- User
    
- RefreshToken
    

---

## Acceptance Criteria

- 로그인한 사용자는 회원 탈퇴를 요청할 수 있다.
    
- 회원 탈퇴 시 User 상태가 `DELETED`로 변경된다.
    
- 회원 탈퇴 시 해당 User의 모든 Refresh Token이 무효화된다.
    
- 탈퇴 후 기존 Refresh Token으로 Access Token을 재발급할 수 없다.
    
- 탈퇴 상태의 User는 다시 로그인할 수 없다.
    
- 이미 탈퇴한 사용자의 탈퇴 요청은 성공으로 처리된다.
    

---

# 3.1 Authentication 요약

Authentication 도메인에서는 사용자의 인증과 세션 관리를 담당한다.

MVP에서 인증 방식은 카카오 로그인으로 제한하며, 자체 이메일 회원가입이나 비밀번호 로그인은 제공하지 않는다.

핵심 정책은 다음과 같다.

- 카카오 계정 기반으로 User를 식별한다.
    
- 최초 로그인 시 User를 자동 생성한다.
    
- Access Token과 Refresh Token을 발급한다.
    
- Refresh Token은 기기 또는 세션 단위로 여러 개 저장될 수 있다.
    
- 로그아웃은 현재 기기의 Refresh Token만 무효화한다.
    
- 회원 탈퇴 시 User는 `DELETED` 상태로 전환된다.
    
- 회원 탈퇴 시 모든 Refresh Token을 무효화한다.
    
- 탈퇴 상태의 User는 로그인 및 토큰 재발급이 불가능하다.


# 3.2 Project

## 3.2.1 개요

Project 도메인은 사용자가 제작하는 하나의 재봉 작품을 관리한다.

소로소로에서 Project는 단순한 작품 카드가 아니라, 제작 과정 전체를 묶는 중심 단위이다. 사용자는 Project를 생성한 뒤, 해당 Project에 DailyLog를 작성하고, 필요한 원단을 Fabric으로 연결하며, 참고한 패턴이나 URL을 ProjectReference로 기록할 수 있다.

Project 도메인은 다음 하위 개념을 포함한다.

- Project
    
- ProjectSpecification
    
- ProjectReference
    
- ProjectFabric
    

각 개념의 역할은 다음과 같다.

|개념|설명|
|---|---|
|Project|사용자가 제작하는 하나의 재봉 작품|
|ProjectSpecification|프로젝트 제작 과정에서 자주 변하지 않는 설정값|
|ProjectReference|프로젝트에 참고한 외부 URL 또는 자료|
|ProjectFabric|Project와 Fabric의 연결 정보|

Project는 사용자의 개인 데이터이며, 소유자만 조회, 수정, 삭제할 수 있다.

---

## 3.2.2 기능 목록

|ID|기능명|Priority|설명|
|---|---|---|---|
|PRJ-001|프로젝트 생성|High|사용자가 새로운 재봉 프로젝트를 생성한다.|
|PRJ-002|프로젝트 목록 조회|High|사용자의 프로젝트 목록을 조회한다.|
|PRJ-003|프로젝트 상세 조회|High|특정 프로젝트의 상세 정보를 조회한다.|
|PRJ-004|프로젝트 수정|High|프로젝트의 기본 정보를 수정한다.|
|PRJ-005|프로젝트 삭제|Medium|프로젝트를 삭제한다.|
|PRJ-006|프로젝트 상태 변경|High|프로젝트 진행 상태를 변경한다.|
|PRJ-007|프로젝트 설정 조회|Medium|프로젝트의 제작 설정 정보를 조회한다.|
|PRJ-008|프로젝트 설정 수정|Medium|프로젝트의 제작 설정 정보를 수정한다.|
|PRJ-009|참고자료 등록|Medium|프로젝트에 참고 URL을 등록한다.|
|PRJ-010|참고자료 수정|Medium|프로젝트 참고자료 정보를 수정한다.|
|PRJ-011|참고자료 삭제|Medium|프로젝트 참고자료를 삭제한다.|
|PRJ-012|프로젝트 원단 연결|High|프로젝트에 사용할 원단을 연결한다.|
|PRJ-013|프로젝트 원단 연결 수정|Medium|프로젝트와 원단 연결 메모를 수정한다.|
|PRJ-014|프로젝트 원단 연결 삭제|Medium|프로젝트와 원단의 연결을 해제한다.|

---

# PRJ-001 프로젝트 생성

## 목적

사용자는 새롭게 제작할 재봉 작품을 Project로 생성할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 프로젝트 생성 화면에서 프로젝트 정보를 입력하고 저장 버튼을 선택한다.

---

## Main Flow

1. 사용자가 프로젝트 생성 화면에 진입한다.
    
2. 사용자가 프로젝트명, 설명, 시작일 등 기본 정보를 입력한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 입력값을 검증한다.
    
6. 서버는 현재 User 소유의 Project를 생성한다.
    
7. 서버는 Project 생성과 함께 기본 ProjectSpecification을 생성한다.
    
8. 서버는 생성된 Project 정보를 반환한다.
    
9. 클라이언트는 생성된 Project 상세 화면으로 이동한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|title|필수|프로젝트명|
|description|선택|프로젝트 설명|
|startedAt|선택|프로젝트 시작일|
|status|선택|프로젝트 상태. 미입력 시 IN_PROGRESS|
|retrospective|선택|프로젝트 회고. 생성 시에는 보통 비어 있음|

---

## Output

|항목|설명|
|---|---|
|projectId|생성된 Project 식별자|
|title|프로젝트명|
|description|프로젝트 설명|
|status|프로젝트 상태|
|startedAt|프로젝트 시작일|
|completedAt|프로젝트 완료일|
|retrospective|프로젝트 회고|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- Project는 반드시 User에 속한다.
    
- Project 생성 시 기본 상태는 `IN_PROGRESS`이다.
    
- Project 생성 시 ProjectSpecification도 함께 생성된다.
    
- Project title은 필수값이다.
    
- Project title은 공백만으로 구성될 수 없다.
    
- Project 생성 시 DailyLog는 자동 생성하지 않는다.
    
- Project 생성 시 Fabric은 자동 연결하지 않는다.
    
- Project 생성 시 Photo는 자동 생성하지 않는다.
    
- Project는 생성한 User만 조회, 수정, 삭제할 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-001|프로젝트명 누락|프로젝트명을 입력해주세요.|
|PRJ-002|프로젝트명 형식 오류|프로젝트명은 공백만 입력할 수 없습니다.|
|PRJ-003|인증되지 않은 사용자|로그인이 필요합니다.|
|PRJ-004|탈퇴 상태 사용자|탈퇴한 사용자는 프로젝트를 생성할 수 없습니다.|

---

## Related Entity

- User
    
- Project
    
- ProjectSpecification
    

---

## Acceptance Criteria

- 로그인한 사용자는 Project를 생성할 수 있다.
    
- Project 생성 시 기본 상태는 `IN_PROGRESS`로 설정된다.
    
- Project 생성 시 빈 ProjectSpecification이 함께 생성된다.
    
- Project title이 없으면 생성할 수 없다.
    
- 생성된 Project는 현재 User의 소유가 된다.
    

---

# PRJ-002 프로젝트 목록 조회

## 목적

사용자는 자신이 생성한 Project 목록을 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 프로젝트 목록 화면에 진입한다.

---

## Main Flow

1. 클라이언트는 Project 목록 조회를 요청한다.
    
2. 서버는 사용자의 인증 정보를 확인한다.
    
3. 서버는 현재 User가 소유한 Project 목록을 조회한다.
    
4. 서버는 정렬 및 필터 조건을 적용한다.
    
5. 서버는 Project 목록을 반환한다.
    
6. 클라이언트는 Project 목록을 화면에 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|status|선택|조회할 Project 상태|
|keyword|선택|프로젝트명 검색어|
|sort|선택|정렬 기준|
|page|선택|페이지 번호|
|size|선택|페이지 크기|

---

## Output

|항목|설명|
|---|---|
|projectId|Project 식별자|
|title|프로젝트명|
|description|프로젝트 설명|
|status|프로젝트 상태|
|startedAt|프로젝트 시작일|
|completedAt|프로젝트 완료일|
|thumbnailUrl|대표 이미지 URL|
|totalDurationMinutes|총 작업 시간|
|dailyLogCount|작성된 DailyLog 수|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- 사용자는 자신의 Project 목록만 조회할 수 있다.
    
- 다른 사용자의 Project는 목록에 포함되지 않는다.
    
- 기본 정렬은 최근 수정일 내림차순이다.
    
- `ARCHIVED` 상태의 Project는 기본 목록에 포함할 수 있으나, 클라이언트는 별도 필터로 구분할 수 있어야 한다.
    
- Project의 총 작업 시간은 연결된 DailyLog의 durationMinutes 합산값으로 제공할 수 있다.
    
- Project 대표 이미지는 Project에 연결된 Photo 중 대표 사진을 기준으로 한다.
    
- 대표 사진이 없으면 thumbnailUrl은 비어 있을 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-005|인증되지 않은 사용자|로그인이 필요합니다.|
|PRJ-006|잘못된 상태 필터|유효하지 않은 프로젝트 상태입니다.|

---

## Related Entity

- User
    
- Project
    
- DailyLog
    
- Photo
    

---

## Acceptance Criteria

- 로그인한 사용자는 자신의 Project 목록을 조회할 수 있다.
    
- 다른 사용자의 Project는 조회 결과에 포함되지 않는다.
    
- 상태별 필터링이 가능하다.
    
- 검색어가 있을 경우 프로젝트명 기준으로 조회할 수 있다.
    
- 대표 이미지가 존재하는 Project는 thumbnailUrl을 반환한다.
    

---

# PRJ-003 프로젝트 상세 조회

## 목적

사용자는 특정 Project의 상세 정보를 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 조회 대상 Project가 존재해야 한다.
    
- 조회 대상 Project는 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 프로젝트 목록에서 특정 Project를 선택한다.

---

## Main Flow

1. 사용자가 Project를 선택한다.
    
2. 클라이언트는 Project 상세 조회를 요청한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 Project 존재 여부를 확인한다.
    
5. 서버는 Project 소유자가 현재 User인지 확인한다.
    
6. 서버는 Project 기본 정보, 설정 정보, 참고자료, 연결된 원단, 사진 요약, 작업 요약 정보를 조회한다.
    
7. 서버는 Project 상세 정보를 반환한다.
    
8. 클라이언트는 Project 상세 화면을 표시한다.
    

---

## Output

|항목|설명|
|---|---|
|projectId|Project 식별자|
|title|프로젝트명|
|description|프로젝트 설명|
|status|프로젝트 상태|
|startedAt|프로젝트 시작일|
|completedAt|프로젝트 완료일|
|retrospective|프로젝트 회고|
|specification|ProjectSpecification 정보|
|references|ProjectReference 목록|
|fabrics|연결된 Fabric 목록|
|photos|Project 사진 목록|
|totalDurationMinutes|총 작업 시간|
|dailyLogCount|DailyLog 수|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- 사용자는 자신의 Project만 상세 조회할 수 있다.
    
- Project 상세에는 ProjectSpecification이 포함된다.
    
- Project 상세에는 ProjectReference 목록이 포함될 수 있다.
    
- Project 상세에는 연결된 Fabric 목록이 포함될 수 있다.
    
- Project 상세에는 Project에 연결된 Photo 목록이 포함될 수 있다.
    
- Project의 총 작업 시간은 DailyLog의 durationMinutes 합산값으로 계산된다.
    
- DailyLog 상세 목록은 Project 상세 응답에 모두 포함하지 않고, 별도 DailyLog 목록 조회 기능에서 제공한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-007|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-008|Project 접근 권한 없음|해당 프로젝트에 접근할 수 없습니다.|
|PRJ-009|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- Project
    
- ProjectSpecification
    
- ProjectReference
    
- ProjectFabric
    
- Fabric
    
- Photo
    
- DailyLog
    

---

## Acceptance Criteria

- 사용자는 자신의 Project 상세 정보를 조회할 수 있다.
    
- 다른 사용자의 Project는 상세 조회할 수 없다.
    
- 존재하지 않는 Project 조회 시 오류가 반환된다.
    
- Project 상세에는 설정, 참고자료, 연결 원단, 사진 요약 정보가 포함된다.
    
- Project 상세에는 총 작업 시간과 DailyLog 수가 포함된다.
    

---

# PRJ-004 프로젝트 수정

## 목적

사용자는 자신이 생성한 Project의 기본 정보를 수정할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 수정 대상 Project가 존재해야 한다.
    
- 수정 대상 Project는 현재 User의 소유여야 한다.
    
- Project 상태가 수정 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project 수정 화면에서 정보를 변경하고 저장 버튼을 선택한다.

---

## Main Flow

1. 사용자가 Project 수정 화면에 진입한다.
    
2. 사용자가 프로젝트명, 설명, 시작일, 완료일, 회고 등을 수정한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Project 존재 여부를 확인한다.
    
6. 서버는 Project 소유자가 현재 User인지 확인한다.
    
7. 서버는 입력값을 검증한다.
    
8. 서버는 Project 정보를 수정한다.
    
9. 서버는 수정된 Project 정보를 반환한다.
    
10. 클라이언트는 수정된 정보를 화면에 반영한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|title|선택|프로젝트명|
|description|선택|프로젝트 설명|
|startedAt|선택|프로젝트 시작일|
|completedAt|선택|프로젝트 완료일|
|retrospective|선택|프로젝트 회고|

---

## Output

수정된 Project 정보

---

## Business Rules

- 사용자는 자신의 Project만 수정할 수 있다.
    
- `ARCHIVED` 상태의 Project는 기본 정보를 수정할 수 없다.
    
- `IN_PROGRESS`, `ON_HOLD`, `COMPLETED` 상태의 Project는 기본 정보를 수정할 수 있다.
    
- title을 수정하는 경우 공백만으로 변경할 수 없다.
    
- completedAt이 존재하는 경우 startedAt보다 이전일 수 없다.
    
- Project 상태 변경은 PRJ-006에서 별도로 처리한다.
    
- ProjectSpecification 수정은 PRJ-008에서 별도로 처리한다.
    
- ProjectReference 수정은 PRJ-010에서 별도로 처리한다.
    
- ProjectFabric 수정은 PRJ-013에서 별도로 처리한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-010|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-011|Project 접근 권한 없음|해당 프로젝트를 수정할 수 없습니다.|
|PRJ-012|프로젝트명 형식 오류|프로젝트명은 공백만 입력할 수 없습니다.|
|PRJ-013|완료일이 시작일보다 빠름|완료일은 시작일보다 빠를 수 없습니다.|
|PRJ-014|Archived Project 수정 시도|보관된 프로젝트는 수정할 수 없습니다.|

---

## Related Entity

- Project
    

---

## Acceptance Criteria

- 사용자는 자신의 Project 기본 정보를 수정할 수 있다.
    
- 다른 사용자의 Project는 수정할 수 없다.
    
- `ARCHIVED` 상태의 Project는 수정할 수 없다.
    
- 완료일은 시작일보다 빠를 수 없다.
    
- 상태 변경은 프로젝트 수정 기능에서 처리하지 않는다.
    

---

# PRJ-005 프로젝트 삭제

## 목적

사용자는 자신이 생성한 Project를 삭제할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 삭제 대상 Project가 존재해야 한다.
    
- 삭제 대상 Project는 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 Project 상세 또는 수정 화면에서 삭제를 선택하고 확인 절차를 완료한다.

---

## Main Flow

1. 사용자가 Project 삭제를 선택한다.
    
2. 클라이언트는 삭제 확인 절차를 진행한다.
    
3. 클라이언트는 Project 삭제 요청을 보낸다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Project 존재 여부를 확인한다.
    
6. 서버는 Project 소유자가 현재 User인지 확인한다.
    
7. 서버는 Project를 삭제 처리한다.
    
8. 서버는 삭제 완료 응답을 반환한다.
    
9. 클라이언트는 Project 목록 화면으로 이동한다.
    

---

## Output

|항목|설명|
|---|---|
|success|삭제 처리 결과|

---

## Business Rules

- 사용자는 자신의 Project만 삭제할 수 있다.
    
- Project 삭제 시 ProjectSpecification은 함께 삭제된다.
    
- Project 삭제 시 ProjectReference는 함께 삭제된다.
    
- Project 삭제 시 DailyLog와 DailyLogTimeEntry는 함께 삭제된다.
    
- Project 삭제 시 ProjectFabric 연결은 함께 삭제된다.
    
- Project 삭제 시 연결된 Fabric 자체는 삭제되지 않는다.
    
- Project 삭제 시 Project에 연결된 Photo는 서비스 화면에서 더 이상 조회되지 않아야 한다.
    
- Project 삭제 후 해당 Project는 목록과 상세 조회에서 제외되어야 한다.
    
- 물리 삭제, 소프트 삭제, S3 Object 삭제 시점은 Backend Design 및 Architecture에서 정의한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-015|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-016|Project 접근 권한 없음|해당 프로젝트를 삭제할 수 없습니다.|
|PRJ-017|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- Project
    
- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- ProjectFabric
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Project를 삭제할 수 있다.
    
- 다른 사용자의 Project는 삭제할 수 없다.
    
- Project 삭제 후 목록에서 조회되지 않는다.
    
- Project 삭제 후 상세 조회가 불가능하다.
    
- Project 삭제 시 연결된 Fabric 자체는 삭제되지 않는다.
    
- Project 삭제 시 연결 정보인 ProjectFabric은 삭제된다.
    

---

# PRJ-006 프로젝트 상태 변경

## 목적

사용자는 Project의 진행 상태를 변경할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 상태 변경 대상 Project가 존재해야 한다.
    
- 상태 변경 대상 Project는 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 Project 상세 또는 수정 화면에서 상태를 변경한다.

---

## Main Flow

1. 사용자가 Project 상태 변경을 선택한다.
    
2. 클라이언트는 변경할 상태를 서버에 전달한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 Project 존재 여부를 확인한다.
    
5. 서버는 Project 소유자가 현재 User인지 확인한다.
    
6. 서버는 상태 변경 가능 여부를 확인한다.
    
7. 서버는 Project 상태를 변경한다.
    
8. 서버는 변경된 Project 상태를 반환한다.
    
9. 클라이언트는 변경된 상태를 화면에 반영한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|status|필수|변경할 Project 상태|

---

## Output

|항목|설명|
|---|---|
|projectId|Project 식별자|
|status|변경된 상태|
|completedAt|완료일|

---

## Project Status

|상태|설명|
|---|---|
|IN_PROGRESS|현재 진행 중인 프로젝트|
|ON_HOLD|원단 부족, 부자재 대기 등으로 일시 중단된 프로젝트|
|COMPLETED|완성된 프로젝트|
|ARCHIVED|보관 처리된 프로젝트|

---

## Business Rules

- 사용자는 자신의 Project 상태만 변경할 수 있다.
    
- Project 상태는 `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `ARCHIVED` 중 하나여야 한다.
    
- `COMPLETED`로 변경할 경우 completedAt을 설정할 수 있다.
    
- `COMPLETED`로 변경할 때 completedAt이 입력되지 않으면 현재 날짜를 완료일로 사용할 수 있다.
    
- `COMPLETED`에서 다시 `IN_PROGRESS` 또는 `ON_HOLD`로 변경할 수 있다.
    
- `COMPLETED`에서 다른 상태로 변경할 경우 completedAt 처리 정책은 Backend Design에서 정의한다.
    
- `ARCHIVED` 상태는 보관 상태를 의미한다.
    
- `ARCHIVED` 상태의 Project는 기본 정보 수정, DailyLog 추가, 원단 연결 추가를 제한한다.
    
- `ARCHIVED` 상태에서 다시 `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`로 되돌릴 수 있다.
    
- `ON_HOLD` 상태에서도 DailyLog 작성은 가능하다.
    
- `ON_HOLD`는 진행이 느려진 상태일 뿐, 기록 자체를 막는 상태가 아니다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-018|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-019|Project 접근 권한 없음|해당 프로젝트 상태를 변경할 수 없습니다.|
|PRJ-020|유효하지 않은 상태|유효하지 않은 프로젝트 상태입니다.|

---

## Related Entity

- Project
    

---

## Acceptance Criteria

- 사용자는 자신의 Project 상태를 변경할 수 있다.
    
- 유효하지 않은 상태값으로는 변경할 수 없다.
    
- `COMPLETED` 변경 시 완료일을 관리할 수 있다.
    
- `ARCHIVED` 상태는 보관 상태로 처리된다.
    
- `ON_HOLD` 상태에서도 DailyLog 작성은 가능하다.
    

---

# PRJ-007 프로젝트 설정 조회

## 목적

사용자는 특정 Project의 제작 설정 정보를 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 대상 Project가 존재해야 한다.
    
- 대상 Project는 현재 User의 소유여야 한다.
    
- ProjectSpecification이 존재해야 한다.
    

---

## Trigger

사용자가 Project 상세 화면 또는 설정 화면에 진입한다.

---

## Main Flow

1. 클라이언트는 Project 설정 조회를 요청한다.
    
2. 서버는 사용자의 인증 정보를 확인한다.
    
3. 서버는 Project 존재 여부를 확인한다.
    
4. 서버는 Project 소유자가 현재 User인지 확인한다.
    
5. 서버는 ProjectSpecification을 조회한다.
    
6. 서버는 ProjectSpecification 정보를 반환한다.
    

---

## Output

|항목|설명|
|---|---|
|projectSpecificationId|ProjectSpecification 식별자|
|projectId|Project 식별자|
|patternMethod|패턴 방식|
|patternName|패턴명|
|finishedWidthCm|완성 가로 길이|
|finishedHeightCm|완성 세로 길이|
|finishedSizeMemo|완성 규격 메모|
|sewingMachineSettingMemo|재봉틀 세팅 메모|
|needleMemo|사용 바늘 메모|
|threadMemo|사용 실 메모|
|memo|기타 설정 메모|
|updatedAt|수정 일시|

---

## Business Rules

- ProjectSpecification은 Project당 하나만 존재한다.
    
- Project 생성 시 기본 ProjectSpecification이 함께 생성된다.
    
- 사용자는 자신의 ProjectSpecification만 조회할 수 있다.
    
- ProjectSpecification은 Project의 제작 설정값을 관리한다.
    
- 자주 변하는 작업 기록은 DailyLog에 기록한다.
    
- 자주 변하지 않는 제작 설정은 ProjectSpecification에 기록한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-021|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-022|Project 접근 권한 없음|해당 프로젝트 설정에 접근할 수 없습니다.|
|PRJ-023|ProjectSpecification 없음|프로젝트 설정 정보를 찾을 수 없습니다.|

---

## Related Entity

- Project
    
- ProjectSpecification
    

---

## Acceptance Criteria

- 사용자는 자신의 Project 설정 정보를 조회할 수 있다.
    
- 다른 사용자의 Project 설정은 조회할 수 없다.
    
- Project에는 하나의 ProjectSpecification이 존재한다.
    

---

# PRJ-008 프로젝트 설정 수정

## 목적

사용자는 Project의 제작 설정 정보를 수정할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 대상 Project가 존재해야 한다.
    
- 대상 Project는 현재 User의 소유여야 한다.
    
- Project 상태가 설정 수정 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project 설정 화면에서 값을 수정하고 저장 버튼을 선택한다.

---

## Main Flow

1. 사용자가 Project 설정 화면에 진입한다.
    
2. 사용자가 패턴 방식, 패턴명, 완성 규격, 재봉틀 세팅, 바늘, 실, 메모 등을 수정한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Project 존재 여부를 확인한다.
    
6. 서버는 Project 소유자가 현재 User인지 확인한다.
    
7. 서버는 입력값을 검증한다.
    
8. 서버는 ProjectSpecification을 수정한다.
    
9. 서버는 수정된 ProjectSpecification 정보를 반환한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|patternMethod|선택|패턴 방식|
|patternName|선택|패턴명|
|finishedWidthCm|선택|완성 가로 길이|
|finishedHeightCm|선택|완성 세로 길이|
|finishedSizeMemo|선택|완성 규격 메모|
|sewingMachineSettingMemo|선택|재봉틀 세팅 메모|
|needleMemo|선택|사용 바늘 메모|
|threadMemo|선택|사용 실 메모|
|memo|선택|기타 설정 메모|

---

## Pattern Method

|값|설명|
|---|---|
|SELF_DRAFTED|처음부터 직접 만든 패턴|
|COPIED|기존 패턴을 그대로 사용|
|MODIFIED|기존 패턴을 참고하되 치수, 구조, 디테일을 변형|

---

## Business Rules

- 사용자는 자신의 ProjectSpecification만 수정할 수 있다.
    
- `ARCHIVED` 상태의 Project는 ProjectSpecification을 수정할 수 없다.
    
- `IN_PROGRESS`, `ON_HOLD`, `COMPLETED` 상태에서는 ProjectSpecification을 수정할 수 있다.
    
- patternMethod는 `SELF_DRAFTED`, `COPIED`, `MODIFIED` 중 하나여야 한다.
    
- finishedWidthCm와 finishedHeightCm는 0보다 큰 값이어야 한다.
    
- finishedWidthCm와 finishedHeightCm는 공통적으로 검색 또는 필터에 활용 가능한 수치형 값으로 관리한다.
    
- 지퍼 길이, 고무줄 길이, 포켓 수, 밑위 등 작품별로 달라지는 규격은 finishedSizeMemo에 자유롭게 기록한다.
    
- ProjectSpecification은 Project당 하나만 존재한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-024|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-025|Project 접근 권한 없음|해당 프로젝트 설정을 수정할 수 없습니다.|
|PRJ-026|유효하지 않은 패턴 방식|유효하지 않은 패턴 방식입니다.|
|PRJ-027|완성 가로 길이 오류|완성 가로 길이는 0보다 커야 합니다.|
|PRJ-028|완성 세로 길이 오류|완성 세로 길이는 0보다 커야 합니다.|
|PRJ-029|Archived Project 설정 수정 시도|보관된 프로젝트 설정은 수정할 수 없습니다.|

---

## Related Entity

- Project
    
- ProjectSpecification
    

---

## Acceptance Criteria

- 사용자는 자신의 Project 설정 정보를 수정할 수 있다.
    
- 다른 사용자의 Project 설정은 수정할 수 없다.
    
- `ARCHIVED` 상태의 Project 설정은 수정할 수 없다.
    
- patternMethod는 허용된 값만 저장할 수 있다.
    
- 완성 가로 및 세로 길이는 0보다 큰 값만 저장할 수 있다.
    

---

# PRJ-009 참고자료 등록

## 목적

사용자는 Project에 참고한 외부 URL 또는 자료를 등록할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 대상 Project가 존재해야 한다.
    
- 대상 Project는 현재 User의 소유여야 한다.
    
- Project 상태가 참고자료 등록 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project 설정 또는 참고자료 영역에서 URL을 추가한다.

---

## Main Flow

1. 사용자가 참고자료 등록 화면 또는 입력 영역에 진입한다.
    
2. 사용자가 URL, 제목, 메모를 입력한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Project 존재 여부를 확인한다.
    
6. 서버는 Project 소유자가 현재 User인지 확인한다.
    
7. 서버는 입력값을 검증한다.
    
8. 서버는 ProjectReference를 생성한다.
    
9. 서버는 생성된 ProjectReference 정보를 반환한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|url|필수|참고자료 URL|
|title|선택|참고자료 제목|
|memo|선택|참고자료 메모|
|sortOrder|선택|정렬 순서|

---

## Output

|항목|설명|
|---|---|
|projectReferenceId|ProjectReference 식별자|
|projectId|Project 식별자|
|url|참고자료 URL|
|title|참고자료 제목|
|memo|참고자료 메모|
|sortOrder|정렬 순서|
|createdAt|생성 일시|

---

## Business Rules

- 사용자는 자신의 Project에만 참고자료를 등록할 수 있다.
    
- `ARCHIVED` 상태의 Project에는 참고자료를 등록할 수 없다.
    
- url은 필수값이다.
    
- url은 URL 형식이어야 한다.
    
- 하나의 Project는 여러 ProjectReference를 가질 수 있다.
    
- 동일 Project에 동일 URL을 중복 등록할 수 있다. 단, 클라이언트에서 사용자에게 중복 가능성을 안내할 수 있다.
    
- ProjectReference는 ProjectSpecification 하위 정보로 관리한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-030|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-031|Project 접근 권한 없음|해당 프로젝트에 참고자료를 등록할 수 없습니다.|
|PRJ-032|URL 누락|참고자료 URL을 입력해주세요.|
|PRJ-033|URL 형식 오류|올바른 URL 형식이 아닙니다.|
|PRJ-034|Archived Project 참고자료 등록 시도|보관된 프로젝트에는 참고자료를 등록할 수 없습니다.|

---

## Related Entity

- Project
    
- ProjectSpecification
    
- ProjectReference
    

---

## Acceptance Criteria

- 사용자는 자신의 Project에 참고자료를 등록할 수 있다.
    
- 다른 사용자의 Project에는 참고자료를 등록할 수 없다.
    
- URL이 없으면 참고자료를 등록할 수 없다.
    
- URL 형식이 올바르지 않으면 참고자료를 등록할 수 없다.
    
- `ARCHIVED` 상태의 Project에는 참고자료를 등록할 수 없다.
    

---

# PRJ-010 참고자료 수정

## 목적

사용자는 Project에 등록한 참고자료의 정보를 수정할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 수정 대상 ProjectReference가 존재해야 한다.
    
- ProjectReference가 속한 Project는 현재 User의 소유여야 한다.
    
- Project 상태가 참고자료 수정 가능한 상태여야 한다.
    

---

## Trigger

사용자가 참고자료 목록에서 특정 참고자료를 수정한다.

---

## Main Flow

1. 사용자가 참고자료 수정 화면 또는 입력 영역에 진입한다.
    
2. 사용자가 URL, 제목, 메모, 정렬 순서를 수정한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 ProjectReference 존재 여부를 확인한다.
    
6. 서버는 ProjectReference가 속한 Project의 소유자가 현재 User인지 확인한다.
    
7. 서버는 입력값을 검증한다.
    
8. 서버는 ProjectReference 정보를 수정한다.
    
9. 서버는 수정된 ProjectReference 정보를 반환한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|url|선택|참고자료 URL|
|title|선택|참고자료 제목|
|memo|선택|참고자료 메모|
|sortOrder|선택|정렬 순서|

---

## Business Rules

- 사용자는 자신의 Project에 속한 ProjectReference만 수정할 수 있다.
    
- `ARCHIVED` 상태의 Project에 속한 ProjectReference는 수정할 수 없다.
    
- url을 수정하는 경우 URL 형식이어야 한다.
    
- title, memo는 비어 있을 수 있다.
    
- sortOrder는 참고자료 표시 순서에 사용된다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-035|ProjectReference 없음|참고자료를 찾을 수 없습니다.|
|PRJ-036|ProjectReference 접근 권한 없음|해당 참고자료를 수정할 수 없습니다.|
|PRJ-037|URL 형식 오류|올바른 URL 형식이 아닙니다.|
|PRJ-038|Archived Project 참고자료 수정 시도|보관된 프로젝트의 참고자료는 수정할 수 없습니다.|

---

## Related Entity

- Project
    
- ProjectSpecification
    
- ProjectReference
    

---

## Acceptance Criteria

- 사용자는 자신의 ProjectReference를 수정할 수 있다.
    
- 다른 사용자의 ProjectReference는 수정할 수 없다.
    
- URL 수정 시 올바른 URL 형식이어야 한다.
    
- `ARCHIVED` 상태의 ProjectReference는 수정할 수 없다.
    

---

# PRJ-011 참고자료 삭제

## 목적

사용자는 Project에 등록한 참고자료를 삭제할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 삭제 대상 ProjectReference가 존재해야 한다.
    
- ProjectReference가 속한 Project는 현재 User의 소유여야 한다.
    
- Project 상태가 참고자료 삭제 가능한 상태여야 한다.
    

---

## Trigger

사용자가 참고자료 목록에서 특정 참고자료 삭제를 선택하고 확인 절차를 완료한다.

---

## Main Flow

1. 사용자가 참고자료 삭제를 선택한다.
    
2. 클라이언트는 삭제 확인 절차를 진행한다.
    
3. 클라이언트는 참고자료 삭제 요청을 보낸다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 ProjectReference 존재 여부를 확인한다.
    
6. 서버는 ProjectReference가 속한 Project의 소유자가 현재 User인지 확인한다.
    
7. 서버는 ProjectReference를 삭제한다.
    
8. 서버는 삭제 완료 응답을 반환한다.
    
9. 클라이언트는 참고자료 목록에서 해당 항목을 제거한다.
    

---

## Output

|항목|설명|
|---|---|
|success|삭제 처리 결과|

---

## Business Rules

- 사용자는 자신의 Project에 속한 ProjectReference만 삭제할 수 있다.
    
- `ARCHIVED` 상태의 Project에 속한 ProjectReference는 삭제할 수 없다.
    
- ProjectReference 삭제 시 Project는 삭제되지 않는다.
    
- ProjectReference 삭제 시 다른 참고자료에는 영향을 주지 않는다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-039|ProjectReference 없음|참고자료를 찾을 수 없습니다.|
|PRJ-040|ProjectReference 접근 권한 없음|해당 참고자료를 삭제할 수 없습니다.|
|PRJ-041|Archived Project 참고자료 삭제 시도|보관된 프로젝트의 참고자료는 삭제할 수 없습니다.|

---

## Related Entity

- Project
    
- ProjectSpecification
    
- ProjectReference
    

---

## Acceptance Criteria

- 사용자는 자신의 ProjectReference를 삭제할 수 있다.
    
- 다른 사용자의 ProjectReference는 삭제할 수 없다.
    
- 삭제된 참고자료는 목록에서 조회되지 않는다.
    
- `ARCHIVED` 상태의 ProjectReference는 삭제할 수 없다.
    

---

# PRJ-012 프로젝트 원단 연결

## 목적

사용자는 Project에 사용할 Fabric을 연결할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 대상 Project가 존재해야 한다.
    
- 대상 Fabric이 존재해야 한다.
    
- 대상 Project와 Fabric은 모두 현재 User의 소유여야 한다.
    
- Project 상태가 원단 연결 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project 상세 화면에서 원단 추가 또는 연결 버튼을 선택한다.

---

## Main Flow

1. 사용자가 Project 상세 화면에서 원단 연결을 선택한다.
    
2. 클라이언트는 사용자의 Fabric 목록을 보여준다.
    
3. 사용자가 연결할 Fabric을 선택한다.
    
4. 사용자가 연결 메모를 입력할 수 있다.
    
5. 클라이언트는 Project와 Fabric 연결 요청을 보낸다.
    
6. 서버는 사용자의 인증 정보를 확인한다.
    
7. 서버는 Project 존재 여부를 확인한다.
    
8. 서버는 Fabric 존재 여부를 확인한다.
    
9. 서버는 Project와 Fabric이 모두 현재 User의 소유인지 확인한다.
    
10. 서버는 동일 Project와 Fabric 연결이 이미 존재하는지 확인한다.
    
11. 서버는 ProjectFabric을 생성한다.
    
12. 서버는 생성된 ProjectFabric 정보를 반환한다.
    
13. 클라이언트는 Project 상세 화면에 연결된 Fabric을 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|fabricId|필수|연결할 Fabric 식별자|
|memo|선택|해당 Project에서 Fabric을 사용한 맥락|

---

## Output

|항목|설명|
|---|---|
|projectFabricId|ProjectFabric 식별자|
|projectId|Project 식별자|
|fabricId|Fabric 식별자|
|fabricName|원단명|
|fabricThumbnailUrl|원단 대표 이미지 URL|
|memo|연결 메모|
|createdAt|생성 일시|

---

## Business Rules

- 사용자는 자신의 Project에 자신의 Fabric만 연결할 수 있다.
    
- 다른 사용자의 Fabric은 연결할 수 없다.
    
- `ARCHIVED` 상태의 Project에는 Fabric을 새로 연결할 수 없다.
    
- 동일 Project에 동일 Fabric은 중복 연결할 수 없다.
    
- ProjectFabric은 단순 조인 정보가 아니라 연결 메모를 가질 수 있는 엔티티이다.
    
- Fabric을 Project에 연결해도 Fabric 자체 정보는 변경되지 않는다.
    
- Project에 Fabric을 연결해도 DailyLog는 자동 생성되지 않는다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-042|Project 없음|프로젝트를 찾을 수 없습니다.|
|PRJ-043|Fabric 없음|원단을 찾을 수 없습니다.|
|PRJ-044|Project 접근 권한 없음|해당 프로젝트에 원단을 연결할 수 없습니다.|
|PRJ-045|Fabric 접근 권한 없음|해당 원단을 연결할 수 없습니다.|
|PRJ-046|중복 연결|이미 연결된 원단입니다.|
|PRJ-047|Archived Project 원단 연결 시도|보관된 프로젝트에는 원단을 연결할 수 없습니다.|

---

## Related Entity

- Project
    
- Fabric
    
- ProjectFabric
    

---

## Acceptance Criteria

- 사용자는 자신의 Project에 자신의 Fabric을 연결할 수 있다.
    
- 다른 사용자의 Fabric은 연결할 수 없다.
    
- 동일 Project에 동일 Fabric은 한 번만 연결할 수 있다.
    
- 연결 메모를 저장할 수 있다.
    
- `ARCHIVED` 상태의 Project에는 Fabric을 연결할 수 없다.
    

---

# PRJ-013 프로젝트 원단 연결 수정

## 목적

사용자는 Project와 Fabric의 연결 메모를 수정할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 수정 대상 ProjectFabric이 존재해야 한다.
    
- ProjectFabric이 속한 Project는 현재 User의 소유여야 한다.
    
- Project 상태가 원단 연결 수정 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project 상세 화면에서 연결된 원단의 메모를 수정한다.

---

## Main Flow

1. 사용자가 연결된 원단 항목의 수정 버튼을 선택한다.
    
2. 사용자가 연결 메모를 수정한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 ProjectFabric 존재 여부를 확인한다.
    
6. 서버는 ProjectFabric이 속한 Project의 소유자가 현재 User인지 확인한다.
    
7. 서버는 Project 상태를 확인한다.
    
8. 서버는 ProjectFabric의 memo를 수정한다.
    
9. 서버는 수정된 ProjectFabric 정보를 반환한다.
    
10. 클라이언트는 수정된 메모를 화면에 반영한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|memo|선택|해당 Project에서 Fabric을 사용한 맥락|

---

## Output

수정된 ProjectFabric 정보

---

## Business Rules

- 사용자는 자신의 Project에 속한 ProjectFabric만 수정할 수 있다.
    
- `ARCHIVED` 상태의 Project에 속한 ProjectFabric은 수정할 수 없다.
    
- ProjectFabric 수정은 연결 메모만 대상으로 한다.
    
- 연결된 Fabric 자체의 정보는 수정하지 않는다.
    
- 연결된 Project 자체의 정보는 수정하지 않는다.
    
- 연결 대상 Fabric을 다른 Fabric으로 교체하는 기능은 MVP에서 제공하지 않는다.
    
- Fabric을 바꾸려면 기존 연결을 삭제한 뒤 새로 연결해야 한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-048|ProjectFabric 없음|프로젝트 원단 연결 정보를 찾을 수 없습니다.|
|PRJ-049|ProjectFabric 접근 권한 없음|해당 원단 연결 정보를 수정할 수 없습니다.|
|PRJ-050|Archived Project 원단 연결 수정 시도|보관된 프로젝트의 원단 연결 정보는 수정할 수 없습니다.|

---

## Related Entity

- Project
    
- Fabric
    
- ProjectFabric
    

---

## Acceptance Criteria

- 사용자는 자신의 ProjectFabric 메모를 수정할 수 있다.
    
- 다른 사용자의 ProjectFabric은 수정할 수 없다.
    
- 연결된 Fabric 자체 정보는 수정되지 않는다.
    
- `ARCHIVED` 상태의 ProjectFabric은 수정할 수 없다.
    
- Fabric 교체는 수정 기능으로 처리하지 않는다.
    

---

# PRJ-014 프로젝트 원단 연결 삭제

## 목적

사용자는 Project와 Fabric의 연결을 해제할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 삭제 대상 ProjectFabric이 존재해야 한다.
    
- ProjectFabric이 속한 Project는 현재 User의 소유여야 한다.
    
- Project 상태가 원단 연결 삭제 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project 상세 화면에서 연결된 원단 삭제를 선택하고 확인 절차를 완료한다.

---

## Main Flow

1. 사용자가 연결된 원단 삭제를 선택한다.
    
2. 클라이언트는 삭제 확인 절차를 진행한다.
    
3. 클라이언트는 ProjectFabric 삭제 요청을 보낸다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 ProjectFabric 존재 여부를 확인한다.
    
6. 서버는 ProjectFabric이 속한 Project의 소유자가 현재 User인지 확인한다.
    
7. 서버는 Project 상태를 확인한다.
    
8. 서버는 ProjectFabric을 삭제한다.
    
9. 서버는 삭제 완료 응답을 반환한다.
    
10. 클라이언트는 Project 상세 화면에서 해당 원단 연결을 제거한다.
    

---

## Output

|항목|설명|
|---|---|
|success|삭제 처리 결과|

---

## Business Rules

- 사용자는 자신의 Project에 속한 ProjectFabric만 삭제할 수 있다.
    
- `ARCHIVED` 상태의 Project에 속한 ProjectFabric은 삭제할 수 없다.
    
- ProjectFabric 삭제 시 Project는 삭제되지 않는다.
    
- ProjectFabric 삭제 시 Fabric은 삭제되지 않는다.
    
- 연결 삭제는 Project와 Fabric의 관계만 제거한다.
    
- 연결 삭제 후 Fabric 상세의 사용 프로젝트 목록에서도 해당 Project가 제외되어야 한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PRJ-051|ProjectFabric 없음|프로젝트 원단 연결 정보를 찾을 수 없습니다.|
|PRJ-052|ProjectFabric 접근 권한 없음|해당 원단 연결 정보를 삭제할 수 없습니다.|
|PRJ-053|Archived Project 원단 연결 삭제 시도|보관된 프로젝트의 원단 연결 정보는 삭제할 수 없습니다.|

---

## Related Entity

- Project
    
- Fabric
    
- ProjectFabric
    

---

## Acceptance Criteria

- 사용자는 자신의 ProjectFabric을 삭제할 수 있다.
    
- 다른 사용자의 ProjectFabric은 삭제할 수 없다.
    
- 연결 삭제 시 Project는 삭제되지 않는다.
    
- 연결 삭제 시 Fabric은 삭제되지 않는다.
    
- 삭제된 연결은 Project 상세와 Fabric 상세에서 더 이상 조회되지 않는다.
    
- `ARCHIVED` 상태의 ProjectFabric은 삭제할 수 없다.
    

---

# 3.2 Project 요약

Project 도메인은 사용자가 제작하는 하나의 재봉 작품을 관리하는 핵심 도메인이다.

본 장에서 정의한 핵심 정책은 다음과 같다.

- Project는 User 소유 데이터이다.
    
- 사용자는 자신의 Project만 조회, 수정, 삭제할 수 있다.
    
- Project 생성 시 기본 ProjectSpecification이 함께 생성된다.
    
- Project 상태는 `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `ARCHIVED`를 사용한다.
    
- `ON_HOLD` 상태에서도 DailyLog 작성은 가능하다.
    
- `ARCHIVED` 상태에서는 기본 정보 수정, DailyLog 추가, 참고자료 변경, 원단 연결 변경을 제한한다.
    
- ProjectSpecification은 Project당 하나만 존재한다.
    
- ProjectReference는 ProjectSpecification 하위의 참고자료로 관리한다.
    
- Project와 Fabric은 ProjectFabric을 통해 연결한다.
    
- ProjectFabric은 단순 조인 테이블이 아니라 연결 메모를 가지는 엔티티이다.
    
- 동일 Project에 동일 Fabric은 중복 연결할 수 없다.
    
- Project 삭제 시 ProjectFabric 연결은 삭제되지만, Fabric 자체는 삭제되지 않는다.

    

# 3.3 DailyLog

## 3.3.1 개요

DailyLog 도메인은 사용자가 특정 날짜에 특정 Project에서 수행한 재봉 작업을 기록하는 기능을 담당한다.

소로소로에서 Project가 하나의 작품을 의미한다면, DailyLog는 그 작품을 만들어 가는 날짜별 작업 기록이다. 사용자는 하나의 Project에 여러 DailyLog를 작성할 수 있으며, 하루에 여러 Project를 작업한 경우 같은 날짜에 여러 DailyLog를 작성할 수 있다.

DailyLog는 재봉 작업 직후 모바일 환경에서 작성될 가능성이 높다. 따라서 사용자가 작성 중 이탈하더라도 기록을 잃지 않도록 임시저장 기능을 제공한다.

DailyLog는 다음 정보를 기록한다.

- 어떤 날짜에 작업했는가
    
- 어떤 Project에서 작업했는가
    
- 어떤 종류의 작업을 했는가
    
- 몇 시부터 몇 시까지 작업했는가
    
- 총 작업 시간이 얼마인가
    
- 어떤 사진을 남겼는가
    
- 어떤 메모를 남겼는가
    

DailyLog 도메인은 다음 하위 개념을 포함한다.

|개념|설명|
|---|---|
|DailyLog|특정 날짜에 특정 Project에서 수행한 작업 기록|
|DailyLogTimeEntry|하나의 DailyLog 안에서 실제 작업한 시간 구간|
|WorkType|해당 DailyLog에서 수행한 작업 종류|
|DailyLogStatus|DailyLog의 저장 상태|

DailyLog는 User 소유 데이터이며, 반드시 하나의 Project에 속한다.

---

## 3.3.2 기능 목록

|ID|기능명|Priority|설명|
|---|---|---|---|
|DL-001|DailyLog 생성|High|특정 Project에 날짜별 작업 일지를 생성한다.|
|DL-002|DailyLog 임시저장|High|작성 중인 DailyLog를 임시저장한다.|
|DL-003|Project별 DailyLog 목록 조회|High|특정 Project에 속한 DailyLog 목록을 조회한다.|
|DL-004|날짜별 DailyLog 목록 조회|High|특정 날짜 또는 기간의 DailyLog 목록을 조회한다.|
|DL-005|DailyLog 상세 조회|High|특정 DailyLog의 상세 정보를 조회한다.|
|DL-006|DailyLog 수정|High|DailyLog의 작업 내용, 작업 종류, 시간 구간, 메모를 수정한다.|
|DL-007|DailyLog 삭제|Medium|DailyLog를 삭제한다.|
|DL-008|DailyLog 발행|High|임시저장된 DailyLog를 정식 기록으로 전환한다.|

---

## 3.3.3 DailyLogStatus

DailyLog는 다음 상태를 가진다.

|상태|설명|
|---|---|
|DRAFT|임시저장 상태|
|PUBLISHED|정식 기록 상태|

### DRAFT

`DRAFT`는 사용자가 DailyLog를 작성 중인 상태이다.

DRAFT 상태에서는 모든 필수값이 완성되지 않아도 저장할 수 있다.

예를 들어 사용자가 메모만 먼저 작성하거나, 사진만 먼저 업로드하거나, 작업 날짜만 선택한 상태에서도 임시저장할 수 있다.

DRAFT 상태의 DailyLog는 다음 계산에 포함하지 않는다.

- Project 총 작업 시간
    
- 월별 캘린더 작업 시간
    
- 재봉 잔디 작업 시간
    
- 작업 통계
    

### PUBLISHED

`PUBLISHED`는 정식 기록으로 발행된 상태이다.

PUBLISHED 상태가 되려면 최소한 다음 값이 필요하다.

- workedDate
    
- timeEntries 1개 이상
    

PUBLISHED 상태의 DailyLog만 다음 계산에 포함한다.

- Project 총 작업 시간
    
- 월별 캘린더 작업 시간
    
- 재봉 잔디 작업 시간
    
- 작업 통계
    

---

## 3.3.4 WorkType

DailyLog의 작업 종류는 다음 값을 사용할 수 있다.

|값|설명|
|---|---|
|PATTERN_DRAFTING|패턴 그리기|
|CUTTING|재단|
|INTERFACING|심지 부착|
|SEWING|재봉|
|PRESSING|다림질|
|FITTING|착용 또는 핏 확인|
|FIXING|수정 또는 보완|
|FINISHING|마감|
|ETC|기타|

WorkType은 여러 개 선택할 수 있다.

예를 들어 하루에 재단과 심지 부착을 함께 했다면 `CUTTING`, `INTERFACING`을 함께 선택할 수 있다.

---

# DL-001 DailyLog 생성

## 목적

사용자는 특정 Project에 날짜별 재봉 작업 기록을 생성할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    
- 대상 Project가 존재해야 한다.
    
- 대상 Project는 현재 User의 소유여야 한다.
    
- 대상 Project 상태가 DailyLog 작성 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project 상세 화면 또는 캘린더 화면에서 작업 일지 작성 버튼을 선택하고 저장한다.

---

## Main Flow

1. 사용자가 DailyLog 작성 화면에 진입한다.
    
2. 사용자가 작업 날짜를 입력한다.
    
3. 사용자가 작업 종류를 선택한다.
    
4. 사용자가 작업 시간 구간을 하나 이상 입력한다.
    
5. 사용자가 작업 메모를 입력한다.
    
6. 사용자가 저장 버튼을 선택한다.
    
7. 서버는 사용자의 인증 정보를 확인한다.
    
8. 서버는 Project 존재 여부를 확인한다.
    
9. 서버는 Project 소유자가 현재 User인지 확인한다.
    
10. 서버는 Project 상태가 DailyLog 작성 가능한 상태인지 확인한다.
    
11. 서버는 입력값을 검증한다.
    
12. 서버는 DailyLog를 `PUBLISHED` 상태로 생성한다.
    
13. 서버는 DailyLogTimeEntry를 생성한다.
    
14. 서버는 DailyLogTimeEntry의 durationMinutes를 합산하여 DailyLog.durationMinutes를 계산한다.
    
15. 서버는 생성된 DailyLog 정보를 반환한다.
    
16. 클라이언트는 DailyLog 상세 화면 또는 Project 상세 화면으로 이동한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|projectId|필수|DailyLog를 작성할 Project 식별자|
|workedDate|필수|작업 날짜|
|title|선택|작업 일지 제목|
|workTypes|선택|작업 종류 목록|
|memo|선택|작업 메모|
|timeEntries|필수|작업 시간 구간 목록|

### timeEntries

|항목|필수 여부|설명|
|---|---|---|
|startedAt|필수|작업 시작 시각|
|endedAt|필수|작업 종료 시각|
|memo|선택|해당 시간 구간에 대한 메모|

---

## Output

|항목|설명|
|---|---|
|dailyLogId|생성된 DailyLog 식별자|
|projectId|Project 식별자|
|projectTitle|Project 제목|
|status|DailyLog 상태|
|workedDate|작업 날짜|
|title|작업 일지 제목|
|workTypes|작업 종류 목록|
|durationMinutes|총 작업 시간|
|memo|작업 메모|
|timeEntries|작업 시간 구간 목록|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- DailyLog는 반드시 하나의 Project에 속한다.
    
- DailyLog는 반드시 User에 속한다.
    
- 사용자는 자신의 Project에만 DailyLog를 작성할 수 있다.
    
- 다른 사용자의 Project에는 DailyLog를 작성할 수 없다.
    
- DailyLog 생성 기능은 정식 기록인 `PUBLISHED` 상태로 생성한다.
    
- `IN_PROGRESS`, `ON_HOLD`, `COMPLETED` 상태의 Project에는 DailyLog를 작성할 수 있다.
    
- `ARCHIVED` 상태의 Project에는 DailyLog를 작성할 수 없다.
    
- `PUBLISHED` 상태의 DailyLog는 workedDate가 필수이다.
    
- `PUBLISHED` 상태의 DailyLog는 timeEntries가 최소 1개 이상 필요하다.
    
- 각 TimeEntry의 endedAt은 startedAt보다 늦어야 한다.
    
- DailyLog.durationMinutes는 사용자가 직접 입력하지 않는다.
    
- DailyLog.durationMinutes는 DailyLogTimeEntry의 durationMinutes 합산값으로 계산한다.
    
- workTypes는 여러 개 선택할 수 있다.
    
- workTypes가 비어 있는 경우에도 DailyLog는 생성할 수 있다.
    
- DailyLog 생성 시 Photo는 자동 생성하지 않는다.
    
- DailyLog 사진은 Photo 도메인에서 별도로 업로드한다.
    
- 하나의 DailyLog는 최대 15장의 사진을 가질 수 있다.
    
- 하나의 DailyLog는 대표 사진을 1장 가질 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-001|Project 없음|프로젝트를 찾을 수 없습니다.|
|DL-002|Project 접근 권한 없음|해당 프로젝트에 일지를 작성할 수 없습니다.|
|DL-003|workedDate 누락|작업 날짜를 입력해주세요.|
|DL-004|TimeEntry 없음|작업 시간 구간을 1개 이상 입력해주세요.|
|DL-005|TimeEntry 시간 오류|작업 종료 시각은 시작 시각보다 늦어야 합니다.|
|DL-006|유효하지 않은 WorkType|유효하지 않은 작업 종류입니다.|
|DL-007|Archived Project 작성 시도|보관된 프로젝트에는 일지를 작성할 수 없습니다.|
|DL-008|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- Project
    
- DailyLog
    
- DailyLogTimeEntry
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Project에 DailyLog를 생성할 수 있다.
    
- 다른 사용자의 Project에는 DailyLog를 생성할 수 없다.
    
- DailyLog 생성 시 상태는 `PUBLISHED`가 된다.
    
- PUBLISHED DailyLog 생성 시 workedDate가 필요하다.
    
- PUBLISHED DailyLog 생성 시 하나 이상의 TimeEntry가 필요하다.
    
- DailyLog.durationMinutes는 TimeEntry 합산값으로 저장된다.
    
- `ARCHIVED` 상태의 Project에는 DailyLog를 생성할 수 없다.
    
- `ON_HOLD` 상태의 Project에는 DailyLog를 생성할 수 있다.
    

---

# DL-002 DailyLog 임시저장

## 목적

사용자는 작성 중인 DailyLog를 정식 기록으로 발행하기 전에 임시저장할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    
- 대상 Project가 존재해야 한다.
    
- 대상 Project는 현재 User의 소유여야 한다.
    
- 대상 Project 상태가 DailyLog 임시저장 가능한 상태여야 한다.
    

---

## Trigger

사용자가 DailyLog 작성 중 임시저장 버튼을 선택하거나, 클라이언트가 작성 중인 내용을 저장해야 하는 상황이 발생한다.

---

## Main Flow

1. 사용자가 DailyLog 작성 화면에 진입한다.
    
2. 사용자가 작업 날짜, 작업 종류, 시간 구간, 메모, 사진 등을 일부 입력한다.
    
3. 사용자가 임시저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Project 존재 여부를 확인한다.
    
6. 서버는 Project 소유자가 현재 User인지 확인한다.
    
7. 서버는 Project 상태가 DailyLog 임시저장 가능한 상태인지 확인한다.
    
8. 서버는 입력 가능한 값을 저장한다.
    
9. 서버는 DailyLog를 `DRAFT` 상태로 저장한다.
    
10. 서버는 저장된 DailyLog 정보를 반환한다.
    
11. 클라이언트는 임시저장 완료 상태를 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|projectId|필수|DailyLog를 임시저장할 Project 식별자|
|workedDate|선택|작업 날짜|
|title|선택|작업 일지 제목|
|workTypes|선택|작업 종류 목록|
|memo|선택|작업 메모|
|timeEntries|선택|작업 시간 구간 목록|

### timeEntries

|항목|필수 여부|설명|
|---|---|---|
|startedAt|조건부|작업 시작 시각|
|endedAt|조건부|작업 종료 시각|
|memo|선택|해당 시간 구간에 대한 메모|

---

## Output

|항목|설명|
|---|---|
|dailyLogId|임시저장된 DailyLog 식별자|
|projectId|Project 식별자|
|status|DailyLog 상태. DRAFT|
|workedDate|작업 날짜|
|title|작업 일지 제목|
|workTypes|작업 종류 목록|
|durationMinutes|총 작업 시간|
|memo|작업 메모|
|timeEntries|작업 시간 구간 목록|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- DailyLog 임시저장 시 상태는 `DRAFT`이다.
    
- DRAFT 상태에서는 workedDate가 없어도 저장할 수 있다.
    
- DRAFT 상태에서는 timeEntries가 없어도 저장할 수 있다.
    
- DRAFT 상태에서는 workTypes가 없어도 저장할 수 있다.
    
- DRAFT 상태에서도 Project는 필수이다.
    
- DRAFT 상태에서도 User 소유 Project에만 저장할 수 있다.
    
- DRAFT 상태에서도 TimeEntry가 입력된 경우 endedAt은 startedAt보다 늦어야 한다.
    
- DRAFT 상태의 DailyLog는 Project 총 작업 시간에 포함하지 않는다.
    
- DRAFT 상태의 DailyLog는 캘린더 작업 시간에 포함하지 않는다.
    
- DRAFT 상태의 DailyLog는 재봉 잔디에 포함하지 않는다.
    
- DRAFT 상태의 DailyLog는 일반 DailyLog 목록에서 기본적으로 제외할 수 있다.
    
- DRAFT 상태의 DailyLog는 사용자가 이어서 작성할 수 있어야 한다.
    
- `ARCHIVED` 상태의 Project에는 DRAFT DailyLog를 생성할 수 없다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-009|Project 없음|프로젝트를 찾을 수 없습니다.|
|DL-010|Project 접근 권한 없음|해당 프로젝트에 일지를 임시저장할 수 없습니다.|
|DL-011|TimeEntry 시간 오류|작업 종료 시각은 시작 시각보다 늦어야 합니다.|
|DL-012|유효하지 않은 WorkType|유효하지 않은 작업 종류입니다.|
|DL-013|Archived Project 임시저장 시도|보관된 프로젝트에는 일지를 임시저장할 수 없습니다.|

---

## Related Entity

- User
    
- Project
    
- DailyLog
    
- DailyLogTimeEntry
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 DailyLog를 DRAFT 상태로 임시저장할 수 있다.
    
- DRAFT 상태에서는 workedDate 없이 저장할 수 있다.
    
- DRAFT 상태에서는 timeEntries 없이 저장할 수 있다.
    
- DRAFT 상태의 DailyLog는 Project 총 작업 시간에 포함되지 않는다.
    
- DRAFT 상태의 DailyLog는 캘린더와 재봉 잔디에 포함되지 않는다.
    
- 사용자는 DRAFT 상태의 DailyLog를 다시 열어 이어서 작성할 수 있다.
    
- `ARCHIVED` 상태의 Project에는 DRAFT DailyLog를 생성할 수 없다.
    

---

# DL-003 Project별 DailyLog 목록 조회

## 목적

사용자는 특정 Project에 작성된 DailyLog 목록을 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 대상 Project가 존재해야 한다.
    
- 대상 Project는 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 Project 상세 화면에서 작업 일지 목록 영역을 조회한다.

---

## Main Flow

1. 클라이언트는 특정 Project의 DailyLog 목록 조회를 요청한다.
    
2. 서버는 사용자의 인증 정보를 확인한다.
    
3. 서버는 Project 존재 여부를 확인한다.
    
4. 서버는 Project 소유자가 현재 User인지 확인한다.
    
5. 서버는 해당 Project에 속한 DailyLog 목록을 조회한다.
    
6. 서버는 정렬 및 페이징 조건을 적용한다.
    
7. 서버는 DailyLog 목록을 반환한다.
    
8. 클라이언트는 Project 상세 화면에 DailyLog 목록을 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|projectId|필수|DailyLog 목록을 조회할 Project 식별자|
|status|선택|DailyLog 상태 필터|
|from|선택|조회 시작 날짜|
|to|선택|조회 종료 날짜|
|workType|선택|작업 종류 필터|
|page|선택|페이지 번호|
|size|선택|페이지 크기|
|sort|선택|정렬 기준|

---

## Output

|항목|설명|
|---|---|
|dailyLogId|DailyLog 식별자|
|projectId|Project 식별자|
|status|DailyLog 상태|
|workedDate|작업 날짜|
|title|작업 일지 제목|
|workTypes|작업 종류 목록|
|durationMinutes|총 작업 시간|
|thumbnailUrl|대표 이미지 URL|
|memoPreview|메모 일부|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- 사용자는 자신의 Project에 속한 DailyLog만 조회할 수 있다.
    
- 다른 사용자의 Project에 속한 DailyLog는 조회할 수 없다.
    
- 기본 조회는 `PUBLISHED` 상태의 DailyLog를 대상으로 한다.
    
- DRAFT 상태의 DailyLog는 status 필터 또는 임시저장 목록 조회 의도가 있을 때 조회할 수 있다.
    
- 기본 정렬은 workedDate 내림차순, createdAt 내림차순이다.
    
- 기간 조건은 `PUBLISHED` 상태 DailyLog의 workedDate 기준으로 적용한다.
    
- DRAFT 상태에서 workedDate가 없는 경우 기간 조건 조회 결과에 포함되지 않는다.
    
- workType 조건이 있는 경우 해당 WorkType을 포함한 DailyLog만 조회한다.
    
- 대표 이미지가 없는 경우 thumbnailUrl은 비어 있을 수 있다.
    
- 목록 조회에서는 TimeEntry 상세 목록을 반환하지 않는다.
    
- TimeEntry 상세는 DailyLog 상세 조회에서 반환한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-014|Project 없음|프로젝트를 찾을 수 없습니다.|
|DL-015|Project 접근 권한 없음|해당 프로젝트의 일지를 조회할 수 없습니다.|
|DL-016|유효하지 않은 날짜 범위|조회 시작일은 종료일보다 늦을 수 없습니다.|
|DL-017|유효하지 않은 WorkType|유효하지 않은 작업 종류입니다.|
|DL-018|유효하지 않은 DailyLogStatus|유효하지 않은 일지 상태입니다.|

---

## Related Entity

- Project
    
- DailyLog
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Project에 속한 DailyLog 목록을 조회할 수 있다.
    
- 다른 사용자의 Project에 속한 DailyLog 목록은 조회할 수 없다.
    
- 기본 목록에는 PUBLISHED 상태의 DailyLog가 조회된다.
    
- status 필터로 DRAFT 상태의 DailyLog를 조회할 수 있다.
    
- 기간 필터로 DailyLog 목록을 조회할 수 있다.
    
- WorkType 필터로 DailyLog 목록을 조회할 수 있다.
    
- 목록 응답에는 DailyLog 요약 정보가 포함된다.
    

---

# DL-004 날짜별 DailyLog 목록 조회

## 목적

사용자는 특정 날짜 또는 기간에 작성한 DailyLog 목록을 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 캘린더에서 특정 날짜를 선택하거나, 기간별 일지 목록 화면에 진입한다.

---

## Main Flow

1. 사용자가 특정 날짜 또는 기간을 선택한다.
    
2. 클라이언트는 날짜 조건을 포함하여 DailyLog 목록 조회를 요청한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 현재 User가 작성한 DailyLog를 조회한다.
    
5. 서버는 날짜 또는 기간 조건을 적용한다.
    
6. 서버는 DailyLog 목록을 반환한다.
    
7. 클라이언트는 해당 날짜 또는 기간의 DailyLog 목록을 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|date|선택|단일 조회 날짜|
|from|선택|조회 시작 날짜|
|to|선택|조회 종료 날짜|
|projectId|선택|특정 Project 필터|
|workType|선택|작업 종류 필터|

---

## Output

|항목|설명|
|---|---|
|dailyLogId|DailyLog 식별자|
|projectId|Project 식별자|
|projectTitle|Project 제목|
|status|DailyLog 상태|
|workedDate|작업 날짜|
|title|작업 일지 제목|
|workTypes|작업 종류 목록|
|durationMinutes|총 작업 시간|
|thumbnailUrl|대표 이미지 URL|
|memoPreview|메모 일부|

---

## Business Rules

- 사용자는 자신의 DailyLog만 조회할 수 있다.
    
- 날짜별 조회는 기본적으로 `PUBLISHED` 상태의 DailyLog만 대상으로 한다.
    
- DRAFT 상태의 DailyLog는 날짜별 조회 기본 결과에 포함하지 않는다.
    
- date가 입력된 경우 해당 날짜의 DailyLog만 조회한다.
    
- from과 to가 입력된 경우 해당 기간의 DailyLog를 조회한다.
    
- date와 기간 조건을 동시에 사용하는 것은 허용하지 않는다.
    
- 날짜 조건이 없으면 기본적으로 최근 PUBLISHED DailyLog 목록을 조회할 수 있다.
    
- 특정 날짜에 여러 Project의 DailyLog가 존재할 수 있다.
    
- 특정 날짜에 같은 Project의 DailyLog가 여러 개 존재할 수 있다.
    
- DailyLog의 날짜 기준은 workedDate이다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-019|날짜 조건 오류|날짜 조건이 올바르지 않습니다.|
|DL-020|유효하지 않은 날짜 범위|조회 시작일은 종료일보다 늦을 수 없습니다.|
|DL-021|유효하지 않은 WorkType|유효하지 않은 작업 종류입니다.|
|DL-022|Project 접근 권한 없음|해당 프로젝트의 일지를 조회할 수 없습니다.|

---

## Related Entity

- User
    
- Project
    
- DailyLog
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 특정 날짜의 DailyLog 목록을 조회할 수 있다.
    
- 사용자는 특정 기간의 DailyLog 목록을 조회할 수 있다.
    
- 날짜별 조회 기본 결과에는 PUBLISHED 상태의 DailyLog만 포함된다.
    
- 같은 날짜에 여러 DailyLog가 존재할 수 있다.
    
- 다른 사용자의 DailyLog는 조회되지 않는다.
    
- 조회 기준 날짜는 workedDate이다.
    

---

# DL-005 DailyLog 상세 조회

## 목적

사용자는 특정 DailyLog의 상세 정보를 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 조회 대상 DailyLog가 존재해야 한다.
    
- 조회 대상 DailyLog는 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 DailyLog 목록에서 특정 일지를 선택한다.

---

## Main Flow

1. 사용자가 DailyLog를 선택한다.
    
2. 클라이언트는 DailyLog 상세 조회를 요청한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 DailyLog 존재 여부를 확인한다.
    
5. 서버는 DailyLog 소유자가 현재 User인지 확인한다.
    
6. 서버는 DailyLog 기본 정보, Project 요약 정보, TimeEntry 목록, Photo 목록을 조회한다.
    
7. 서버는 DailyLog 상세 정보를 반환한다.
    
8. 클라이언트는 DailyLog 상세 화면을 표시한다.
    

---

## Output

|항목|설명|
|---|---|
|dailyLogId|DailyLog 식별자|
|projectId|Project 식별자|
|projectTitle|Project 제목|
|status|DailyLog 상태|
|workedDate|작업 날짜|
|title|작업 일지 제목|
|workTypes|작업 종류 목록|
|durationMinutes|총 작업 시간|
|memo|작업 메모|
|timeEntries|작업 시간 구간 목록|
|photos|DailyLog 사진 목록|
|createdAt|생성 일시|
|updatedAt|수정 일시|

### timeEntries

|항목|설명|
|---|---|
|timeEntryId|TimeEntry 식별자|
|startedAt|작업 시작 시각|
|endedAt|작업 종료 시각|
|durationMinutes|작업 시간|
|memo|시간 구간 메모|

---

## Business Rules

- 사용자는 자신의 DailyLog만 상세 조회할 수 있다.
    
- 다른 사용자의 DailyLog는 상세 조회할 수 없다.
    
- DailyLog 상세에는 TimeEntry 목록이 포함된다.
    
- DailyLog 상세에는 Photo 목록이 포함될 수 있다.
    
- DailyLog 상세에는 Project 요약 정보가 포함된다.
    
- PUBLISHED 상태의 DailyLog는 durationMinutes가 TimeEntry 합산값과 일치해야 한다.
    
- DRAFT 상태의 DailyLog는 timeEntries가 없을 수 있다.
    
- DRAFT 상태의 DailyLog는 durationMinutes가 0일 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-023|DailyLog 없음|일지를 찾을 수 없습니다.|
|DL-024|DailyLog 접근 권한 없음|해당 일지에 접근할 수 없습니다.|
|DL-025|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- Project
    
- DailyLog
    
- DailyLogTimeEntry
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 DailyLog 상세 정보를 조회할 수 있다.
    
- 다른 사용자의 DailyLog는 조회할 수 없다.
    
- 상세 응답에는 TimeEntry 목록이 포함된다.
    
- 상세 응답에는 Photo 목록이 포함된다.
    
- DRAFT 상태의 DailyLog도 상세 조회할 수 있다.
    

---

# DL-006 DailyLog 수정

## 목적

사용자는 자신이 작성한 DailyLog의 작업 내용, 작업 종류, 작업 시간 구간, 메모를 수정할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 수정 대상 DailyLog가 존재해야 한다.
    
- 수정 대상 DailyLog는 현재 User의 소유여야 한다.
    
- DailyLog가 속한 Project 상태가 수정 가능한 상태여야 한다.
    

---

## Trigger

사용자가 DailyLog 상세 화면에서 수정 버튼을 선택하고 저장한다.

---

## Main Flow

1. 사용자가 DailyLog 수정 화면에 진입한다.
    
2. 사용자가 작업 날짜, 제목, 작업 종류, 시간 구간, 메모를 수정한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 DailyLog 존재 여부를 확인한다.
    
6. 서버는 DailyLog 소유자가 현재 User인지 확인한다.
    
7. 서버는 DailyLog가 속한 Project 상태를 확인한다.
    
8. 서버는 입력값을 검증한다.
    
9. 서버는 DailyLog 기본 정보를 수정한다.
    
10. 서버는 DailyLogTimeEntry 목록을 수정한다.
    
11. 서버는 TimeEntry의 durationMinutes를 다시 계산한다.
    
12. 서버는 DailyLog.durationMinutes를 다시 계산한다.
    
13. 서버는 수정된 DailyLog 정보를 반환한다.
    
14. 클라이언트는 수정된 정보를 화면에 반영한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|workedDate|선택|작업 날짜|
|title|선택|작업 일지 제목|
|workTypes|선택|작업 종류 목록|
|memo|선택|작업 메모|
|timeEntries|선택|작업 시간 구간 목록|

### timeEntries

|항목|필수 여부|설명|
|---|---|---|
|startedAt|필수|작업 시작 시각|
|endedAt|필수|작업 종료 시각|
|memo|선택|해당 시간 구간 메모|

---

## Output

수정된 DailyLog 상세 정보

---

## Business Rules

- 사용자는 자신의 DailyLog만 수정할 수 있다.
    
- 다른 사용자의 DailyLog는 수정할 수 없다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog는 수정할 수 없다.
    
- `IN_PROGRESS`, `ON_HOLD`, `COMPLETED` 상태의 Project에 속한 DailyLog는 수정할 수 있다.
    
- PUBLISHED 상태를 유지하는 경우 workedDate는 비워둘 수 없다.
    
- PUBLISHED 상태를 유지하는 경우 timeEntries는 최소 1개 이상이어야 한다.
    
- DRAFT 상태에서는 workedDate와 timeEntries가 없어도 수정할 수 있다.
    
- 각 TimeEntry의 endedAt은 startedAt보다 늦어야 한다.
    
- DailyLog.durationMinutes는 TimeEntry 수정 후 다시 계산되어야 한다.
    
- workTypes는 여러 개 선택할 수 있다.
    
- workTypes가 비어 있는 상태도 허용한다.
    
- DailyLog 수정 기능은 Photo 추가/삭제를 직접 처리하지 않는다.
    
- Photo 추가/삭제는 Photo 도메인 기능에서 처리한다.
    

---

## TimeEntry 수정 정책

MVP에서는 DailyLog 수정 시 timeEntries를 개별 PATCH하지 않고, 요청에 포함된 timeEntries 목록으로 전체 교체하는 방식을 사용한다.

즉, 사용자가 DailyLog 수정 화면에서 시간 구간을 수정하면 서버는 해당 DailyLog의 기존 TimeEntry 목록을 새로운 목록 기준으로 갱신한다.

세부 구현 방식은 Backend Design에서 정의한다.

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-026|DailyLog 없음|일지를 찾을 수 없습니다.|
|DL-027|DailyLog 접근 권한 없음|해당 일지를 수정할 수 없습니다.|
|DL-028|workedDate 누락|작업 날짜를 입력해주세요.|
|DL-029|TimeEntry 없음|작업 시간 구간을 1개 이상 입력해주세요.|
|DL-030|TimeEntry 시간 오류|작업 종료 시각은 시작 시각보다 늦어야 합니다.|
|DL-031|유효하지 않은 WorkType|유효하지 않은 작업 종류입니다.|
|DL-032|Archived Project 일지 수정 시도|보관된 프로젝트의 일지는 수정할 수 없습니다.|

---

## Related Entity

- Project
    
- DailyLog
    
- DailyLogTimeEntry
    

---

## Acceptance Criteria

- 사용자는 자신의 DailyLog를 수정할 수 있다.
    
- 다른 사용자의 DailyLog는 수정할 수 없다.
    
- DailyLog 수정 시 TimeEntry를 변경할 수 있다.
    
- TimeEntry 변경 후 DailyLog.durationMinutes가 다시 계산된다.
    
- DRAFT 상태에서는 workedDate와 timeEntries 없이도 수정할 수 있다.
    
- PUBLISHED 상태에서는 workedDate와 하나 이상의 TimeEntry가 필요하다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog는 수정할 수 없다.
    
- Photo 변경은 DailyLog 수정 기능에서 처리하지 않는다.
    

---

# DL-007 DailyLog 삭제

## 목적

사용자는 자신이 작성한 DailyLog를 삭제할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 삭제 대상 DailyLog가 존재해야 한다.
    
- 삭제 대상 DailyLog는 현재 User의 소유여야 한다.
    
- DailyLog가 속한 Project 상태가 삭제 가능한 상태여야 한다.
    

---

## Trigger

사용자가 DailyLog 상세 화면에서 삭제를 선택하고 확인 절차를 완료한다.

---

## Main Flow

1. 사용자가 DailyLog 삭제를 선택한다.
    
2. 클라이언트는 삭제 확인 절차를 진행한다.
    
3. 클라이언트는 DailyLog 삭제 요청을 보낸다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 DailyLog 존재 여부를 확인한다.
    
6. 서버는 DailyLog 소유자가 현재 User인지 확인한다.
    
7. 서버는 DailyLog가 속한 Project 상태를 확인한다.
    
8. 서버는 DailyLog를 삭제한다.
    
9. 서버는 삭제 완료 응답을 반환한다.
    
10. 클라이언트는 DailyLog 목록 또는 Project 상세 화면에서 해당 DailyLog를 제거한다.
    

---

## Output

|항목|설명|
|---|---|
|success|삭제 처리 결과|

---

## Business Rules

- 사용자는 자신의 DailyLog만 삭제할 수 있다.
    
- 다른 사용자의 DailyLog는 삭제할 수 없다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog는 삭제할 수 없다.
    
- DailyLog 삭제 시 DailyLogTimeEntry는 함께 삭제된다.
    
- DailyLog 삭제 시 해당 DailyLog에 연결된 Photo는 서비스 화면에서 더 이상 조회되지 않아야 한다.
    
- DailyLog 삭제 후 Project의 총 작업 시간 계산에서 해당 DailyLog의 durationMinutes는 제외되어야 한다.
    
- DailyLog 삭제 후 캘린더와 재봉 잔디 계산에서도 해당 작업 시간은 제외되어야 한다.
    
- DRAFT 상태의 DailyLog 삭제 시 통계에는 영향이 없다.
    
- 물리 삭제, 소프트 삭제, S3 Object 삭제 시점은 Backend Design 및 Architecture에서 정의한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-033|DailyLog 없음|일지를 찾을 수 없습니다.|
|DL-034|DailyLog 접근 권한 없음|해당 일지를 삭제할 수 없습니다.|
|DL-035|Archived Project 일지 삭제 시도|보관된 프로젝트의 일지는 삭제할 수 없습니다.|

---

## Related Entity

- Project
    
- DailyLog
    
- DailyLogTimeEntry
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 DailyLog를 삭제할 수 있다.
    
- 다른 사용자의 DailyLog는 삭제할 수 없다.
    
- DailyLog 삭제 시 TimeEntry도 함께 삭제된다.
    
- 삭제된 DailyLog는 목록과 상세 조회에서 제외된다.
    
- 삭제된 PUBLISHED DailyLog의 작업 시간은 Project 총 작업 시간, 캘린더, 재봉 잔디 계산에서 제외된다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog는 삭제할 수 없다.
    

---

# DL-008 DailyLog 발행

## 목적

사용자는 임시저장된 DailyLog를 정식 기록으로 전환할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 발행 대상 DailyLog가 존재해야 한다.
    
- 발행 대상 DailyLog는 현재 User의 소유여야 한다.
    
- 발행 대상 DailyLog는 `DRAFT` 상태여야 한다.
    
- DailyLog가 속한 Project 상태가 발행 가능한 상태여야 한다.
    

---

## Trigger

사용자가 임시저장된 DailyLog를 열고 저장 또는 발행 버튼을 선택한다.

---

## Main Flow

1. 사용자가 DRAFT 상태의 DailyLog를 연다.
    
2. 사용자가 workedDate와 timeEntries 등 필수값을 입력한다.
    
3. 사용자가 발행 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 DailyLog 존재 여부를 확인한다.
    
6. 서버는 DailyLog 소유자가 현재 User인지 확인한다.
    
7. 서버는 DailyLog가 DRAFT 상태인지 확인한다.
    
8. 서버는 DailyLog가 속한 Project 상태를 확인한다.
    
9. 서버는 발행에 필요한 필수값을 검증한다.
    
10. 서버는 TimeEntry의 durationMinutes를 계산한다.
    
11. 서버는 DailyLog.durationMinutes를 계산한다.
    
12. 서버는 DailyLog 상태를 `PUBLISHED`로 변경한다.
    
13. 서버는 발행된 DailyLog 정보를 반환한다.
    
14. 클라이언트는 DailyLog 상세 화면 또는 Project 상세 화면으로 이동한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|workedDate|필수|작업 날짜|
|title|선택|작업 일지 제목|
|workTypes|선택|작업 종류 목록|
|memo|선택|작업 메모|
|timeEntries|필수|작업 시간 구간 목록|

### timeEntries

|항목|필수 여부|설명|
|---|---|---|
|startedAt|필수|작업 시작 시각|
|endedAt|필수|작업 종료 시각|
|memo|선택|해당 시간 구간 메모|

---

## Output

발행된 DailyLog 상세 정보

---

## Business Rules

- DRAFT 상태의 DailyLog만 PUBLISHED 상태로 발행할 수 있다.
    
- 이미 PUBLISHED 상태인 DailyLog는 발행 대상이 아니다.
    
- 발행 시 workedDate는 필수이다.
    
- 발행 시 timeEntries는 최소 1개 이상 필요하다.
    
- 각 TimeEntry의 endedAt은 startedAt보다 늦어야 한다.
    
- 발행 시 DailyLog.durationMinutes를 계산한다.
    
- 발행된 DailyLog는 Project 총 작업 시간에 포함된다.
    
- 발행된 DailyLog는 캘린더 작업 시간에 포함된다.
    
- 발행된 DailyLog는 재봉 잔디에 포함된다.
    
- `ARCHIVED` 상태의 Project에 속한 DRAFT DailyLog는 발행할 수 없다.
    
- `IN_PROGRESS`, `ON_HOLD`, `COMPLETED` 상태의 Project에 속한 DRAFT DailyLog는 발행할 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|DL-036|DailyLog 없음|일지를 찾을 수 없습니다.|
|DL-037|DailyLog 접근 권한 없음|해당 일지를 발행할 수 없습니다.|
|DL-038|DRAFT 상태 아님|임시저장 상태의 일지만 발행할 수 있습니다.|
|DL-039|workedDate 누락|작업 날짜를 입력해주세요.|
|DL-040|TimeEntry 없음|작업 시간 구간을 1개 이상 입력해주세요.|
|DL-041|TimeEntry 시간 오류|작업 종료 시각은 시작 시각보다 늦어야 합니다.|
|DL-042|Archived Project 발행 시도|보관된 프로젝트의 일지는 발행할 수 없습니다.|

---

## Related Entity

- Project
    
- DailyLog
    
- DailyLogTimeEntry
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 DRAFT DailyLog를 PUBLISHED 상태로 발행할 수 있다.
    
- 발행 시 workedDate가 필요하다.
    
- 발행 시 하나 이상의 TimeEntry가 필요하다.
    
- 발행 후 DailyLog.durationMinutes가 계산된다.
    
- 발행된 DailyLog는 Project 총 작업 시간에 포함된다.
    
- 발행된 DailyLog는 캘린더와 재봉 잔디에 포함된다.
    
- `ARCHIVED` 상태의 Project에 속한 DRAFT DailyLog는 발행할 수 없다.
    

---

# 3.3 DailyLog 요약

DailyLog 도메인은 사용자의 재봉 작업 과정을 날짜별로 기록하는 핵심 도메인이다.

본 장에서 정의한 핵심 정책은 다음과 같다.

- DailyLog는 반드시 하나의 Project에 속한다.
    
- DailyLog는 반드시 User에 속한다.
    
- 사용자는 자신의 DailyLog만 조회, 수정, 삭제할 수 있다.
    
- 하루에 여러 DailyLog를 작성할 수 있다.
    
- 같은 날짜에 여러 Project의 DailyLog를 작성할 수 있다.
    
- 같은 날짜에 같은 Project의 DailyLog도 여러 개 작성할 수 있다.
    
- DailyLog는 `DRAFT`, `PUBLISHED` 상태를 가진다.
    
- DRAFT 상태의 DailyLog는 임시저장 기록이다.
    
- DRAFT 상태에서는 workedDate와 timeEntries 없이도 저장할 수 있다.
    
- DRAFT 상태의 DailyLog는 Project 총 작업 시간, 캘린더, 재봉 잔디에 포함하지 않는다.
    
- PUBLISHED 상태의 DailyLog는 정식 기록이다.
    
- PUBLISHED 상태에서는 workedDate와 하나 이상의 TimeEntry가 필요하다.
    
- PUBLISHED 상태의 DailyLog만 Project 총 작업 시간, 캘린더, 재봉 잔디에 포함한다.
    
- DailyLog.durationMinutes는 직접 입력하지 않고 TimeEntry 합산으로 계산한다.
    
- WorkType은 여러 개 선택할 수 있다.
    
- WorkType이 비어 있어도 DailyLog 작성은 가능하다.
    
- `ON_HOLD` 상태의 Project에도 DailyLog를 작성할 수 있다.
    
- `ARCHIVED` 상태의 Project에는 DailyLog를 작성, 임시저장, 수정, 삭제, 발행할 수 없다.
    
- DailyLog 사진은 Photo 도메인에서 관리한다.
    
- 하나의 DailyLog는 최대 15장의 사진과 대표 사진 1장을 가질 수 있다.
    


# 3.4 Fabric

## 3.4.1 개요

Fabric 도메인은 사용자가 구매한 원단 정보를 기록하고 관리하는 기능을 담당한다.

소로소로에서 Fabric은 Project의 하위 데이터가 아니라 독립적으로 관리되는 원단 구매 기록이다. 사용자는 원단을 먼저 등록해두고, 이후 특정 Project에 연결할 수 있다.

재봉에서는 프로젝트를 먼저 정한 뒤 원단을 구매하는 경우도 있지만, 마음에 드는 원단을 먼저 구매하고 나중에 어울리는 작품을 만드는 경우도 많다. 따라서 Fabric은 Project에 종속되지 않고 User가 독립적으로 관리하는 자산으로 취급한다.

Fabric은 다음 정보를 기록한다.

- 사용자가 부르는 원단명
    
- 쇼핑몰 상품명
    
- 품번
    
- 상품 URL
    
- 구매처
    
- 구매일
    
- 구매 가격
    
- 색상
    
- 규격
    
- 원단폭
    
- 혼용률
    
- 메모
    
- 평점
    
- 재구매 의사
    
- 사진
    
- 사용된 Project 목록
    

Fabric은 User 소유 데이터이며, 사용자는 자신의 Fabric만 조회, 수정, 삭제할 수 있다.

---

## 3.4.2 기능 목록

|ID|기능명|Priority|설명|
|---|---|---|---|
|FAB-001|원단 등록|High|사용자가 구매한 원단 정보를 등록한다.|
|FAB-002|원단 목록 조회|High|사용자가 등록한 원단 목록을 조회한다.|
|FAB-003|원단 상세 조회|High|특정 원단의 상세 정보를 조회한다.|
|FAB-004|원단 수정|High|등록한 원단 정보를 수정한다.|
|FAB-005|원단 삭제|Medium|등록한 원단을 삭제한다.|
|FAB-006|원단 사용 프로젝트 조회|High|특정 원단이 사용된 Project 목록을 조회한다.|

---

## 3.4.3 RepurchaseIntention

Fabric은 사용자의 재구매 의사를 기록할 수 있다.

|값|설명|
|---|---|
|YES|다시 구매하고 싶음|
|NO|다시 구매하고 싶지 않음|
|UNKNOWN|아직 판단하지 않음|

재구매 의사는 필수값이 아니며, 사용자가 명시하지 않은 경우 기본값은 `UNKNOWN`이다.

---

# FAB-001 원단 등록

## 목적

사용자는 구매한 원단 정보를 Fabric으로 등록할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 원단 등록 화면에서 원단 정보를 입력하고 저장 버튼을 선택한다.

---

## Main Flow

1. 사용자가 원단 등록 화면에 진입한다.
    
2. 사용자가 원단명, 상품명, 구매처, 가격, 색상, 규격, 혼용률 등 원단 정보를 입력한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 입력값을 검증한다.
    
6. 서버는 현재 User 소유의 Fabric을 생성한다.
    
7. 서버는 생성된 Fabric 정보를 반환한다.
    
8. 클라이언트는 Fabric 상세 화면으로 이동한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|name|필수|사용자가 부르는 원단명|
|productName|선택|쇼핑몰 상품명|
|productCode|선택|품번|
|productUrl|선택|상품 URL|
|storeName|선택|구매처|
|purchasedAt|선택|구매일|
|purchasePrice|선택|구매 가격|
|color|선택|색상|
|size|선택|원단 규격. 예: 140cm * 90cm|
|width|선택|원단폭. 예: 140cm|
|materialComposition|선택|혼용률 또는 재질|
|memo|선택|원단 메모|
|rating|선택|사용자 평점|
|repurchaseIntention|선택|재구매 의사|

---

## Output

|항목|설명|
|---|---|
|fabricId|생성된 Fabric 식별자|
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
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- Fabric은 반드시 User에 속한다.
    
- Fabric은 Project에 종속되지 않는다.
    
- 사용자는 Fabric만 먼저 등록할 수 있다.
    
- Fabric 등록 시 Project와 자동 연결하지 않는다.
    
- Fabric 등록 시 Photo는 자동 생성하지 않는다.
    
- Fabric 사진은 Photo 도메인에서 별도로 업로드한다.
    
- Fabric은 최대 2장의 사진을 가질 수 있다.
    
- name은 필수값이다.
    
- name은 공백만으로 구성될 수 없다.
    
- productUrl이 입력된 경우 URL 형식이어야 한다.
    
- purchasePrice가 입력된 경우 0 이상이어야 한다.
    
- rating이 입력된 경우 허용 범위 안의 값이어야 한다.
    
- repurchaseIntention이 입력되지 않으면 기본값은 `UNKNOWN`이다.
    
- 같은 품번이어도 구매일, 구매처, 가격이 다르면 별도의 Fabric으로 등록할 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|FAB-001|원단명 누락|원단명을 입력해주세요.|
|FAB-002|원단명 형식 오류|원단명은 공백만 입력할 수 없습니다.|
|FAB-003|상품 URL 형식 오류|올바른 상품 URL 형식이 아닙니다.|
|FAB-004|구매 가격 오류|구매 가격은 0 이상이어야 합니다.|
|FAB-005|평점 범위 오류|평점 값이 올바르지 않습니다.|
|FAB-006|유효하지 않은 재구매 의사|유효하지 않은 재구매 의사입니다.|
|FAB-007|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- Fabric
    
- Photo
    

---

## Acceptance Criteria

- 로그인한 사용자는 Fabric을 등록할 수 있다.
    
- Fabric 등록 시 Project 연결은 필요하지 않다.
    
- Fabric name이 없으면 등록할 수 없다.
    
- productUrl은 URL 형식이어야 한다.
    
- repurchaseIntention 미입력 시 `UNKNOWN`으로 저장된다.
    
- 생성된 Fabric은 현재 User의 소유가 된다.
    

---

# FAB-002 원단 목록 조회

## 목적

사용자는 자신이 등록한 Fabric 목록을 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 원단 목록 화면에 진입한다.

---

## Main Flow

1. 클라이언트는 Fabric 목록 조회를 요청한다.
    
2. 서버는 사용자의 인증 정보를 확인한다.
    
3. 서버는 현재 User가 소유한 Fabric 목록을 조회한다.
    
4. 서버는 검색, 필터, 정렬 조건을 적용한다.
    
5. 서버는 Fabric 목록을 반환한다.
    
6. 클라이언트는 Fabric 목록을 화면에 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|keyword|선택|원단명, 상품명, 품번, 구매처 검색어|
|storeName|선택|구매처 필터|
|color|선택|색상 필터|
|repurchaseIntention|선택|재구매 의사 필터|
|page|선택|페이지 번호|
|size|선택|페이지 크기|
|sort|선택|정렬 기준|

---

## Output

|항목|설명|
|---|---|
|fabricId|Fabric 식별자|
|name|사용자가 부르는 원단명|
|productName|쇼핑몰 상품명|
|productCode|품번|
|storeName|구매처|
|purchasedAt|구매일|
|purchasePrice|구매 가격|
|color|색상|
|width|원단폭|
|materialComposition|혼용률 또는 재질|
|repurchaseIntention|재구매 의사|
|thumbnailUrl|원단 대표 이미지 URL|
|usedProjectCount|해당 원단을 사용한 Project 수|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- 사용자는 자신의 Fabric 목록만 조회할 수 있다.
    
- 다른 사용자의 Fabric은 목록에 포함되지 않는다.
    
- 기본 정렬은 최근 수정일 내림차순이다.
    
- keyword는 name, productName, productCode, storeName을 대상으로 검색할 수 있다.
    
- repurchaseIntention 필터는 `YES`, `NO`, `UNKNOWN` 중 하나여야 한다.
    
- Fabric 대표 이미지는 Fabric에 연결된 Photo 중 정렬 우선순위가 가장 높은 이미지를 사용할 수 있다.
    
- 대표 이미지가 없으면 thumbnailUrl은 비어 있을 수 있다.
    
- usedProjectCount는 ProjectFabric 연결 수를 기준으로 제공할 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|FAB-008|인증되지 않은 사용자|로그인이 필요합니다.|
|FAB-009|유효하지 않은 재구매 의사 필터|유효하지 않은 재구매 의사입니다.|

---

## Related Entity

- User
    
- Fabric
    
- ProjectFabric
    
- Photo
    

---

## Acceptance Criteria

- 로그인한 사용자는 자신의 Fabric 목록을 조회할 수 있다.
    
- 다른 사용자의 Fabric은 조회 결과에 포함되지 않는다.
    
- keyword 검색이 가능하다.
    
- 재구매 의사 기준으로 필터링할 수 있다.
    
- 대표 이미지가 존재하는 Fabric은 thumbnailUrl을 반환한다.
    
- Fabric 목록에는 사용된 Project 수가 포함될 수 있다.
    

---

# FAB-003 원단 상세 조회

## 목적

사용자는 특정 Fabric의 상세 정보를 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 조회 대상 Fabric이 존재해야 한다.
    
- 조회 대상 Fabric은 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 원단 목록에서 특정 Fabric을 선택한다.

---

## Main Flow

1. 사용자가 Fabric을 선택한다.
    
2. 클라이언트는 Fabric 상세 조회를 요청한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 Fabric 존재 여부를 확인한다.
    
5. 서버는 Fabric 소유자가 현재 User인지 확인한다.
    
6. 서버는 Fabric 기본 정보, Photo 목록, 사용 Project 요약 정보를 조회한다.
    
7. 서버는 Fabric 상세 정보를 반환한다.
    
8. 클라이언트는 Fabric 상세 화면을 표시한다.
    

---

## Output

|항목|설명|
|---|---|
|fabricId|Fabric 식별자|
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
|photos|Fabric 사진 목록|
|usedProjects|사용된 Project 요약 목록|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- 사용자는 자신의 Fabric만 상세 조회할 수 있다.
    
- 다른 사용자의 Fabric은 상세 조회할 수 없다.
    
- Fabric 상세에는 Photo 목록이 포함될 수 있다.
    
- Fabric 상세에는 해당 Fabric이 연결된 Project 요약 목록이 포함될 수 있다.
    
- Fabric이 어떤 Project에도 연결되지 않은 경우 usedProjects는 빈 목록일 수 있다.
    
- Fabric 사진은 최대 2장까지 존재할 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|FAB-010|Fabric 없음|원단을 찾을 수 없습니다.|
|FAB-011|Fabric 접근 권한 없음|해당 원단에 접근할 수 없습니다.|
|FAB-012|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- Fabric
    
- ProjectFabric
    
- Project
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Fabric 상세 정보를 조회할 수 있다.
    
- 다른 사용자의 Fabric은 상세 조회할 수 없다.
    
- 존재하지 않는 Fabric 조회 시 오류가 반환된다.
    
- Fabric 상세에는 원단 기본 정보가 포함된다.
    
- Fabric 상세에는 사진 목록과 사용 Project 요약 목록이 포함될 수 있다.
    

---

# FAB-004 원단 수정

## 목적

사용자는 자신이 등록한 Fabric 정보를 수정할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 수정 대상 Fabric이 존재해야 한다.
    
- 수정 대상 Fabric은 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 Fabric 상세 또는 수정 화면에서 정보를 변경하고 저장 버튼을 선택한다.

---

## Main Flow

1. 사용자가 Fabric 수정 화면에 진입한다.
    
2. 사용자가 원단명, 상품명, 품번, 상품 URL, 구매처, 가격, 색상, 규격, 혼용률, 메모, 평점, 재구매 의사 등을 수정한다.
    
3. 사용자가 저장 버튼을 선택한다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Fabric 존재 여부를 확인한다.
    
6. 서버는 Fabric 소유자가 현재 User인지 확인한다.
    
7. 서버는 입력값을 검증한다.
    
8. 서버는 Fabric 정보를 수정한다.
    
9. 서버는 수정된 Fabric 정보를 반환한다.
    
10. 클라이언트는 수정된 정보를 화면에 반영한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|name|선택|사용자가 부르는 원단명|
|productName|선택|쇼핑몰 상품명|
|productCode|선택|품번|
|productUrl|선택|상품 URL|
|storeName|선택|구매처|
|purchasedAt|선택|구매일|
|purchasePrice|선택|구매 가격|
|color|선택|색상|
|size|선택|원단 규격|
|width|선택|원단폭|
|materialComposition|선택|혼용률 또는 재질|
|memo|선택|원단 메모|
|rating|선택|사용자 평점|
|repurchaseIntention|선택|재구매 의사|

---

## Output

수정된 Fabric 상세 정보

---

## Business Rules

- 사용자는 자신의 Fabric만 수정할 수 있다.
    
- 다른 사용자의 Fabric은 수정할 수 없다.
    
- name을 수정하는 경우 공백만으로 변경할 수 없다.
    
- productUrl을 수정하는 경우 URL 형식이어야 한다.
    
- purchasePrice를 수정하는 경우 0 이상이어야 한다.
    
- rating을 수정하는 경우 허용 범위 안의 값이어야 한다.
    
- repurchaseIntention은 `YES`, `NO`, `UNKNOWN` 중 하나여야 한다.
    
- Fabric 수정은 연결된 Project 정보를 수정하지 않는다.
    
- Fabric 수정은 ProjectFabric의 연결 메모를 수정하지 않는다.
    
- Fabric 사진 추가, 삭제는 Photo 도메인 기능에서 처리한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|FAB-013|Fabric 없음|원단을 찾을 수 없습니다.|
|FAB-014|Fabric 접근 권한 없음|해당 원단을 수정할 수 없습니다.|
|FAB-015|원단명 형식 오류|원단명은 공백만 입력할 수 없습니다.|
|FAB-016|상품 URL 형식 오류|올바른 상품 URL 형식이 아닙니다.|
|FAB-017|구매 가격 오류|구매 가격은 0 이상이어야 합니다.|
|FAB-018|평점 범위 오류|평점 값이 올바르지 않습니다.|
|FAB-019|유효하지 않은 재구매 의사|유효하지 않은 재구매 의사입니다.|

---

## Related Entity

- User
    
- Fabric
    
- ProjectFabric
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Fabric 정보를 수정할 수 있다.
    
- 다른 사용자의 Fabric은 수정할 수 없다.
    
- name은 공백만으로 수정할 수 없다.
    
- productUrl은 URL 형식이어야 한다.
    
- repurchaseIntention은 허용된 값만 저장할 수 있다.
    
- Fabric 수정 시 연결된 Project와 ProjectFabric 메모는 변경되지 않는다.
    

---

# FAB-005 원단 삭제

## 목적

사용자는 자신이 등록한 Fabric을 삭제할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 삭제 대상 Fabric이 존재해야 한다.
    
- 삭제 대상 Fabric은 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 Fabric 상세 또는 수정 화면에서 삭제를 선택하고 확인 절차를 완료한다.

---

## Main Flow

1. 사용자가 Fabric 삭제를 선택한다.
    
2. 클라이언트는 삭제 확인 절차를 진행한다.
    
3. 클라이언트는 Fabric 삭제 요청을 보낸다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Fabric 존재 여부를 확인한다.
    
6. 서버는 Fabric 소유자가 현재 User인지 확인한다.
    
7. 서버는 Fabric을 삭제 처리한다.
    
8. 서버는 삭제 완료 응답을 반환한다.
    
9. 클라이언트는 Fabric 목록 화면으로 이동한다.
    

---

## Output

|항목|설명|
|---|---|
|success|삭제 처리 결과|

---

## Business Rules

- 사용자는 자신의 Fabric만 삭제할 수 있다.
    
- 다른 사용자의 Fabric은 삭제할 수 없다.
    
- Fabric 삭제 시 Project 자체는 삭제되지 않는다.
    
- Fabric 삭제 시 해당 Fabric과 연결된 ProjectFabric은 함께 삭제된다.
    
- Fabric 삭제 후 Project 상세의 연결 원단 목록에서 해당 Fabric은 제외되어야 한다.
    
- Fabric 삭제 후 Fabric 목록과 상세 조회에서 해당 Fabric은 제외되어야 한다.
    
- Fabric 삭제 시 해당 Fabric에 연결된 Photo는 서비스 화면에서 더 이상 조회되지 않아야 한다.
    
- 물리 삭제, 소프트 삭제, S3 Object 삭제 시점은 Backend Design 및 Architecture에서 정의한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|FAB-020|Fabric 없음|원단을 찾을 수 없습니다.|
|FAB-021|Fabric 접근 권한 없음|해당 원단을 삭제할 수 없습니다.|
|FAB-022|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- Fabric
    
- ProjectFabric
    
- Project
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Fabric을 삭제할 수 있다.
    
- 다른 사용자의 Fabric은 삭제할 수 없다.
    
- Fabric 삭제 후 목록에서 조회되지 않는다.
    
- Fabric 삭제 후 상세 조회가 불가능하다.
    
- Fabric 삭제 시 연결된 Project는 삭제되지 않는다.
    
- Fabric 삭제 시 해당 Fabric과 연결된 ProjectFabric은 삭제된다.
    

---

# FAB-006 원단 사용 프로젝트 조회

## 목적

사용자는 특정 Fabric이 어떤 Project에 사용되었는지 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 조회 대상 Fabric이 존재해야 한다.
    
- 조회 대상 Fabric은 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 Fabric 상세 화면에서 사용 프로젝트 영역을 조회한다.

---

## Main Flow

1. 사용자가 Fabric 상세 화면에 진입한다.
    
2. 클라이언트는 특정 Fabric의 사용 Project 목록 조회를 요청한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 Fabric 존재 여부를 확인한다.
    
5. 서버는 Fabric 소유자가 현재 User인지 확인한다.
    
6. 서버는 해당 Fabric과 연결된 ProjectFabric 목록을 조회한다.
    
7. 서버는 Project 요약 정보를 함께 반환한다.
    
8. 클라이언트는 해당 Fabric이 사용된 Project 목록을 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|fabricId|필수|사용 Project 목록을 조회할 Fabric 식별자|
|page|선택|페이지 번호|
|size|선택|페이지 크기|

---

## Output

|항목|설명|
|---|---|
|projectFabricId|ProjectFabric 식별자|
|projectId|Project 식별자|
|projectTitle|Project 제목|
|projectStatus|Project 상태|
|projectThumbnailUrl|Project 대표 이미지 URL|
|memo|해당 Project에서 Fabric을 사용한 맥락|
|connectedAt|연결 일시|

---

## Business Rules

- 사용자는 자신의 Fabric에 대해서만 사용 Project 목록을 조회할 수 있다.
    
- 다른 사용자의 Fabric에 대한 사용 Project 목록은 조회할 수 없다.
    
- 사용 Project 목록은 ProjectFabric 연결 정보를 기준으로 조회한다.
    
- ProjectFabric의 memo가 있으면 함께 반환한다.
    
- Fabric이 어떤 Project에도 연결되지 않은 경우 빈 목록을 반환한다.
    
- 삭제된 Project와의 연결은 조회 결과에 포함되지 않는다.
    
- 삭제된 ProjectFabric 연결은 조회 결과에 포함되지 않는다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|FAB-023|Fabric 없음|원단을 찾을 수 없습니다.|
|FAB-024|Fabric 접근 권한 없음|해당 원단의 사용 프로젝트를 조회할 수 없습니다.|

---

## Related Entity

- Fabric
    
- ProjectFabric
    
- Project
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Fabric이 사용된 Project 목록을 조회할 수 있다.
    
- 다른 사용자의 Fabric 사용 Project 목록은 조회할 수 없다.
    
- 연결된 Project가 없는 경우 빈 목록이 반환된다.
    
- 사용 Project 목록에는 Project 요약 정보와 연결 메모가 포함된다.
    

---

# 3.4 Fabric 요약

Fabric 도메인은 사용자가 구매한 원단 정보를 독립적으로 관리하는 핵심 도메인이다.

본 장에서 정의한 핵심 정책은 다음과 같다.

- Fabric은 반드시 User에 속한다.
    
- Fabric은 Project에 종속되지 않는다.
    
- 사용자는 원단만 먼저 등록할 수 있다.
    
- 사용자는 자신의 Fabric만 조회, 수정, 삭제할 수 있다.
    
- 다른 사용자의 Fabric에는 접근할 수 없다.
    
- Fabric은 ProjectFabric을 통해 Project와 연결된다.
    
- Fabric 삭제 시 Project 자체는 삭제되지 않는다.
    
- Fabric 삭제 시 연결 정보인 ProjectFabric은 삭제된다.
    
- Fabric 사진은 Photo 도메인에서 관리한다.
    
- 하나의 Fabric은 최대 2장의 사진을 가질 수 있다.
    
- repurchaseIntention은 `YES`, `NO`, `UNKNOWN`을 사용한다.
    
- repurchaseIntention 미입력 시 기본값은 `UNKNOWN`이다.
    
- 같은 품번이어도 구매일, 구매처, 가격이 다르면 별도의 Fabric으로 등록할 수 있다.

# 3.5 Photo

## 3.5.1 개요

Photo 도메인은 Project, DailyLog, Fabric에 첨부되는 이미지를 관리하는 기능을 담당한다.

소로소로는 재봉 과정을 사진 중심으로 기록하는 서비스이므로, 이미지 업로드와 조회 경험이 중요하다. 사용자는 작품의 완성 사진, 작업 과정 사진, 원단 사진 등을 각 도메인에 연결할 수 있어야 한다.

Photo는 특정 도메인에 종속된 별도 테이블로 나누지 않고, 공통 이미지 엔티티로 관리한다.

Photo는 다음 대상에 연결될 수 있다.

|ownerType|설명|
|---|---|
|PROJECT|Project에 연결된 사진|
|DAILY_LOG|DailyLog에 연결된 사진|
|FABRIC|Fabric에 연결된 사진|

Photo는 항상 User 소유 데이터이며, 사용자는 자신의 Photo만 조회, 수정, 삭제할 수 있다.

---

## 3.5.2 기능 목록

|ID|기능명|Priority|설명|
|---|---|---|---|
|PHO-001|Presigned URL 발급|High|이미지 업로드를 위한 Presigned URL을 발급한다.|
|PHO-002|업로드 완료 처리|High|클라이언트가 S3 업로드 완료를 서버에 알린다.|
|PHO-003|사진 목록 조회|High|특정 owner에 연결된 Photo 목록을 조회한다.|
|PHO-004|대표 사진 설정|High|Project 또는 DailyLog의 대표 사진을 설정한다.|
|PHO-005|사진 정렬 순서 수정|Medium|사진의 표시 순서를 수정한다.|
|PHO-006|사진 삭제|Medium|등록된 Photo를 삭제한다.|

---

## 3.5.3 Photo Owner 정책

Photo는 다음 owner에 연결될 수 있다.

|ownerType|연결 대상|사진 수 제한|대표 사진|
|---|---|--:|---|
|PROJECT|Project|최대 15장|1장|
|DAILY_LOG|DailyLog|최대 15장|1장|
|FABRIC|Fabric|최대 2장|별도 대표 사진 지정 없음|

### PROJECT Photo

Project Photo는 작품 전체를 대표하는 사진, 완성 사진, 디테일 사진 등을 저장한다.

Project에는 최대 15장의 Photo를 연결할 수 있다.

Project는 대표 사진을 1장 가질 수 있다.

---

### DAILY_LOG Photo

DailyLog Photo는 특정 날짜의 작업 과정, 중간 상태, 실패 기록, 작업 결과 등을 저장한다.

DailyLog에는 최대 15장의 Photo를 연결할 수 있다.

DailyLog는 대표 사진을 1장 가질 수 있다.

DRAFT 상태의 DailyLog에도 Photo를 연결할 수 있다.

---

### FABRIC Photo

Fabric Photo는 원단 사진 또는 쇼핑몰 상품 이미지를 저장한다.

Fabric에는 최대 2장의 Photo를 연결할 수 있다.

Fabric은 별도의 대표 사진 지정 기능을 제공하지 않는다. 목록 화면에서는 정렬 순서가 가장 앞선 Photo를 대표 이미지처럼 사용할 수 있다.

---

## 3.5.4 PhotoStatus

Photo는 업로드 및 이미지 처리 상태를 가진다.

|상태|설명|
|---|---|
|UPLOADING|Presigned URL 발급 후 클라이언트가 원본 이미지를 업로드 중인 상태|
|PROCESSING|원본 업로드 완료 후 이미지 변환 작업이 진행 중인 상태|
|READY|이미지 업로드 및 변환이 완료되어 조회 가능한 상태|
|FAILED|이미지 업로드 또는 변환에 실패한 상태|

Photo는 `READY` 상태가 된 이후 서비스 화면에서 정상적으로 조회될 수 있다.

---

## 3.5.5 이미지 종류

소로소로는 하나의 Photo에 대해 다음 이미지 파일을 관리한다.

|종류|설명|
|---|---|
|Original|사용자가 업로드한 원본 이미지|
|Medium|상세 화면 조회에 사용하는 최적화 이미지|
|Thumbnail|목록, 카드, 캘린더 등에서 사용하는 축소 이미지|

Original은 원본 보존을 위해 저장하고, Medium과 Thumbnail은 빠른 조회 경험을 위해 사용한다.

이미지 변환 방식, 저장 경로, Queue Worker 처리 구조는 Architecture 및 Backend Design 문서에서 정의한다.

---

# PHO-001 Presigned URL 발급

## 목적

사용자는 이미지를 서버를 거치지 않고 S3에 직접 업로드할 수 있도록 Presigned URL을 발급받을 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    
- Photo를 연결할 owner가 존재해야 한다.
    
- Photo를 연결할 owner는 현재 User의 소유여야 한다.
    
- owner 상태가 Photo 추가 가능한 상태여야 한다.
    

---

## Trigger

사용자가 Project, DailyLog, Fabric 화면에서 사진 추가를 선택한다.

---

## Main Flow

1. 사용자가 사진 추가를 선택한다.
    
2. 클라이언트는 업로드할 파일 정보를 서버에 전달한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 ownerType과 ownerId를 확인한다.
    
5. 서버는 owner가 존재하는지 확인한다.
    
6. 서버는 owner가 현재 User의 소유인지 확인한다.
    
7. 서버는 owner별 사진 수 제한을 확인한다.
    
8. 서버는 Photo를 `UPLOADING` 상태로 생성한다.
    
9. 서버는 Original 이미지 업로드를 위한 Presigned URL을 발급한다.
    
10. 서버는 Photo 식별자와 Presigned URL을 반환한다.
    
11. 클라이언트는 Presigned URL을 이용해 S3에 원본 이미지를 업로드한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|ownerType|필수|Photo를 연결할 대상 타입|
|ownerId|필수|Photo를 연결할 대상 식별자|
|fileName|필수|업로드할 파일명|
|contentType|필수|이미지 Content-Type|
|sizeBytes|필수|이미지 파일 크기|
|isThumbnail|선택|대표 사진 여부. PROJECT, DAILY_LOG에서 사용|
|sortOrder|선택|사진 정렬 순서|

---

## Output

|항목|설명|
|---|---|
|photoId|생성된 Photo 식별자|
|uploadUrl|S3 업로드용 Presigned URL|
|originalKey|원본 이미지 저장 Key|
|expiresInSeconds|Presigned URL 만료 시간|
|status|Photo 상태. UPLOADING|

---

## Business Rules

- 사용자는 자신의 owner에만 Photo를 추가할 수 있다.
    
- ownerType은 `PROJECT`, `DAILY_LOG`, `FABRIC` 중 하나여야 한다.
    
- Project에는 최대 15장의 Photo를 추가할 수 있다.
    
- DailyLog에는 최대 15장의 Photo를 추가할 수 있다.
    
- Fabric에는 최대 2장의 Photo를 추가할 수 있다.
    
- Project가 `ARCHIVED` 상태이면 Project Photo를 추가할 수 없다.
    
- DailyLog가 속한 Project가 `ARCHIVED` 상태이면 DailyLog Photo를 추가할 수 없다.
    
- Fabric Photo는 Fabric이 현재 User 소유이면 추가할 수 있다.
    
- 지원하지 않는 파일 형식은 업로드할 수 없다.
    
- 이미지 파일 크기는 허용된 최대 크기를 초과할 수 없다.
    
- Presigned URL 발급 시 Photo 상태는 `UPLOADING`이다.
    
- Presigned URL 발급만으로 이미지는 조회 가능 상태가 되지 않는다.
    
- 실제 이미지 조회 가능 여부는 업로드 완료 처리 이후 결정된다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PHO-001|ownerType 오류|유효하지 않은 사진 연결 대상입니다.|
|PHO-002|owner 없음|사진을 연결할 대상을 찾을 수 없습니다.|
|PHO-003|owner 접근 권한 없음|해당 대상에 사진을 추가할 수 없습니다.|
|PHO-004|사진 수 제한 초과|등록 가능한 사진 수를 초과했습니다.|
|PHO-005|파일 형식 오류|지원하지 않는 이미지 형식입니다.|
|PHO-006|파일 크기 초과|이미지 파일 크기가 너무 큽니다.|
|PHO-007|Archived Project 사진 추가 시도|보관된 프로젝트에는 사진을 추가할 수 없습니다.|
|PHO-008|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- Project
    
- DailyLog
    
- Fabric
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Project, DailyLog, Fabric에 Photo 업로드를 시작할 수 있다.
    
- Presigned URL 발급 시 Photo가 `UPLOADING` 상태로 생성된다.
    
- owner별 사진 수 제한을 초과하면 Presigned URL을 발급하지 않는다.
    
- 다른 사용자의 owner에는 Photo를 추가할 수 없다.
    
- `ARCHIVED` 상태의 Project 또는 그 Project에 속한 DailyLog에는 Photo를 추가할 수 없다.
    

---

# PHO-002 업로드 완료 처리

## 목적

클라이언트는 S3에 원본 이미지 업로드를 완료한 후, 서버에 업로드 완료 사실을 알릴 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- Photo가 존재해야 한다.
    
- Photo는 현재 User의 소유여야 한다.
    
- Photo 상태는 `UPLOADING`이어야 한다.
    
- 클라이언트는 Presigned URL을 통해 원본 이미지를 S3에 업로드한 상태여야 한다.
    

---

## Trigger

클라이언트가 S3 원본 이미지 업로드에 성공한 뒤 업로드 완료 API를 호출한다.

---

## Main Flow

1. 클라이언트가 원본 이미지를 S3에 업로드한다.
    
2. 클라이언트는 서버에 업로드 완료 요청을 보낸다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 Photo 존재 여부를 확인한다.
    
5. 서버는 Photo 소유자가 현재 User인지 확인한다.
    
6. 서버는 Photo 상태가 `UPLOADING`인지 확인한다.
    
7. 서버는 원본 이미지가 업로드되었음을 확인한다.
    
8. 서버는 Photo 상태를 `PROCESSING`으로 변경한다.
    
9. 서버는 이미지 변환 작업이 수행될 수 있도록 처리 요청을 등록한다.
    
10. 서버는 업로드 완료 처리 결과를 반환한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|photoId|필수|업로드 완료 처리할 Photo 식별자|

---

## Output

|항목|설명|
|---|---|
|photoId|Photo 식별자|
|status|Photo 상태. PROCESSING|
|originalKey|원본 이미지 Key|

---

## Business Rules

- 사용자는 자신의 Photo에 대해서만 업로드 완료 처리를 할 수 있다.
    
- `UPLOADING` 상태의 Photo만 업로드 완료 처리할 수 있다.
    
- 업로드 완료 처리 후 Photo 상태는 `PROCESSING`이 된다.
    
- 이미지 변환이 완료되면 Photo 상태는 `READY`가 된다.
    
- 이미지 변환에 실패하면 Photo 상태는 `FAILED`가 된다.
    
- `READY` 상태가 되기 전에는 Medium, Thumbnail URL이 비어 있을 수 있다.
    
- 업로드 완료 처리 전에는 서비스 화면에서 정상 이미지로 표시하지 않는다.
    
- 업로드 완료 처리는 클라이언트가 S3 업로드를 마친 뒤 호출해야 한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PHO-009|Photo 없음|사진 정보를 찾을 수 없습니다.|
|PHO-010|Photo 접근 권한 없음|해당 사진에 접근할 수 없습니다.|
|PHO-011|잘못된 Photo 상태|업로드 완료 처리할 수 없는 상태입니다.|
|PHO-012|원본 이미지 없음|업로드된 원본 이미지를 찾을 수 없습니다.|
|PHO-013|이미지 처리 요청 실패|이미지 처리 요청에 실패했습니다.|

---

## Related Entity

- User
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Photo에 대해 업로드 완료 처리를 할 수 있다.
    
- 업로드 완료 처리 후 Photo 상태는 `PROCESSING`이 된다.
    
- 원본 이미지가 존재하지 않으면 업로드 완료 처리에 실패한다.
    
- 다른 사용자의 Photo는 업로드 완료 처리할 수 없다.
    
- 이미지 변환 완료 후 Photo는 `READY` 상태가 될 수 있다.
    

---

# PHO-003 사진 목록 조회

## 목적

사용자는 특정 owner에 연결된 Photo 목록을 조회할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 조회 대상 owner가 존재해야 한다.
    
- 조회 대상 owner는 현재 User의 소유여야 한다.
    

---

## Trigger

사용자가 Project 상세, DailyLog 상세, Fabric 상세 화면에 진입한다.

---

## Main Flow

1. 클라이언트는 ownerType과 ownerId를 기준으로 Photo 목록 조회를 요청한다.
    
2. 서버는 사용자의 인증 정보를 확인한다.
    
3. 서버는 owner 존재 여부를 확인한다.
    
4. 서버는 owner 소유자가 현재 User인지 확인한다.
    
5. 서버는 owner에 연결된 Photo 목록을 조회한다.
    
6. 서버는 정렬 순서를 적용한다.
    
7. 서버는 Photo 목록을 반환한다.
    
8. 클라이언트는 Photo 목록을 화면에 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|ownerType|필수|Photo 연결 대상 타입|
|ownerId|필수|Photo 연결 대상 식별자|
|includeFailed|선택|FAILED 상태 포함 여부|

---

## Output

|항목|설명|
|---|---|
|photoId|Photo 식별자|
|ownerType|Photo 연결 대상 타입|
|ownerId|Photo 연결 대상 식별자|
|status|Photo 상태|
|isThumbnail|대표 사진 여부|
|originalUrl|원본 이미지 URL|
|mediumUrl|Medium 이미지 URL|
|thumbnailUrl|Thumbnail 이미지 URL|
|width|이미지 너비|
|height|이미지 높이|
|sizeBytes|이미지 파일 크기|
|sortOrder|정렬 순서|
|createdAt|생성 일시|
|updatedAt|수정 일시|

---

## Business Rules

- 사용자는 자신의 owner에 연결된 Photo만 조회할 수 있다.
    
- 다른 사용자의 owner에 연결된 Photo는 조회할 수 없다.
    
- 기본 조회는 `READY`, `PROCESSING`, `UPLOADING` 상태의 Photo를 포함할 수 있다.
    
- `FAILED` 상태의 Photo는 기본 목록에서 제외할 수 있다.
    
- Photo 목록은 sortOrder 오름차순으로 정렬한다.
    
- Project와 DailyLog는 isThumbnail이 true인 Photo를 최대 1장만 가질 수 있다.
    
- Fabric은 isThumbnail을 대표 사진 정책으로 사용하지 않는다.
    
- Medium URL과 Thumbnail URL은 Photo가 `READY` 상태일 때 제공된다.
    
- Photo가 `PROCESSING` 상태이면 변환 이미지 URL은 비어 있을 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PHO-014|ownerType 오류|유효하지 않은 사진 연결 대상입니다.|
|PHO-015|owner 없음|사진 목록을 조회할 대상을 찾을 수 없습니다.|
|PHO-016|owner 접근 권한 없음|해당 사진 목록에 접근할 수 없습니다.|

---

## Related Entity

- User
    
- Project
    
- DailyLog
    
- Fabric
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 owner에 연결된 Photo 목록을 조회할 수 있다.
    
- 다른 사용자의 owner에 연결된 Photo 목록은 조회할 수 없다.
    
- Photo 목록은 sortOrder 기준으로 정렬된다.
    
- READY 상태의 Photo는 조회용 이미지 URL을 제공한다.
    
- PROCESSING 상태의 Photo는 상태값을 통해 처리 중임을 알 수 있다.
    

---

# PHO-004 대표 사진 설정

## 목적

사용자는 Project 또는 DailyLog의 대표 사진을 설정할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 대상 Photo가 존재해야 한다.
    
- 대상 Photo는 현재 User의 소유여야 한다.
    
- 대상 Photo의 ownerType은 `PROJECT` 또는 `DAILY_LOG`여야 한다.
    
- 대상 Photo는 `READY` 상태여야 한다.
    

---

## Trigger

사용자가 Project 또는 DailyLog 사진 목록에서 특정 사진을 대표 사진으로 선택한다.

---

## Main Flow

1. 사용자가 특정 Photo를 대표 사진으로 선택한다.
    
2. 클라이언트는 대표 사진 설정 요청을 보낸다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 Photo 존재 여부를 확인한다.
    
5. 서버는 Photo 소유자가 현재 User인지 확인한다.
    
6. 서버는 Photo의 ownerType이 대표 사진 설정 가능한 대상인지 확인한다.
    
7. 서버는 owner가 수정 가능한 상태인지 확인한다.
    
8. 서버는 같은 owner에 연결된 기존 대표 사진을 해제한다.
    
9. 서버는 선택한 Photo를 대표 사진으로 설정한다.
    
10. 서버는 변경된 Photo 정보를 반환한다.
    
11. 클라이언트는 대표 사진 변경 결과를 화면에 반영한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|photoId|필수|대표 사진으로 설정할 Photo 식별자|

---

## Output

|항목|설명|
|---|---|
|photoId|대표 사진으로 설정된 Photo 식별자|
|ownerType|Photo 연결 대상 타입|
|ownerId|Photo 연결 대상 식별자|
|isThumbnail|대표 사진 여부|

---

## Business Rules

- 대표 사진은 Project와 DailyLog에서만 설정할 수 있다.
    
- Fabric은 별도의 대표 사진 설정 기능을 제공하지 않는다.
    
- Project는 대표 사진을 최대 1장만 가질 수 있다.
    
- DailyLog는 대표 사진을 최대 1장만 가질 수 있다.
    
- 새로운 대표 사진을 설정하면 기존 대표 사진은 자동 해제된다.
    
- 대표 사진으로 설정할 Photo는 현재 User 소유여야 한다.
    
- 대표 사진으로 설정할 Photo는 해당 owner에 연결된 Photo여야 한다.
    
- 대표 사진으로 설정할 Photo는 `READY` 상태여야 한다.
    
- `ARCHIVED` 상태의 Project에서는 대표 사진을 변경할 수 없다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog에서는 대표 사진을 변경할 수 없다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PHO-017|Photo 없음|사진 정보를 찾을 수 없습니다.|
|PHO-018|Photo 접근 권한 없음|해당 사진을 대표 사진으로 설정할 수 없습니다.|
|PHO-019|대표 사진 설정 불가 ownerType|해당 대상에는 대표 사진을 설정할 수 없습니다.|
|PHO-020|Photo 상태 오류|처리 완료된 사진만 대표 사진으로 설정할 수 있습니다.|
|PHO-021|Archived Project 대표 사진 변경 시도|보관된 프로젝트의 대표 사진은 변경할 수 없습니다.|

---

## Related Entity

- Project
    
- DailyLog
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Project Photo를 대표 사진으로 설정할 수 있다.
    
- 사용자는 자신의 DailyLog Photo를 대표 사진으로 설정할 수 있다.
    
- Fabric Photo는 대표 사진으로 설정할 수 없다.
    
- 하나의 Project에는 대표 사진이 최대 1장만 존재한다.
    
- 하나의 DailyLog에는 대표 사진이 최대 1장만 존재한다.
    
- 대표 사진 변경 시 기존 대표 사진은 자동 해제된다.
    
- READY 상태의 Photo만 대표 사진으로 설정할 수 있다.
    

---

# PHO-005 사진 정렬 순서 수정

## 목적

사용자는 같은 owner에 연결된 Photo의 표시 순서를 변경할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 정렬 순서를 수정할 Photo들이 존재해야 한다.
    
- 대상 Photo들은 현재 User의 소유여야 한다.
    
- 대상 Photo들은 같은 owner에 속해야 한다.
    
- owner 상태가 Photo 정렬 수정 가능한 상태여야 한다.
    

---

## Trigger

사용자가 사진 목록에서 드래그 앤 드롭 또는 순서 변경 기능을 사용한다.

---

## Main Flow

1. 사용자가 사진 순서를 변경한다.
    
2. 클라이언트는 변경된 Photo 순서 목록을 서버에 전달한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 모든 Photo가 존재하는지 확인한다.
    
5. 서버는 모든 Photo가 현재 User 소유인지 확인한다.
    
6. 서버는 모든 Photo가 같은 owner에 속하는지 확인한다.
    
7. 서버는 owner가 수정 가능한 상태인지 확인한다.
    
8. 서버는 Photo의 sortOrder를 수정한다.
    
9. 서버는 변경된 Photo 목록을 반환한다.
    
10. 클라이언트는 변경된 순서를 화면에 반영한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|ownerType|필수|Photo 연결 대상 타입|
|ownerId|필수|Photo 연결 대상 식별자|
|photoOrders|필수|Photo ID와 sortOrder 목록|

### photoOrders

|항목|필수 여부|설명|
|---|---|---|
|photoId|필수|Photo 식별자|
|sortOrder|필수|변경할 정렬 순서|

---

## Output

변경된 Photo 목록

---

## Business Rules

- 사용자는 자신의 Photo 정렬 순서만 수정할 수 있다.
    
- 모든 Photo는 동일한 owner에 속해야 한다.
    
- 다른 owner에 속한 Photo를 함께 정렬할 수 없다.
    
- 다른 사용자의 Photo는 정렬할 수 없다.
    
- sortOrder는 owner 내 표시 순서로 사용된다.
    
- `ARCHIVED` 상태의 Project에서는 Photo 정렬 순서를 변경할 수 없다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog에서는 Photo 정렬 순서를 변경할 수 없다.
    
- Fabric Photo는 Fabric이 현재 User 소유이면 정렬할 수 있다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PHO-022|Photo 없음|사진 정보를 찾을 수 없습니다.|
|PHO-023|Photo 접근 권한 없음|해당 사진 순서를 변경할 수 없습니다.|
|PHO-024|서로 다른 owner의 Photo 포함|같은 대상의 사진만 정렬할 수 있습니다.|
|PHO-025|owner 접근 권한 없음|해당 대상의 사진 순서를 변경할 수 없습니다.|
|PHO-026|Archived Project 사진 정렬 시도|보관된 프로젝트의 사진 순서는 변경할 수 없습니다.|

---

## Related Entity

- Project
    
- DailyLog
    
- Fabric
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Photo 정렬 순서를 변경할 수 있다.
    
- 같은 owner에 속한 Photo만 함께 정렬할 수 있다.
    
- 정렬 변경 후 Photo 목록 조회 시 변경된 순서로 반환된다.
    
- 다른 사용자의 Photo는 정렬할 수 없다.
    
- `ARCHIVED` 상태의 Project 관련 Photo는 정렬할 수 없다.
    

---

# PHO-006 사진 삭제

## 목적

사용자는 등록한 Photo를 삭제할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- 삭제 대상 Photo가 존재해야 한다.
    
- 삭제 대상 Photo는 현재 User의 소유여야 한다.
    
- Photo가 연결된 owner 상태가 삭제 가능한 상태여야 한다.
    

---

## Trigger

사용자가 사진 목록에서 특정 Photo 삭제를 선택하고 확인 절차를 완료한다.

---

## Main Flow

1. 사용자가 Photo 삭제를 선택한다.
    
2. 클라이언트는 삭제 확인 절차를 진행한다.
    
3. 클라이언트는 Photo 삭제 요청을 보낸다.
    
4. 서버는 사용자의 인증 정보를 확인한다.
    
5. 서버는 Photo 존재 여부를 확인한다.
    
6. 서버는 Photo 소유자가 현재 User인지 확인한다.
    
7. 서버는 owner가 삭제 가능한 상태인지 확인한다.
    
8. 서버는 Photo를 삭제 처리한다.
    
9. 서버는 삭제 완료 응답을 반환한다.
    
10. 클라이언트는 사진 목록에서 해당 Photo를 제거한다.
    

---

## Output

|항목|설명|
|---|---|
|success|삭제 처리 결과|

---

## Business Rules

- 사용자는 자신의 Photo만 삭제할 수 있다.
    
- 다른 사용자의 Photo는 삭제할 수 없다.
    
- 대표 사진으로 설정된 Photo도 삭제할 수 있다.
    
- 대표 사진이 삭제되면 해당 owner는 대표 사진이 없는 상태가 될 수 있다.
    
- Fabric Photo 삭제 시 Fabric 자체는 삭제되지 않는다.
    
- Project Photo 삭제 시 Project 자체는 삭제되지 않는다.
    
- DailyLog Photo 삭제 시 DailyLog 자체는 삭제되지 않는다.
    
- `ARCHIVED` 상태의 Project에 연결된 Photo는 삭제할 수 없다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog Photo는 삭제할 수 없다.
    
- Photo 삭제 후 해당 Photo는 목록과 상세 조회에서 제외되어야 한다.
    
- S3 Object 물리 삭제 시점은 Backend Design 및 Architecture 문서에서 정의한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|PHO-027|Photo 없음|사진 정보를 찾을 수 없습니다.|
|PHO-028|Photo 접근 권한 없음|해당 사진을 삭제할 수 없습니다.|
|PHO-029|owner 접근 권한 없음|해당 대상의 사진을 삭제할 수 없습니다.|
|PHO-030|Archived Project 사진 삭제 시도|보관된 프로젝트의 사진은 삭제할 수 없습니다.|

---

## Related Entity

- Project
    
- DailyLog
    
- Fabric
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 자신의 Photo를 삭제할 수 있다.
    
- 다른 사용자의 Photo는 삭제할 수 없다.
    
- 삭제된 Photo는 목록에서 조회되지 않는다.
    
- 대표 사진을 삭제할 수 있다.
    
- 대표 사진 삭제 후 owner는 대표 사진이 없는 상태가 될 수 있다.
    
- Photo 삭제 시 owner 자체는 삭제되지 않는다.
    
- `ARCHIVED` 상태의 Project 관련 Photo는 삭제할 수 없다.
    

---

# 3.5 Photo 요약

Photo 도메인은 Project, DailyLog, Fabric에 연결되는 이미지를 공통으로 관리하는 도메인이다.

본 장에서 정의한 핵심 정책은 다음과 같다.

- Photo는 User 소유 데이터이다.
    
- Photo는 `PROJECT`, `DAILY_LOG`, `FABRIC` 중 하나의 owner에 연결된다.
    
- 사용자는 자신의 owner에만 Photo를 추가할 수 있다.
    
- Project는 최대 15장의 Photo를 가질 수 있다.
    
- DailyLog는 최대 15장의 Photo를 가질 수 있다.
    
- Fabric은 최대 2장의 Photo를 가질 수 있다.
    
- Project와 DailyLog는 대표 사진을 1장 가질 수 있다.
    
- Fabric은 별도 대표 사진 설정 기능을 제공하지 않는다.
    
- Photo는 `UPLOADING`, `PROCESSING`, `READY`, `FAILED` 상태를 가진다.
    
- Presigned URL 발급 시 Photo는 `UPLOADING` 상태로 생성된다.
    
- S3 원본 업로드 완료 후 업로드 완료 처리를 해야 한다.
    
- 업로드 완료 처리 후 Photo는 `PROCESSING` 상태가 된다.
    
- 이미지 변환 완료 후 Photo는 `READY` 상태가 된다.
    
- 이미지 변환 실패 시 Photo는 `FAILED` 상태가 된다.
    
- READY 상태의 Photo만 대표 사진으로 설정할 수 있다.
    
- `ARCHIVED` 상태의 Project 또는 해당 Project에 속한 DailyLog에는 Photo를 추가, 수정, 삭제할 수 없다.
    
- S3 Object 삭제 시점과 이미지 변환 상세 방식은 Backend Design 및 Architecture 문서에서 정의한다.



# 3.6 Calendar

## 3.6.1 개요

Calendar 도메인은 사용자의 재봉 작업 기록을 날짜 단위로 조회하고 시각화하는 기능을 담당한다.

소로소로에서 Calendar는 단순한 날짜 표시 기능이 아니라, 사용자가 언제 재봉 작업을 했고, 얼마나 오래 작업했는지 확인할 수 있도록 돕는 기록 시각화 기능이다.

Calendar 도메인은 DailyLog와 DailyLogTimeEntry의 데이터를 기반으로 한다.

Calendar 도메인은 다음 기능을 포함한다.

|개념|설명|
|---|---|
|Monthly Calendar|월별 날짜 기준 작업 기록 요약|
|Daily Summary|특정 날짜의 작업 시간 및 DailyLog 요약|
|Contribution|연도별 작업 시간 기반 재봉 잔디|

Calendar는 별도의 독립 입력 데이터를 생성하지 않는다.  
사용자가 작성한 `PUBLISHED` 상태의 DailyLog를 기준으로 조회 결과를 계산한다.

---

## 3.6.2 기능 목록

|ID|기능명|Priority|설명|
|---|---|---|---|
|CAL-001|월별 캘린더 조회|High|특정 월의 날짜별 작업 기록 요약을 조회한다.|
|CAL-002|특정 날짜 작업 요약 조회|High|특정 날짜의 DailyLog 요약과 총 작업 시간을 조회한다.|
|CAL-003|재봉 잔디 조회|Medium|연도별 작업 시간 기반 Contribution 데이터를 조회한다.|

---

# CAL-001 월별 캘린더 조회

## 목적

사용자는 특정 월에 어떤 날짜에 재봉 작업을 했는지 캘린더 형태로 확인할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 캘린더 화면에 진입하거나, 월을 변경한다.

---

## Main Flow

1. 사용자가 캘린더 화면에 진입한다.
    
2. 클라이언트는 조회할 연도와 월을 서버에 전달한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 해당 연도와 월에 포함되는 사용자의 DailyLog를 조회한다.
    
5. 서버는 `PUBLISHED` 상태의 DailyLog만 집계 대상으로 사용한다.
    
6. 서버는 workedDate 기준으로 날짜별 DailyLog 수와 작업 시간을 집계한다.
    
7. 서버는 월별 캘린더 데이터를 반환한다.
    
8. 클라이언트는 날짜별 작업 여부와 작업 시간을 캘린더에 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|year|필수|조회할 연도|
|month|필수|조회할 월|

---

## Output

|항목|설명|
|---|---|
|year|조회 연도|
|month|조회 월|
|days|날짜별 작업 요약 목록|

### days

|항목|설명|
|---|---|
|date|날짜|
|dailyLogCount|해당 날짜의 PUBLISHED DailyLog 수|
|totalDurationMinutes|해당 날짜의 총 작업 시간|
|hasDailyLog|해당 날짜에 작업 기록이 있는지 여부|
|level|작업 시간 기반 표시 단계|

---

## Calendar Level

월별 캘린더의 날짜별 표시 단계는 하루 총 작업 시간을 기준으로 한다.

|Level|기준|
|---|---|
|0|0분|
|1|1분 이상 30분 이하|
|2|31분 이상 120분 이하|
|3|121분 이상 240분 이하|
|4|241분 이상|

Calendar Level은 시각화를 위한 표시 값이며, 원본 데이터로 저장하지 않는다.

---

## Business Rules

- 사용자는 자신의 DailyLog만 캘린더에서 조회할 수 있다.
    
- 다른 사용자의 DailyLog는 캘린더 집계에 포함되지 않는다.
    
- 캘린더 집계 기준 날짜는 DailyLog의 workedDate이다.
    
- `PUBLISHED` 상태의 DailyLog만 캘린더 집계에 포함한다.
    
- `DRAFT` 상태의 DailyLog는 캘린더 집계에 포함하지 않는다.
    
- DailyLog의 durationMinutes를 날짜별로 합산하여 totalDurationMinutes를 계산한다.
    
- 같은 날짜에 여러 DailyLog가 있으면 해당 날짜의 dailyLogCount와 totalDurationMinutes에 모두 반영한다.
    
- 같은 날짜에 여러 Project의 DailyLog가 있어도 하나의 날짜로 합산한다.
    
- level은 해당 날짜의 totalDurationMinutes를 기준으로 계산한다.
    
- 조회 월에 DailyLog가 없는 날짜도 days에 포함할 수 있다.
    
- 조회 월은 1월부터 12월 사이여야 한다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|CAL-001|연도 누락|조회 연도를 입력해주세요.|
|CAL-002|월 누락|조회 월을 입력해주세요.|
|CAL-003|월 범위 오류|조회 월은 1월부터 12월 사이여야 합니다.|
|CAL-004|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- DailyLog
    
- DailyLogTimeEntry
    
- Project
    

---

## Acceptance Criteria

- 사용자는 특정 월의 날짜별 작업 기록 요약을 조회할 수 있다.
    
- 캘린더에는 PUBLISHED 상태의 DailyLog만 반영된다.
    
- DRAFT 상태의 DailyLog는 캘린더에 반영되지 않는다.
    
- 같은 날짜의 여러 DailyLog는 날짜별로 합산된다.
    
- 날짜별 totalDurationMinutes가 반환된다.
    
- 날짜별 level이 반환된다.
    
- 다른 사용자의 DailyLog는 집계에 포함되지 않는다.
    

---

# CAL-002 특정 날짜 작업 요약 조회

## 목적

사용자는 특정 날짜에 작성한 DailyLog 목록과 해당 날짜의 총 작업 시간을 확인할 수 있어야 한다.

---

## Actor

User

---

## Priority

High

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 캘린더에서 특정 날짜를 선택한다.

---

## Main Flow

1. 사용자가 캘린더에서 특정 날짜를 선택한다.
    
2. 클라이언트는 선택한 날짜를 서버에 전달한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 해당 날짜의 사용자의 DailyLog를 조회한다.
    
5. 서버는 `PUBLISHED` 상태의 DailyLog만 조회 대상으로 사용한다.
    
6. 서버는 해당 날짜의 DailyLog 목록과 총 작업 시간을 계산한다.
    
7. 서버는 날짜별 작업 요약 정보를 반환한다.
    
8. 클라이언트는 해당 날짜의 작업 요약과 DailyLog 목록을 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|date|필수|조회할 날짜|

---

## Output

|항목|설명|
|---|---|
|date|조회 날짜|
|totalDurationMinutes|해당 날짜의 총 작업 시간|
|dailyLogCount|해당 날짜의 PUBLISHED DailyLog 수|
|dailyLogs|해당 날짜의 DailyLog 요약 목록|

### dailyLogs

|항목|설명|
|---|---|
|dailyLogId|DailyLog 식별자|
|projectId|Project 식별자|
|projectTitle|Project 제목|
|workedDate|작업 날짜|
|title|DailyLog 제목|
|workTypes|작업 종류 목록|
|durationMinutes|DailyLog 작업 시간|
|thumbnailUrl|대표 이미지 URL|
|memoPreview|메모 일부|

---

## Business Rules

- 사용자는 자신의 DailyLog만 날짜별로 조회할 수 있다.
    
- 다른 사용자의 DailyLog는 조회 결과에 포함되지 않는다.
    
- 조회 기준 날짜는 DailyLog의 workedDate이다.
    
- `PUBLISHED` 상태의 DailyLog만 날짜별 작업 요약에 포함한다.
    
- `DRAFT` 상태의 DailyLog는 날짜별 작업 요약에 포함하지 않는다.
    
- 같은 날짜에 여러 Project의 DailyLog가 존재할 수 있다.
    
- 같은 날짜에 같은 Project의 DailyLog가 여러 개 존재할 수 있다.
    
- totalDurationMinutes는 해당 날짜의 PUBLISHED DailyLog durationMinutes 합산값이다.
    
- DailyLog 목록은 기본적으로 createdAt 오름차순 또는 workedDate 기준 정렬을 사용할 수 있다.
    
- 정렬의 상세 기준은 API Specification에서 정의한다.
    
- 해당 날짜에 DailyLog가 없으면 dailyLogs는 빈 목록이고 totalDurationMinutes는 0이다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|CAL-005|날짜 누락|조회 날짜를 입력해주세요.|
|CAL-006|날짜 형식 오류|올바른 날짜 형식이 아닙니다.|
|CAL-007|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- DailyLog
    
- DailyLogTimeEntry
    
- Project
    
- Photo
    

---

## Acceptance Criteria

- 사용자는 특정 날짜의 DailyLog 목록을 조회할 수 있다.
    
- 특정 날짜의 totalDurationMinutes가 반환된다.
    
- 특정 날짜에 DailyLog가 없으면 빈 목록과 0분이 반환된다.
    
- PUBLISHED 상태의 DailyLog만 조회 결과에 포함된다.
    
- DRAFT 상태의 DailyLog는 조회 결과에 포함되지 않는다.
    
- 다른 사용자의 DailyLog는 조회 결과에 포함되지 않는다.
    

---

# CAL-003 재봉 잔디 조회

## 목적

사용자는 연도별 재봉 작업 시간을 Contribution 형태로 확인할 수 있어야 한다.

---

## Actor

User

---

## Priority

Medium

---

## Preconditions

- 사용자는 로그인 상태여야 한다.
    
- Access Token이 유효해야 한다.
    
- User 상태가 활성 상태여야 한다.
    

---

## Trigger

사용자가 홈 화면, 통계 화면, 또는 프로필 영역에서 재봉 잔디를 조회한다.

---

## Main Flow

1. 사용자가 재봉 잔디 영역에 진입한다.
    
2. 클라이언트는 조회할 연도를 서버에 전달한다.
    
3. 서버는 사용자의 인증 정보를 확인한다.
    
4. 서버는 해당 연도에 포함되는 사용자의 DailyLog를 조회한다.
    
5. 서버는 `PUBLISHED` 상태의 DailyLog만 집계 대상으로 사용한다.
    
6. 서버는 workedDate 기준으로 날짜별 작업 시간을 합산한다.
    
7. 서버는 날짜별 totalDurationMinutes를 기준으로 level을 계산한다.
    
8. 서버는 연도별 Contribution 데이터를 반환한다.
    
9. 클라이언트는 Contribution 형태로 재봉 잔디를 표시한다.
    

---

## Input

|항목|필수 여부|설명|
|---|---|---|
|year|필수|조회할 연도|

---

## Output

|항목|설명|
|---|---|
|year|조회 연도|
|totalDurationMinutes|해당 연도의 총 작업 시간|
|totalDailyLogCount|해당 연도의 PUBLISHED DailyLog 수|
|activeDays|작업 기록이 있는 날짜 수|
|contributions|날짜별 Contribution 목록|

### contributions

|항목|설명|
|---|---|
|date|날짜|
|dailyLogCount|해당 날짜의 PUBLISHED DailyLog 수|
|totalDurationMinutes|해당 날짜의 총 작업 시간|
|level|작업 시간 기반 표시 단계|

---

## Contribution Level

재봉 잔디의 날짜별 표시 단계는 하루 총 작업 시간을 기준으로 한다.

|Level|기준|
|---|---|
|0|0분|
|1|1분 이상 30분 이하|
|2|31분 이상 120분 이하|
|3|121분 이상 240분 이하|
|4|241분 이상|

Contribution Level은 시각화를 위한 표시 값이며, 원본 데이터로 저장하지 않는다.

---

## Business Rules

- 사용자는 자신의 DailyLog만 재봉 잔디에서 조회할 수 있다.
    
- 다른 사용자의 DailyLog는 재봉 잔디 집계에 포함되지 않는다.
    
- 재봉 잔디 집계 기준 날짜는 DailyLog의 workedDate이다.
    
- `PUBLISHED` 상태의 DailyLog만 재봉 잔디 집계에 포함한다.
    
- `DRAFT` 상태의 DailyLog는 재봉 잔디 집계에 포함하지 않는다.
    
- totalDurationMinutes는 해당 연도 전체 PUBLISHED DailyLog durationMinutes 합산값이다.
    
- totalDailyLogCount는 해당 연도 전체 PUBLISHED DailyLog 수이다.
    
- activeDays는 totalDurationMinutes가 1분 이상인 날짜 수이다.
    
- 같은 날짜에 여러 DailyLog가 있으면 날짜별로 합산한다.
    
- level은 날짜별 totalDurationMinutes를 기준으로 계산한다.
    
- 조회 연도에 DailyLog가 없는 날짜도 level 0으로 표현할 수 있다.
    
- Contribution 데이터는 원본 데이터로 저장하지 않고 조회 시 계산한다.
    
- Contribution 집계 기준은 작업 횟수가 아니라 작업 시간이다.
    

---

## Exceptions

|코드|상황|메시지 예시|
|---|---|---|
|CAL-008|연도 누락|조회 연도를 입력해주세요.|
|CAL-009|연도 형식 오류|올바른 연도 형식이 아닙니다.|
|CAL-010|인증되지 않은 사용자|로그인이 필요합니다.|

---

## Related Entity

- User
    
- DailyLog
    
- DailyLogTimeEntry
    
- Project
    

---

## Acceptance Criteria

- 사용자는 특정 연도의 재봉 잔디를 조회할 수 있다.
    
- 재봉 잔디는 PUBLISHED 상태의 DailyLog만 반영한다.
    
- DRAFT 상태의 DailyLog는 재봉 잔디에 반영하지 않는다.
    
- 날짜별 totalDurationMinutes와 level이 반환된다.
    
- 연도 전체 totalDurationMinutes가 반환된다.
    
- 연도 전체 activeDays가 반환된다.
    
- 다른 사용자의 DailyLog는 집계에 포함되지 않는다.
    
- Contribution Level은 작업 시간이 기준이다.
    

---

# 3.6 Calendar 요약

Calendar 도메인은 DailyLog 데이터를 기반으로 사용자의 재봉 작업 기록을 날짜 단위로 시각화하는 도메인이다.

본 장에서 정의한 핵심 정책은 다음과 같다.

- Calendar는 별도 데이터를 생성하지 않는다.
    
- Calendar는 DailyLog 데이터를 조회하고 집계한다.
    
- 캘린더와 재봉 잔디는 `PUBLISHED` 상태의 DailyLog만 반영한다.
    
- `DRAFT` 상태의 DailyLog는 캘린더와 재봉 잔디에 반영하지 않는다.
    
- 집계 기준 날짜는 DailyLog의 workedDate이다.
    
- 집계 기준 시간은 DailyLog.durationMinutes이다.
    
- 같은 날짜에 여러 DailyLog가 있으면 날짜별로 합산한다.
    
- 같은 날짜에 여러 Project의 DailyLog가 있어도 하나의 날짜로 합산한다.
    
- Calendar Level과 Contribution Level은 하루 총 작업 시간을 기준으로 계산한다.
    
- Level 값은 시각화를 위한 표시 값이며 원본 데이터로 저장하지 않는다.
    
- 다른 사용자의 DailyLog는 캘린더와 재봉 잔디 집계에 포함되지 않는다.



------

# 4. Non-functional Requirements

## 4.1 개요

Non-functional Requirements는 소로소로(SOROSORO)가 기능적으로 동작하는 것뿐 아니라, 사용자가 안정적이고 빠르게 서비스를 이용할 수 있도록 하기 위한 품질 요구사항을 정의한다.

본 장에서는 다음 항목을 다룬다.

- 성능
    
- 보안
    
- 가용성
    
- 확장성
    
- 유지보수성
    
- 사용성
    
- 데이터 무결성
    
- 이미지 처리
    
- 운영 및 모니터링
    
- 백업 및 복구
    

---

## 4.2 NFR 목록

|ID|항목|Priority|설명|
|---|---|---|---|
|NFR-001|응답 성능|High|주요 API는 사용자가 불편을 느끼지 않는 수준으로 응답해야 한다.|
|NFR-002|이미지 조회 성능|High|이미지 중심 서비스이므로 목록 및 상세 화면에서 이미지를 빠르게 조회할 수 있어야 한다.|
|NFR-003|인증 보안|High|인증된 사용자만 개인 데이터에 접근할 수 있어야 한다.|
|NFR-004|데이터 접근 제어|High|사용자는 자신의 데이터만 조회, 수정, 삭제할 수 있어야 한다.|
|NFR-005|토큰 보안|High|Access Token과 Refresh Token은 안전하게 관리되어야 한다.|
|NFR-006|데이터 무결성|High|Project, DailyLog, Fabric, Photo 간의 관계가 일관되게 유지되어야 한다.|
|NFR-007|이미지 업로드 안정성|High|이미지 업로드와 리사이징 과정에서 실패 상태를 관리할 수 있어야 한다.|
|NFR-008|확장성|Medium|향후 AI 자동 기록, 커뮤니티, 통계 기능 확장을 고려해야 한다.|
|NFR-009|유지보수성|High|도메인별 책임이 분리되어 있어야 한다.|
|NFR-010|가용성|Medium|단일 서버 기반 MVP 환경에서도 장애 발생 시 복구 가능한 구조를 가져야 한다.|
|NFR-011|사용성|High|모바일 웹 환경에서 핵심 기록 흐름이 불편하지 않아야 한다.|
|NFR-012|운영 및 모니터링|Medium|주요 오류와 비동기 작업 실패를 확인할 수 있어야 한다.|
|NFR-013|백업 및 복구|Medium|데이터 손실에 대비한 백업과 복구 방안을 고려해야 한다.|

---

# NFR-001 응답 성능

## 목적

사용자가 주요 기능을 이용할 때 불필요한 대기 없이 서비스를 사용할 수 있어야 한다.

---

## 요구사항

- 주요 조회 API는 일반적인 사용 환경에서 빠르게 응답해야 한다.
    
- Project 목록, DailyLog 목록, Fabric 목록 조회는 페이징 또는 제한된 개수 조회를 지원해야 한다.
    
- 목록 조회에서는 상세 데이터를 과도하게 포함하지 않아야 한다.
    
- Project 상세 조회는 필요한 요약 정보를 포함하되, 모든 DailyLog 상세 목록을 한 번에 반환하지 않는다.
    
- DailyLog 상세 조회에서만 TimeEntry 상세 목록을 반환한다.
    
- Calendar와 Contribution 조회는 DailyLog 데이터를 기반으로 계산하되, 불필요한 전체 데이터 로딩을 피해야 한다.
    

---

## Acceptance Criteria

- Project 목록 조회는 페이징 또는 제한된 개수 조회가 가능하다.
    
- Fabric 목록 조회는 페이징 또는 제한된 개수 조회가 가능하다.
    
- DailyLog 목록 조회는 페이징 또는 기간 조건 조회가 가능하다.
    
- Project 상세 조회는 모든 DailyLog 상세 내용을 한 번에 포함하지 않는다.
    
- Calendar 조회는 월 단위로 제한된다.
    
- Contribution 조회는 연도 단위로 제한된다.
    

---

# NFR-002 이미지 조회 성능

## 목적

소로소로는 이미지 중심 서비스이므로, 사용자는 목록과 상세 화면에서 이미지를 빠르게 확인할 수 있어야 한다.

---

## 요구사항

- 이미지 원본과 조회용 이미지를 분리해야 한다.
    
- 목록 화면에서는 Thumbnail 이미지를 사용해야 한다.
    
- 상세 화면에서는 Medium 이미지를 사용할 수 있어야 한다.
    
- 원본 이미지는 필요 시에만 조회한다.
    
- 이미지는 CloudFront를 통해 제공하는 것을 기준으로 한다.
    
- 이미지 변환이 완료되지 않은 경우 클라이언트는 처리 중 상태를 표시할 수 있어야 한다.
    

---

## Acceptance Criteria

- Photo는 Original, Medium, Thumbnail 이미지 정보를 가질 수 있다.
    
- 목록 화면에서는 Thumbnail URL을 사용할 수 있다.
    
- 상세 화면에서는 Medium URL을 사용할 수 있다.
    
- READY 상태의 Photo는 조회 가능한 이미지 URL을 제공한다.
    
- PROCESSING 상태의 Photo는 처리 중임을 구분할 수 있다.
    

---

# NFR-003 인증 보안

## 목적

인증되지 않은 사용자는 개인 기록 데이터에 접근할 수 없어야 한다.

---

## 요구사항

- 인증이 필요한 API는 Access Token을 요구해야 한다.
    
- Access Token이 없거나 유효하지 않으면 요청을 거부해야 한다.
    
- 만료된 Access Token은 Refresh Token을 통해 재발급받을 수 있어야 한다.
    
- Refresh Token이 만료되었거나 유효하지 않으면 재로그인이 필요해야 한다.
    
- 탈퇴 상태의 User는 인증된 사용자로 처리되지 않아야 한다.
    

---

## Acceptance Criteria

- 인증이 필요한 API는 Access Token 없이 호출할 수 없다.
    
- 만료된 Access Token으로 보호된 API에 접근할 수 없다.
    
- 유효한 Refresh Token으로 Access Token을 재발급할 수 있다.
    
- 탈퇴 상태의 User는 로그인 및 토큰 재발급이 불가능하다.
    

---

# NFR-004 데이터 접근 제어

## 목적

사용자는 자신의 데이터만 조회, 수정, 삭제할 수 있어야 한다.

---

## 요구사항

- 모든 주요 데이터는 User 소유권을 기준으로 접근 제어해야 한다.
    
- Project는 소유자만 접근할 수 있어야 한다.
    
- DailyLog는 소유자만 접근할 수 있어야 한다.
    
- Fabric은 소유자만 접근할 수 있어야 한다.
    
- Photo는 소유자만 접근할 수 있어야 한다.
    
- ProjectFabric은 연결된 Project와 Fabric이 모두 현재 User 소유일 때만 접근할 수 있어야 한다.
    
- 다른 사용자의 식별자를 추측해 요청하더라도 데이터가 노출되지 않아야 한다.
    

---

## Acceptance Criteria

- 다른 사용자의 Project를 조회할 수 없다.
    
- 다른 사용자의 DailyLog를 조회할 수 없다.
    
- 다른 사용자의 Fabric을 조회할 수 없다.
    
- 다른 사용자의 Photo를 조회할 수 없다.
    
- 다른 사용자의 Fabric을 자신의 Project에 연결할 수 없다.
    

---

# NFR-005 토큰 보안

## 목적

Access Token과 Refresh Token은 안전하게 발급, 저장, 검증, 무효화되어야 한다.

---

## 요구사항

- Access Token은 짧은 유효 기간을 가져야 한다.
    
- Refresh Token은 Access Token보다 긴 유효 기간을 가질 수 있다.
    
- Refresh Token은 서버 저장소에서 관리되어야 한다.
    
- 하나의 User는 여러 Refresh Token을 가질 수 있다.
    
- 로그아웃 시 현재 기기의 Refresh Token만 무효화해야 한다.
    
- 회원 탈퇴 시 해당 User의 모든 Refresh Token을 무효화해야 한다.
    
- 유효하지 않은 Refresh Token으로 Access Token을 재발급할 수 없어야 한다.
    

---

## Acceptance Criteria

- 로그아웃 후 해당 Refresh Token으로 Access Token을 재발급할 수 없다.
    
- 한 기기에서 로그아웃해도 다른 기기의 로그인은 유지된다.
    
- 회원 탈퇴 시 모든 Refresh Token이 무효화된다.
    
- 저장소에 존재하지 않는 Refresh Token은 사용할 수 없다.
    

---

# NFR-006 데이터 무결성

## 목적

Project, DailyLog, Fabric, Photo 간의 데이터 관계가 일관되게 유지되어야 한다.

---

## 요구사항

- Project는 반드시 User에 속해야 한다.
    
- DailyLog는 반드시 User와 Project에 속해야 한다.
    
- Fabric은 반드시 User에 속해야 한다.
    
- ProjectFabric은 Project와 Fabric을 연결해야 한다.
    
- 동일 Project에 동일 Fabric은 중복 연결할 수 없어야 한다.
    
- DailyLogTimeEntry는 반드시 DailyLog에 속해야 한다.
    
- PUBLISHED 상태의 DailyLog는 하나 이상의 TimeEntry를 가져야 한다.
    
- DailyLog.durationMinutes는 TimeEntry 합산값과 일치해야 한다.
    
- DRAFT 상태의 DailyLog는 통계 계산에 포함되지 않아야 한다.
    
- Photo는 반드시 User와 owner 정보를 가져야 한다.
    
- Photo ownerType은 `PROJECT`, `DAILY_LOG`, `FABRIC` 중 하나여야 한다.
    

---

## Acceptance Criteria

- Project 없이 DailyLog를 생성할 수 없다.
    
- User 없이 Project, DailyLog, Fabric, Photo를 생성할 수 없다.
    
- 동일 Project와 동일 Fabric의 중복 연결이 불가능하다.
    
- PUBLISHED DailyLog는 TimeEntry 없이 생성될 수 없다.
    
- DRAFT DailyLog는 캘린더와 재봉 잔디 집계에 포함되지 않는다.
    
- 잘못된 ownerType으로 Photo를 생성할 수 없다.
    

---

# NFR-007 이미지 업로드 안정성

## 목적

이미지 업로드 및 변환 과정에서 실패가 발생해도 사용자는 상태를 확인할 수 있어야 하며, 서비스 데이터가 불완전하게 노출되지 않아야 한다.

---

## 요구사항

- Presigned URL 발급 시 Photo는 `UPLOADING` 상태로 생성되어야 한다.
    
- 클라이언트가 S3 업로드 완료를 서버에 알려야 한다.
    
- 업로드 완료 처리 후 Photo는 `PROCESSING` 상태가 되어야 한다.
    
- 이미지 변환 완료 후 Photo는 `READY` 상태가 되어야 한다.
    
- 이미지 변환 실패 시 Photo는 `FAILED` 상태가 되어야 한다.
    
- READY 상태가 아닌 Photo는 조회 화면에서 처리 중 또는 실패 상태로 표시할 수 있어야 한다.
    
- 업로드가 완료되지 않은 Photo는 정상 이미지처럼 표시하지 않아야 한다.
    
- 이미지 파일 형식과 크기는 제한되어야 한다.
    

---

## Acceptance Criteria

- Photo는 업로드 및 처리 상태를 가진다.
    
- 업로드 완료 전 Photo는 READY 상태가 아니다.
    
- 이미지 변환 실패 시 FAILED 상태로 구분된다.
    
- READY 상태의 Photo만 대표 사진으로 설정할 수 있다.
    
- owner별 사진 수 제한을 초과할 수 없다.
    

---

# NFR-008 확장성

## 목적

MVP 이후 AI 자동 기록, 커뮤니티, 통계 기능을 확장할 수 있는 구조를 가져야 한다.

---

## 요구사항

- Project와 Fabric은 독립 도메인으로 유지해야 한다.
    
- Project와 Fabric의 관계는 ProjectFabric으로 관리해야 한다.
    
- Photo는 Project, DailyLog, Fabric에서 공통으로 사용할 수 있어야 한다.
    
- AI 기반 원단 정보 자동 기록 기능은 Fabric 생성 전 단계의 별도 Job 구조로 확장할 수 있어야 한다.
    
- 커뮤니티 기능 도입 시 개인 Project를 직접 공개하기보다 별도 공개 모델을 고려할 수 있어야 한다.
    
- 통계 기능은 DailyLog의 workedDate와 durationMinutes를 기반으로 확장할 수 있어야 한다.
    

---

## Acceptance Criteria

- Fabric은 Project 없이도 존재할 수 있다.
    
- Project와 Fabric은 다대다 관계를 표현할 수 있다.
    
- Photo는 여러 ownerType을 지원한다.
    
- DailyLog의 작업 시간 데이터로 Calendar와 Contribution을 계산할 수 있다.
    
- 향후 AI Extraction Job 도입 시 Fabric 도메인과 연결할 수 있다.
    

---

# NFR-009 유지보수성

## 목적

서비스 기능이 확장되더라도 도메인별 책임이 명확하게 분리되어야 한다.

---

## 요구사항

- Authentication, Project, DailyLog, Fabric, Photo, Calendar 도메인은 책임이 구분되어야 한다.
    
- Project 기본 정보 수정과 ProjectSpecification 수정은 분리되어야 한다.
    
- DailyLog 수정과 Photo 수정은 분리되어야 한다.
    
- Fabric 수정과 ProjectFabric 수정은 분리되어야 한다.
    
- Calendar는 데이터를 직접 생성하지 않고 DailyLog 데이터를 조회 및 집계해야 한다.
    
- 기능 요구사항 ID는 API, 테스트 케이스, 이슈 관리에서 추적 가능해야 한다.
    

---

## Acceptance Criteria

- Project 기능 변경이 Fabric 기능에 불필요한 영향을 주지 않는다.
    
- Photo 기능은 Project, DailyLog, Fabric에서 공통으로 사용할 수 있다.
    
- Calendar는 DailyLog 데이터를 기반으로 계산된다.
    
- 요구사항 ID를 기준으로 테스트 케이스를 작성할 수 있다.
    

---

# NFR-010 가용성

## 목적

MVP 환경에서도 사용자는 주요 기능을 안정적으로 사용할 수 있어야 한다.

---

## 요구사항

- 서버 장애 발생 시 재시작 가능한 배포 구조를 가져야 한다.
    
- 애플리케이션은 Docker 기반으로 실행 가능해야 한다.
    
- Docker Compose를 통해 서버 실행 환경을 재현할 수 있어야 한다.
    
- EC2와 Nginx 기반 배포 환경에서 서비스 요청을 처리할 수 있어야 한다.
    
- 외부 서비스 장애 시 오류를 명확히 반환해야 한다.
    
- S3 장애 시 이미지 업로드 및 조회 실패를 구분할 수 있어야 한다.
    
- Kakao OAuth 장애 시 로그인 실패를 구분할 수 있어야 한다.
    

---

## Acceptance Criteria

- Docker 환경에서 애플리케이션을 실행할 수 있다.
    
- Docker Compose로 로컬 또는 서버 실행 환경을 구성할 수 있다.
    
- 외부 서비스 장애 시 사용자에게 실패 상황을 전달할 수 있다.
    
- 이미지 업로드 실패와 로그인 실패는 각각 구분되어야 한다.
    

---

# NFR-011 사용성

## 목적

사용자는 모바일 웹 환경에서 재봉 기록을 빠르고 직관적으로 작성할 수 있어야 한다.

---

## 요구사항

- 핵심 기능은 모바일 화면에서 사용 가능해야 한다.
    
- DailyLog 작성 화면은 작업 날짜, 작업 종류, 시간 구간, 메모, 사진을 쉽게 입력할 수 있어야 한다.
    
- DailyLog는 임시저장을 지원해야 한다.
    
- 원단 등록 화면은 필수 입력을 최소화해야 한다.
    
- Project 생성은 최소 정보로 가능해야 한다.
    
- 사진 업로드 진행 상태를 사용자가 알 수 있어야 한다.
    
- 이미지 처리 중 상태를 사용자가 알 수 있어야 한다.
    
- Calendar와 Contribution은 작업 습관을 직관적으로 보여줘야 한다.
    

---

## Acceptance Criteria

- 사용자는 모바일에서 Project를 생성할 수 있다.
    
- 사용자는 모바일에서 DailyLog를 작성하고 임시저장할 수 있다.
    
- 사용자는 모바일에서 Fabric을 등록할 수 있다.
    
- 사진 업로드 상태를 확인할 수 있다.
    
- 캘린더에서 날짜별 작업 여부를 확인할 수 있다.
    
- 재봉 잔디에서 작업 시간 기반 활동량을 확인할 수 있다.
    

---

# NFR-012 운영 및 모니터링

## 목적

서비스 운영 중 발생하는 오류와 비동기 작업 실패를 확인할 수 있어야 한다.

---

## 요구사항

- 주요 API 오류는 서버 로그로 확인할 수 있어야 한다.
    
- 인증 실패, 권한 오류, 입력값 오류, 외부 서비스 오류는 구분되어야 한다.
    
- 이미지 처리 실패는 Photo 상태로 확인할 수 있어야 한다.
    
- Queue 또는 Worker 처리 실패는 운영자가 확인할 수 있어야 한다.
    
- 서버 배포 후 애플리케이션 실행 상태를 확인할 수 있어야 한다.
    
- 장애 원인 분석을 위해 최소한의 요청 로그와 에러 로그를 남겨야 한다.
    

---

## Acceptance Criteria

- API 오류 발생 시 원인을 구분할 수 있는 로그가 남는다.
    
- 이미지 변환 실패 시 Photo 상태가 FAILED로 남는다.
    
- 외부 서비스 오류와 내부 서버 오류를 구분할 수 있다.
    
- 배포 후 서버 실행 여부를 확인할 수 있다.
    

---

# NFR-013 백업 및 복구

## 목적

사용자의 기록 데이터 손실을 방지하기 위해 백업과 복구 가능성을 고려해야 한다.

---

## 요구사항

- PostgreSQL 데이터는 백업 가능해야 한다.
    
- 이미지 파일은 S3에 저장되어야 한다.
    
- DB 데이터와 S3 이미지 참조 간 불일치가 발생할 수 있음을 고려해야 한다.
    
- 삭제된 데이터와 이미지의 실제 물리 삭제 시점은 별도 정책으로 관리해야 한다.
    
- 장애 발생 시 최근 백업 시점 기준으로 데이터를 복구할 수 있어야 한다.
    

---

## Acceptance Criteria

- 데이터베이스 백업 전략을 수립할 수 있다.
    
- 이미지 파일은 애플리케이션 서버 로컬 디스크에 영구 저장하지 않는다.
    
- DB의 Photo 정보와 S3 Object의 연결 관계를 추적할 수 있다.
    
- 복구 시 DB 데이터와 이미지 참조를 함께 확인할 수 있다.
    

---

# 4장 요약

Non-functional Requirements는 소로소로가 안정적으로 동작하기 위해 필요한 품질 요구사항을 정의한다.

본 장에서 정의한 핵심 기준은 다음과 같다.

- 주요 목록 조회는 페이징 또는 제한된 범위 조회를 지원한다.
    
- 이미지 조회는 Original, Medium, Thumbnail을 분리한다.
    
- 인증이 필요한 API는 Access Token을 요구한다.
    
- 모든 주요 데이터는 User 소유권 기준으로 접근 제어한다.
    
- Refresh Token은 서버에서 관리하며 로그아웃과 탈퇴 시 무효화된다.
    
- PUBLISHED DailyLog만 통계와 캘린더에 반영한다.
    
- DRAFT DailyLog는 통계에 포함하지 않는다.
    
- Photo는 업로드 및 처리 상태를 가진다.
    
- 이미지 업로드와 리사이징 실패 상태를 구분한다.
    
- MVP는 Docker, Docker Compose, EC2, Nginx 기반 배포를 기준으로 한다.
    
- 모바일 웹에서 기록 작성 경험을 우선한다.
    
- 운영 중 오류와 비동기 처리 실패를 확인할 수 있어야 한다.

---
# 5. Global Business Rules

## 5.1 개요

Global Business Rules는 소로소로(SOROSORO) 서비스 전체에 공통으로 적용되는 비즈니스 규칙을 정의한다.

각 도메인별 세부 규칙은 Functional Requirements에서 정의하지만, 여러 도메인에 반복적으로 적용되는 공통 원칙은 본 장에서 별도로 관리한다.

본 장에서 다루는 규칙은 다음과 같다.

- 사용자 소유권
    
- 인증 및 접근 제어
    
- 상태값 관리
    
- 삭제 정책
    
- 날짜 및 시간 기준
    
- 이미지 정책
    
- 통계 집계 기준
    
- 임시저장 정책
    
- 데이터 정합성
    
- MVP 범위 제한
    

---

## 5.2 GBR 목록

|ID|규칙명|설명|
|---|---|---|
|GBR-001|User 소유권 원칙|모든 주요 데이터는 User를 기준으로 소유된다.|
|GBR-002|인증 필수 원칙|개인 데이터 접근 API는 인증된 사용자만 호출할 수 있다.|
|GBR-003|본인 데이터 접근 원칙|사용자는 자신의 데이터만 조회, 수정, 삭제할 수 있다.|
|GBR-004|Project 상태 제약|Project 상태에 따라 수정, 기록, 연결 가능 여부가 달라진다.|
|GBR-005|DailyLog 상태 제약|DRAFT와 PUBLISHED는 필수값과 통계 반영 여부가 다르다.|
|GBR-006|삭제 영향 범위|삭제 시 연결 데이터의 처리 범위를 명확히 한다.|
|GBR-007|날짜 기준|기록과 통계는 DailyLog.workedDate를 기준으로 한다.|
|GBR-008|작업 시간 계산 기준|작업 시간은 TimeEntry 합산값을 기준으로 한다.|
|GBR-009|이미지 소유 및 owner 원칙|Photo는 User와 ownerType, ownerId를 기준으로 관리한다.|
|GBR-010|대표 이미지 원칙|Project와 DailyLog는 대표 사진을 1장만 가질 수 있다.|
|GBR-011|통계 집계 기준|통계는 PUBLISHED DailyLog만 반영한다.|
|GBR-012|MVP 제외 기능 제한|MVP 제외 기능은 요구사항 및 구현 범위에 포함하지 않는다.|

---

# GBR-001 User 소유권 원칙

## 규칙

소로소로의 모든 주요 데이터는 반드시 User를 기준으로 소유된다.

User 소유 데이터는 다음과 같다.

- Project
    
- DailyLog
    
- Fabric
    
- Photo
    
- RefreshToken
    

ProjectSpecification, ProjectReference, ProjectFabric, DailyLogTimeEntry는 직접 User를 기준으로 관리하지 않더라도, 상위 도메인을 통해 User 소유권을 가진다.

예를 들어 ProjectReference는 ProjectSpecification에 속하고, ProjectSpecification은 Project에 속하며, Project는 User에 속한다. 따라서 ProjectReference 역시 해당 Project의 User 소유 데이터로 간주한다.

---

## 적용 대상

- Authentication
    
- Project
    
- DailyLog
    
- Fabric
    
- Photo
    
- Calendar
    

---

## 근거

소로소로는 MVP에서 개인 기록 서비스를 전제로 한다.

다른 사용자와 데이터를 공유하거나 공개하는 기능은 제공하지 않으므로, 모든 데이터는 생성한 User에게만 귀속된다.

---

## 예시

- 사용자가 생성한 Project는 해당 사용자만 조회할 수 있다.
    
- 사용자가 작성한 DailyLog는 해당 사용자만 수정할 수 있다.
    
- 사용자가 등록한 Fabric은 해당 사용자만 Project에 연결할 수 있다.
    
- 사용자가 업로드한 Photo는 해당 사용자만 삭제할 수 있다.
    

---

# GBR-002 인증 필수 원칙

## 규칙

개인 데이터에 접근하는 모든 API는 인증된 사용자만 호출할 수 있다.

인증이 필요한 기능은 다음과 같다.

- Project 생성, 조회, 수정, 삭제
    
- DailyLog 생성, 임시저장, 조회, 수정, 삭제, 발행
    
- Fabric 등록, 조회, 수정, 삭제
    
- ProjectFabric 연결, 수정, 삭제
    
- Photo 업로드, 조회, 대표 사진 설정, 정렬, 삭제
    
- Calendar 조회
    
- Contribution 조회
    
- 내 정보 조회
    
- 로그아웃
    
- 회원 탈퇴
    

---

## 예외

인증 없이 접근 가능한 기능은 다음으로 제한한다.

- 서비스 소개 화면 조회
    
- 카카오 로그인 요청
    

---

## 적용 대상

- Authentication
    
- Project
    
- DailyLog
    
- Fabric
    
- Photo
    
- Calendar
    

---

# GBR-003 본인 데이터 접근 원칙

## 규칙

사용자는 자신의 데이터만 조회, 수정, 삭제할 수 있다.

다른 사용자의 데이터 식별자를 알고 있더라도 해당 데이터에 접근할 수 없어야 한다.

---

## 적용 예시

- 다른 사용자의 projectId로 Project 상세 조회를 요청하면 실패해야 한다.
    
- 다른 사용자의 fabricId를 자신의 Project에 연결할 수 없어야 한다.
    
- 다른 사용자의 dailyLogId로 DailyLog를 수정할 수 없어야 한다.
    
- 다른 사용자의 photoId로 Photo를 삭제할 수 없어야 한다.
    

---

## 예외

MVP에서는 예외를 두지 않는다.

공개 프로젝트, 공유 링크, 커뮤니티 기능이 도입되는 경우 별도 권한 정책을 정의한다.

---

# GBR-004 Project 상태 제약

## 규칙

Project는 다음 상태를 가진다.

|상태|설명|
|---|---|
|IN_PROGRESS|현재 진행 중인 프로젝트|
|ON_HOLD|원단 부족, 부자재 대기 등으로 일시 중단된 프로젝트|
|COMPLETED|완성된 프로젝트|
|ARCHIVED|보관 처리된 프로젝트|

Project 상태에 따라 가능한 작업이 달라진다.

---

## 상태별 허용 작업

|작업|IN_PROGRESS|ON_HOLD|COMPLETED|ARCHIVED|
|---|--:|--:|--:|--:|
|Project 기본 정보 수정|가능|가능|가능|불가|
|ProjectSpecification 수정|가능|가능|가능|불가|
|ProjectReference 등록/수정/삭제|가능|가능|가능|불가|
|DailyLog 생성|가능|가능|가능|불가|
|DailyLog 임시저장|가능|가능|가능|불가|
|DailyLog 수정|가능|가능|가능|불가|
|DailyLog 삭제|가능|가능|가능|불가|
|DRAFT DailyLog 발행|가능|가능|가능|불가|
|Fabric 연결|가능|가능|가능|불가|
|ProjectFabric 수정/삭제|가능|가능|가능|불가|
|Project Photo 추가/대표 변경/정렬/삭제|가능|가능|가능|불가|
|Project 상세 조회|가능|가능|가능|가능|
|Project 목록 조회|가능|가능|가능|가능|
|Project 상태 변경|가능|가능|가능|가능|

---

## 세부 규칙

- `ON_HOLD`는 일시 중단 상태이며, 기록 자체를 막는 상태가 아니다.
    
- `ON_HOLD` 상태에서도 DailyLog를 작성할 수 있다.
    
- `COMPLETED` 상태는 완성된 상태이지만, 회고나 추가 기록을 위해 수정 가능 상태로 둔다.
    
- `ARCHIVED` 상태는 보관 상태이며, 기본적으로 조회와 상태 변경만 허용한다.
    
- `ARCHIVED` 상태에서 다시 다른 상태로 변경하면 수정 가능한 상태로 복귀할 수 있다.
    

---

# GBR-005 DailyLog 상태 제약

## 규칙

DailyLog는 다음 상태를 가진다.

|상태|설명|
|---|---|
|DRAFT|임시저장 상태|
|PUBLISHED|정식 기록 상태|

DailyLog는 `DRAFT`, `PUBLISHED` 상태와 관계없이 사용자가 수정할 수 있다.

단, 상태에 따라 필수값과 통계 반영 여부가 달라진다.

---

## DRAFT 규칙

DRAFT 상태는 사용자가 작성 중인 임시저장 기록이다.

DRAFT 상태에서는 다음 값이 없어도 저장하거나 수정할 수 있다.

- workedDate
    
- workTypes
    
- timeEntries
    
- memo
    
- photos
    

단, DRAFT 상태에서도 다음 값은 필요하다.

- User
    
- Project
    

DRAFT 상태의 DailyLog는 다음 계산에 포함하지 않는다.

- Project 총 작업 시간
    
- Calendar 날짜별 작업 시간
    
- Contribution 재봉 잔디
    
- 작업 통계
    

---

## PUBLISHED 규칙

PUBLISHED 상태는 정식 기록이다.

PUBLISHED 상태의 DailyLog도 수정할 수 있다.

수정 가능한 항목은 다음과 같다.

- workedDate
    
- title
    
- workTypes
    
- memo
    
- timeEntries
    
- photos
    

단, PUBLISHED 상태를 유지하려면 다음 값이 필요하다.

- workedDate
    
- timeEntries 1개 이상
    

PUBLISHED 상태의 DailyLog는 다음 계산에 포함한다.

- Project 총 작업 시간
    
- Calendar 날짜별 작업 시간
    
- Contribution 재봉 잔디
    
- 작업 통계
    

PUBLISHED 상태의 DailyLog를 수정하면, 수정된 workedDate와 durationMinutes를 기준으로 통계가 다시 계산되어야 한다.

---

## 상태 전환 규칙

|전환|허용 여부|설명|
|---|--:|---|
|DRAFT → PUBLISHED|허용|필수값 검증 후 정식 기록으로 발행|
|PUBLISHED → DRAFT|MVP에서는 불허|정식 기록을 다시 임시저장 상태로 되돌리지는 않음|

---

## 세부 규칙

- DRAFT DailyLog는 언제든지 수정할 수 있다.
    
- PUBLISHED DailyLog도 언제든지 수정할 수 있다.
    
- PUBLISHED DailyLog 수정 시 workedDate와 timeEntries 필수 조건은 유지되어야 한다.
    
- PUBLISHED DailyLog의 TimeEntry가 수정되면 durationMinutes를 다시 계산한다.
    
- PUBLISHED DailyLog의 workedDate가 수정되면 Calendar와 Contribution 집계 날짜도 변경된다.
    
- PUBLISHED DailyLog를 수정해도 상태는 PUBLISHED로 유지된다.
    
- MVP에서는 PUBLISHED DailyLog를 DRAFT 상태로 되돌리는 기능을 제공하지 않는다.
    

---

# GBR-006 삭제 영향 범위

## 규칙

삭제 요청이 발생할 경우, 삭제 대상과 연결 데이터의 영향 범위를 명확히 관리해야 한다.

---

## Project 삭제

Project 삭제 시 함께 제거되는 데이터는 다음과 같다.

- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- ProjectFabric
    
- Project에 연결된 Photo의 조회 가능 상태
    

Project 삭제 시 삭제되지 않는 데이터는 다음과 같다.

- Fabric
    

즉, Project를 삭제해도 사용자가 등록한 Fabric 자체는 유지된다.

---

## Fabric 삭제

Fabric 삭제 시 함께 제거되는 데이터는 다음과 같다.

- ProjectFabric
    
- Fabric에 연결된 Photo의 조회 가능 상태
    

Fabric 삭제 시 삭제되지 않는 데이터는 다음과 같다.

- Project
    
- DailyLog
    

즉, Fabric을 삭제해도 해당 Fabric이 연결되어 있던 Project 자체는 유지된다.

---

## DailyLog 삭제

DailyLog 삭제 시 함께 제거되는 데이터는 다음과 같다.

- DailyLogTimeEntry
    
- DailyLog에 연결된 Photo의 조회 가능 상태
    

DailyLog 삭제 후 해당 DailyLog의 작업 시간은 다음 계산에서 제외되어야 한다.

- Project 총 작업 시간
    
- Calendar 날짜별 작업 시간
    
- Contribution 재봉 잔디
    

단, DRAFT 상태의 DailyLog는 원래 통계에 포함되지 않으므로 삭제해도 통계 값에 영향을 주지 않는다.

---

## Photo 삭제

Photo 삭제 시 owner 데이터는 삭제되지 않는다.

예를 들어 Project Photo를 삭제해도 Project는 삭제되지 않는다.

Photo 삭제 후 해당 Photo는 다음 조회 결과에서 제외되어야 한다.

- Photo 목록
    
- Project 상세 사진 목록
    
- DailyLog 상세 사진 목록
    
- Fabric 상세 사진 목록
    

---

## 물리 삭제와 소프트 삭제

본 SRS에서는 사용자가 삭제 요청을 했을 때 서비스 화면과 조회 결과에서 해당 데이터가 제거되어야 함을 정의한다.

다음 항목은 Backend Design 또는 Architecture 문서에서 정의한다.

- 물리 삭제 여부
    
- 소프트 삭제 여부
    
- S3 Object 물리 삭제 시점
    
- 삭제 복구 가능 여부
    
- 삭제 지연 처리 여부
    

---

# GBR-007 날짜 기준

## 규칙

소로소로의 작업 기록과 통계 기준 날짜는 DailyLog의 `workedDate`이다.

---

## 적용 대상

- DailyLog 날짜별 조회
    
- Project 총 작업 기간 계산
    
- Calendar 월별 조회
    
- Contribution 재봉 잔디
    
- 작업 통계
    

---

## 세부 규칙

- DailyLog.createdAt은 기록 생성 시점이다.
    
- DailyLog.workedDate는 실제 작업한 날짜이다.
    
- Calendar와 Contribution은 createdAt이 아니라 workedDate를 기준으로 계산한다.
    
- DRAFT 상태의 DailyLog는 workedDate가 없을 수 있다.
    
- workedDate가 없는 DRAFT DailyLog는 날짜 기반 조회와 통계에 포함하지 않는다.
    
- PUBLISHED 상태의 DailyLog는 반드시 workedDate를 가져야 한다.
    
- PUBLISHED 상태의 DailyLog는 수정될 수 있으므로, workedDate가 변경되면 날짜 기반 조회와 통계 기준도 변경된다.
    

---

# GBR-008 작업 시간 계산 기준

## 규칙

DailyLog의 총 작업 시간은 DailyLogTimeEntry의 durationMinutes 합산값으로 계산한다.

---

## 세부 규칙

- 사용자는 DailyLog.durationMinutes를 직접 입력하지 않는다.
    
- 각 DailyLogTimeEntry는 startedAt과 endedAt을 가진다.
    
- endedAt은 startedAt보다 늦어야 한다.
    
- 각 TimeEntry의 durationMinutes는 startedAt과 endedAt의 차이로 계산한다.
    
- DailyLog.durationMinutes는 해당 DailyLog의 TimeEntry durationMinutes 합산값이다.
    
- PUBLISHED DailyLog는 하나 이상의 TimeEntry를 가져야 한다.
    
- PUBLISHED DailyLog의 TimeEntry가 수정되면 DailyLog.durationMinutes를 다시 계산한다.
    
- DRAFT DailyLog는 TimeEntry가 없어도 저장하거나 수정할 수 있다.
    
- DRAFT DailyLog는 작업 시간 집계에 포함하지 않는다.
    

---

## 적용 대상

- DailyLog 상세 조회
    
- Project 총 작업 시간
    
- Calendar 날짜별 총 작업 시간
    
- Contribution 날짜별 작업 시간
    
- 향후 통계 대시보드
    

---

# GBR-009 이미지 소유 및 owner 원칙

## 규칙

Photo는 반드시 User와 owner 정보를 가진다.

Photo의 ownerType은 다음 중 하나이다.

|ownerType|설명|
|---|---|
|PROJECT|Project에 연결된 사진|
|DAILY_LOG|DailyLog에 연결된 사진|
|FABRIC|Fabric에 연결된 사진|

Photo는 ownerType과 ownerId를 통해 연결 대상을 구분한다.

---

## 세부 규칙

- Photo는 반드시 User 소유 데이터이다.
    
- 사용자는 자신의 owner에만 Photo를 추가할 수 있다.
    
- 다른 사용자의 owner에는 Photo를 추가할 수 없다.
    
- Photo의 owner는 존재해야 한다.
    
- ownerType은 허용된 값 중 하나여야 한다.
    
- Photo는 owner의 상태 제약을 따른다.
    
- Project가 ARCHIVED 상태이면 Project Photo를 추가, 수정, 삭제할 수 없다.
    
- DailyLog가 속한 Project가 ARCHIVED 상태이면 DailyLog Photo를 추가, 수정, 삭제할 수 없다.
    
- Fabric Photo는 Fabric이 현재 User 소유이면 추가, 수정, 삭제할 수 있다.
    
- DRAFT 상태의 DailyLog에도 Photo를 추가, 수정, 삭제할 수 있다.
    
- PUBLISHED 상태의 DailyLog에도 Photo를 추가, 수정, 삭제할 수 있다.
    

---

# GBR-010 대표 이미지 원칙

## 규칙

Project와 DailyLog는 대표 사진을 1장만 가질 수 있다.

Fabric은 별도의 대표 사진 설정 기능을 제공하지 않는다.

---

## 세부 규칙

- Project는 대표 사진을 최대 1장 가질 수 있다.
    
- DailyLog는 대표 사진을 최대 1장 가질 수 있다.
    
- DRAFT 상태의 DailyLog도 대표 사진을 가질 수 있다.
    
- PUBLISHED 상태의 DailyLog도 대표 사진을 가질 수 있다.
    
- 새로운 대표 사진을 설정하면 기존 대표 사진은 자동 해제된다.
    
- 대표 사진은 READY 상태의 Photo만 설정할 수 있다.
    
- 대표 사진이 삭제되면 owner는 대표 사진이 없는 상태가 될 수 있다.
    
- Fabric은 isThumbnail을 대표 사진 정책으로 사용하지 않는다.
    
- Fabric 목록에서는 sortOrder가 가장 앞선 Photo를 대표 이미지처럼 사용할 수 있다.
    

---

# GBR-011 통계 집계 기준

## 규칙

Calendar, Contribution, Project 총 작업 시간 등 통계성 데이터는 PUBLISHED DailyLog만 기준으로 한다.

---

## 집계 포함 대상

- PUBLISHED DailyLog
    
- PUBLISHED DailyLog의 durationMinutes
    
- PUBLISHED DailyLog의 workedDate
    

---

## 집계 제외 대상

- DRAFT DailyLog
    
- 삭제된 DailyLog
    
- 삭제된 Project에 속한 DailyLog
    
- workedDate가 없는 DailyLog
    
- durationMinutes가 없는 DailyLog
    

---

## 세부 규칙

- Project 총 작업 시간은 해당 Project의 PUBLISHED DailyLog durationMinutes 합산값이다.
    
- Calendar 날짜별 총 작업 시간은 해당 날짜의 PUBLISHED DailyLog durationMinutes 합산값이다.
    
- Contribution 날짜별 작업 시간은 해당 날짜의 PUBLISHED DailyLog durationMinutes 합산값이다.
    
- 같은 날짜에 여러 Project의 DailyLog가 있으면 날짜별로 합산한다.
    
- 같은 날짜에 같은 Project의 DailyLog가 여러 개 있어도 모두 합산한다.
    
- Calendar Level과 Contribution Level은 하루 총 작업 시간을 기준으로 계산한다.
    
- Level 값은 원본 데이터로 저장하지 않고 조회 시 계산한다.
    
- PUBLISHED DailyLog가 수정되면 수정된 workedDate와 durationMinutes 기준으로 통계가 다시 계산되어야 한다.
    
- PUBLISHED DailyLog가 삭제되면 해당 DailyLog의 durationMinutes는 통계에서 제외되어야 한다.
    

---

# GBR-012 MVP 제외 기능 제한

## 규칙

MVP에서 제외한 기능은 SRS의 기능 요구사항 및 구현 범위에 포함하지 않는다.

---

## MVP 제외 기능

다음 기능은 MVP에서 제외한다.

- AI 기반 원단 자동 기록
    
- 커뮤니티
    
- 댓글
    
- 좋아요
    
- 팔로우
    
- 공개 프로젝트
    
- 원단 재고 관리
    
- 알림
    
- 검색 고도화
    
- 통계 대시보드
    

---

## 세부 규칙

- MVP에서는 다른 사용자의 데이터를 조회하는 기능을 제공하지 않는다.
    
- MVP에서는 Project 공개 기능을 제공하지 않는다.
    
- MVP에서는 Fabric 공유 기능을 제공하지 않는다.
    
- MVP에서는 AI가 Fabric을 자동 생성하지 않는다.
    
- MVP에서는 알림 발송 기능을 제공하지 않는다.
    
- MVP에서는 고도화된 통계 화면을 제공하지 않는다.
    
- 향후 확장 기능은 Future Scope에서 별도로 정의한다.
    

---

# 5장 요약

Global Business Rules는 소로소로 전체 도메인에 공통 적용되는 비즈니스 규칙을 정의한다.

본 장에서 확정한 핵심 기준은 다음과 같다.

- 모든 주요 데이터는 User 소유 데이터이다.
    
- 인증된 사용자만 개인 데이터에 접근할 수 있다.
    
- 사용자는 자신의 데이터만 조회, 수정, 삭제할 수 있다.
    
- Project 상태는 `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `ARCHIVED`를 사용한다.
    
- `ARCHIVED` 상태의 Project는 조회와 상태 변경을 제외한 대부분의 수정 작업을 제한한다.
    
- DailyLog는 `DRAFT`, `PUBLISHED` 상태를 가진다.
    
- DRAFT DailyLog와 PUBLISHED DailyLog는 모두 수정할 수 있다.
    
- DRAFT DailyLog는 통계에 포함하지 않는다.
    
- PUBLISHED DailyLog만 Calendar, Contribution, Project 총 작업 시간에 반영한다.
    
- PUBLISHED DailyLog를 수정하면 수정된 workedDate와 durationMinutes 기준으로 통계를 다시 계산한다.
    
- MVP에서는 PUBLISHED DailyLog를 DRAFT로 되돌리는 기능은 제공하지 않는다.
    
- 기록과 통계 기준 날짜는 DailyLog.workedDate이다.
    
- 작업 시간은 DailyLogTimeEntry 합산으로 계산한다.
    
- Project와 DailyLog는 대표 사진을 1장만 가질 수 있다.
    
- Fabric은 별도 대표 사진 설정 기능을 제공하지 않는다.
    
- MVP 제외 기능은 기능 요구사항과 구현 범위에 포함하지 않는다.

---
# 6. Permission Policy

## 6.1 개요

Permission Policy는 소로소로(SOROSORO)에서 사용자가 어떤 데이터에 접근할 수 있는지, 어떤 작업을 수행할 수 있는지를 정의한다.

소로소로 MVP는 개인 기록 서비스를 전제로 한다. 따라서 모든 주요 데이터는 User 소유권을 기준으로 접근 제어한다.

본 장에서 다루는 권한 정책은 다음과 같다.

- 인증 상태에 따른 접근 권한
    
- User 소유 데이터 접근 권한
    
- Project 권한
    
- DailyLog 권한
    
- Fabric 권한
    
- ProjectFabric 권한
    
- Photo 권한
    
- Calendar 권한
    
- 탈퇴 사용자 권한
    
- 상태 기반 권한 제한
    

---

## 6.2 Permission Rule 목록

|ID|규칙명|설명|
|---|---|---|
|PERM-001|인증 사용자 접근 원칙|개인 데이터 API는 인증된 사용자만 접근할 수 있다.|
|PERM-002|Guest 접근 제한|Guest는 개인 데이터에 접근할 수 없다.|
|PERM-003|User 소유 데이터 접근|User는 자신이 소유한 데이터만 접근할 수 있다.|
|PERM-004|Project 접근 권한|Project는 소유자만 조회, 수정, 삭제할 수 있다.|
|PERM-005|DailyLog 접근 권한|DailyLog는 소유자만 조회, 수정, 삭제할 수 있다.|
|PERM-006|Fabric 접근 권한|Fabric은 소유자만 조회, 수정, 삭제할 수 있다.|
|PERM-007|ProjectFabric 접근 권한|Project와 Fabric이 모두 현재 User 소유일 때만 접근할 수 있다.|
|PERM-008|Photo 접근 권한|Photo는 소유자와 owner 소유권을 기준으로 접근 제어한다.|
|PERM-009|Calendar 접근 권한|Calendar는 현재 User의 DailyLog만 집계한다.|
|PERM-010|탈퇴 사용자 접근 제한|탈퇴 상태의 User는 서비스 기능에 접근할 수 없다.|
|PERM-011|ARCHIVED Project 권한 제한|ARCHIVED 상태의 Project는 조회와 상태 변경 외 대부분의 수정 작업을 제한한다.|

---

# PERM-001 인증 사용자 접근 원칙

## 규칙

개인 데이터에 접근하는 모든 API는 인증된 사용자만 호출할 수 있다.

인증 여부는 Access Token을 기준으로 판단한다.

---

## 인증이 필요한 기능

다음 기능은 인증된 사용자만 사용할 수 있다.

- 내 정보 조회
    
- 로그아웃
    
- 회원 탈퇴
    
- Project 생성, 조회, 수정, 삭제
    
- ProjectSpecification 조회, 수정
    
- ProjectReference 등록, 수정, 삭제
    
- DailyLog 생성, 임시저장, 조회, 수정, 삭제, 발행
    
- Fabric 등록, 조회, 수정, 삭제
    
- ProjectFabric 연결, 수정, 삭제
    
- Photo Presigned URL 발급
    
- Photo 업로드 완료 처리
    
- Photo 조회, 대표 사진 설정, 정렬, 삭제
    
- Calendar 조회
    
- Contribution 조회
    

---

## 권한 없음 처리

Access Token이 없거나 유효하지 않은 경우 요청은 거부된다.

---

## Acceptance Criteria

- Access Token 없이 개인 데이터 API를 호출할 수 없다.
    
- 유효하지 않은 Access Token으로 개인 데이터 API를 호출할 수 없다.
    
- 만료된 Access Token으로 개인 데이터 API를 호출할 수 없다.
    
- 인증 실패 시 개인 데이터는 응답에 포함되지 않는다.
    

---

# PERM-002 Guest 접근 제한

## 규칙

Guest는 로그인하지 않은 사용자이다.

Guest는 개인 데이터에 접근할 수 없다.

---

## Guest가 접근 가능한 기능

Guest가 접근 가능한 기능은 다음으로 제한한다.

- 서비스 소개 화면 조회
    
- 카카오 로그인 요청
    

---

## Guest가 접근할 수 없는 기능

Guest는 다음 데이터에 접근할 수 없다.

- Project
    
- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- Fabric
    
- ProjectFabric
    
- Photo
    
- Calendar
    
- Contribution
    
- User 개인 정보
    

---

## Acceptance Criteria

- Guest는 Project 목록을 조회할 수 없다.
    
- Guest는 DailyLog를 작성할 수 없다.
    
- Guest는 Fabric을 등록할 수 없다.
    
- Guest는 Photo를 업로드할 수 없다.
    
- Guest는 Calendar와 Contribution을 조회할 수 없다.
    

---

# PERM-003 User 소유 데이터 접근

## 규칙

User는 자신이 소유한 데이터만 조회, 수정, 삭제할 수 있다.

다른 사용자의 데이터 식별자를 알고 있더라도 접근할 수 없어야 한다.

---

## 소유권 판단 기준

|데이터|소유권 판단 기준|
|---|---|
|Project|Project.userId|
|DailyLog|DailyLog.userId|
|Fabric|Fabric.userId|
|Photo|Photo.userId|
|RefreshToken|RefreshToken.userId|
|ProjectSpecification|연결된 Project의 userId|
|ProjectReference|연결된 ProjectSpecification의 Project.userId|
|DailyLogTimeEntry|연결된 DailyLog의 userId|
|ProjectFabric|연결된 Project.userId와 Fabric.userId|

---

## 예시

- 현재 User의 id가 1이고, Project.userId가 2이면 해당 Project에 접근할 수 없다.
    
- 현재 User의 id가 1이고, Fabric.userId가 2이면 해당 Fabric을 자신의 Project에 연결할 수 없다.
    
- 현재 User의 id가 1이고, Photo.userId가 2이면 해당 Photo를 삭제할 수 없다.
    

---

## Acceptance Criteria

- User는 자신의 데이터만 조회할 수 있다.
    
- User는 자신의 데이터만 수정할 수 있다.
    
- User는 자신의 데이터만 삭제할 수 있다.
    
- 다른 사용자의 식별자로 요청해도 데이터가 노출되지 않는다.
    

---

# PERM-004 Project 접근 권한

## 규칙

Project는 해당 Project를 생성한 User만 접근할 수 있다.

---

## 허용 작업

Project 소유자는 다음 작업을 수행할 수 있다.

- Project 생성
    
- Project 목록 조회
    
- Project 상세 조회
    
- Project 기본 정보 수정
    
- Project 삭제
    
- Project 상태 변경
    
- ProjectSpecification 조회 및 수정
    
- ProjectReference 등록, 수정, 삭제
    
- Project에 Fabric 연결
    
- ProjectFabric 수정 및 삭제
    
- Project Photo 추가, 대표 변경, 정렬, 삭제
    
- Project에 DailyLog 작성
    

---

## 제한 작업

Project 소유자가 아닌 User는 다음 작업을 수행할 수 없다.

- Project 상세 조회
    
- Project 기본 정보 수정
    
- Project 삭제
    
- Project 상태 변경
    
- ProjectSpecification 조회 및 수정
    
- ProjectReference 등록, 수정, 삭제
    
- Project에 Fabric 연결
    
- Project Photo 추가
    
- Project에 DailyLog 작성
    

---

## 상태 기반 제한

Project가 `ARCHIVED` 상태인 경우, 소유자라도 다음 작업은 제한된다.

- Project 기본 정보 수정
    
- ProjectSpecification 수정
    
- ProjectReference 등록, 수정, 삭제
    
- DailyLog 생성
    
- DailyLog 임시저장
    
- DailyLog 수정
    
- DailyLog 삭제
    
- DRAFT DailyLog 발행
    
- Fabric 연결
    
- ProjectFabric 수정 및 삭제
    
- Project Photo 추가, 대표 변경, 정렬, 삭제
    

단, 다음 작업은 가능하다.

- Project 목록 조회
    
- Project 상세 조회
    
- Project 상태 변경
    

---

## Acceptance Criteria

- Project 소유자는 자신의 Project를 조회할 수 있다.
    
- Project 소유자가 아닌 User는 해당 Project를 조회할 수 없다.
    
- Project 소유자는 자신의 Project 상태를 변경할 수 있다.
    
- `ARCHIVED` 상태의 Project는 조회와 상태 변경만 가능하다.
    

---

# PERM-005 DailyLog 접근 권한

## 규칙

DailyLog는 해당 DailyLog를 작성한 User만 접근할 수 있다.

DailyLog는 반드시 하나의 Project에 속하므로, DailyLog 접근 시 DailyLog 소유권과 Project 소유권이 모두 현재 User 기준으로 확인되어야 한다.

---

## 허용 작업

DailyLog 소유자는 다음 작업을 수행할 수 있다.

- DailyLog 생성
    
- DailyLog 임시저장
    
- Project별 DailyLog 목록 조회
    
- 날짜별 DailyLog 목록 조회
    
- DailyLog 상세 조회
    
- DailyLog 수정
    
- DailyLog 삭제
    
- DRAFT DailyLog 발행
    
- DailyLog Photo 추가, 대표 변경, 정렬, 삭제
    

---

## 상태별 권한

|DailyLog 상태|조회|수정|삭제|발행|통계 반영|
|---|--:|--:|--:|--:|--:|
|DRAFT|가능|가능|가능|가능|미반영|
|PUBLISHED|가능|가능|가능|해당 없음|반영|

---

## Project 상태에 따른 제한

DailyLog가 속한 Project가 `ARCHIVED` 상태인 경우, 소유자라도 다음 작업은 제한된다.

- DailyLog 생성
    
- DailyLog 임시저장
    
- DailyLog 수정
    
- DailyLog 삭제
    
- DRAFT DailyLog 발행
    
- DailyLog Photo 추가, 대표 변경, 정렬, 삭제
    

단, 다음 작업은 가능하다.

- DailyLog 목록 조회
    
- DailyLog 상세 조회
    

---

## 권한 검증 기준

DailyLog 접근 권한은 다음 순서로 확인한다.

1. 요청 사용자가 인증된 User인지 확인한다.
    
2. DailyLog가 존재하는지 확인한다.
    
3. DailyLog.userId가 현재 User의 id와 일치하는지 확인한다.
    
4. DailyLog가 속한 Project가 현재 User의 소유인지 확인한다.
    
5. 수정성 작업인 경우 Project 상태가 허용 가능한 상태인지 확인한다.
    

---

## Acceptance Criteria

- User는 자신의 DailyLog를 조회할 수 있다.
    
- User는 자신의 DRAFT DailyLog를 수정할 수 있다.
    
- User는 자신의 PUBLISHED DailyLog를 수정할 수 있다.
    
- User는 자신의 DRAFT DailyLog를 PUBLISHED 상태로 발행할 수 있다.
    
- 다른 사용자의 DailyLog는 조회, 수정, 삭제할 수 없다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog는 수정성 작업을 수행할 수 없다.
    

---

# PERM-006 Fabric 접근 권한

## 규칙

Fabric은 해당 Fabric을 등록한 User만 접근할 수 있다.

Fabric은 Project와 독립적으로 존재할 수 있으므로, Fabric 접근 권한은 Fabric.userId를 기준으로 판단한다.

---

## 허용 작업

Fabric 소유자는 다음 작업을 수행할 수 있다.

- Fabric 등록
    
- Fabric 목록 조회
    
- Fabric 상세 조회
    
- Fabric 수정
    
- Fabric 삭제
    
- Fabric 사용 Project 목록 조회
    
- Fabric Photo 추가, 정렬, 삭제
    

---

## 제한 작업

Fabric 소유자가 아닌 User는 다음 작업을 수행할 수 없다.

- Fabric 상세 조회
    
- Fabric 수정
    
- Fabric 삭제
    
- Fabric을 자신의 Project에 연결
    
- Fabric Photo 추가, 정렬, 삭제
    
- Fabric 사용 Project 목록 조회
    

---

## Acceptance Criteria

- User는 자신의 Fabric을 조회할 수 있다.
    
- User는 자신의 Fabric을 수정할 수 있다.
    
- User는 자신의 Fabric을 삭제할 수 있다.
    
- 다른 사용자의 Fabric은 조회, 수정, 삭제할 수 없다.
    
- 다른 사용자의 Fabric은 자신의 Project에 연결할 수 없다.
    

---

# PERM-007 ProjectFabric 접근 권한

## 규칙

ProjectFabric은 Project와 Fabric의 연결 정보이다.

ProjectFabric에 접근하려면 연결된 Project와 Fabric이 모두 현재 User의 소유여야 한다.

---

## 허용 작업

현재 User가 Project와 Fabric을 모두 소유한 경우 다음 작업을 수행할 수 있다.

- Project와 Fabric 연결
    
- ProjectFabric 메모 수정
    
- ProjectFabric 삭제
    
- Project 상세에서 연결된 Fabric 목록 조회
    
- Fabric 상세에서 사용 Project 목록 조회
    

---

## 제한 작업

다음 경우 ProjectFabric 작업은 허용되지 않는다.

- Project가 현재 User의 소유가 아닌 경우
    
- Fabric이 현재 User의 소유가 아닌 경우
    
- Project와 Fabric 중 하나라도 다른 User의 소유인 경우
    
- Project가 `ARCHIVED` 상태이고 수정성 작업을 요청한 경우
    

---

## 상태 기반 제한

Project가 `ARCHIVED` 상태인 경우 다음 작업은 제한된다.

- Project와 Fabric 연결
    
- ProjectFabric 메모 수정
    
- ProjectFabric 삭제
    

단, 다음 작업은 가능하다.

- Project 상세에서 연결된 Fabric 목록 조회
    
- Fabric 상세에서 사용 Project 목록 조회
    

---

## Acceptance Criteria

- User는 자신의 Project와 자신의 Fabric만 연결할 수 있다.
    
- 다른 사용자의 Fabric은 자신의 Project에 연결할 수 없다.
    
- 다른 사용자의 Project에는 자신의 Fabric을 연결할 수 없다.
    
- ProjectFabric 수정 시 Project와 Fabric이 모두 현재 User 소유여야 한다.
    
- `ARCHIVED` 상태의 Project에서는 ProjectFabric 수정성 작업을 수행할 수 없다.
    

---

# PERM-008 Photo 접근 권한

## 규칙

Photo는 User 소유권과 owner 소유권을 기준으로 접근 제어한다.

Photo는 다음 owner에 연결될 수 있다.

- Project
    
- DailyLog
    
- Fabric
    

Photo 접근 시에는 Photo.userId뿐 아니라 owner가 현재 User 소유인지도 확인해야 한다.

---

## 허용 작업

Photo 소유자는 owner가 현재 User 소유일 때 다음 작업을 수행할 수 있다.

- Presigned URL 발급
    
- 업로드 완료 처리
    
- Photo 목록 조회
    
- 대표 사진 설정
    
- Photo 정렬 순서 수정
    
- Photo 삭제
    

---

## ownerType별 권한

|ownerType|권한 판단 기준|
|---|---|
|PROJECT|Project.userId가 현재 User와 일치해야 한다.|
|DAILY_LOG|DailyLog.userId와 DailyLog가 속한 Project.userId가 현재 User와 일치해야 한다.|
|FABRIC|Fabric.userId가 현재 User와 일치해야 한다.|

---

## 상태 기반 제한

다음 경우 Photo 수정성 작업은 제한된다.

|대상|제한 조건|
|---|---|
|PROJECT Photo|Project가 `ARCHIVED` 상태인 경우|
|DAILY_LOG Photo|DailyLog가 속한 Project가 `ARCHIVED` 상태인 경우|
|FABRIC Photo|Fabric이 현재 User 소유가 아닌 경우|

Photo 수정성 작업은 다음을 의미한다.

- Presigned URL 발급
    
- 업로드 완료 처리
    
- 대표 사진 설정
    
- 정렬 순서 수정
    
- 삭제
    

---

## 대표 사진 권한

대표 사진 설정은 다음 조건을 만족해야 한다.

- Photo는 현재 User 소유여야 한다.
    
- Photo의 owner도 현재 User 소유여야 한다.
    
- Photo의 ownerType은 `PROJECT` 또는 `DAILY_LOG`여야 한다.
    
- Photo는 `READY` 상태여야 한다.
    
- Fabric Photo는 대표 사진으로 설정할 수 없다.
    

---

## Acceptance Criteria

- User는 자신의 owner에 Photo를 추가할 수 있다.
    
- User는 다른 사용자의 owner에 Photo를 추가할 수 없다.
    
- User는 자신의 Photo만 삭제할 수 있다.
    
- Project와 DailyLog는 READY 상태의 Photo만 대표 사진으로 설정할 수 있다.
    
- Fabric Photo는 대표 사진으로 설정할 수 없다.
    
- `ARCHIVED` 상태의 Project 관련 Photo는 수정성 작업을 수행할 수 없다.
    

---

# PERM-009 Calendar 접근 권한

## 규칙

Calendar와 Contribution은 현재 User의 DailyLog만 조회 및 집계한다.

---

## 허용 작업

인증된 User는 다음 작업을 수행할 수 있다.

- 자신의 월별 Calendar 조회
    
- 자신의 특정 날짜 작업 요약 조회
    
- 자신의 연도별 Contribution 조회
    

---

## 집계 권한 기준

Calendar와 Contribution은 다음 데이터를 기준으로 한다.

- 현재 User가 소유한 DailyLog
    
- `PUBLISHED` 상태의 DailyLog
    
- 삭제되지 않은 DailyLog
    
- workedDate가 존재하는 DailyLog
    
- durationMinutes가 존재하는 DailyLog
    

---

## 제한 작업

다음 데이터는 Calendar와 Contribution 집계에 포함하지 않는다.

- 다른 사용자의 DailyLog
    
- DRAFT 상태의 DailyLog
    
- 삭제된 DailyLog
    
- 삭제된 Project에 속한 DailyLog
    
- workedDate가 없는 DailyLog
    

---

## Acceptance Criteria

- User는 자신의 Calendar만 조회할 수 있다.
    
- User는 자신의 Contribution만 조회할 수 있다.
    
- 다른 사용자의 DailyLog는 Calendar 집계에 포함되지 않는다.
    
- DRAFT DailyLog는 Calendar와 Contribution 집계에 포함되지 않는다.
    
- PUBLISHED DailyLog만 Calendar와 Contribution 집계에 반영된다.
    

---

# PERM-010 탈퇴 사용자 접근 제한

## 규칙

탈퇴 상태의 User는 서비스 기능에 접근할 수 없다.

User 상태가 `DELETED`인 경우, 인증 및 개인 데이터 접근은 제한된다.

---

## 제한 작업

탈퇴 상태의 User는 다음 작업을 수행할 수 없다.

- 로그인
    
- Access Token 재발급
    
- Project 생성, 조회, 수정, 삭제
    
- DailyLog 생성, 조회, 수정, 삭제
    
- Fabric 등록, 조회, 수정, 삭제
    
- Photo 업로드, 조회, 삭제
    
- Calendar 조회
    
- Contribution 조회
    

---

## 토큰 처리

회원 탈퇴 시 다음 처리를 수행한다.

- User 상태를 `DELETED`로 변경한다.
    
- 해당 User의 모든 Refresh Token을 무효화한다.
    
- 기존 Refresh Token으로 Access Token을 재발급할 수 없도록 한다.
    

---

## Acceptance Criteria

- 탈퇴 상태의 User는 로그인할 수 없다.
    
- 탈퇴 상태의 User는 Access Token을 재발급받을 수 없다.
    
- 탈퇴 처리 후 기존 Refresh Token은 사용할 수 없다.
    
- 탈퇴 상태의 User는 개인 데이터 API를 호출할 수 없다.
    

---

# PERM-011 ARCHIVED Project 권한 제한

## 규칙

`ARCHIVED` 상태의 Project는 보관 상태로 간주한다.

보관된 Project는 조회와 상태 변경만 허용하고, 기본적으로 수정성 작업은 제한한다.

---

## 허용 작업

ARCHIVED 상태에서도 가능한 작업은 다음과 같다.

- Project 목록 조회
    
- Project 상세 조회
    
- Project 상태 변경
    
- Project에 연결된 DailyLog 조회
    
- Project에 연결된 Fabric 조회
    
- Project에 연결된 Photo 조회
    

---

## 제한 작업

ARCHIVED 상태에서는 다음 작업을 수행할 수 없다.

- Project 기본 정보 수정
    
- ProjectSpecification 수정
    
- ProjectReference 등록, 수정, 삭제
    
- DailyLog 생성
    
- DailyLog 임시저장
    
- DailyLog 수정
    
- DailyLog 삭제
    
- DRAFT DailyLog 발행
    
- Fabric 연결
    
- ProjectFabric 수정
    
- ProjectFabric 삭제
    
- Project Photo 추가
    
- Project Photo 대표 변경
    
- Project Photo 정렬
    
- Project Photo 삭제
    
- DailyLog Photo 추가
    
- DailyLog Photo 대표 변경
    
- DailyLog Photo 정렬
    
- DailyLog Photo 삭제
    

---

## 상태 복귀

ARCHIVED 상태의 Project는 다시 다음 상태로 변경될 수 있다.

- IN_PROGRESS
    
- ON_HOLD
    
- COMPLETED
    

Project가 ARCHIVED 상태에서 다른 상태로 변경되면, 해당 상태에서 허용되는 수정성 작업을 다시 수행할 수 있다.

---

## Acceptance Criteria

- ARCHIVED Project는 조회할 수 있다.
    
- ARCHIVED Project는 상태 변경할 수 있다.
    
- ARCHIVED Project는 기본 정보 수정이 불가능하다.
    
- ARCHIVED Project에는 DailyLog를 추가할 수 없다.
    
- ARCHIVED Project에 속한 DailyLog는 수정할 수 없다.
    
- ARCHIVED Project 관련 Photo는 수정성 작업을 수행할 수 없다.
    
- ARCHIVED Project를 다른 상태로 변경하면 수정성 작업이 다시 가능하다.
    

---

# 6장 요약

Permission Policy는 소로소로의 데이터 접근 권한과 상태 기반 제한을 정의한다.

본 장에서 확정한 핵심 기준은 다음과 같다.

- 개인 데이터 API는 인증된 사용자만 호출할 수 있다.
    
- Guest는 서비스 소개와 카카오 로그인 요청만 사용할 수 있다.
    
- User는 자신의 데이터만 조회, 수정, 삭제할 수 있다.
    
- Project는 소유자만 접근할 수 있다.
    
- DailyLog는 소유자만 접근할 수 있다.
    
- DRAFT DailyLog와 PUBLISHED DailyLog는 모두 수정할 수 있다.
    
- Fabric은 소유자만 접근할 수 있다.
    
- ProjectFabric은 Project와 Fabric이 모두 현재 User 소유일 때만 접근할 수 있다.
    
- Photo는 Photo 소유권과 owner 소유권을 모두 확인해야 한다.
    
- Calendar와 Contribution은 현재 User의 PUBLISHED DailyLog만 집계한다.
    
- 탈퇴 상태의 User는 로그인, 토큰 재발급, 개인 데이터 접근이 불가능하다.
    
- ARCHIVED Project는 조회와 상태 변경만 가능하며, 수정성 작업은 제한된다.
---
# 7. Data Requirements

## 7.1 개요

Data Requirements는 소로소로(SOROSORO)에서 관리해야 하는 주요 데이터와 데이터 간 관계, 필수 속성, 상태값, 제약조건을 정의한다.

본 장은 ERD나 실제 테이블 설계를 확정하는 문서가 아니다.  
데이터베이스 컬럼 타입, 인덱스, 외래키, 삭제 전략, JPA 연관관계, QueryDSL 조회 전략 등은 `04_ERD.md`와 `05_Backend_Design.md`에서 상세히 정의한다.

본 장에서는 SRS 관점에서 다음 내용을 정의한다.

- 주요 데이터 엔티티
    
- 엔티티별 핵심 속성
    
- 엔티티 간 관계
    
- 상태값 및 Enum
    
- 필수값
    
- 데이터 제약조건
    
- 삭제 시 데이터 영향 범위
    
- 통계 계산에 필요한 데이터 기준
    

---

## 7.2 Data Entity 목록

소로소로 MVP에서 관리하는 주요 데이터 엔티티는 다음과 같다.

|Entity|설명|
|---|---|
|User|카카오 로그인을 통해 서비스를 이용하는 사용자|
|RefreshToken|사용자 로그인 세션 유지를 위한 토큰|
|Project|사용자가 제작하는 하나의 재봉 작품|
|ProjectSpecification|Project의 제작 설정 정보|
|ProjectReference|Project에 참고한 외부 URL 또는 자료|
|DailyLog|특정 날짜에 특정 Project에서 수행한 작업 기록|
|DailyLogTimeEntry|DailyLog 안의 작업 시간 구간|
|Fabric|사용자가 구매한 원단 기록|
|ProjectFabric|Project와 Fabric의 연결 정보|
|Photo|Project, DailyLog, Fabric에 연결되는 이미지|

---

## 7.3 Entity Relationship Overview

소로소로의 주요 데이터 관계는 다음과 같다.

```text
User
 ├── RefreshToken
 ├── Project
 │    ├── ProjectSpecification
 │    │    └── ProjectReference
 │    ├── DailyLog
 │    │    └── DailyLogTimeEntry
 │    └── ProjectFabric
 │         └── Fabric
 │
 ├── Fabric
 └── Photo
```

### 주요 관계 요약

|관계|설명|
|---|---|
|User 1 : N Project|한 사용자는 여러 Project를 가질 수 있다.|
|User 1 : N DailyLog|한 사용자는 여러 DailyLog를 작성할 수 있다.|
|User 1 : N Fabric|한 사용자는 여러 Fabric을 등록할 수 있다.|
|User 1 : N Photo|한 사용자는 여러 Photo를 업로드할 수 있다.|
|User 1 : N RefreshToken|한 사용자는 여러 기기 또는 세션의 RefreshToken을 가질 수 있다.|
|Project 1 : 1 ProjectSpecification|하나의 Project는 하나의 ProjectSpecification을 가진다.|
|ProjectSpecification 1 : N ProjectReference|하나의 ProjectSpecification은 여러 참고자료를 가질 수 있다.|
|Project 1 : N DailyLog|하나의 Project는 여러 DailyLog를 가질 수 있다.|
|DailyLog 1 : N DailyLogTimeEntry|하나의 DailyLog는 여러 작업 시간 구간을 가질 수 있다.|
|Project N : M Fabric|Project와 Fabric은 ProjectFabric을 통해 다대다 관계를 가진다.|
|Project 1 : N ProjectFabric|하나의 Project는 여러 Fabric 연결 정보를 가질 수 있다.|
|Fabric 1 : N ProjectFabric|하나의 Fabric은 여러 Project에 연결될 수 있다.|
|Photo N : 1 Owner|Photo는 Project, DailyLog, Fabric 중 하나에 연결된다.|

---

# 7.4 User

## 설명

User는 카카오 로그인을 통해 소로소로를 이용하는 사용자이다.

MVP에서는 카카오 로그인만 제공하므로 User는 카카오 계정 식별자를 기준으로 생성된다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|User 식별자|
|kakaoId|필수|카카오 사용자 식별자|
|nickname|선택|사용자 닉네임|
|profileImageUrl|선택|카카오 프로필 이미지 URL|
|role|필수|사용자 권한|
|status|필수|사용자 상태|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## UserStatus

|값|설명|
|---|---|
|ACTIVE|활성 사용자|
|DELETED|탈퇴 사용자|

---

## UserRole

|값|설명|
|---|---|
|USER|일반 사용자|

MVP에서는 별도의 Admin 기능을 제공하지 않는다.

---

## 데이터 규칙

- kakaoId는 User를 식별하는 핵심 값이다.
    
- 하나의 kakaoId는 하나의 User와 연결된다.
    
- User 상태가 `DELETED`이면 로그인, 토큰 재발급, 개인 데이터 접근이 제한된다.
    
- User는 Project, DailyLog, Fabric, Photo, RefreshToken의 소유자이다.
    

---

# 7.5 RefreshToken

## 설명

RefreshToken은 사용자의 로그인 상태를 유지하고 Access Token을 재발급하기 위한 데이터이다.

하나의 User는 여러 RefreshToken을 가질 수 있다.  
이는 여러 기기 또는 여러 브라우저 세션에서 로그인할 수 있는 상황을 고려한 구조이다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|RefreshToken 식별자|
|userId|필수|RefreshToken 소유 User 식별자|
|token|필수|Refresh Token 값|
|expiredAt|필수|Refresh Token 만료 일시|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## 데이터 규칙

- RefreshToken은 반드시 User에 속한다.
    
- 하나의 User는 여러 RefreshToken을 가질 수 있다.
    
- 로그아웃 시 현재 기기의 RefreshToken만 무효화된다.
    
- 회원 탈퇴 시 해당 User의 모든 RefreshToken이 무효화된다.
    
- 만료된 RefreshToken으로 Access Token을 재발급할 수 없다.
    
- 저장소에 존재하지 않는 RefreshToken은 사용할 수 없다.
    

---

# 7.6 Project

## 설명

Project는 사용자가 제작하는 하나의 재봉 작품이다.

예시는 다음과 같다.

- 딸기 리본 카드지갑
    
- 파자마 바지
    
- 에코백
    
- 파우치
    
- 잠옷 세트
    

Project는 DailyLog, ProjectSpecification, ProjectReference, ProjectFabric, Photo의 중심이 되는 도메인이다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|Project 식별자|
|userId|필수|Project 소유 User 식별자|
|title|필수|프로젝트명|
|description|선택|프로젝트 설명|
|status|필수|Project 상태|
|startedAt|선택|프로젝트 시작일|
|completedAt|선택|프로젝트 완료일|
|retrospective|선택|프로젝트 회고|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## ProjectStatus

|값|설명|
|---|---|
|IN_PROGRESS|현재 진행 중인 프로젝트|
|ON_HOLD|원단 부족, 부자재 대기 등으로 일시 중단된 프로젝트|
|COMPLETED|완성된 프로젝트|
|ARCHIVED|보관 처리된 프로젝트|

---

## 데이터 규칙

- Project는 반드시 User에 속한다.
    
- Project title은 필수값이다.
    
- Project title은 공백만으로 구성될 수 없다.
    
- Project 생성 시 기본 상태는 `IN_PROGRESS`이다.
    
- Project 생성 시 ProjectSpecification이 함께 생성되어야 한다.
    
- Project는 여러 DailyLog를 가질 수 있다.
    
- Project는 여러 Fabric과 연결될 수 있다.
    
- Project는 최대 15장의 Photo를 가질 수 있다.
    
- Project는 대표 Photo를 최대 1장 가질 수 있다.
    
- `ARCHIVED` 상태의 Project는 조회와 상태 변경을 제외한 수정성 작업이 제한된다.
    
- Project 삭제 시 연결된 DailyLog, ProjectSpecification, ProjectReference, ProjectFabric은 함께 제거되어야 한다.
    
- Project 삭제 시 연결된 Fabric 자체는 삭제되지 않는다.
    

---

# 7.7 ProjectSpecification

## 설명

ProjectSpecification은 Project의 제작 설정 정보를 관리한다.

Project가 작품 자체를 의미한다면, ProjectSpecification은 해당 작품을 만들 때 사용한 패턴 방식, 완성 규격, 재봉틀 세팅, 바늘, 실 등 자주 변하지 않는 설정값을 기록한다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|ProjectSpecification 식별자|
|projectId|필수|연결된 Project 식별자|
|patternMethod|선택|패턴 방식|
|patternName|선택|패턴명|
|finishedWidthCm|선택|완성 가로 길이|
|finishedHeightCm|선택|완성 세로 길이|
|finishedSizeMemo|선택|완성 규격 메모|
|sewingMachineSettingMemo|선택|재봉틀 세팅 메모|
|needleMemo|선택|사용 바늘 메모|
|threadMemo|선택|사용 실 메모|
|memo|선택|기타 설정 메모|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## PatternMethod

|값|설명|
|---|---|
|SELF_DRAFTED|처음부터 직접 만든 패턴|
|COPIED|기존 패턴을 그대로 사용|
|MODIFIED|기존 패턴을 참고하되 치수, 구조, 디테일을 변형|

---

## 데이터 규칙

- ProjectSpecification은 반드시 Project에 속한다.
    
- 하나의 Project는 하나의 ProjectSpecification만 가진다.
    
- Project 생성 시 빈 ProjectSpecification이 함께 생성된다.
    
- patternMethod는 허용된 값만 저장할 수 있다.
    
- finishedWidthCm와 finishedHeightCm가 입력된 경우 0보다 커야 한다.
    
- 지퍼 길이, 고무줄 길이, 포켓 수, 밑위 등 작품별로 달라지는 규격은 finishedSizeMemo에 기록한다.
    
- `ARCHIVED` 상태의 Project에 속한 ProjectSpecification은 수정할 수 없다.
    

---

# 7.8 ProjectReference

## 설명

ProjectReference는 Project를 만들 때 참고한 외부 URL 또는 자료를 기록한다.

예시는 다음과 같다.

- 유튜브 튜토리얼 링크
    
- 블로그 제작 과정 링크
    
- 구매한 패턴 상품 링크
    
- 참고 이미지 URL
    
- 부자재 구매 링크
    

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|ProjectReference 식별자|
|projectSpecificationId|필수|연결된 ProjectSpecification 식별자|
|url|필수|참고자료 URL|
|title|선택|참고자료 제목|
|memo|선택|참고자료 메모|
|sortOrder|선택|정렬 순서|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## 데이터 규칙

- ProjectReference는 반드시 ProjectSpecification에 속한다.
    
- 하나의 ProjectSpecification은 여러 ProjectReference를 가질 수 있다.
    
- url은 필수값이다.
    
- url은 URL 형식이어야 한다.
    
- 동일 Project에 동일 URL을 중복 등록할 수 있다.
    
- sortOrder는 ProjectReference 표시 순서에 사용할 수 있다.
    
- `ARCHIVED` 상태의 Project에 속한 ProjectReference는 등록, 수정, 삭제할 수 없다.
    

---

# 7.9 DailyLog

## 설명

DailyLog는 특정 날짜에 특정 Project에서 수행한 재봉 작업 기록이다.

DailyLog는 작업 날짜, 작업 종류, 작업 메모, 작업 시간, 사진 등을 통해 Project의 제작 과정을 누적한다.

DailyLog는 임시저장 상태와 정식 기록 상태를 가진다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|DailyLog 식별자|
|userId|필수|DailyLog 소유 User 식별자|
|projectId|필수|연결된 Project 식별자|
|status|필수|DailyLog 상태|
|workedDate|조건부|작업 날짜. PUBLISHED 상태에서는 필수|
|title|선택|작업 일지 제목|
|workTypes|선택|작업 종류 목록|
|durationMinutes|필수|총 작업 시간|
|memo|선택|작업 메모|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## DailyLogStatus

|값|설명|
|---|---|
|DRAFT|임시저장 상태|
|PUBLISHED|정식 기록 상태|

---

## WorkType

|값|설명|
|---|---|
|PATTERN_DRAFTING|패턴 그리기|
|CUTTING|재단|
|INTERFACING|심지 부착|
|SEWING|재봉|
|PRESSING|다림질|
|FITTING|착용 또는 핏 확인|
|FIXING|수정 또는 보완|
|FINISHING|마감|
|ETC|기타|

---

## 데이터 규칙

- DailyLog는 반드시 User에 속한다.
    
- DailyLog는 반드시 Project에 속한다.
    
- DailyLog가 속한 Project도 현재 User 소유여야 한다.
    
- DailyLog는 `DRAFT`, `PUBLISHED` 상태를 가진다.
    
- DRAFT 상태에서는 workedDate가 없어도 저장할 수 있다.
    
- DRAFT 상태에서는 TimeEntry가 없어도 저장할 수 있다.
    
- DRAFT 상태에서는 통계에 반영하지 않는다.
    
- PUBLISHED 상태에서는 workedDate가 필수이다.
    
- PUBLISHED 상태에서는 하나 이상의 DailyLogTimeEntry가 필요하다.
    
- PUBLISHED 상태의 DailyLog만 Project 총 작업 시간, Calendar, Contribution에 반영한다.
    
- DRAFT DailyLog와 PUBLISHED DailyLog는 모두 수정할 수 있다.
    
- PUBLISHED DailyLog를 수정하면 수정된 workedDate와 durationMinutes를 기준으로 통계를 다시 계산해야 한다.
    
- MVP에서는 PUBLISHED 상태를 DRAFT 상태로 되돌리지 않는다.
    
- DailyLog는 최대 15장의 Photo를 가질 수 있다.
    
- DailyLog는 대표 Photo를 최대 1장 가질 수 있다.
    
- `ARCHIVED` 상태의 Project에 속한 DailyLog는 생성, 임시저장, 수정, 삭제, 발행할 수 없다.
    

---

# 7.10 DailyLogTimeEntry

## 설명

DailyLogTimeEntry는 하나의 DailyLog 안에서 실제 작업한 시간 구간을 의미한다.

예시는 다음과 같다.

- 15:00 ~ 16:00 지퍼 달기
    
- 20:00 ~ 21:00 단추 달기
    
- 22:30 ~ 23:10 마감 정리
    

하나의 DailyLog는 여러 DailyLogTimeEntry를 가질 수 있다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|DailyLogTimeEntry 식별자|
|dailyLogId|필수|연결된 DailyLog 식별자|
|startedAt|필수|작업 시작 시각|
|endedAt|필수|작업 종료 시각|
|durationMinutes|필수|작업 시간|
|memo|선택|시간 구간 메모|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## 데이터 규칙

- DailyLogTimeEntry는 반드시 DailyLog에 속한다.
    
- 하나의 DailyLog는 여러 DailyLogTimeEntry를 가질 수 있다.
    
- endedAt은 startedAt보다 늦어야 한다.
    
- durationMinutes는 endedAt과 startedAt의 차이로 계산한다.
    
- DailyLog.durationMinutes는 DailyLogTimeEntry.durationMinutes 합산값이다.
    
- PUBLISHED DailyLog는 하나 이상의 DailyLogTimeEntry를 가져야 한다.
    
- DRAFT DailyLog는 DailyLogTimeEntry가 없어도 저장할 수 있다.
    
- DailyLog 수정 시 TimeEntry 변경이 발생하면 DailyLog.durationMinutes도 다시 계산되어야 한다.
    
- TimeEntry의 개별 수정 API는 MVP에서 제공하지 않고, DailyLog 수정 요청 안에서 전체 교체 방식으로 처리할 수 있다.
    

---

# 7.11 Fabric

## 설명

Fabric은 사용자가 구매한 원단 정보를 기록하는 독립 도메인이다.

Fabric은 특정 Project에 종속되지 않는다.  
사용자는 원단을 먼저 등록한 뒤, 이후 여러 Project에 연결할 수 있다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|Fabric 식별자|
|userId|필수|Fabric 소유 User 식별자|
|name|필수|사용자가 부르는 원단명|
|productName|선택|쇼핑몰 상품명|
|productCode|선택|품번|
|productUrl|선택|상품 URL|
|storeName|선택|구매처|
|purchasedAt|선택|구매일|
|purchasePrice|선택|구매 가격|
|color|선택|색상|
|size|선택|원단 규격. 예: 140cm * 90cm|
|width|선택|원단폭. 예: 140cm|
|materialComposition|선택|혼용률 또는 재질|
|memo|선택|원단 메모|
|rating|선택|사용자 평점|
|repurchaseIntention|필수|재구매 의사|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## RepurchaseIntention

|값|설명|
|---|---|
|YES|다시 구매하고 싶음|
|NO|다시 구매하고 싶지 않음|
|UNKNOWN|아직 판단하지 않음|

---

## 데이터 규칙

- Fabric은 반드시 User에 속한다.
    
- Fabric은 Project 없이도 존재할 수 있다.
    
- Fabric은 ProjectFabric을 통해 Project와 연결된다.
    
- name은 필수값이다.
    
- name은 공백만으로 구성될 수 없다.
    
- productUrl이 입력된 경우 URL 형식이어야 한다.
    
- purchasePrice가 입력된 경우 0 이상이어야 한다.
    
- rating이 입력된 경우 허용 범위 안의 값이어야 한다.
    
- repurchaseIntention의 기본값은 `UNKNOWN`이다.
    
- 같은 품번이어도 구매일, 구매처, 가격이 다르면 별도의 Fabric으로 등록할 수 있다.
    
- Fabric은 최대 2장의 Photo를 가질 수 있다.
    
- Fabric은 별도 대표 Photo 설정 기능을 제공하지 않는다.
    
- Fabric 삭제 시 Project는 삭제되지 않는다.
    
- Fabric 삭제 시 해당 Fabric과 연결된 ProjectFabric은 함께 제거되어야 한다.
    

---

# 7.12 ProjectFabric

## 설명

ProjectFabric은 Project와 Fabric의 연결 정보를 나타낸다.

Project와 Fabric은 다대다 관계를 가지며, 이를 ProjectFabric으로 표현한다.

ProjectFabric은 단순 조인 데이터가 아니라, 특정 Project에서 해당 Fabric을 어떤 맥락으로 사용했는지 기록할 수 있는 연결 엔티티이다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|ProjectFabric 식별자|
|projectId|필수|연결된 Project 식별자|
|fabricId|필수|연결된 Fabric 식별자|
|memo|선택|해당 Project에서 Fabric을 사용한 맥락|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## 데이터 규칙

- ProjectFabric은 반드시 Project와 Fabric을 연결한다.
    
- Project와 Fabric은 모두 같은 User의 소유여야 한다.
    
- 동일 Project에 동일 Fabric은 중복 연결할 수 없다.
    
- ProjectFabric은 연결 메모를 가질 수 있다.
    
- ProjectFabric을 삭제해도 Project는 삭제되지 않는다.
    
- ProjectFabric을 삭제해도 Fabric은 삭제되지 않는다.
    
- Project가 삭제되면 연결된 ProjectFabric은 제거되어야 한다.
    
- Fabric이 삭제되면 연결된 ProjectFabric은 제거되어야 한다.
    
- `ARCHIVED` 상태의 Project에는 ProjectFabric을 새로 생성, 수정, 삭제할 수 없다.
    
- `ARCHIVED` 상태의 Project에 연결된 ProjectFabric은 조회만 가능하다.
    

---

# 7.13 Photo

## 설명

Photo는 Project, DailyLog, Fabric에 연결되는 공통 이미지 데이터이다.

Photo는 ownerType과 ownerId를 통해 어떤 도메인에 연결되는지 구분한다.

---

## 주요 속성

|속성|필수 여부|설명|
|---|---|---|
|id|필수|Photo 식별자|
|userId|필수|Photo 소유 User 식별자|
|ownerType|필수|Photo 연결 대상 타입|
|ownerId|필수|Photo 연결 대상 식별자|
|isThumbnail|필수|대표 사진 여부|
|originalKey|필수|원본 이미지 저장 Key|
|mediumKey|선택|Medium 이미지 저장 Key|
|thumbnailKey|선택|Thumbnail 이미지 저장 Key|
|width|선택|이미지 너비|
|height|선택|이미지 높이|
|sizeBytes|필수|이미지 파일 크기|
|sortOrder|선택|정렬 순서|
|status|필수|Photo 상태|
|processedAt|선택|이미지 처리 완료 일시|
|createdAt|필수|생성 일시|
|updatedAt|필수|수정 일시|

---

## OwnerType

|값|설명|
|---|---|
|PROJECT|Project에 연결된 사진|
|DAILY_LOG|DailyLog에 연결된 사진|
|FABRIC|Fabric에 연결된 사진|

---

## PhotoStatus

|값|설명|
|---|---|
|UPLOADING|Presigned URL 발급 후 클라이언트가 원본 이미지를 업로드 중인 상태|
|PROCESSING|원본 업로드 완료 후 이미지 변환 작업이 진행 중인 상태|
|READY|이미지 업로드 및 변환이 완료되어 조회 가능한 상태|
|FAILED|이미지 업로드 또는 변환에 실패한 상태|

---

## 데이터 규칙

- Photo는 반드시 User에 속한다.
    
- Photo는 반드시 ownerType과 ownerId를 가진다.
    
- ownerType은 `PROJECT`, `DAILY_LOG`, `FABRIC` 중 하나여야 한다.
    
- ownerId는 ownerType에 해당하는 실제 데이터의 식별자여야 한다.
    
- 사용자는 자신의 owner에만 Photo를 추가할 수 있다.
    
- Project는 최대 15장의 Photo를 가질 수 있다.
    
- DailyLog는 최대 15장의 Photo를 가질 수 있다.
    
- Fabric은 최대 2장의 Photo를 가질 수 있다.
    
- Project와 DailyLog는 대표 Photo를 최대 1장 가질 수 있다.
    
- Fabric은 별도 대표 Photo 설정 기능을 제공하지 않는다.
    
- Project와 DailyLog의 대표 Photo는 `READY` 상태만 설정할 수 있다.
    
- Presigned URL 발급 시 Photo는 `UPLOADING` 상태로 생성된다.
    
- 업로드 완료 처리 후 Photo는 `PROCESSING` 상태가 된다.
    
- 이미지 변환 완료 후 Photo는 `READY` 상태가 된다.
    
- 이미지 변환 실패 시 Photo는 `FAILED` 상태가 된다.
    
- READY 상태가 아닌 Photo는 정상 이미지로 표시하지 않는다.
    
- `ARCHIVED` 상태의 Project와 관련된 Photo는 수정성 작업이 제한된다.
    

---

# 7.14 Derived Data Requirements

## 설명

Derived Data는 별도 원본 데이터로 저장하기보다 기존 데이터를 기반으로 계산되는 값이다.

소로소로에서는 다음 값을 조회 시 계산하거나 캐시 형태로 관리할 수 있다.

---

## 파생 데이터 목록

|파생 데이터|계산 기준|
|---|---|
|Project.totalDurationMinutes|해당 Project의 PUBLISHED DailyLog durationMinutes 합산|
|Project.dailyLogCount|해당 Project의 PUBLISHED DailyLog 수|
|Calendar.totalDurationMinutes|특정 날짜의 PUBLISHED DailyLog durationMinutes 합산|
|Calendar.dailyLogCount|특정 날짜의 PUBLISHED DailyLog 수|
|Contribution.level|특정 날짜의 totalDurationMinutes 기준|
|Fabric.usedProjectCount|해당 Fabric과 연결된 ProjectFabric 수|
|Fabric.usedProjects|해당 Fabric과 연결된 Project 목록|
|Thumbnail URL|Photo.thumbnailKey 기반|
|Medium URL|Photo.mediumKey 기반|

---

## 데이터 규칙

- Derived Data는 원본 데이터와 불일치하지 않아야 한다.
    
- DRAFT DailyLog는 작업 시간 파생 데이터에 포함하지 않는다.
    
- PUBLISHED DailyLog가 수정되면 관련 Derived Data도 변경된 기준으로 계산되어야 한다.
    
- PUBLISHED DailyLog가 삭제되면 관련 Derived Data에서 제외되어야 한다.
    
- Calendar Level과 Contribution Level은 원본 데이터로 저장하지 않고 조회 시 계산한다.
    
- 이미지 URL은 저장 Key를 기반으로 생성할 수 있다.
    

---

# 7.15 Data Validation Requirements

## 공통 검증 규칙

|항목|규칙|
|---|---|
|필수값|필수 속성은 누락될 수 없다.|
|문자열|필수 문자열은 공백만으로 구성될 수 없다.|
|URL|URL 속성은 URL 형식이어야 한다.|
|날짜 범위|종료일은 시작일보다 빠를 수 없다.|
|시간 범위|endedAt은 startedAt보다 늦어야 한다.|
|금액|금액은 0 이상이어야 한다.|
|정렬 순서|sortOrder는 owner 또는 그룹 내 표시 순서로 사용된다.|
|Enum|허용된 값 외의 Enum은 저장할 수 없다.|
|소유권|데이터 생성, 수정, 삭제 시 User 소유권을 확인해야 한다.|

---

## Entity별 주요 검증

|Entity|검증|
|---|---|
|Project|title 필수, status 허용값 검증|
|ProjectSpecification|patternMethod 허용값 검증, finishedWidthCm/finishedHeightCm 0 초과|
|ProjectReference|url 필수, URL 형식 검증|
|DailyLog|status 허용값 검증, PUBLISHED일 경우 workedDate와 TimeEntry 필수|
|DailyLogTimeEntry|endedAt > startedAt|
|Fabric|name 필수, productUrl 형식 검증, purchasePrice 0 이상, repurchaseIntention 허용값 검증|
|ProjectFabric|동일 Project-Fabric 중복 연결 금지|
|Photo|ownerType 허용값 검증, status 허용값 검증, owner별 사진 수 제한|
|RefreshToken|expiredAt 필수, token 필수|

---

# 7.16 Data Retention and Deletion Requirements

## 삭제 기준

SRS에서는 삭제 요청 후 사용자가 해당 데이터를 더 이상 조회할 수 없어야 한다는 요구사항을 정의한다.

물리 삭제, 소프트 삭제, 삭제 지연, S3 Object 삭제 시점은 Backend Design 및 Architecture 문서에서 정의한다.

---

## 삭제 영향 범위

|삭제 대상|함께 제거되거나 조회 제외되는 데이터|유지되는 데이터|
|---|---|---|
|Project|ProjectSpecification, ProjectReference, DailyLog, DailyLogTimeEntry, ProjectFabric, Project Photo 조회 가능 상태|Fabric|
|DailyLog|DailyLogTimeEntry, DailyLog Photo 조회 가능 상태|Project, Fabric|
|Fabric|ProjectFabric, Fabric Photo 조회 가능 상태|Project, DailyLog|
|ProjectFabric|Project와 Fabric의 연결|Project, Fabric|
|Photo|Photo 자체|연결된 owner|
|User 탈퇴|RefreshToken 무효화, User 상태 DELETED|개인 데이터 처리 정책은 별도 정의|

---

# 7.17 Data Requirements 요약

Data Requirements는 소로소로에서 관리해야 하는 데이터와 데이터 간 관계를 정의한다.

본 장에서 확정한 핵심 기준은 다음과 같다.

- User는 Project, DailyLog, Fabric, Photo, RefreshToken의 소유자이다.
    
- Project는 하나의 재봉 작품이다.
    
- Project는 하나의 ProjectSpecification을 가진다.
    
- ProjectSpecification은 여러 ProjectReference를 가질 수 있다.
    
- Project는 여러 DailyLog를 가질 수 있다.
    
- DailyLog는 DRAFT 또는 PUBLISHED 상태를 가진다.
    
- DRAFT DailyLog는 통계에 포함하지 않는다.
    
- PUBLISHED DailyLog만 Calendar, Contribution, Project 총 작업 시간에 반영한다.
    
- DRAFT DailyLog와 PUBLISHED DailyLog는 모두 수정할 수 있다.
    
- DailyLogTimeEntry는 DailyLog의 작업 시간 구간이다.
    
- DailyLog.durationMinutes는 DailyLogTimeEntry의 합산값이다.
    
- Fabric은 Project와 독립적으로 존재할 수 있다.
    
- Project와 Fabric은 ProjectFabric을 통해 다대다 관계를 가진다.
    
- 동일 Project에 동일 Fabric은 중복 연결할 수 없다.
    
- Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 데이터이다.
    
- Project와 DailyLog는 최대 15장의 Photo를 가질 수 있다.
    
- Fabric은 최대 2장의 Photo를 가질 수 있다.
    
- Project와 DailyLog는 대표 Photo를 1장만 가질 수 있다.
    
- Fabric은 별도 대표 Photo 설정 기능을 제공하지 않는다.
    
- Calendar와 Contribution은 별도 원본 데이터를 생성하지 않고 DailyLog 데이터를 기반으로 계산한다.

---
# 8. External Interface Requirements

## 8.1 개요

External Interface Requirements는 소로소로(SOROSORO)가 외부 환경과 어떻게 상호작용해야 하는지를 정의한다.

본 장에서는 다음 인터페이스를 다룬다.

- 사용자 인터페이스
    
- API 인터페이스
    
- 인증 인터페이스
    
- 이미지 업로드 및 조회 인터페이스
    
- 외부 서비스 인터페이스
    
- 배포 및 실행 환경 인터페이스
    
- 오류 응답 인터페이스
    

구체적인 API Endpoint, Request, Response, Error Code 상세는 `07_API.md`에서 별도로 정의한다.

---

## 8.2 Interface 목록

|ID|인터페이스|설명|
|---|---|---|
|EIF-001|User Interface|사용자가 모바일 웹 또는 데스크톱 웹에서 서비스를 이용하는 화면|
|EIF-002|REST API Interface|클라이언트와 백엔드 서버 간 HTTP API|
|EIF-003|Kakao OAuth Interface|카카오 로그인을 위한 외부 인증 인터페이스|
|EIF-004|JWT Authentication Interface|Access Token 기반 API 인증 인터페이스|
|EIF-005|S3 Upload Interface|Presigned URL 기반 이미지 업로드 인터페이스|
|EIF-006|CloudFront Image Delivery Interface|이미지 조회를 위한 CDN 인터페이스|
|EIF-007|Queue and Worker Interface|이미지 리사이징 비동기 처리 인터페이스|
|EIF-008|Deployment Interface|Docker, Docker Compose, EC2, Nginx 기반 실행 환경|
|EIF-009|Error Response Interface|클라이언트가 처리 가능한 일관된 오류 응답 형식|

---

# EIF-001 User Interface

## 설명

소로소로는 모바일 웹 중심의 사용자 인터페이스를 제공한다.

재봉 기록은 작업 중간 또는 작업 직후에 작성될 가능성이 높으므로, 사용자는 모바일 환경에서 Project, DailyLog, Fabric, Photo 기능을 불편 없이 사용할 수 있어야 한다.

---

## 주요 화면

MVP에서 필요한 주요 화면은 다음과 같다.

|화면|설명|
|---|---|
|로그인 화면|카카오 로그인 진입|
|홈 화면|최근 Project, 최근 DailyLog, 재봉 잔디 요약 표시|
|Project 목록 화면|사용자의 Project 목록 조회|
|Project 상세 화면|Project 기본 정보, 설정, 일지, 원단, 사진 조회|
|Project 생성/수정 화면|Project 기본 정보 입력 및 수정|
|Project 설정 화면|ProjectSpecification 및 ProjectReference 관리|
|DailyLog 작성 화면|작업 날짜, 작업 종류, 시간 구간, 메모, 사진 입력|
|DailyLog 상세 화면|특정 DailyLog 상세 조회|
|Fabric 목록 화면|등록한 원단 목록 조회|
|Fabric 상세 화면|원단 상세 정보와 사용 Project 조회|
|Fabric 등록/수정 화면|원단 정보 입력 및 수정|
|Calendar 화면|월별 작업 기록 조회|
|Contribution 화면|연도별 재봉 잔디 조회|
|마이페이지/설정 화면|내 정보 조회, 로그아웃, 회원 탈퇴|

---

## 사용자 인터페이스 요구사항

- 사용자는 모바일 웹에서 주요 기능을 사용할 수 있어야 한다.
    
- Project 생성은 최소 정보로 가능해야 한다.
    
- DailyLog는 작성 중 임시저장이 가능해야 한다.
    
- DailyLog 작성 화면은 작업 날짜, 작업 종류, 시간 구간, 메모, 사진 입력을 지원해야 한다.
    
- Fabric 등록 화면은 원단명만으로도 등록할 수 있어야 한다.
    
- 사진 업로드 중, 처리 중, 실패 상태를 사용자에게 표시할 수 있어야 한다.
    
- Calendar는 날짜별 작업 여부와 작업 시간을 시각적으로 보여줘야 한다.
    
- Contribution은 연도별 작업 습관을 직관적으로 보여줘야 한다.
    
- 권한이 없는 데이터에 접근한 경우 사용자는 적절한 오류 또는 접근 불가 화면을 확인해야 한다.
    

---

## Acceptance Criteria

- 사용자는 모바일 웹에서 카카오 로그인할 수 있다.
    
- 사용자는 모바일 웹에서 Project를 생성할 수 있다.
    
- 사용자는 모바일 웹에서 DailyLog를 작성하고 임시저장할 수 있다.
    
- 사용자는 모바일 웹에서 Fabric을 등록할 수 있다.
    
- 사용자는 모바일 웹에서 사진 업로드 상태를 확인할 수 있다.
    
- 사용자는 Calendar와 Contribution을 통해 작업 기록을 확인할 수 있다.
    

---

# EIF-002 REST API Interface

## 설명

클라이언트와 백엔드 서버는 REST API를 통해 통신한다.

API는 JSON 기반 Request와 Response를 사용한다.

---

## 기본 원칙

- API 기본 경로는 `/api/v1`을 사용한다.
    
- Request Body는 JSON 형식을 사용한다.
    
- Response Body는 JSON 형식을 사용한다.
    
- 인증이 필요한 API는 Authorization Header를 사용한다.
    
- 리소스 식별자는 URL Path Variable로 전달한다.
    
- 필터, 정렬, 페이징 조건은 Query Parameter로 전달할 수 있다.
    
- 성공 응답과 오류 응답은 일관된 형식을 가져야 한다.
    

---

## 주요 API 그룹

|API 그룹|설명|
|---|---|
|Auth API|카카오 로그인, 토큰 재발급, 로그아웃, 내 정보 조회, 회원 탈퇴|
|Project API|Project 생성, 조회, 수정, 삭제, 상태 변경|
|ProjectSpecification API|Project 설정 조회 및 수정|
|ProjectReference API|참고자료 등록, 수정, 삭제|
|DailyLog API|DailyLog 생성, 임시저장, 조회, 수정, 삭제, 발행|
|Fabric API|Fabric 등록, 조회, 수정, 삭제, 사용 Project 조회|
|ProjectFabric API|Project와 Fabric 연결, 연결 메모 수정, 연결 삭제|
|Photo API|Presigned URL 발급, 업로드 완료 처리, 목록 조회, 대표 사진 설정, 정렬, 삭제|
|Calendar API|월별 Calendar 조회, 날짜별 작업 요약 조회, Contribution 조회|

---

## Acceptance Criteria

- 클라이언트는 JSON 기반으로 서버와 통신할 수 있다.
    
- API는 `/api/v1` prefix를 사용한다.
    
- 인증이 필요한 API는 Authorization Header를 요구한다.
    
- 목록 조회 API는 Query Parameter로 필터와 페이징 조건을 받을 수 있다.
    
- 오류 발생 시 일관된 JSON 오류 응답을 반환한다.
    

---

# EIF-003 Kakao OAuth Interface

## 설명

소로소로 MVP는 카카오 로그인만 제공한다.

사용자는 카카오 계정을 통해 인증하고, 서버는 카카오 사용자 정보를 기반으로 User를 생성하거나 조회한다.

---

## 연동 흐름

```text
사용자
→ 클라이언트에서 카카오 로그인 요청
→ 카카오 인증
→ 클라이언트가 Authorization Code 획득
→ 서버에 Authorization Code 전달
→ 서버가 카카오 Token 요청
→ 서버가 카카오 사용자 정보 조회
→ User 생성 또는 기존 User 조회
→ 서비스 Access Token / Refresh Token 발급
```

---

## 요구사항

- 클라이언트는 카카오 인증 후 Authorization Code를 서버에 전달해야 한다.
    
- 서버는 Authorization Code를 사용해 카카오 Access Token을 요청해야 한다.
    
- 서버는 카카오 Access Token을 사용해 카카오 사용자 정보를 조회해야 한다.
    
- 서버는 카카오 사용자 식별자를 기준으로 User를 생성하거나 조회해야 한다.
    
- 카카오 인증 실패 시 서비스 토큰을 발급하지 않아야 한다.
    
- 카카오 사용자 정보 조회 실패 시 로그인은 실패해야 한다.
    
- 탈퇴 상태의 User는 카카오 로그인을 시도해도 서비스 토큰을 발급받을 수 없다.
    

---

## Acceptance Criteria

- 사용자는 카카오 계정으로 로그인할 수 있다.
    
- 최초 카카오 로그인 시 User가 생성된다.
    
- 기존 카카오 계정 로그인 시 기존 User를 조회한다.
    
- 카카오 인증 실패 시 User와 Token이 생성되지 않는다.
    
- 탈퇴 상태의 User는 로그인할 수 없다.
    

---

# EIF-004 JWT Authentication Interface

## 설명

소로소로의 보호된 API는 JWT Access Token을 통해 인증한다.

Access Token이 만료된 경우 Refresh Token으로 새로운 Access Token을 발급받을 수 있다.

---

## 요청 Header

인증이 필요한 API는 다음 Header를 사용한다.

```http
Authorization: Bearer {accessToken}
```

---

## 요구사항

- Access Token은 보호된 API 요청 시 Authorization Header에 포함되어야 한다.
    
- Access Token이 없으면 보호된 API에 접근할 수 없어야 한다.
    
- Access Token이 만료되었거나 유효하지 않으면 요청은 실패해야 한다.
    
- Refresh Token은 Access Token 재발급에 사용한다.
    
- Refresh Token은 서버 저장소에서 관리되어야 한다.
    
- 로그아웃 시 현재 기기의 Refresh Token은 무효화되어야 한다.
    
- 회원 탈퇴 시 해당 User의 모든 Refresh Token은 무효화되어야 한다.
    

---

## Acceptance Criteria

- 유효한 Access Token으로 보호된 API를 호출할 수 있다.
    
- Access Token이 없으면 보호된 API 호출이 실패한다.
    
- 유효하지 않은 Access Token으로 보호된 API 호출이 실패한다.
    
- 유효한 Refresh Token으로 Access Token을 재발급받을 수 있다.
    
- 로그아웃 후 해당 Refresh Token으로 Access Token을 재발급받을 수 없다.
    

---

# EIF-005 S3 Upload Interface

## 설명

이미지 업로드는 Presigned URL을 이용해 클라이언트가 S3에 직접 원본 이미지를 업로드하는 방식을 사용한다.

백엔드 서버는 이미지 파일을 직접 전송받아 저장하지 않는다.

---

## 업로드 흐름

```text
Client
→ Server: Presigned URL 요청
→ Server: Photo row 생성, status = UPLOADING
→ Server: Presigned URL 반환
→ Client
→ S3: 원본 이미지 직접 업로드
→ Client
→ Server: 업로드 완료 처리 요청
→ Server: Photo status = PROCESSING
→ Server: 이미지 처리 작업 등록
```

---

## 요구사항

- 클라이언트는 파일명, Content-Type, 파일 크기, owner 정보를 서버에 전달해야 한다.
    
- 서버는 owner 존재 여부와 소유권을 검증해야 한다.
    
- 서버는 owner별 사진 수 제한을 검증해야 한다.
    
- 서버는 Photo를 `UPLOADING` 상태로 생성해야 한다.
    
- 서버는 원본 이미지 업로드용 Presigned URL을 반환해야 한다.
    
- 클라이언트는 Presigned URL을 사용해 S3에 원본 이미지를 업로드해야 한다.
    
- 클라이언트는 업로드 완료 후 서버에 완료 처리를 요청해야 한다.
    
- 업로드 완료 처리 전 Photo는 정상 이미지로 표시되지 않아야 한다.
    

---

## Acceptance Criteria

- 클라이언트는 Presigned URL을 발급받을 수 있다.
    
- Presigned URL 발급 시 Photo가 `UPLOADING` 상태로 생성된다.
    
- 클라이언트는 Presigned URL로 S3에 원본 이미지를 업로드할 수 있다.
    
- 업로드 완료 처리 후 Photo 상태는 `PROCESSING`이 된다.
    
- owner별 사진 수 제한을 초과하면 Presigned URL이 발급되지 않는다.
    

---

# EIF-006 CloudFront Image Delivery Interface

## 설명

이미지 조회는 CloudFront를 통해 제공하는 것을 기준으로 한다.

Photo는 원본 이미지, Medium 이미지, Thumbnail 이미지를 가질 수 있으며, 화면 용도에 따라 적절한 이미지를 사용한다.

---

## 이미지 사용 기준

|이미지|사용 위치|
|---|---|
|Thumbnail|목록, 카드, 캘린더, 작은 미리보기|
|Medium|상세 화면|
|Original|원본 확인 또는 확대 보기|

---

## 요구사항

- Photo가 `READY` 상태이면 조회 가능한 이미지 URL을 제공해야 한다.
    
- 목록 화면에서는 Thumbnail 이미지를 사용해야 한다.
    
- 상세 화면에서는 Medium 이미지를 사용할 수 있어야 한다.
    
- Original 이미지는 필요한 경우에만 사용해야 한다.
    
- `PROCESSING` 상태의 Photo는 이미지 처리 중으로 표시할 수 있어야 한다.
    
- `FAILED` 상태의 Photo는 실패 상태로 표시하거나 기본 이미지로 대체할 수 있어야 한다.
    

---

## Acceptance Criteria

- READY 상태의 Photo는 Thumbnail URL을 제공할 수 있다.
    
- READY 상태의 Photo는 Medium URL을 제공할 수 있다.
    
- 목록 화면에서 원본 이미지를 직접 사용하지 않는다.
    
- PROCESSING 상태의 Photo는 처리 중임을 구분할 수 있다.
    
- FAILED 상태의 Photo는 실패 상태임을 구분할 수 있다.
    

---

# EIF-007 Queue and Worker Interface

## 설명

이미지 리사이징은 비동기 Queue와 Worker를 통해 처리한다.

업로드 완료 처리 후 서버는 이미지 변환 작업을 Queue에 등록하고, Worker는 해당 작업을 처리하여 Medium과 Thumbnail 이미지를 생성한다.

---

## 처리 흐름

```text
Server
→ Queue: photoId 발행
→ Worker
→ DB: Photo 조회
→ S3: Original 이미지 다운로드
→ Worker: Medium, Thumbnail 생성
→ S3: 변환 이미지 업로드
→ DB: mediumKey, thumbnailKey, status = READY, processedAt 저장
```

---

## 요구사항

- Queue 메시지는 Photo를 식별할 수 있는 정보를 포함해야 한다.
    
- MVP에서는 Queue 메시지에 `photoId`를 포함하는 것을 기준으로 한다.
    
- Worker는 photoId로 Photo를 조회해야 한다.
    
- Worker는 Photo 상태가 처리 가능한 상태인지 확인해야 한다.
    
- Worker는 S3에서 Original 이미지를 다운로드해야 한다.
    
- Worker는 Medium과 Thumbnail 이미지를 생성해야 한다.
    
- Worker는 생성된 이미지를 S3에 업로드해야 한다.
    
- Worker는 처리 성공 시 Photo 상태를 `READY`로 변경해야 한다.
    
- Worker는 처리 실패 시 Photo 상태를 `FAILED`로 변경해야 한다.
    
- Worker 처리 실패는 운영자가 확인할 수 있어야 한다.
    

---

## Acceptance Criteria

- 업로드 완료 처리 후 이미지 변환 작업이 등록된다.
    
- Worker는 Photo를 기준으로 이미지 변환을 수행할 수 있다.
    
- 처리 성공 시 Photo 상태는 `READY`가 된다.
    
- 처리 실패 시 Photo 상태는 `FAILED`가 된다.
    
- READY 상태의 Photo는 Medium과 Thumbnail Key를 가진다.
    

---

# EIF-008 Deployment Interface

## 설명

소로소로 MVP는 Docker, Docker Compose, EC2, Nginx 기반으로 배포 가능한 구조를 가진다.

---

## 실행 환경

|항목|기준|
|---|---|
|Application|Spring Boot|
|Runtime|Java 17|
|Database|PostgreSQL|
|Container|Docker|
|Local/Server Composition|Docker Compose|
|Server|EC2|
|Reverse Proxy|Nginx|
|CI/CD|GitHub Actions|

---

## 요구사항

- 애플리케이션은 Docker 이미지로 빌드 가능해야 한다.
    
- Docker Compose를 통해 애플리케이션과 의존 서비스를 실행할 수 있어야 한다.
    
- EC2 서버에서 Docker 기반으로 애플리케이션을 실행할 수 있어야 한다.
    
- Nginx는 외부 요청을 애플리케이션으로 전달해야 한다.
    
- GitHub Actions를 통해 빌드 및 배포 자동화를 구성할 수 있어야 한다.
    
- 환경 변수는 코드에 하드코딩하지 않고 외부 설정으로 주입해야 한다.
    
- 운영 환경의 민감 정보는 외부에 노출되지 않아야 한다.
    

---

## Acceptance Criteria

- Dockerfile을 통해 애플리케이션 이미지를 빌드할 수 있다.
    
- Docker Compose로 실행 환경을 구성할 수 있다.
    
- EC2에서 애플리케이션을 실행할 수 있다.
    
- Nginx를 통해 외부 요청을 애플리케이션으로 전달할 수 있다.
    
- GitHub Actions 기반 배포 자동화를 구성할 수 있다.
    

---

# EIF-009 Error Response Interface

## 설명

클라이언트는 서버 오류를 일관된 형식으로 받아 처리할 수 있어야 한다.

오류 응답은 사용자가 이해할 수 있는 메시지와 개발자가 원인을 추적할 수 있는 코드를 포함해야 한다.

---

## 오류 응답 기본 형식

```json
{
  "code": "PRJ-001",
  "message": "프로젝트명을 입력해주세요.",
  "details": null
}
```

---

## 요구사항

- 오류 응답은 일관된 JSON 형식을 가져야 한다.
    
- 오류 응답에는 오류 코드가 포함되어야 한다.
    
- 오류 응답에는 사용자에게 표시 가능한 메시지가 포함되어야 한다.
    
- 입력값 검증 오류는 어떤 필드에서 문제가 발생했는지 확인할 수 있어야 한다.
    
- 인증 오류와 권한 오류는 구분되어야 한다.
    
- 외부 서비스 오류는 내부 서버 오류와 구분되어야 한다.
    
- 서버 내부 오류는 민감한 시스템 정보를 노출하지 않아야 한다.
    

---

## 오류 유형

|유형|설명|
|---|---|
|Validation Error|입력값 검증 실패|
|Authentication Error|인증 실패|
|Authorization Error|권한 없음|
|Not Found Error|리소스 없음|
|Conflict Error|중복 또는 상태 충돌|
|External Service Error|카카오, S3 등 외부 서비스 오류|
|Internal Server Error|서버 내부 오류|

---

## Acceptance Criteria

- 클라이언트는 code와 message를 기준으로 오류를 처리할 수 있다.
    
- 인증 실패와 권한 실패는 서로 다른 오류로 구분된다.
    
- 입력값 오류는 사용자에게 수정 가능한 메시지를 제공한다.
    
- 서버 내부 오류는 민감 정보를 포함하지 않는다.
    
- 외부 서비스 오류는 별도 오류 유형으로 구분된다.
    

---

# 8장 요약

External Interface Requirements는 소로소로가 외부 환경과 상호작용하는 방식을 정의한다.

본 장에서 확정한 핵심 기준은 다음과 같다.

- 소로소로는 모바일 웹 중심 사용자 인터페이스를 제공한다.
    
- 클라이언트와 서버는 JSON 기반 REST API로 통신한다.
    
- API 기본 경로는 `/api/v1`을 사용한다.
    
- 카카오 OAuth를 통해 로그인한다.
    
- 보호된 API는 JWT Access Token을 사용한다.
    
- 이미지는 Presigned URL을 통해 S3에 직접 업로드한다.
    
- 이미지 조회는 CloudFront를 기준으로 한다.
    
- 이미지 리사이징은 Queue와 Worker를 통해 비동기로 처리한다.
    
- 배포 환경은 Docker, Docker Compose, EC2, Nginx를 기준으로 한다.
    
- 오류 응답은 일관된 JSON 형식으로 제공한다.

---
# 9. Future Scope

## 9.1 개요

Future Scope는 소로소로(SOROSORO) MVP 이후 확장할 수 있는 기능 범위를 정의한다.

본 장에 포함된 기능은 현재 MVP 구현 범위에 포함하지 않는다.  
따라서 본 SRS의 기능 요구사항, API 요구사항, 데이터 요구사항에서 필수 구현 대상으로 보지 않는다.

Future Scope의 목적은 다음과 같다.

- MVP 이후 확장 방향을 명확히 한다.
    
- 현재 설계가 향후 기능 확장을 방해하지 않도록 한다.
    
- MVP 구현 범위가 과도하게 커지는 것을 방지한다.
    
- 포트폴리오 관점에서 서비스 확장 가능성을 설명할 수 있도록 한다.
    

---

## 9.2 Future Scope 목록

|ID|기능|우선순위|설명|
|---|---|---|---|
|FS-001|AI 기반 원단 정보 자동 기록|High|주문 내역 캡처 또는 상품 페이지에서 원단 정보를 자동 추출한다.|
|FS-002|원단 검색 고도화|Medium|소재, 색상, 구매처, 품번, 재구매 의사 기반 검색을 강화한다.|
|FS-003|원단 재고 관리|Medium|남은 원단 길이, 사용량, 보관 위치 등을 관리한다.|
|FS-004|통계 대시보드|Medium|작업 시간, 작업 유형, 프로젝트 완료율 등을 시각화한다.|
|FS-005|알림 기능|Low|임시저장 일지, 장기 미완료 프로젝트 등을 알려준다.|
|FS-006|공개 프로젝트|Medium|사용자가 선택한 Project를 외부에 공개할 수 있게 한다.|
|FS-007|커뮤니티 기능|Low|공개 프로젝트 기반으로 좋아요, 댓글, 팔로우를 제공한다.|
|FS-008|패턴/부자재 관리|Low|패턴, 지퍼, 단추, 실 등 부자재 정보를 별도로 관리한다.|
|FS-009|데이터 내보내기|Low|Project, DailyLog, Fabric 데이터를 PDF 또는 CSV로 내보낸다.|
|FS-010|멀티 OAuth 로그인|Low|카카오 외 구글, 애플 등의 소셜 로그인을 지원한다.|

---

# FS-001 AI 기반 원단 정보 자동 기록

## 설명

AI 기반 원단 정보 자동 기록 기능은 사용자가 직접 원단 정보를 입력하는 부담을 줄이기 위한 확장 기능이다.

사용자는 주문 내역 캡처 이미지 또는 상품 페이지 URL을 입력하고, 시스템은 이미지 인식, OCR, LLM 또는 DOM 추출을 통해 원단 정보를 자동으로 추출한다.

---

## 예상 흐름

```text
주문 내역 캡처 또는 상품 URL 입력
→ FabricExtractionJob 생성
→ OCR / Vision LLM / DOM Extraction 수행
→ 원단 후보 정보 추출
→ 사용자 검수 및 수정
→ Fabric 생성
```

---

## 예상 추출 정보

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

---

## 설계 방향

AI 추출 결과는 바로 Fabric으로 저장하지 않는다.

LLM 또는 OCR 결과는 틀릴 수 있으므로, 사용자가 확인하고 수정한 뒤 Fabric으로 저장하는 구조가 필요하다.

이를 위해 다음과 같은 별도 도메인을 고려할 수 있다.

|Entity|설명|
|---|---|
|FabricExtractionJob|원단 정보 추출 작업 단위|
|ExtractedFabricItem|추출된 원단 후보 정보|

---

## MVP 제외 이유

- AI 추출 정확도 검증이 필요하다.
    
- OCR, Vision LLM, DOM Parsing 등 기술 선택지가 많다.
    
- 쇼핑몰마다 페이지 구조와 이미지 구성이 다르다.
    
- 사용자가 검수하는 UI가 별도로 필요하다.
    
- MVP 핵심은 원단 자동화보다 기록 구조 검증에 있다.
    

---

## 현재 설계와의 연결

현재 Fabric 도메인은 향후 AI 자동 기록 기능과 연결될 수 있도록 다음 정보를 정규 필드로 가진다.

- productName
    
- productCode
    
- productUrl
    
- storeName
    
- purchasePrice
    
- color
    
- size
    
- width
    
- materialComposition
    

따라서 AI 추출 기능은 Fabric 생성 전 단계로 자연스럽게 확장할 수 있다.

---

# FS-002 원단 검색 고도화

## 설명

MVP에서는 keyword, 구매처, 색상, 재구매 의사 정도의 기본 검색과 필터만 제공한다.

향후에는 사용자가 등록한 원단이 많아질 것을 고려하여 검색과 필터를 고도화할 수 있다.

---

## 확장 기능

- 소재별 필터
    
- 색상별 필터
    
- 구매처별 필터
    
- 구매일 범위 필터
    
- 가격 범위 필터
    
- 재구매 의사 필터
    
- 사용 여부 필터
    
- 사용된 Project 기준 필터
    
- 품번 검색
    
- 상품 URL 검색
    
- 원단폭 기준 필터
    
- 메모 전문 검색
    

---

## 설계 고려사항

- materialComposition은 자유 텍스트이므로 검색 정확도에 한계가 있을 수 있다.
    
- 색상과 소재를 정규화할지 여부를 별도 판단해야 한다.
    
- 검색 고도화를 위해 PostgreSQL Full Text Search 또는 별도 검색 엔진 도입을 검토할 수 있다.
    

---

# FS-003 원단 재고 관리

## 설명

MVP의 Fabric은 구매한 원단 정보를 기록하는 수준이다.

향후에는 원단을 얼마나 사용했고, 얼마나 남았는지 관리하는 재고 기능으로 확장할 수 있다.

---

## 확장 기능

- 구매 길이 입력
    
- 사용 길이 입력
    
- 남은 길이 자동 계산
    
- Project별 사용량 기록
    
- 원단 보관 위치 기록
    
- 사용 완료 여부 표시
    
- 원단 재고 부족 표시
    
- 자투리 원단 관리
    

---

## 예상 추가 데이터

|Entity|설명|
|---|---|
|FabricInventory|원단 재고 정보|
|FabricUsage|Project별 원단 사용량|
|FabricStorageLocation|원단 보관 위치|

---

## MVP 제외 이유

- 사용량 단위가 원단마다 다를 수 있다.
    
- 사용량을 정확히 입력하는 UX가 복잡해질 수 있다.
    
- MVP에서는 원단 구매 이력과 Project 연결만으로도 핵심 문제를 해결할 수 있다.
    

---

# FS-004 통계 대시보드

## 설명

MVP에서는 Calendar와 Contribution을 통해 날짜별 작업 기록을 시각화한다.

향후에는 사용자의 재봉 습관과 작업 패턴을 더 자세히 보여주는 통계 대시보드로 확장할 수 있다.

---

## 확장 기능

- 월별 총 작업 시간
    
- 주별 평균 작업 시간
    
- Project별 작업 시간
    
- WorkType별 작업 비율
    
- 완료 Project 수
    
- 진행 중 Project 수
    
- 평균 Project 완료 기간
    
- 가장 많이 사용한 원단 구매처
    
- 재구매 의사가 높은 원단 통계
    
- 장기 미완료 Project 목록
    

---

## 데이터 기준

통계 대시보드는 다음 데이터를 기반으로 한다.

- PUBLISHED DailyLog
    
- DailyLogTimeEntry
    
- ProjectStatus
    
- WorkType
    
- ProjectFabric
    
- Fabric
    

---

## MVP 제외 이유

- MVP 단계에서는 기록 생성 경험이 우선이다.
    
- 충분한 기록 데이터가 쌓이기 전에는 통계 가치가 낮다.
    
- 통계 기준을 너무 일찍 고정하면 데이터 모델이 불필요하게 복잡해질 수 있다.
    

---

# FS-005 알림 기능

## 설명

알림 기능은 사용자가 기록을 이어가거나 미완성 Project를 관리할 수 있도록 돕는 확장 기능이다.

---

## 확장 기능

- DRAFT DailyLog 작성 재개 알림
    
- 오래 업데이트되지 않은 Project 알림
    
- 장기 ON_HOLD Project 알림
    
- 목표 작업 시간 알림
    
- 원단 재구매 메모 알림
    
- 프로젝트 회고 작성 알림
    

---

## 예상 알림 채널

- 서비스 내부 알림
    
- 이메일 알림
    
- 푸시 알림
    

---

## MVP 제외 이유

- 알림 채널별 구현 부담이 있다.
    
- 사용자 설정 UI가 필요하다.
    
- 개인 기록 서비스의 MVP에서는 알림 없이도 핵심 기능을 사용할 수 있다.
    

---

# FS-006 공개 프로젝트

## 설명

공개 프로젝트 기능은 사용자가 선택한 Project를 다른 사람에게 보여줄 수 있도록 하는 기능이다.

MVP에서는 모든 Project가 개인 데이터로 관리되며, 외부 공개 기능을 제공하지 않는다.

향후 공개 기능을 도입할 경우, 개인 기록과 공개 콘텐츠를 구분해야 한다.

---

## 확장 기능

- Project 공개 여부 설정
    
- 공개용 Project 상세 페이지
    
- 공개용 대표 이미지
    
- 공개용 DailyLog 선택 노출
    
- 원단 정보 일부 공개 여부 설정
    
- 공유 링크 생성
    
- 검색 엔진 노출 여부 설정
    

---

## 설계 고려사항

공개 기능은 단순히 기존 Project를 그대로 공개하는 방식보다, 공개용 모델을 별도로 두는 것이 안전할 수 있다.

예상 구조는 다음과 같다.

|Entity|설명|
|---|---|
|PublicProject|공개용 Project 정보|
|PublicDailyLog|공개 가능한 DailyLog 요약|
|PublicPhoto|공개 가능한 Photo|

---

## MVP 제외 이유

- 현재 서비스의 핵심은 개인 기록이다.
    
- 공개 기능은 권한 정책을 복잡하게 만든다.
    
- 개인 메모, 구매 정보, 가격 정보가 노출될 위험이 있다.
    
- 공개 범위 설정 UI가 필요하다.
    

---

# FS-007 커뮤니티 기능

## 설명

커뮤니티 기능은 공개 프로젝트를 기반으로 사용자 간 상호작용을 제공하는 기능이다.

---

## 확장 기능

- 공개 Project 탐색
    
- 좋아요
    
- 댓글
    
- 팔로우
    
- 저장
    
- 태그
    
- 인기 Project
    
- 사용자 프로필
    
- 신고 기능
    

---

## 설계 고려사항

커뮤니티 기능은 개인 기록 서비스와 성격이 다르므로 별도 도메인으로 분리하는 것이 좋다.

예상 도메인은 다음과 같다.

- PublicProject
    
- Comment
    
- Like
    
- Follow
    
- Report
    
- Notification
    

---

## MVP 제외 이유

- MVP의 목표는 개인 기록 구조 검증이다.
    
- 커뮤니티는 사용자 간 권한, 신고, 공개 범위, 운영 정책이 필요하다.
    
- 초기 구현 범위가 과도하게 커진다.
    

---

# FS-008 패턴/부자재 관리

## 설명

MVP에서는 패턴명, 참고 URL, 재봉틀 세팅, 바늘, 실 등은 ProjectSpecification의 메모성 정보로 관리한다.

향후에는 패턴, 부자재, 도구 정보를 독립적으로 관리할 수 있다.

---

## 확장 기능

- 패턴 아카이브
    
- 부자재 등록
    
- 지퍼, 단추, 고무줄, 실 관리
    
- 부자재 구매처 기록
    
- Project별 부자재 연결
    
- 자주 쓰는 재봉틀 세팅 저장
    
- 바늘/실 조합 기록
    

---

## 예상 추가 데이터

|Entity|설명|
|---|---|
|Pattern|패턴 정보|
|Notion|부자재 정보|
|ProjectNotion|Project와 부자재 연결|
|SewingMachineSetting|재봉틀 세팅 프리셋|
|Tool|재봉 도구 정보|

---

## MVP 제외 이유

- 부자재 종류와 속성이 다양하다.
    
- MVP에서는 ProjectSpecification 메모 필드로 충분히 기록 가능하다.
    
- 독립 도메인으로 분리하면 입력 부담이 커질 수 있다.
    

---

# FS-009 데이터 내보내기

## 설명

데이터 내보내기 기능은 사용자가 자신의 기록을 외부 파일로 저장하거나 포트폴리오, 블로그, 개인 백업용으로 활용할 수 있도록 한다.

---

## 확장 기능

- Project PDF 내보내기
    
- DailyLog PDF 내보내기
    
- Fabric CSV 내보내기
    
- 작업 시간 통계 CSV 내보내기
    
- 이미지 포함 ZIP 다운로드
    
- 공개용 제작기 생성
    

---

## MVP 제외 이유

- 파일 생성 로직이 별도로 필요하다.
    
- 이미지 포함 내보내기는 처리 시간이 길어질 수 있다.
    
- MVP에서는 서비스 내부 조회 경험이 우선이다.
    

---

# FS-010 멀티 OAuth 로그인

## 설명

MVP에서는 카카오 로그인만 제공한다.

향후 사용자의 접근성을 높이기 위해 다른 소셜 로그인 Provider를 추가할 수 있다.

---

## 확장 Provider

- Google
    
- Apple
    
- Naver
    

---

## 설계 고려사항

멀티 OAuth를 도입하려면 User와 OAuth Provider 정보를 분리하는 구조가 필요할 수 있다.

예상 추가 데이터는 다음과 같다.

|Entity|설명|
|---|---|
|UserOAuthAccount|User와 OAuth Provider 계정의 연결 정보|

---

## MVP 제외 이유

- MVP에서는 카카오 로그인만으로도 사용자 인증 흐름 검증이 가능하다.
    
- 여러 OAuth Provider를 지원하면 계정 병합, 중복 가입, 탈퇴 정책이 복잡해진다.
    
- 인증 도메인의 구현 범위가 커진다.
    

---

# 9장 요약

Future Scope는 MVP 이후 확장 가능한 기능을 정의한다.

본 장에서 확정한 핵심 기준은 다음과 같다.

- AI 기반 원단 정보 자동 기록은 MVP 이후 1순위 확장 기능이다.
    
- AI 추출 결과는 바로 Fabric으로 저장하지 않고, 사용자 검수 후 저장해야 한다.
    
- Fabric 도메인은 AI 추출 결과를 수용할 수 있도록 주요 원단 정보를 정규 필드로 가진다.
    
- 원단 검색, 재고 관리, 통계 대시보드는 기록 데이터가 쌓인 이후 확장한다.
    
- 공개 프로젝트와 커뮤니티 기능은 개인 기록 서비스와 권한 정책이 달라 별도 도메인으로 분리하는 것이 적절하다.
    
- 패턴과 부자재 관리는 MVP에서는 ProjectSpecification 메모로 충분히 처리한다.
    
- 데이터 내보내기와 멀티 OAuth는 MVP 이후 편의 기능으로 검토한다.
    
- Future Scope에 포함된 기능은 현재 MVP 구현 범위에 포함하지 않는다.