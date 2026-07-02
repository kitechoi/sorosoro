
# 05. Backend Design — 소로소로(SOROSORO)

> 소로소로 백엔드 애플리케이션의 구조, 계층 책임, 도메인별 구현 방식을 정의하는 문서

---

# 1. 문서 개요

## 1.1 문서 목적

이 문서는 소로소로(SOROSORO)의 백엔드 설계 기준을 정의한다.

소로소로는 Java 17과 Spring Boot 기반으로 구현하는 개인 재봉 기록 서비스이다.  
본 문서는 PRD, SRS, ADR, ERD에서 정의한 요구사항을 실제 백엔드 코드 구조로 옮기기 위한 기준을 제공한다.

본 문서에서는 다음 내용을 정의한다.

- 백엔드 계층 구조
    
- 패키지 구조
    
- 도메인별 책임
    
- Service 계층의 트랜잭션 경계
    
- Repository 사용 기준
    
- DTO 설계 기준
    
- 인증과 인가 흐름
    
- 도메인 검증 규칙
    
- 이미지 업로드와 비동기 처리 구조
    
- Calendar/Contribution 조회 방식
    
- 예외 처리와 응답 형식
    
- 테스트 전략
    

---

## 1.2 문서 범위

본 문서는 MVP 백엔드 구현 범위를 기준으로 한다.

포함 범위는 다음과 같다.

- Authentication
    
- User
    
- Project
    
- ProjectSpecification
    
- ProjectReference
    
- DailyLog
    
- DailyLogTimeEntry
    
- Fabric
    
- ProjectFabric
    
- Photo
    
- Calendar
    
- Image Processing Worker
    
- Common Exception
    
- Security
    
- QueryDSL 기반 조회
    

제외 범위는 다음과 같다.

- AI 기반 원단 자동 기록
    
- Community
    
- Comment
    
- Like
    
- Follow
    
- Notification
    
- Statistics Dashboard
    
- Admin 기능
    
- Multi-user 공유 기능
    

---

# 2. 기술 스택

|영역|기술|
|---|---|
|Language|Java 17|
|Framework|Spring Boot|
|Security|Spring Security|
|Authentication|JWT, Refresh Token|
|ORM|Spring Data JPA|
|Query|QueryDSL|
|Database|PostgreSQL|
|Migration|Flyway|
|Storage|AWS S3|
|CDN|CloudFront|
|Async Processing|Queue + Worker|
|API Documentation|Springdoc OpenAPI|
|Test|JUnit 5|
|Build|Gradle|
|Deployment|Docker, Docker Compose, EC2, Nginx|

---

# 3. 전체 아키텍처

## 3.1 요청 처리 흐름

```text
Client
→ Controller
→ Service
→ Repository
→ Database
```

이미지 업로드와 리사이징은 다음 흐름을 따른다.

```text
Client
→ Controller
→ PhotoService
→ S3 Presigned URL 발급
→ Client가 S3에 직접 업로드
→ complete-upload API 호출
→ Queue에 photoId 발행
→ Worker가 이미지 리사이징
→ Photo 상태 변경
```

---

## 3.2 계층 구조

소로소로 백엔드는 다음 계층으로 구성한다.

```text
presentation
application
domain
infrastructure
common
```

|계층|책임|
|---|---|
|presentation|HTTP 요청/응답, DTO 변환, 인증 사용자 전달|
|application|유스케이스 실행, 트랜잭션 관리, 도메인 검증|
|domain|Entity, Enum, 핵심 도메인 규칙|
|infrastructure|외부 시스템 연동, S3, Queue, OAuth, QueryDSL 구현|
|common|공통 예외, 응답, 보안 유틸, 시간 처리|

---

## 3.3 계층별 의존 방향

의존 방향은 다음을 따른다.

```text
presentation
→ application
→ domain
→ infrastructure
```

다만 실제 Spring Boot 구현에서는 Repository 인터페이스와 구현체가 같은 모듈 안에 있을 수 있다.  
MVP에서는 과도한 추상화를 피하고, 다음 기준을 따른다.

- Controller는 Service를 호출한다.
    
- Service는 Repository를 호출한다.
    
- Service는 도메인 검증과 트랜잭션을 담당한다.
    
- Entity는 가능한 한 도메인 상태 변경 메서드를 가진다.
    
- 외부 연동은 별도 Component로 분리한다.
    

---

# 4. 패키지 구조

## 4.1 기본 패키지 구조

```text
com.sorosoro
 ├── auth
 │    ├── presentation
 │    ├── application
 │    ├── domain
 │    ├── infrastructure
 │    └── dto
 │
 ├── user
 │    ├── domain
 │    ├── repository
 │    └── application
 │
 ├── project
 │    ├── presentation
 │    ├── application
 │    ├── domain
 │    ├── repository
 │    └── dto
 │
 ├── dailylog
 │    ├── presentation
 │    ├── application
 │    ├── domain
 │    ├── repository
 │    └── dto
 │
 ├── fabric
 │    ├── presentation
 │    ├── application
 │    ├── domain
 │    ├── repository
 │    └── dto
 │
 ├── photo
 │    ├── presentation
 │    ├── application
 │    ├── domain
 │    ├── repository
 │    ├── infrastructure
 │    └── dto
 │
 ├── calendar
 │    ├── presentation
 │    ├── application
 │    ├── repository
 │    └── dto
 │
 ├── worker
 │    └── photo
 │
 └── common
      ├── config
      ├── exception
      ├── response
      ├── security
      ├── time
      └── util
```

---

## 4.2 패키지 설계 기준

도메인별 패키지를 우선한다.

예를 들어 Project 관련 Controller, Service, Entity, Repository, DTO는 `project` 패키지 안에 둔다.

이유는 다음과 같다.

- 도메인별 응집도를 높일 수 있다.
    
- 기능 수정 시 관련 코드를 찾기 쉽다.
    
- Project, DailyLog, Fabric, Photo의 책임을 분리할 수 있다.
    
- 포트폴리오에서 도메인 중심 구조를 설명하기 좋다.
    

---

# 5. 공통 설계 원칙

## 5.1 Controller 원칙

Controller는 HTTP 계층의 책임만 가진다.

Controller의 책임은 다음과 같다.

- Request Body 검증
    
- PathVariable, RequestParam 수신
    
- 인증 사용자 정보 수신
    
- Service 호출
    
- Response DTO 반환
    

Controller에서 하지 않는 일은 다음과 같다.

- 비즈니스 규칙 검증
    
- Entity 직접 수정
    
- Repository 직접 호출
    
- 트랜잭션 처리
    
- 외부 API 직접 호출
    

---

## 5.2 Service 원칙

Service는 유스케이스 단위의 비즈니스 로직을 담당한다.

Service의 책임은 다음과 같다.

- 트랜잭션 경계 설정
    
- Entity 조회
    
- 소유권 검증
    
- 상태 검증
    
- 도메인 규칙 검증
    
- Entity 상태 변경
    
- Repository 저장
    
- 외부 Component 호출
    

Service 메서드는 가능한 한 하나의 사용자 행동을 표현한다.

예시는 다음과 같다.

```text
createProject
updateProject
archiveProject
createDailyLogDraft
publishDailyLog
connectFabricToProject
issuePresignedUrl
completePhotoUpload
```

---

## 5.3 Repository 원칙

Repository는 데이터 접근을 담당한다.

단순 CRUD는 Spring Data JPA Repository를 사용한다.

조건 조합이나 집계 조회는 QueryDSL을 사용한다.

|조회 유형|사용 방식|
|---|---|
|단순 ID 조회|Spring Data JPA|
|소유권 포함 단건 조회|Spring Data JPA 또는 QueryDSL|
|목록 조회|QueryDSL|
|Calendar 집계|QueryDSL|
|Contribution 집계|QueryDSL|
|Fabric 검색|QueryDSL|
|Project별 DailyLog 조회|QueryDSL|

---

## 5.4 Entity 원칙

Entity는 단순 데이터 컨테이너가 아니라, 자기 상태를 변경하는 메서드를 가진다.

예시는 다음과 같다.

```java
project.updateBasicInfo(...);
project.changeStatus(...);
dailyLog.publish(...);
dailyLog.updateContent(...);
dailyLog.replaceTimeEntries(...);
photo.markProcessing();
photo.markReady(...);
photo.markFailed();
```

단, MVP에서는 지나치게 복잡한 도메인 모델링보다 명확성과 구현 가능성을 우선한다.

---

## 5.5 DTO 원칙

Request DTO와 Response DTO를 분리한다.

Entity를 API 응답으로 직접 반환하지 않는다.

DTO 설계 기준은 다음과 같다.

- Request DTO는 입력 검증에 집중한다.
    
- Response DTO는 클라이언트 화면에 필요한 값을 제공한다.
    
- Entity 내부 구조를 그대로 노출하지 않는다.
    
- 날짜와 시간 형식은 일관되게 유지한다.
    
- Photo URL은 Photo key를 직접 노출하지 않고, 변환된 URL 또는 접근 가능한 URL로 반환한다.
    

---

# 6. 인증 및 인가 설계

## 6.1 인증 방식

MVP에서는 카카오 로그인과 JWT 기반 인증을 사용한다.

인증 흐름은 다음과 같다.

```text
Client
→ Kakao Login
→ Server: Kakao authorization code 전달
→ Server: Kakao 사용자 정보 조회
→ User 조회 또는 생성
→ Access Token 발급
→ Refresh Token 저장
→ Client에 Token 반환
```

---

## 6.2 Access Token

Access Token은 API 요청 인증에 사용한다.

요청 헤더는 다음 형식을 사용한다.

```text
Authorization: Bearer {accessToken}
```

Access Token에는 최소한 다음 정보를 포함한다.

- userId
    
- role
    
- issuedAt
    
- expiredAt
    

---

## 6.3 Refresh Token

Refresh Token은 Access Token 재발급에 사용한다.

Refresh Token은 DB에 저장한다.

저장 테이블은 다음과 같다.

```text
refresh_tokens
```

Refresh Token 정책은 다음과 같다.

- 로그인 시 Refresh Token 생성
    
- Access Token 재발급 시 Refresh Token 검증
    
- 로그아웃 시 현재 Refresh Token 삭제
    
- 회원 탈퇴 시 User의 Refresh Token 삭제
    
- 만료된 Refresh Token은 정리 가능
    

---

## 6.4 인증 사용자 주입

Controller에서는 현재 인증된 사용자 ID를 사용할 수 있어야 한다.

예상 방식은 다음 중 하나이다.

```java
@AuthenticationPrincipal CustomUserPrincipal principal
```

또는 커스텀 어노테이션을 사용할 수 있다.

```java
@CurrentUser Long userId
```

MVP에서는 구현 단순성을 위해 `@AuthenticationPrincipal`을 우선 사용한다.

---

## 6.5 인가 원칙

모든 주요 도메인 데이터는 User 소유권을 기준으로 접근을 제한한다.

|도메인|소유권 기준|
|---|---|
|Project|project.userId|
|DailyLog|dailyLog.userId|
|Fabric|fabric.userId|
|Photo|photo.userId + owner 소유권|
|ProjectFabric|project.userId와 fabric.userId 모두 확인|

다른 사용자의 데이터에 접근하면 `403 FORBIDDEN`을 반환한다.

---

# 7. 도메인별 설계

---

# 7.1 User/Auth

## 7.1.1 책임

Auth 도메인의 책임은 다음과 같다.

- 카카오 로그인
    
- JWT 발급
    
- Refresh Token 저장
    
- Access Token 재발급
    
- 로그아웃
    
- 현재 사용자 조회
    
- 회원 탈퇴
    

---

## 7.1.2 주요 클래스

```text
auth
 ├── AuthController
 ├── AuthService
 ├── JwtTokenProvider
 ├── KakaoOAuthClient
 ├── RefreshToken
 ├── RefreshTokenRepository
 └── dto
```

---

## 7.1.3 주요 유스케이스

|유스케이스|설명|
|---|---|
|loginWithKakao|카카오 인증 후 서비스 토큰 발급|
|reissueAccessToken|Refresh Token으로 Access Token 재발급|
|logout|Refresh Token 삭제|
|getMe|현재 사용자 정보 조회|
|withdraw|User status를 DELETED로 변경|

---

# 7.2 Project

## 7.2.1 책임

Project 도메인의 책임은 다음과 같다.

- Project 생성
    
- Project 목록 조회
    
- Project 상세 조회
    
- Project 기본 정보 수정
    
- Project 상태 변경
    
- Project 삭제
    
- ProjectSpecification 조회/수정
    
- ProjectReference 등록/수정/삭제
    
- Project와 Fabric 연결 관리
    

---

## 7.2.2 주요 클래스

```text
project
 ├── ProjectController
 ├── ProjectService
 ├── Project
 ├── ProjectSpecification
 ├── ProjectReference
 ├── ProjectRepository
 ├── ProjectSpecificationRepository
 ├── ProjectReferenceRepository
 ├── ProjectQueryRepository
 └── dto
```

---

## 7.2.3 Project 생성

Project는 title만으로 생성할 수 있다.

생성 시 기본 상태는 다음과 같다.

```text
status = IN_PROGRESS
```

Project 생성 시 빈 ProjectSpecification을 함께 생성할 수 있다.

처리 흐름은 다음과 같다.

```text
1. 현재 User 조회
2. title 검증
3. Project 생성
4. ProjectSpecification 생성
5. 저장
6. 응답 반환
```

---

## 7.2.4 Project 수정

Project 수정 시 검증 규칙은 다음과 같다.

- 현재 User 소유 Project인지 확인
    
- Project가 ARCHIVED 상태인지 확인
    
- title이 비어 있지 않은지 확인
    
- completedAt이 startedAt보다 빠르지 않은지 확인
    

`ARCHIVED` 상태의 Project는 기본 정보 수정이 제한된다.

---

## 7.2.5 Project 상태 변경

Project 상태는 다음 값을 가진다.

```text
IN_PROGRESS
ON_HOLD
COMPLETED
ARCHIVED
```

상태 변경 규칙은 Service 계층에서 처리한다.

MVP에서는 복잡한 상태 전이 제한을 두지 않되, 다음 규칙은 적용한다.

- ARCHIVED 상태에서도 상태 변경은 가능하다.
    
- COMPLETED로 변경할 때 completedAt을 입력할 수 있다.
    
- ARCHIVED 상태에서는 조회와 상태 변경을 제외한 수정성 작업을 제한한다.
    

---

# 7.3 ProjectFabric

## 7.3.1 책임

ProjectFabric은 Project와 Fabric의 연결 관계를 관리한다.

책임은 다음과 같다.

- Project에 Fabric 연결
    
- 연결 메모 수정
    
- 연결 삭제
    
- Project별 연결 Fabric 조회
    
- Fabric별 사용 Project 조회
    

---

## 7.3.2 연결 생성 검증

Project에 Fabric을 연결할 때 다음을 검증한다.

```text
1. Project가 존재하는지 확인
2. Project가 현재 User 소유인지 확인
3. Project가 ARCHIVED 상태가 아닌지 확인
4. Fabric이 존재하는지 확인
5. Fabric이 현재 User 소유인지 확인
6. 동일 Project-Fabric 연결이 이미 존재하지 않는지 확인
```

동일 Project에 동일 Fabric은 중복 연결할 수 없다.

DB 제약조건은 다음과 같다.

```text
unique(project_id, fabric_id)
```

---

# 7.4 Fabric

## 7.4.1 책임

Fabric 도메인의 책임은 다음과 같다.

- Fabric 등록
    
- Fabric 목록 조회
    
- Fabric 상세 조회
    
- Fabric 수정
    
- Fabric 삭제
    
- Fabric이 사용된 Project 목록 조회
    

---

## 7.4.2 주요 클래스

```text
fabric
 ├── FabricController
 ├── FabricService
 ├── Fabric
 ├── FabricRepository
 ├── FabricQueryRepository
 └── dto
```

---

## 7.4.3 Fabric 등록

Fabric은 Project 없이도 생성할 수 있다.

필수값은 다음이다.

```text
name
```

기본 재구매 의사는 다음으로 둘 수 있다.

```text
repurchaseIntention = UNKNOWN
```

---

## 7.4.4 Fabric 수정

Fabric 수정 시 검증 규칙은 다음과 같다.

- 현재 User 소유 Fabric인지 확인
    
- name이 비어 있지 않은지 확인
    
- rating이 존재한다면 1~5 범위인지 확인
    
- purchasePrice가 존재한다면 0 이상인지 확인
    

---

## 7.4.5 Fabric 삭제

Fabric 삭제 시 다음을 처리한다.

- Fabric 소유권 확인
    
- 연결된 ProjectFabric 삭제
    
- Fabric Photo 삭제 또는 삭제 예정 처리
    
- Fabric 삭제
    

Fabric 삭제 시 Project는 삭제하지 않는다.

---

# 7.5 DailyLog

## 7.5.1 책임

DailyLog 도메인의 책임은 다음과 같다.

- DailyLog 생성
    
- DailyLog 임시저장
    
- DailyLog 발행
    
- DailyLog 수정
    
- DailyLog 삭제
    
- Project별 DailyLog 목록 조회
    
- 날짜별 DailyLog 목록 조회
    
- TimeEntry 관리
    
- durationMinutes 계산
    

---

## 7.5.2 주요 클래스

```text
dailylog
 ├── DailyLogController
 ├── DailyLogService
 ├── DailyLog
 ├── DailyLogTimeEntry
 ├── DailyLogRepository
 ├── DailyLogTimeEntryRepository
 ├── DailyLogQueryRepository
 └── dto
```

---

## 7.5.3 DailyLog 상태

DailyLog는 다음 상태를 가진다.

```text
DRAFT
PUBLISHED
```

상태별 규칙은 다음과 같다.

|상태|workedDate|TimeEntry|통계 반영|
|---|---|---|---|
|DRAFT|선택|선택|미반영|
|PUBLISHED|필수|1개 이상 필수|반영|

---

## 7.5.4 DailyLog 생성

DailyLog 생성은 두 흐름으로 나눌 수 있다.

```text
DRAFT 생성
PUBLISHED 생성
```

DRAFT 생성은 필수값을 최소화한다.

PUBLISHED 생성은 다음을 검증한다.

- workedDate 필수
    
- TimeEntry 1개 이상
    
- 각 TimeEntry의 startedAt, endedAt 유효
    
- durationMinutes 계산 가능
    

---

## 7.5.5 DailyLog 발행

DRAFT DailyLog를 PUBLISHED로 전환할 수 있다.

발행 시 검증 규칙은 다음과 같다.

```text
1. DailyLog가 현재 User 소유인지 확인
2. Project가 ARCHIVED 상태가 아닌지 확인
3. DailyLog.status가 DRAFT인지 확인
4. workedDate가 존재하는지 확인
5. TimeEntry가 1개 이상 존재하는지 확인
6. durationMinutes 재계산
7. status = PUBLISHED
```

MVP에서는 PUBLISHED → DRAFT 전환을 제공하지 않는다.

---

## 7.5.6 DailyLog 수정

DRAFT와 PUBLISHED 모두 수정 가능하다.

수정 가능 항목은 다음과 같다.

- workedDate
    
- title
    
- workTypes
    
- memo
    
- timeEntries
    

PUBLISHED 수정 시 다음 조건은 유지해야 한다.

- workedDate 필수
    
- TimeEntry 1개 이상 필수
    
- durationMinutes 재계산
    

Project가 ARCHIVED 상태이면 DailyLog 수정은 제한된다.

---

## 7.5.7 TimeEntry 처리 방식

MVP에서는 TimeEntry 개별 수정 API를 제공하지 않는다.

DailyLog 수정 요청에 포함된 TimeEntry 목록을 기준으로 기존 TimeEntry를 전체 교체한다.

처리 흐름은 다음과 같다.

```text
1. 기존 TimeEntry 삭제
2. 요청 TimeEntry 목록 검증
3. 새 TimeEntry 생성
4. 각 durationMinutes 계산
5. DailyLog.durationMinutes 합산
```

---

## 7.5.8 TimeEntry 검증

TimeEntry는 다음 규칙을 따른다.

- startedAt 필수
    
- endedAt 필수
    
- endedAt은 startedAt보다 늦어야 한다.
    
- durationMinutes는 endedAt - startedAt으로 계산한다.
    
- durationMinutes는 0보다 커야 한다.
    
- MVP에서는 자정을 넘는 TimeEntry를 지원하지 않는다.
    

예를 들어 다음 입력은 허용하지 않는다.

```text
23:30 ~ 00:30
```

자정을 넘는 작업은 날짜별 DailyLog를 나누어 기록한다.

---

# 7.6 Photo

## 7.6.1 책임

Photo 도메인의 책임은 다음과 같다.

- Presigned URL 발급
    
- Photo row 생성
    
- 업로드 완료 처리
    
- Photo 목록 조회
    
- 대표 사진 설정
    
- Photo 정렬 순서 수정
    
- Photo 삭제
    
- Photo 상태 관리
    

---

## 7.6.2 주요 클래스

```text
photo
 ├── PhotoController
 ├── PhotoService
 ├── Photo
 ├── PhotoRepository
 ├── PhotoQueryRepository
 ├── S3StorageClient
 ├── PhotoQueueProducer
 ├── CloudFrontUrlResolver
 └── dto
```

---

## 7.6.3 Photo ownerType

Photo는 다음 ownerType 중 하나를 가진다.

```text
PROJECT
DAILY_LOG
FABRIC
```

Photo는 ownerType과 ownerId를 통해 연결 대상을 표현한다.

DB 레벨 FK는 걸지 않고, Service 계층에서 owner 존재 여부와 소유권을 검증한다.

---

## 7.6.4 Presigned URL 발급

Presigned URL 발급 흐름은 다음과 같다.

```text
1. 사용자 인증 확인
2. ownerType 유효성 확인
3. ownerId 존재 확인
4. owner 소유권 확인
5. owner 상태 확인
6. owner별 사진 수 제한 확인
7. 파일 형식과 크기 검증
8. Photo 생성, status = UPLOADING
9. S3 originalKey 생성
10. Presigned URL 발급
11. photoId, uploadUrl 반환
```

---

## 7.6.5 ownerType별 사진 수 제한

|ownerType|최대 사진 수|
|---|--:|
|PROJECT|15장|
|DAILY_LOG|15장|
|FABRIC|2장|

사진 수 제한은 Presigned URL 발급 전에 검증한다.

---

## 7.6.6 Photo 상태 전이

Photo 상태 전이는 다음과 같다.

```text
UPLOADING
→ PROCESSING
→ READY
```

실패 시 다음 상태로 변경된다.

```text
PROCESSING
→ FAILED
```

상태별 의미는 다음과 같다.

|상태|설명|
|---|---|
|UPLOADING|Presigned URL 발급 후 원본 업로드 대기 또는 진행 중|
|PROCESSING|원본 업로드 완료 후 이미지 변환 대기 또는 진행 중|
|READY|이미지 변환 완료|
|FAILED|이미지 처리 실패|

---

## 7.6.7 업로드 완료 처리

클라이언트는 S3 업로드 완료 후 서버에 완료 요청을 보낸다.

```text
POST /api/v1/photos/{photoId}/complete-upload
```

처리 흐름은 다음과 같다.

```text
1. Photo 조회
2. Photo 소유권 확인
3. Photo.status == UPLOADING 검증
4. S3 originalKey object 존재 확인
5. Photo.status = PROCESSING
6. Queue에 photoId 발행
7. 응답 반환
```

---

## 7.6.8 대표 사진 설정

Project와 DailyLog는 대표 사진을 1장 가질 수 있다.

Fabric은 대표 사진 설정 기능을 제공하지 않는다.

검증 규칙은 다음과 같다.

- ownerType이 PROJECT 또는 DAILY_LOG인지 확인
    
- Photo가 현재 User 소유인지 확인
    
- Photo.status가 READY인지 확인
    
- 같은 owner에 속한 Photo인지 확인
    
- 기존 대표 사진 해제
    
- 요청 Photo를 대표 사진으로 설정
    

---

## 7.6.9 Photo 삭제

Photo 삭제 시 다음을 처리한다.

- Photo 소유권 확인
    
- owner 상태 확인
    
- DB row 삭제
    
- S3 object 삭제
    

삭제 대상 S3 object는 다음과 같다.

- originalKey
    
- mediumKey
    
- thumbnailKey
    

MVP에서는 즉시 삭제를 우선 검토한다.

S3 삭제 실패 시 정책은 다음 중 하나로 확장할 수 있다.

- 실패 로그 기록
    
- 삭제 재시도 Queue 발행
    
- 삭제 예정 상태 추가
    
- 주기적 정리 배치
    

---

# 7.7 Photo Worker

## 7.7.1 책임

Photo Worker는 이미지 리사이징을 담당한다.

Worker는 HTTP 요청을 처리하지 않는다.

Worker의 책임은 다음과 같다.

- Queue에서 photoId 수신
    
- Photo 조회
    
- originalKey 기준 원본 이미지 다운로드
    
- Medium 이미지 생성
    
- Thumbnail 이미지 생성
    
- S3 업로드
    
- Photo 상태 변경
    

---

## 7.7.2 처리 흐름

```text
1. Queue에서 photoId 수신
2. Photo 조회
3. Photo.status == PROCESSING 확인
4. S3 originalKey 다운로드
5. 이미지 메타데이터 추출
6. medium.webp 생성
7. thumbnail.webp 생성
8. S3 업로드
9. Photo.mediumKey 저장
10. Photo.thumbnailKey 저장
11. Photo.width, height, sizeBytes 저장
12. Photo.status = READY
13. Photo.processedAt 저장
```

실패 시 다음 처리로 전환한다.

```text
1. 예외 발생
2. 실패 로그 기록
3. Photo.status = FAILED
```

---

## 7.7.3 Worker 실행 모드

MVP에서는 API Server와 Worker를 같은 코드베이스에서 실행하되, 실행 모드를 분리한다.

예상 방식은 다음과 같다.

```text
spring.profiles.active=api
spring.profiles.active=worker
```

또는 환경 변수를 사용할 수 있다.

```text
APP_MODE=api
APP_MODE=worker
```

Docker Compose에서는 app과 worker를 별도 서비스로 실행한다.

```text
app
worker
postgres
queue
```

---

# 7.8 Calendar

## 7.8.1 책임

Calendar 도메인의 책임은 다음과 같다.

- 월별 Calendar 조회
    
- 특정 날짜 작업 요약 조회
    
- 연도별 Contribution 조회
    

Calendar와 Contribution은 별도 테이블에 저장하지 않는다.

PUBLISHED DailyLog를 조회 시점에 집계한다.

---

## 7.8.2 월별 Calendar 조회

조회 조건은 다음과 같다.

```text
userId = currentUser.id
status = PUBLISHED
workedDate between monthStart and monthEnd
```

응답 데이터는 날짜별로 다음 값을 포함한다.

- date
    
- dailyLogCount
    
- totalDurationMinutes
    
- level
    

---

## 7.8.3 특정 날짜 작업 요약 조회

조회 조건은 다음과 같다.

```text
userId = currentUser.id
status = PUBLISHED
workedDate = requestedDate
```

응답 데이터는 다음을 포함한다.

- date
    
- totalDurationMinutes
    
- dailyLogCount
    
- DailyLog summary list
    

---

## 7.8.4 Contribution 조회

Contribution은 연도별로 조회한다.

조회 조건은 다음과 같다.

```text
userId = currentUser.id
status = PUBLISHED
workedDate between yearStart and yearEnd
```

응답 데이터는 다음을 포함한다.

- year
    
- totalDurationMinutes
    
- activeDays
    
- dailyLogCount
    
- dailyContributions
    

---

## 7.8.5 Level 계산 기준

Calendar와 Contribution의 level은 다음 기준으로 계산한다.

|Level|기준|
|---|---|
|0|0분|
|1|1분 이상 30분 이하|
|2|31분 이상 120분 이하|
|3|121분 이상 240분 이하|
|4|241분 이상|

Level은 DB에 저장하지 않고 응답 생성 시 계산한다.

---

# 8. 트랜잭션 설계

## 8.1 기본 원칙

트랜잭션은 Service 계층에서 관리한다.

조회 전용 메서드는 다음을 사용한다.

```java
@Transactional(readOnly = true)
```

상태 변경 메서드는 다음을 사용한다.

```java
@Transactional
```

---

## 8.2 주요 트랜잭션 경계

|유스케이스|트랜잭션|
|---|---|
|Project 생성|Project + ProjectSpecification 생성|
|Project 수정|Project 상태 변경|
|Project 삭제|Project 관련 하위 데이터 삭제|
|Project-Fabric 연결|Project, Fabric 소유권 검증 + ProjectFabric 생성|
|DailyLog 생성|DailyLog + TimeEntry 생성|
|DailyLog 수정|DailyLog 수정 + TimeEntry 전체 교체 + duration 재계산|
|DailyLog 발행|상태 검증 + duration 재계산 + PUBLISHED 변경|
|Fabric 생성|Fabric 생성|
|Fabric 삭제|ProjectFabric 삭제 + Fabric 삭제|
|Photo Presigned URL 발급|Photo 생성|
|Photo complete-upload|Photo 상태 변경 + Queue 발행|
|Worker 이미지 처리|Photo 상태 변경|

---

## 8.3 외부 시스템 호출과 트랜잭션

외부 시스템 호출은 트랜잭션과 분리하는 것을 우선한다.

예를 들어 S3 업로드 URL 발급은 Photo row 생성과 함께 처리되지만, 실제 파일 업로드는 클라이언트가 S3에 직접 수행한다.

Worker에서 S3 업로드와 DB 상태 변경은 실패 가능성을 고려해야 한다.

MVP에서는 다음 정책을 따른다.

- S3 원본 다운로드 실패 → Photo FAILED
    
- 이미지 변환 실패 → Photo FAILED
    
- 변환 이미지 업로드 실패 → Photo FAILED
    
- DB 상태 변경 실패 → 로그 기록 후 재처리 가능성 검토
    

---

# 9. 검증 규칙

## 9.1 공통 검증

|항목|규칙|
|---|---|
|인증|보호 API는 Access Token 필요|
|소유권|모든 도메인은 currentUser 소유 여부 확인|
|상태|ARCHIVED Project는 수정성 작업 제한|
|Null|필수값은 Request DTO와 Service에서 검증|
|길이|제목, 이름 등은 최대 길이 제한|
|날짜|완료일은 시작일보다 빠를 수 없음|
|시간|TimeEntry endedAt은 startedAt보다 늦어야 함|

---

## 9.2 Project 검증

- title 필수
    
- Project 소유권 확인
    
- ARCHIVED 상태 수정 제한
    
- completedAt이 startedAt보다 빠르지 않아야 함
    

---

## 9.3 DailyLog 검증

- DailyLog 소유권 확인
    
- Project 소유권 확인
    
- Project ARCHIVED 상태 확인
    
- DRAFT는 workedDate, TimeEntry 없이 저장 가능
    
- PUBLISHED는 workedDate 필수
    
- PUBLISHED는 TimeEntry 1개 이상 필수
    
- PUBLISHED → DRAFT 전환 불가
    
- TimeEntry 변경 시 durationMinutes 재계산
    

---

## 9.4 Fabric 검증

- name 필수
    
- Fabric 소유권 확인
    
- rating은 1~5
    
- purchasePrice는 0 이상
    
- repurchaseIntention은 YES, NO, UNKNOWN 중 하나
    

---

## 9.5 ProjectFabric 검증

- Project 소유권 확인
    
- Fabric 소유권 확인
    
- Project ARCHIVED 상태 확인
    
- 동일 Project-Fabric 중복 연결 불가
    

---

## 9.6 Photo 검증

- ownerType 유효성 확인
    
- ownerId 존재 확인
    
- owner 소유권 확인
    
- owner별 사진 수 제한 확인
    
- Project ARCHIVED 상태이면 Project/DailyLog Photo 추가 제한
    
- 파일 contentType 허용 여부 확인
    
- 파일 크기 제한 확인
    
- READY 상태 Photo만 대표 사진 설정 가능
    
- Fabric Photo는 대표 사진 설정 불가
    

---

# 10. 예외 처리 설계

## 10.1 공통 에러 응답 형식

에러 응답은 다음 형식을 사용한다.

```json
{
  "code": "PRJ-001",
  "message": "프로젝트명을 입력해주세요.",
  "details": null
}
```

---

## 10.2 예외 분류

|HTTP Status|상황|
|---|---|
|400 Bad Request|요청값 검증 실패|
|401 Unauthorized|인증 실패|
|403 Forbidden|권한 없음|
|404 Not Found|리소스 없음|
|409 Conflict|중복 또는 상태 충돌|
|500 Internal Server Error|서버 내부 오류|

---

## 10.3 도메인별 에러 코드 Prefix

|도메인|Prefix|
|---|---|
|Authentication|AUTH|
|User|USER|
|Project|PRJ|
|DailyLog|DL|
|Fabric|FAB|
|Photo|PHO|
|Calendar|CAL|
|Common|COM|

---

## 10.4 예외 예시

|코드|메시지|상태|
|---|---|---|
|AUTH-001|인증이 필요합니다.|401|
|AUTH-002|유효하지 않은 토큰입니다.|401|
|PRJ-001|프로젝트명을 입력해주세요.|400|
|PRJ-002|프로젝트를 찾을 수 없습니다.|404|
|PRJ-003|보관된 프로젝트는 수정할 수 없습니다.|409|
|DL-001|작업 날짜를 입력해주세요.|400|
|DL-002|정식 기록에는 작업 시간이 필요합니다.|400|
|DL-003|발행된 기록은 임시저장으로 되돌릴 수 없습니다.|409|
|FAB-001|원단명을 입력해주세요.|400|
|FAB-002|원단을 찾을 수 없습니다.|404|
|PHO-001|사진을 업로드할 대상을 찾을 수 없습니다.|404|
|PHO-002|사진 수 제한을 초과했습니다.|409|
|PHO-003|업로드 중 상태의 사진만 완료 처리할 수 있습니다.|409|
|PHO-004|처리 완료된 사진만 대표 사진으로 설정할 수 있습니다.|409|

---

# 11. API 응답 설계 기준

## 11.1 성공 응답

단건 조회 응답 예시는 다음과 같다.

```json
{
  "id": 1,
  "title": "딸기 파우치",
  "status": "IN_PROGRESS",
  "createdAt": "2026-07-02T10:30:00"
}
```

목록 조회 응답 예시는 다음과 같다.

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "hasNext": false
}
```

MVP에서는 무한스크롤이 필요한 목록에만 cursor 기반 pagination을 검토한다.

---

## 11.2 날짜와 시간 형식

날짜와 시간은 다음 형식을 사용한다.

|값|형식|예시|
|---|---|---|
|Date|yyyy-MM-dd|2026-07-02|
|Time|HH:mm|15:30|
|DateTime|ISO-8601|2026-07-02T15:30:00|

MVP에서는 TimeEntry의 날짜는 DailyLog.workedDate를 기준으로 해석한다.

---

# 12. QueryDSL 설계

## 12.1 QueryDSL 사용 대상

QueryDSL은 다음 기능에서 사용한다.

- Project 목록 조회
    
- DailyLog 기간별 조회
    
- Fabric 검색
    
- Fabric 사용 Project 조회
    
- Project 상세의 연결 Fabric 조회
    
- Calendar 월별 집계
    
- Contribution 연도별 집계
    
- Photo owner별 조회
    

---

## 12.2 Calendar 집계 Query 기준

Calendar 조회는 다음 조건을 사용한다.

```text
userId = currentUser.id
status = PUBLISHED
workedDate between monthStart and monthEnd
group by workedDate
```

반환 값은 다음이다.

- date
    
- dailyLogCount
    
- totalDurationMinutes
    

level은 애플리케이션에서 계산한다.

---

## 12.3 Contribution 집계 Query 기준

Contribution 조회는 다음 조건을 사용한다.

```text
userId = currentUser.id
status = PUBLISHED
workedDate between yearStart and yearEnd
group by workedDate
```

연도 전체 요약 값은 다음을 계산한다.

- activeDays
    
- totalDurationMinutes
    
- dailyLogCount
    

---

# 13. 이미지 처리 설계

## 13.1 이미지 파일 정책

MVP에서 허용하는 이미지 형식은 다음을 우선 검토한다.

|형식|허용 여부|
|---|---|
|image/jpeg|허용|
|image/png|허용|
|image/webp|허용|
|image/heic|MVP에서는 제외 검토|

최대 파일 크기는 MVP에서 다음 기준을 우선 검토한다.

```text
10MB
```

정확한 제한은 프론트엔드 UX와 테스트 결과에 따라 조정한다.

---

## 13.2 이미지 크기 정책

리사이징 결과는 다음을 우선 검토한다.

|이미지|용도|기준|
|---|---|---|
|original|원본 보관|원본 유지|
|medium|상세 화면|긴 변 1280px 이하|
|thumbnail|목록/카드|긴 변 400px 이하|

모든 변환 이미지는 WebP 저장을 우선 검토한다.

---

## 13.3 S3 Key 규칙

S3 Key는 다음 형식을 사용한다.

```text
users/{userId}/photos/{photoId}/original
users/{userId}/photos/{photoId}/medium.webp
users/{userId}/photos/{photoId}/thumbnail.webp
```

이 구조를 사용하면 User와 Photo 기준으로 이미지 파일을 관리할 수 있다.

---

## 13.4 CloudFront URL 생성

API 응답에서는 S3 key를 직접 노출하지 않는다.

Photo가 READY 상태이면 CloudFront 도메인을 기준으로 URL을 생성한다.

예시는 다음과 같다.

```text
https://cdn.sorosoro.com/users/{userId}/photos/{photoId}/thumbnail.webp
```

MVP에서는 CloudFront domain을 환경 변수로 관리한다.

---

# 14. 삭제 정책 구현

## 14.1 물리 삭제와 Soft Delete

MVP에서는 대부분의 도메인에 물리 삭제를 우선 적용한다.

다만 User는 탈퇴 시 `status = DELETED`로 변경한다.

|도메인|삭제 방식|
|---|---|
|User|Soft Delete|
|Project|물리 삭제|
|DailyLog|물리 삭제|
|Fabric|물리 삭제|
|ProjectFabric|물리 삭제|
|Photo|물리 삭제 + S3 object 삭제|
|RefreshToken|물리 삭제|

---

## 14.2 삭제 시 연관 데이터 처리

|삭제 대상|함께 처리|
|---|---|
|Project|Specification, Reference, DailyLog, TimeEntry, ProjectFabric, Project/DailyLog Photo|
|DailyLog|TimeEntry, DailyLog Photo|
|Fabric|ProjectFabric, Fabric Photo|
|Photo|S3 original/medium/thumbnail 삭제|
|User 탈퇴|RefreshToken 삭제, User status 변경|

Project 삭제 시 Fabric은 삭제하지 않는다.  
Fabric 삭제 시 Project는 삭제하지 않는다.

---

# 15. 테스트 전략

## 15.1 테스트 범위

MVP에서 우선 작성할 테스트는 다음과 같다.

- Service 단위 테스트
    
- Repository QueryDSL 테스트
    
- Controller 통합 테스트 일부
    
- Security 인증 테스트 일부
    
- Photo Worker 테스트 일부
    

---

## 15.2 도메인별 핵심 테스트

### Project

- title만으로 Project 생성 가능
    
- Project 생성 시 ProjectSpecification 생성
    
- 다른 사용자의 Project 수정 불가
    
- ARCHIVED Project 수정 불가
    
- Project 상태 변경 가능
    

### DailyLog

- DRAFT는 workedDate 없이 저장 가능
    
- DRAFT는 TimeEntry 없이 저장 가능
    
- PUBLISHED는 workedDate 필수
    
- PUBLISHED는 TimeEntry 1개 이상 필수
    
- PUBLISHED → DRAFT 전환 불가
    
- PUBLISHED 수정 시 durationMinutes 재계산
    
- Project가 ARCHIVED이면 DailyLog 수정 불가
    

### Fabric

- name만으로 Fabric 생성 가능
    
- 다른 사용자의 Fabric 수정 불가
    
- rating은 1~5만 허용
    
- Fabric 삭제 시 Project는 유지
    
- Fabric 삭제 시 ProjectFabric 삭제
    

### ProjectFabric

- Project와 Fabric 모두 현재 User 소유여야 연결 가능
    
- 동일 Project-Fabric 중복 연결 불가
    
- ARCHIVED Project에는 Fabric 연결 불가
    

### Photo

- owner 소유권이 있어야 Presigned URL 발급 가능
    
- owner별 사진 수 제한 적용
    
- complete-upload 시 PROCESSING으로 변경
    
- READY 상태 Photo만 대표 사진 설정 가능
    
- Fabric Photo는 대표 사진 설정 불가
    

### Calendar

- DRAFT DailyLog는 집계 제외
    
- PUBLISHED DailyLog는 집계 포함
    
- 같은 날짜의 DailyLog durationMinutes 합산
    
- workedDate 변경 시 집계 날짜 변경
    
- 다른 사용자의 DailyLog는 집계 제외
    

---

# 16. Backend Design 요약

소로소로 백엔드는 Java 17과 Spring Boot 기반으로 구현한다.

설계 원칙은 다음과 같다.

- 도메인별 패키지 구조를 사용한다.
    
- Controller는 HTTP 요청/응답만 담당한다.
    
- Service는 유스케이스, 트랜잭션, 검증을 담당한다.
    
- Repository는 데이터 접근을 담당한다.
    
- 복잡한 조회와 집계는 QueryDSL을 사용한다.
    
- 모든 주요 데이터는 User 소유권을 검증한다.
    
- Project가 ARCHIVED 상태이면 수정성 작업을 제한한다.
    
- DailyLog는 DRAFT/PUBLISHED 상태를 가진다.
    
- PUBLISHED DailyLog만 Calendar와 Contribution에 반영한다.
    
- DailyLog 작업 시간은 TimeEntry 합산으로 계산한다.
    
- Fabric은 Project와 독립 도메인으로 관리한다.
    
- Project와 Fabric은 ProjectFabric으로 연결한다.
    
- Photo는 공통 이미지 엔티티로 관리한다.
    
- 이미지 업로드는 Presigned URL 기반 S3 직접 업로드를 사용한다.
    
- 이미지 리사이징은 Queue와 Worker로 비동기 처리한다.
    
- Calendar와 Contribution은 별도 저장 없이 조회 시 계산한다.
    
- 예외 응답은 code, message, details 형식으로 통일한다.