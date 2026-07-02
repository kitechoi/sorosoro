# 09. Test Strategy — 소로소로(SOROSORO)

> 소로소로 MVP의 테스트 범위, 테스트 기준, 도메인별 핵심 테스트 케이스를 정의하는 문서

---

# 1. 문서 개요

## 1.1 문서 목적

이 문서는 소로소로(SOROSORO)의 테스트 전략을 정의한다.

소로소로는 개인 재봉 기록 서비스로, Project, DailyLog, Fabric, Photo, Calendar 기능을 중심으로 구성된다.

본 문서의 목적은 다음과 같다.

- MVP에서 반드시 검증해야 할 기능을 정의한다.
    
- 도메인별 핵심 비즈니스 규칙을 테스트 케이스로 정리한다.
    
- Service 테스트, Repository 테스트, API 통합 테스트의 역할을 구분한다.
    
- 이미지 업로드와 Worker 비동기 처리 테스트 기준을 정의한다.
    
- 배포 전 검증 시나리오를 정리한다.
    
- 포트폴리오에서 테스트 설계 의도를 설명할 수 있도록 한다.
    

---

## 1.2 테스트 범위

MVP 테스트 범위는 다음과 같다.

- Unit Test
    
- Service Test
    
- Repository Test
    
- Controller Integration Test
    
- Security Test
    
- Worker Test
    
- End-to-End Scenario Test
    
- Deployment Smoke Test
    

---

## 1.3 테스트 제외 범위

MVP에서는 다음 테스트는 우선순위에서 제외한다.

- 대규모 부하 테스트
    
- 장시간 안정성 테스트
    
- 브라우저 E2E 자동화 테스트
    
- AI 원단 자동 기록 테스트
    
- 모바일 UI 테스트
    
- 다중 사용자 동시 편집 테스트
    
- 고도화 모니터링 테스트
    
- 장애 자동 복구 테스트
    

---

# 2. 테스트 원칙

## 2.1 핵심 원칙

소로소로의 테스트는 다음 원칙을 따른다.

1. 비즈니스 규칙은 Service 테스트에서 검증한다.
    
2. 복잡한 조회와 집계는 Repository 테스트에서 검증한다.
    
3. 인증과 권한 흐름은 통합 테스트에서 검증한다.
    
4. Entity의 단순 getter/setter 테스트는 작성하지 않는다.
    
5. 외부 서비스는 Mock 또는 Stub으로 대체한다.
    
6. 이미지 처리 Worker는 성공/실패 상태 전이를 중심으로 검증한다.
    
7. Calendar와 Contribution은 DRAFT/PUBLISHED 기준을 반드시 검증한다.
    
8. 배포 전에는 핵심 사용자 시나리오를 수동 또는 자동으로 검증한다.
    

---

## 2.2 테스트 우선순위

|우선순위|테스트 대상|이유|
|--:|---|---|
|1|DailyLog|상태, 시간 계산, 통계 반영 기준이 핵심|
|2|Photo|업로드 상태 전이와 이미지 처리 흐름이 복잡|
|3|ProjectFabric|소유권, 중복 연결, 독립 도메인 관계 검증 필요|
|4|Calendar/Contribution|조회 시 계산되는 집계 정확성 필요|
|5|Auth/Security|보호 API 접근 제어 필요|
|6|Project/Fabric CRUD|기본 도메인 안정성 필요|
|7|배포 Smoke Test|운영 환경 정상 동작 확인 필요|

---

# 3. 테스트 종류

## 3.1 Unit Test

Unit Test는 순수한 계산 로직과 도메인 메서드를 검증한다.

대상은 다음과 같다.

- TimeEntry durationMinutes 계산
    
- DailyLog durationMinutes 합산
    
- Calendar level 계산
    
- Photo 상태 전이 메서드
    
- Project 상태 변경 메서드
    
- Fabric rating 검증
    
- S3 Key 생성 로직
    

---

## 3.2 Service Test

Service Test는 유스케이스 단위 비즈니스 규칙을 검증한다.

대상은 다음과 같다.

- Project 생성/수정/삭제
    
- DailyLog 생성/수정/발행/삭제
    
- Fabric 생성/수정/삭제
    
- ProjectFabric 연결/수정/삭제
    
- Photo Presigned URL 발급
    
- Photo complete-upload
    
- 대표 사진 설정
    
- Auth 로그인/토큰 재발급
    

Service Test는 가장 중요한 테스트 계층이다.

---

## 3.3 Repository Test

Repository Test는 실제 DB 또는 Testcontainers 기반 PostgreSQL에서 쿼리를 검증한다.

대상은 다음과 같다.

- Project 목록 조회
    
- Fabric 검색
    
- Project별 DailyLog 목록 조회
    
- 날짜별 DailyLog 목록 조회
    
- Calendar 월별 집계
    
- Contribution 연도별 집계
    
- Photo owner별 조회
    
- ProjectFabric 중복 조회
    

---

## 3.4 Controller Integration Test

Controller Integration Test는 HTTP 요청/응답, 인증, DTO 검증, 에러 응답 형식을 검증한다.

대상은 다음과 같다.

- 보호 API 인증 실패
    
- 요청값 검증 실패
    
- 정상 API 응답 구조
    
- 공통 에러 응답 형식
    
- 상태 코드
    
- JSON 필드명
    

---

## 3.5 Security Test

Security Test는 인증과 권한 검증을 확인한다.

대상은 다음과 같다.

- Access Token 없는 요청
    
- 유효하지 않은 Access Token
    
- 다른 사용자의 Project 접근
    
- 다른 사용자의 Fabric 접근
    
- 다른 사용자의 DailyLog 접근
    
- 다른 사용자의 Photo 접근
    
- ProjectFabric 연결 시 양쪽 소유권 검증
    

---

## 3.6 Worker Test

Worker Test는 이미지 리사이징 비동기 처리 흐름을 검증한다.

대상은 다음과 같다.

- Queue 메시지 수신
    
- photoId로 Photo 조회
    
- PROCESSING 상태 Photo만 처리
    
- 이미지 처리 성공 시 READY 전환
    
- 이미지 처리 실패 시 FAILED 전환
    
- 중복 메시지 수신 시 skip
    
- S3 다운로드 실패 처리
    
- S3 업로드 실패 처리
    

---

## 3.7 Deployment Smoke Test

Deployment Smoke Test는 배포 후 운영 환경에서 핵심 기능이 동작하는지 확인한다.

대상은 다음과 같다.

- Health Check
    
- 로그인
    
- Project 생성
    
- DailyLog 생성
    
- Fabric 생성
    
- Photo Presigned URL 발급
    
- complete-upload
    
- Worker 처리
    
- Calendar 조회
    

---

# 4. 테스트 환경

## 4.1 Local Test

로컬 테스트는 개발 중 빠른 피드백을 위해 사용한다.

구성은 다음과 같다.

```text
Spring Boot Test
H2 또는 PostgreSQL Testcontainer
Mock S3 Client
Mock Queue
```

---

## 4.2 Repository Test 환경

Repository Test는 PostgreSQL 기준으로 검증하는 것을 우선한다.

이유는 다음과 같다.

- 실제 운영 DB가 PostgreSQL이다.
    
- 날짜 집계 쿼리와 인덱스 기준을 확인해야 한다.
    
- QueryDSL 쿼리가 PostgreSQL에서 정상 동작하는지 확인해야 한다.
    
- WorkTypes 저장 방식이 PostgreSQL 기능을 사용할 수 있다.
    

가능하면 Testcontainers PostgreSQL을 사용한다.

---

## 4.3 외부 서비스 Mock 기준

외부 서비스는 테스트에서 직접 호출하지 않는다.

|외부 서비스|테스트 방식|
|---|---|
|Kakao OAuth|Mock KakaoOAuthClient|
|S3|Mock S3StorageClient|
|CloudFront|Fake URL Resolver|
|Queue|Fake Queue Producer|
|Redis Queue|통합 테스트에서만 사용|
|JWT|실제 JwtTokenProvider 또는 테스트 토큰 사용|

---

# 5. 테스트 데이터 기준

## 5.1 기본 사용자 데이터

테스트에서는 최소 두 명의 사용자를 생성한다.

```text
userA
userB
```

이유는 소유권 검증을 테스트하기 위해서이다.

---

## 5.2 기본 Project 데이터

```text
userA
 ├── projectA1: IN_PROGRESS
 ├── projectA2: ARCHIVED
 └── projectA3: COMPLETED

userB
 └── projectB1: IN_PROGRESS
```

---

## 5.3 기본 Fabric 데이터

```text
userA
 ├── fabricA1
 └── fabricA2

userB
 └── fabricB1
```

---

## 5.4 기본 DailyLog 데이터

```text
projectA1
 ├── dailyLogDraft
 ├── dailyLogPublished1
 └── dailyLogPublished2
```

---

## 5.5 기본 Photo 데이터

```text
projectA1
 ├── photoUploading
 ├── photoProcessing
 ├── photoReady
 └── photoFailed
```

---

# 6. 도메인별 테스트 전략

---

# 6.1 Auth/User Test

## 6.1.1 테스트 대상

- 카카오 로그인
    
- User 생성
    
- 기존 User 재로그인
    
- Access Token 발급
    
- Refresh Token 저장
    
- Access Token 재발급
    
- 로그아웃
    
- 회원 탈퇴
    

---

## 6.1.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|AUTH-TC-001|신규 카카오 사용자 로그인|User 생성, Access Token 발급|
|AUTH-TC-002|기존 카카오 사용자 로그인|기존 User 반환, 중복 생성 없음|
|AUTH-TC-003|Refresh Token으로 Access Token 재발급|새 Access Token 반환|
|AUTH-TC-004|유효하지 않은 Refresh Token으로 재발급|401 반환|
|AUTH-TC-005|로그아웃|Refresh Token 삭제|
|AUTH-TC-006|회원 탈퇴|User status = DELETED|
|AUTH-TC-007|탈퇴 사용자 로그인 정책 확인|정책에 따라 재활성화 또는 실패|

---

# 6.2 Project Test

## 6.2.1 테스트 대상

- Project 생성
    
- Project 목록 조회
    
- Project 상세 조회
    
- Project 수정
    
- Project 상태 변경
    
- Project 삭제
    
- ProjectSpecification 생성/수정
    
- ProjectReference 등록/수정/삭제
    

---

## 6.2.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|PRJ-TC-001|title만으로 Project 생성|IN_PROGRESS 상태로 생성|
|PRJ-TC-002|Project 생성 시 Specification 생성|빈 Specification 함께 생성|
|PRJ-TC-003|title 없이 Project 생성|400 반환|
|PRJ-TC-004|다른 사용자의 Project 수정|403 반환|
|PRJ-TC-005|ARCHIVED Project 수정|409 반환|
|PRJ-TC-006|Project 상태를 COMPLETED로 변경|상태 변경, completedAt 저장|
|PRJ-TC-007|completedAt이 startedAt보다 빠름|400 반환|
|PRJ-TC-008|Project 삭제|하위 DailyLog, ProjectFabric 삭제|
|PRJ-TC-009|Project 삭제 시 Fabric 유지|Fabric 삭제되지 않음|
|PRJ-TC-010|ProjectReference 등록|Reference 생성|
|PRJ-TC-011|ProjectReference 수정|Reference 값 변경|
|PRJ-TC-012|ProjectReference 삭제|Reference 삭제|

---

# 6.3 Fabric Test

## 6.3.1 테스트 대상

- Fabric 등록
    
- Fabric 목록 조회
    
- Fabric 상세 조회
    
- Fabric 수정
    
- Fabric 삭제
    
- Fabric 검색
    
- Fabric 사용 Project 조회
    

---

## 6.3.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|FAB-TC-001|name만으로 Fabric 생성|repurchaseIntention UNKNOWN|
|FAB-TC-002|name 없이 Fabric 생성|400 반환|
|FAB-TC-003|rating 1~5 범위|정상 저장|
|FAB-TC-004|rating 6 입력|400 반환|
|FAB-TC-005|다른 사용자의 Fabric 수정|403 반환|
|FAB-TC-006|Fabric 삭제|Fabric 삭제|
|FAB-TC-007|Fabric 삭제 시 Project 유지|Project 삭제되지 않음|
|FAB-TC-008|keyword로 Fabric 검색|원단명/상품명/품번 기준 조회|
|FAB-TC-009|storeName으로 필터|구매처 기준 조회|
|FAB-TC-010|repurchaseIntention으로 필터|재구매 의사 기준 조회|

---

# 6.4 ProjectFabric Test

## 6.4.1 테스트 대상

- Project에 Fabric 연결
    
- 연결 메모 수정
    
- 연결 삭제
    
- 중복 연결 방지
    
- 소유권 검증
    
- ARCHIVED Project 제약
    

---

## 6.4.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|PF-TC-001|Project와 Fabric 연결|ProjectFabric 생성|
|PF-TC-002|동일 Project-Fabric 중복 연결|409 반환|
|PF-TC-003|다른 사용자의 Fabric 연결|403 반환|
|PF-TC-004|다른 사용자의 Project에 연결|403 반환|
|PF-TC-005|ARCHIVED Project에 Fabric 연결|409 반환|
|PF-TC-006|연결 메모 수정|memo 변경|
|PF-TC-007|연결 삭제|ProjectFabric 삭제|
|PF-TC-008|연결 삭제 시 Project 유지|Project 삭제되지 않음|
|PF-TC-009|연결 삭제 시 Fabric 유지|Fabric 삭제되지 않음|
|PF-TC-010|Project 상세에서 Fabric 목록 조회|연결된 Fabric 반환|
|PF-TC-011|Fabric 상세에서 사용 Project 조회|연결된 Project 반환|

---

# 6.5 DailyLog Test

## 6.5.1 테스트 대상

- DRAFT DailyLog 생성
    
- PUBLISHED DailyLog 생성
    
- DailyLog 수정
    
- DailyLog 발행
    
- DailyLog 삭제
    
- TimeEntry 전체 교체
    
- durationMinutes 계산
    
- 상태별 검증
    
- ARCHIVED Project 제약
    

---

## 6.5.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|DL-TC-001|DRAFT를 workedDate 없이 생성|성공|
|DL-TC-002|DRAFT를 TimeEntry 없이 생성|성공|
|DL-TC-003|PUBLISHED를 workedDate 없이 생성|400 반환|
|DL-TC-004|PUBLISHED를 TimeEntry 없이 생성|400 반환|
|DL-TC-005|PUBLISHED 정상 생성|durationMinutes 계산|
|DL-TC-006|TimeEntry endedAt이 startedAt보다 빠름|400 반환|
|DL-TC-007|TimeEntry durationMinutes 계산|endedAt-startedAt|
|DL-TC-008|여러 TimeEntry 합산|DailyLog durationMinutes 합산|
|DL-TC-009|DRAFT 발행|status PUBLISHED|
|DL-TC-010|PUBLISHED를 다시 발행|409 반환|
|DL-TC-011|PUBLISHED → DRAFT 전환 시도|409 반환|
|DL-TC-012|PUBLISHED 수정|수정 가능|
|DL-TC-013|PUBLISHED 수정 시 TimeEntry 전체 교체|기존 삭제, 새로 생성|
|DL-TC-014|PUBLISHED 수정 후 durationMinutes 재계산|합산값 변경|
|DL-TC-015|다른 사용자의 DailyLog 수정|403 반환|
|DL-TC-016|ARCHIVED Project에 DailyLog 생성|409 반환|
|DL-TC-017|ARCHIVED Project의 DailyLog 수정|409 반환|
|DL-TC-018|DailyLog 삭제|TimeEntry 함께 삭제|

---

# 6.6 Calendar/Contribution Test

## 6.6.1 테스트 대상

- 월별 Calendar 조회
    
- 특정 날짜 작업 요약 조회
    
- 연도별 Contribution 조회
    
- DRAFT 제외
    
- PUBLISHED 포함
    
- 날짜별 작업 시간 합산
    
- Level 계산
    

---

## 6.6.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|CAL-TC-001|DRAFT DailyLog가 있는 날짜 조회|집계 제외|
|CAL-TC-002|PUBLISHED DailyLog가 있는 날짜 조회|집계 포함|
|CAL-TC-003|같은 날짜에 PUBLISHED DailyLog 2개|dailyLogCount 2, 시간 합산|
|CAL-TC-004|다른 사용자의 DailyLog 존재|집계 제외|
|CAL-TC-005|workedDate가 조회 월 밖임|집계 제외|
|CAL-TC-006|totalDurationMinutes 0분|level 0|
|CAL-TC-007|totalDurationMinutes 1~30분|level 1|
|CAL-TC-008|totalDurationMinutes 31~120분|level 2|
|CAL-TC-009|totalDurationMinutes 121~240분|level 3|
|CAL-TC-010|totalDurationMinutes 241분 이상|level 4|
|CAL-TC-011|Contribution activeDays 계산|작업일 수 반환|
|CAL-TC-012|Contribution yearly total 계산|연도 총 작업 시간 반환|

---

# 6.7 Photo Test

## 6.7.1 테스트 대상

- Presigned URL 발급
    
- Photo 생성
    
- 업로드 완료 처리
    
- Photo 목록 조회
    
- 대표 사진 설정
    
- Photo 정렬
    
- Photo 삭제
    
- ownerType별 사진 수 제한
    
- ownerType별 권한 검증
    

---

## 6.7.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|PHO-TC-001|Project Photo Presigned URL 발급|Photo UPLOADING 생성|
|PHO-TC-002|DailyLog Photo Presigned URL 발급|Photo UPLOADING 생성|
|PHO-TC-003|Fabric Photo Presigned URL 발급|Photo UPLOADING 생성|
|PHO-TC-004|존재하지 않는 ownerId|404 반환|
|PHO-TC-005|다른 사용자의 owner에 Photo 추가|403 반환|
|PHO-TC-006|Project Photo 15장 초과|409 반환|
|PHO-TC-007|DailyLog Photo 15장 초과|409 반환|
|PHO-TC-008|Fabric Photo 2장 초과|409 반환|
|PHO-TC-009|지원하지 않는 contentType|400 반환|
|PHO-TC-010|파일 크기 초과|400 반환|
|PHO-TC-011|complete-upload 성공|Photo PROCESSING 변경, Queue 발행|
|PHO-TC-012|UPLOADING이 아닌 Photo complete-upload|409 반환|
|PHO-TC-013|S3 object 없음|400 또는 409 반환|
|PHO-TC-014|READY Photo 대표 설정|isThumbnail true|
|PHO-TC-015|PROCESSING Photo 대표 설정|409 반환|
|PHO-TC-016|Fabric Photo 대표 설정|409 반환|
|PHO-TC-017|Project 대표 사진 1장 유지|기존 대표 해제|
|PHO-TC-018|Photo 정렬 수정|sortOrder 변경|
|PHO-TC-019|Photo 삭제|DB row 삭제, S3 삭제 요청|

---

# 6.8 Worker Test

## 6.8.1 테스트 대상

- Queue 메시지 수신
    
- Photo 조회
    
- 상태 검증
    
- S3 원본 다운로드
    
- 이미지 변환
    
- S3 변환 이미지 업로드
    
- Photo READY/FAILED 상태 변경
    
- 중복 메시지 처리
    

---

## 6.8.2 핵심 테스트 케이스

|ID|테스트 케이스|기대 결과|
|---|---|---|
|WRK-TC-001|PROCESSING Photo 처리 성공|READY 상태 변경|
|WRK-TC-002|mediumKey, thumbnailKey 저장|변환 이미지 Key 저장|
|WRK-TC-003|processedAt 저장|처리 완료 시간 저장|
|WRK-TC-004|Photo 상태가 READY|Worker skip|
|WRK-TC-005|Photo 상태가 FAILED|Worker skip|
|WRK-TC-006|S3 원본 다운로드 실패|FAILED 상태 변경|
|WRK-TC-007|이미지 변환 실패|FAILED 상태 변경|
|WRK-TC-008|S3 업로드 실패|FAILED 상태 변경|
|WRK-TC-009|중복 메시지 수신|중복 처리 없이 skip|

---

# 7. API 통합 테스트 전략

## 7.1 통합 테스트 대상

API 통합 테스트는 다음을 검증한다.

- HTTP Method
    
- Endpoint
    
- Request Body
    
- Response Body
    
- Status Code
    
- 인증 필요 여부
    
- 에러 응답 형식
    

---

## 7.2 우선 작성할 API 통합 테스트

|우선순위|API|이유|
|--:|---|---|
|1|DailyLog 생성/발행/수정|핵심 비즈니스 규칙|
|2|Photo Presigned URL/complete-upload|상태 전이와 외부 연동|
|3|ProjectFabric 연결|소유권과 중복 제약|
|4|Calendar 조회|집계 정확성|
|5|Auth 보호 API|인증 필수 검증|
|6|Project/Fabric CRUD|기본 CRUD 안정성|

---

## 7.3 공통 검증 항목

모든 통합 테스트에서 다음을 확인한다.

- 인증 없는 요청은 401
    
- 다른 사용자 리소스 접근은 403
    
- 존재하지 않는 리소스는 404
    
- 잘못된 요청값은 400
    
- 상태 충돌은 409
    
- 에러 응답은 code, message, details 형식
    
- 성공 응답 필드가 API 문서와 일치
    

---

# 8. End-to-End Scenario Test

## 8.1 Scenario 1. 기본 기록 흐름

```text
1. 사용자 로그인
2. Project 생성
3. Fabric 생성
4. Project에 Fabric 연결
5. DailyLog DRAFT 생성
6. DailyLog 발행
7. Calendar 조회
8. Contribution 조회
```

### 기대 결과

- Project가 생성된다.
    
- Fabric이 생성된다.
    
- ProjectFabric이 생성된다.
    
- DailyLog가 PUBLISHED 상태가 된다.
    
- Calendar에 작업 시간이 반영된다.
    
- Contribution에 작업 시간이 반영된다.
    

---

## 8.2 Scenario 2. 이미지 업로드 흐름

```text
1. Project 생성
2. Presigned URL 발급
3. S3 원본 업로드
4. complete-upload 호출
5. Worker 처리
6. Photo READY 확인
7. Project 상세에서 thumbnailUrl 확인
```

### 기대 결과

- Presigned URL이 발급된다.
    
- Photo가 UPLOADING으로 생성된다.
    
- complete-upload 후 PROCESSING이 된다.
    
- Worker 처리 후 READY가 된다.
    
- mediumUrl과 thumbnailUrl이 반환된다.
    

---

## 8.3 Scenario 3. 권한 검증 흐름

```text
1. userA가 Project 생성
2. userB가 userA Project 조회 시도
3. userB가 userA Project 수정 시도
4. userB가 userA Project에 Fabric 연결 시도
```

### 기대 결과

- 다른 사용자의 리소스 접근은 403을 반환한다.
    
- 데이터는 변경되지 않는다.
    

---

## 8.4 Scenario 4. ARCHIVED Project 제약

```text
1. Project 생성
2. Project 상태를 ARCHIVED로 변경
3. Project 수정 시도
4. DailyLog 생성 시도
5. Fabric 연결 시도
6. Photo 추가 시도
7. Project 상태 변경 시도
```

### 기대 결과

- 수정성 작업은 실패한다.
    
- Project 상태 변경은 가능하다.
    

---

# 9. 테스트 작성 순서

## 9.1 권장 순서

테스트는 구현 순서와 비슷하게 작성하되, 복잡한 도메인부터 우선 보강한다.

```text
1. 공통 예외 응답 테스트
2. Auth/Security 테스트
3. Project Service 테스트
4. Fabric Service 테스트
5. ProjectFabric Service 테스트
6. DailyLog Service 테스트
7. Calendar Repository 테스트
8. Photo Service 테스트
9. Worker 테스트
10. API 통합 테스트
11. E2E 시나리오 테스트
```

---

## 9.2 반드시 먼저 작성할 테스트

다음 테스트는 MVP 안정성에 직접 영향을 주므로 먼저 작성한다.

- PUBLISHED DailyLog는 workedDate 없이 생성할 수 없다.
    
- PUBLISHED DailyLog는 TimeEntry 없이 생성할 수 없다.
    
- DailyLog.durationMinutes는 TimeEntry 합산값이다.
    
- DRAFT DailyLog는 Calendar에 포함되지 않는다.
    
- PUBLISHED DailyLog는 Calendar에 포함된다.
    
- Project와 Fabric은 중복 연결할 수 없다.
    
- 다른 사용자의 리소스는 수정할 수 없다.
    
- READY Photo만 대표 사진으로 설정할 수 있다.
    
- Fabric Photo는 대표 사진으로 설정할 수 없다.
    

---

# 10. 테스트 데이터 관리

## 10.1 Fixture 사용

테스트 데이터 생성을 위해 Fixture 클래스를 둔다.

예시는 다음과 같다.

```text
UserFixture
ProjectFixture
FabricFixture
DailyLogFixture
PhotoFixture
```

Fixture 사용 이유는 다음과 같다.

- 테스트 가독성 향상
    
- 중복 데이터 생성 코드 제거
    
- 도메인 기본값 통일
    
- 테스트 의도 명확화
    

---

## 10.2 Fixture 예시

```java
public class ProjectFixture {
    public static Project inProgressProject(User user) {
        return Project.create(
            user,
            "딸기 파우치",
            "딸기 원단으로 만드는 파우치"
        );
    }

    public static Project archivedProject(User user) {
        Project project = inProgressProject(user);
        project.changeStatus(ProjectStatus.ARCHIVED);
        return project;
    }
}
```

---

## 10.3 테스트 독립성

각 테스트는 독립적으로 실행 가능해야 한다.

원칙은 다음과 같다.

- 테스트 간 데이터 공유 금지
    
- 테스트 실행 순서에 의존하지 않기
    
- 테스트 후 DB 초기화
    
- 외부 서비스 직접 호출 금지
    
- 현재 시간 의존 로직은 Clock 또는 TimeProvider로 제어
    

---

# 11. Mocking 기준

## 11.1 Mock 대상

다음은 Mock 또는 Fake로 대체한다.

|대상|방식|
|---|---|
|KakaoOAuthClient|Mock|
|S3StorageClient|Fake 또는 Mock|
|PhotoQueueProducer|Fake 또는 Mock|
|CloudFrontUrlResolver|Fake|
|JwtTokenProvider|실제 또는 테스트용|
|TimeProvider|Fake|

---

## 11.2 Mock하지 않는 대상

다음은 가능하면 실제 구현을 사용한다.

|대상|이유|
|---|---|
|Service|비즈니스 규칙 검증 대상|
|Repository QueryDSL|쿼리 검증 필요|
|Entity|도메인 상태 변경 검증 대상|
|Validator|입력 검증 확인 필요|

---

# 12. 배포 전 체크리스트

## 12.1 로컬 체크리스트

-  전체 테스트 통과
    
-  Flyway migration 성공
    
-  Swagger 실행 확인
    
-  Health Check 확인
    
-  Auth API 확인
    
-  Project CRUD 확인
    
-  DailyLog DRAFT/PUBLISHED 확인
    
-  Fabric CRUD 확인
    
-  ProjectFabric 연결 확인
    
-  Photo Presigned URL 확인
    
-  Worker 처리 확인
    
-  Calendar 조회 확인
    
-  Contribution 조회 확인
    

---

## 12.2 운영 배포 체크리스트

-  EC2 접속 가능
    
-  Docker 설치 완료
    
-  Docker Compose 설치 완료
    
-  `.env` 설정 완료
    
-  PostgreSQL volume 설정 완료
    
-  Nginx 설정 완료
    
-  S3 Bucket 설정 완료
    
-  CloudFront 설정 완료
    
-  GitHub Actions Secrets 설정 완료
    
-  `docker compose up -d` 성공
    
-  `/actuator/health` 정상
    
-  API Server 로그 정상
    
-  Worker 로그 정상
    
-  이미지 업로드 정상
    
-  Calendar 조회 정상
    

---

# 13. 테스트 완료 기준

## 13.1 MVP 테스트 완료 기준

MVP 테스트는 다음 조건을 만족하면 완료로 본다.

- 핵심 Service 테스트가 작성되어 있다.
    
- DailyLog 상태별 테스트가 통과한다.
    
- TimeEntry 계산 테스트가 통과한다.
    
- ProjectFabric 중복 연결 테스트가 통과한다.
    
- Photo 상태 전이 테스트가 통과한다.
    
- Calendar/Contribution 집계 테스트가 통과한다.
    
- 인증 없는 요청이 401을 반환한다.
    
- 다른 사용자 리소스 접근이 403을 반환한다.
    
- 주요 API 통합 테스트가 통과한다.
    
- 배포 환경에서 Smoke Test가 성공한다.
    

---

## 13.2 포트폴리오 기준 완료 조건

포트폴리오 관점에서는 다음을 설명할 수 있어야 한다.

- 어떤 테스트를 우선순위로 두었는가
    
- 왜 DailyLog와 Photo 테스트를 중요하게 봤는가
    
- 상태 기반 도메인 규칙을 어떻게 검증했는가
    
- 조회 시 계산되는 Calendar/Contribution을 어떻게 검증했는가
    
- 외부 서비스 S3, Kakao, Queue를 테스트에서 어떻게 분리했는가
    
- 배포 후 어떤 Smoke Test를 수행했는가
    

---

# 14. 향후 테스트 확장

MVP 이후 다음 테스트를 추가할 수 있다.

## 14.1 Performance Test

- Calendar 월별 조회 성능
    
- Contribution 연도별 조회 성능
    
- Fabric 검색 성능
    
- 이미지 업로드 동시 요청 처리
    

---

## 14.2 Load Test

- 다중 사용자 API 요청
    
- 이미지 업로드 동시 요청
    
- Worker 처리량
    
- Queue 적체 상황
    

---

## 14.3 Contract Test

프론트엔드와 백엔드가 분리될 경우 API Contract Test를 도입할 수 있다.

대상은 다음과 같다.

- Request DTO
    
- Response DTO
    
- Error Response
    
- Enum 값
    
- Pagination 형식
    

---

## 14.4 Monitoring Test

운영 고도화 시 다음을 검증한다.

- Worker 실패 알림
    
- 오래된 PROCESSING Photo 탐지
    
- Queue 적체 알림
    
- S3 삭제 실패 재처리
    
- DB 백업 성공 여부
    

---

# 15. Test Strategy 요약

소로소로 MVP 테스트 전략은 다음 원칙을 따른다.

- 핵심 비즈니스 규칙은 Service 테스트로 검증한다.
    
- 복잡한 조회와 집계는 Repository 테스트로 검증한다.
    
- 인증과 권한은 API 통합 테스트로 검증한다.
    
- 외부 서비스는 Mock 또는 Fake로 대체한다.
    
- DailyLog는 상태와 작업 시간 계산을 집중적으로 테스트한다.
    
- Photo는 업로드 상태 전이와 ownerType별 정책을 집중적으로 테스트한다.
    
- Calendar와 Contribution은 PUBLISHED DailyLog 기준 집계를 검증한다.
    
- Worker는 READY/FAILED 상태 전이를 중심으로 테스트한다.
    
- 배포 전에는 핵심 사용자 시나리오와 이미지 업로드 흐름을 Smoke Test로 확인한다.
    

이 전략의 목표는 단순히 테스트 수를 늘리는 것이 아니라, 소로소로의 핵심 도메인 규칙과 운영상 실패 가능성이 큰 흐름을 우선적으로 검증하는 것이다.