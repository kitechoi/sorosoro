# 07. API — 소로소로(SOROSORO)

> 소로소로 MVP의 REST API 엔드포인트, 요청/응답 형식, 에러 코드를 정의하는 문서

---

# 1. 문서 개요

## 1.1 문서 목적

이 문서는 소로소로(SOROSORO)의 MVP API 명세를 정의한다.

소로소로는 개인 재봉 기록 서비스로, 사용자가 Project, DailyLog, Fabric, Photo를 관리하고 Calendar와 Contribution으로 작업 기록을 확인할 수 있도록 한다.

본 문서에서는 다음 내용을 정의한다.

- API 공통 규칙
    
- 인증 방식
    
- 요청/응답 형식
    
- 에러 응답 형식
    
- 도메인별 엔드포인트
    
- Request DTO
    
- Response DTO
    
- 상태별 API 동작
    
- Pagination 기준
    
- 이미지 업로드 흐름
    

---

## 1.2 API Base URL

운영 환경 API Base URL은 다음과 같다.

```text
https://api.sorosoro.com/api/v1
```

로컬 개발 환경 API Base URL은 다음과 같다.

```text
http://localhost:8080/api/v1
```

---

## 1.3 API 설계 원칙

API 설계 원칙은 다음과 같다.

- RESTful API를 기본으로 한다.
    
- 모든 보호 API는 JWT Access Token을 요구한다.
    
- Request와 Response는 JSON 형식을 사용한다.
    
- Entity를 API 응답에 직접 노출하지 않는다.
    
- 날짜와 시간 형식은 일관되게 유지한다.
    
- 이미지 파일은 API 서버로 직접 업로드하지 않는다.
    
- 이미지는 Presigned URL을 통해 S3에 직접 업로드한다.
    
- Calendar와 Contribution은 저장된 통계가 아니라 조회 시 계산된 값을 반환한다.
    

---

# 2. 공통 규칙

## 2.1 Content-Type

요청 Body가 있는 API는 다음 Content-Type을 사용한다.

```http
Content-Type: application/json
```

---

## 2.2 인증 헤더

보호 API는 다음 Authorization 헤더를 사용한다.

```http
Authorization: Bearer {accessToken}
```

Access Token이 없거나 유효하지 않으면 `401 Unauthorized`를 반환한다.

---

## 2.3 날짜와 시간 형식

|값|형식|예시|
|---|---|---|
|Date|yyyy-MM-dd|2026-07-02|
|Time|HH:mm|15:30|
|DateTime|ISO-8601|2026-07-02T15:30:00|

---

## 2.4 공통 성공 응답

단건 응답은 별도 wrapper 없이 객체를 반환한다.

```json
{
  "id": 1,
  "title": "딸기 파우치"
}
```

목록 응답은 다음 형식을 사용한다.

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "hasNext": false
}
```

---

## 2.5 공통 에러 응답

에러 응답은 다음 형식을 사용한다.

```json
{
  "code": "PRJ-001",
  "message": "프로젝트명을 입력해주세요.",
  "details": null
}
```

---

## 2.6 HTTP Status 기준

|Status|의미|
|--:|---|
|200|조회, 수정, 삭제 성공|
|201|생성 성공|
|204|응답 Body 없는 성공|
|400|요청값 검증 실패|
|401|인증 실패|
|403|권한 없음|
|404|리소스 없음|
|409|중복 또는 상태 충돌|
|500|서버 내부 오류|

---

# 3. 에러 코드 Prefix

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

# 4. Authentication API

## 4.1 카카오 로그인

### Endpoint

```http
POST /api/v1/auth/kakao/login
```

### Description

카카오 Authorization Code를 받아 카카오 사용자 정보를 조회하고, 서비스 Access Token과 Refresh Token을 발급한다.

---

### Request

```json
{
  "authorizationCode": "kakao-authorization-code",
  "redirectUri": "https://sorosoro.com/oauth/kakao/callback"
}
```

---

### Response

```json
{
  "accessToken": "access-token",
  "refreshToken": "refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "nickname": "최연",
    "profileImageUrl": "https://..."
  }
}
```

---

## 4.2 Access Token 재발급

### Endpoint

```http
POST /api/v1/auth/reissue
```

### Request

```json
{
  "refreshToken": "refresh-token"
}
```

---

### Response

```json
{
  "accessToken": "new-access-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 4.3 로그아웃

### Endpoint

```http
POST /api/v1/auth/logout
```

### Auth

Required

---

### Request

```json
{
  "refreshToken": "refresh-token"
}
```

---

### Response

```json
{
  "message": "로그아웃되었습니다."
}
```

---

## 4.4 내 정보 조회

### Endpoint

```http
GET /api/v1/users/me
```

### Auth

Required

---

### Response

```json
{
  "id": 1,
  "nickname": "최연",
  "profileImageUrl": "https://...",
  "status": "ACTIVE",
  "createdAt": "2026-07-02T10:30:00"
}
```

---

## 4.5 회원 탈퇴

### Endpoint

```http
DELETE /api/v1/users/me
```

### Auth

Required

---

### Response

```json
{
  "message": "회원 탈퇴가 완료되었습니다."
}
```

---

# 5. Project API

## 5.1 Project 생성

### Endpoint

```http
POST /api/v1/projects
```

### Auth

Required

---

### Request

```json
{
  "title": "딸기 파우치",
  "description": "딸기 원단으로 만드는 작은 파우치",
  "startedAt": "2026-07-02"
}
```

---

### Response

```json
{
  "id": 1,
  "title": "딸기 파우치",
  "description": "딸기 원단으로 만드는 작은 파우치",
  "status": "IN_PROGRESS",
  "startedAt": "2026-07-02",
  "completedAt": null,
  "retrospective": null,
  "createdAt": "2026-07-02T10:30:00",
  "updatedAt": "2026-07-02T10:30:00"
}
```

---

### Rules

- `title`은 필수이다.
    
- Project는 title만으로 생성할 수 있다.
    
- 생성 시 기본 상태는 `IN_PROGRESS`이다.
    
- Project 생성 시 빈 ProjectSpecification을 함께 생성할 수 있다.
    

---

## 5.2 Project 목록 조회

### Endpoint

```http
GET /api/v1/projects
```

### Auth

Required

---

### Query Parameters

|이름|필수|설명|
|---|--:|---|
|status|N|Project 상태 필터|
|page|N|페이지 번호|
|size|N|페이지 크기|

---

### Response

```json
{
  "items": [
    {
      "id": 1,
      "title": "딸기 파우치",
      "description": "딸기 원단으로 만드는 작은 파우치",
      "status": "IN_PROGRESS",
      "startedAt": "2026-07-02",
      "completedAt": null,
      "thumbnailUrl": "https://cdn.sorosoro.com/...",
      "createdAt": "2026-07-02T10:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "hasNext": false
}
```

---

## 5.3 Project 상세 조회

### Endpoint

```http
GET /api/v1/projects/{projectId}
```

### Auth

Required

---

### Response

```json
{
  "id": 1,
  "title": "딸기 파우치",
  "description": "딸기 원단으로 만드는 작은 파우치",
  "status": "IN_PROGRESS",
  "startedAt": "2026-07-02",
  "completedAt": null,
  "retrospective": null,
  "totalDurationMinutes": 240,
  "thumbnailUrl": "https://cdn.sorosoro.com/...",
  "specification": {
    "id": 1,
    "patternMethod": "SELF_DRAFTED",
    "patternName": "기본 파우치 패턴",
    "finishedWidthCm": 20.0,
    "finishedHeightCm": 12.0,
    "finishedSizeMemo": "완성 후 약 20x12cm",
    "sewingMachineSettingMemo": "직선 2.5mm",
    "needleMemo": "11호 바늘",
    "threadMemo": "흰색 코아사",
    "memo": "얇은 원단이라 심지 필요"
  },
  "references": [
    {
      "id": 1,
      "url": "https://example.com",
      "title": "파우치 참고",
      "memo": "지퍼 처리 참고",
      "sortOrder": 0
    }
  ],
  "fabrics": [
    {
      "projectFabricId": 1,
      "fabricId": 1,
      "name": "딸기 면 원단",
      "memo": "겉감으로 사용",
      "thumbnailUrl": "https://cdn.sorosoro.com/..."
    }
  ],
  "createdAt": "2026-07-02T10:30:00",
  "updatedAt": "2026-07-02T10:30:00"
}
```

---

## 5.4 Project 수정

### Endpoint

```http
PATCH /api/v1/projects/{projectId}
```

### Auth

Required

---

### Request

```json
{
  "title": "딸기 지퍼 파우치",
  "description": "지퍼를 단 딸기 원단 파우치",
  "startedAt": "2026-07-02",
  "completedAt": null,
  "retrospective": null
}
```

---

### Response

```json
{
  "id": 1,
  "title": "딸기 지퍼 파우치",
  "description": "지퍼를 단 딸기 원단 파우치",
  "status": "IN_PROGRESS",
  "startedAt": "2026-07-02",
  "completedAt": null,
  "retrospective": null,
  "updatedAt": "2026-07-02T11:00:00"
}
```

---

### Rules

- 현재 User 소유 Project만 수정할 수 있다.
    
- `ARCHIVED` 상태의 Project는 기본 정보 수정이 제한된다.
    
- `completedAt`은 `startedAt`보다 빠를 수 없다.
    

---

## 5.5 Project 삭제

### Endpoint

```http
DELETE /api/v1/projects/{projectId}
```

### Auth

Required

---

### Response

```json
{
  "message": "프로젝트가 삭제되었습니다."
}
```

---

### Rules

- Project 삭제 시 DailyLog, TimeEntry, ProjectFabric, Project Photo, DailyLog Photo도 함께 삭제된다.
    
- Fabric 자체는 삭제되지 않는다.
    

---

## 5.6 Project 상태 변경

### Endpoint

```http
PATCH /api/v1/projects/{projectId}/status
```

### Auth

Required

---

### Request

```json
{
  "status": "COMPLETED",
  "completedAt": "2026-07-20"
}
```

---

### Response

```json
{
  "id": 1,
  "status": "COMPLETED",
  "completedAt": "2026-07-20",
  "updatedAt": "2026-07-20T12:00:00"
}
```

---

# 6. Project Specification API

## 6.1 Project 설정 조회

### Endpoint

```http
GET /api/v1/projects/{projectId}/specification
```

### Auth

Required

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "patternMethod": "SELF_DRAFTED",
  "patternName": "기본 파우치 패턴",
  "finishedWidthCm": 20.0,
  "finishedHeightCm": 12.0,
  "finishedSizeMemo": "완성 후 약 20x12cm",
  "sewingMachineSettingMemo": "직선 2.5mm",
  "needleMemo": "11호 바늘",
  "threadMemo": "흰색 코아사",
  "memo": "얇은 원단이라 심지 필요"
}
```

---

## 6.2 Project 설정 수정

### Endpoint

```http
PUT /api/v1/projects/{projectId}/specification
```

### Auth

Required

---

### Request

```json
{
  "patternMethod": "SELF_DRAFTED",
  "patternName": "기본 파우치 패턴",
  "finishedWidthCm": 20.0,
  "finishedHeightCm": 12.0,
  "finishedSizeMemo": "완성 후 약 20x12cm",
  "sewingMachineSettingMemo": "직선 2.5mm",
  "needleMemo": "11호 바늘",
  "threadMemo": "흰색 코아사",
  "memo": "얇은 원단이라 심지 필요"
}
```

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "patternMethod": "SELF_DRAFTED",
  "patternName": "기본 파우치 패턴",
  "finishedWidthCm": 20.0,
  "finishedHeightCm": 12.0,
  "finishedSizeMemo": "완성 후 약 20x12cm",
  "sewingMachineSettingMemo": "직선 2.5mm",
  "needleMemo": "11호 바늘",
  "threadMemo": "흰색 코아사",
  "memo": "얇은 원단이라 심지 필요",
  "updatedAt": "2026-07-02T11:00:00"
}
```

---

# 7. Project Reference API

## 7.1 참고자료 등록

### Endpoint

```http
POST /api/v1/projects/{projectId}/references
```

### Auth

Required

---

### Request

```json
{
  "url": "https://example.com/tutorial",
  "title": "파우치 참고",
  "memo": "지퍼 처리 참고",
  "sortOrder": 0
}
```

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "url": "https://example.com/tutorial",
  "title": "파우치 참고",
  "memo": "지퍼 처리 참고",
  "sortOrder": 0,
  "createdAt": "2026-07-02T10:30:00"
}
```

---

## 7.2 참고자료 수정

### Endpoint

```http
PATCH /api/v1/projects/{projectId}/references/{referenceId}
```

### Auth

Required

---

### Request

```json
{
  "url": "https://example.com/tutorial",
  "title": "수정된 참고자료",
  "memo": "수정된 메모",
  "sortOrder": 1
}
```

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "url": "https://example.com/tutorial",
  "title": "수정된 참고자료",
  "memo": "수정된 메모",
  "sortOrder": 1,
  "updatedAt": "2026-07-02T11:00:00"
}
```

---

## 7.3 참고자료 삭제

### Endpoint

```http
DELETE /api/v1/projects/{projectId}/references/{referenceId}
```

### Auth

Required

---

### Response

```json
{
  "message": "참고자료가 삭제되었습니다."
}
```

---

# 8. DailyLog API

## 8.1 DailyLog 생성

### Endpoint

```http
POST /api/v1/projects/{projectId}/daily-logs
```

### Auth

Required

---

### Description

DailyLog를 생성한다.  
Request의 `status`가 `DRAFT`이면 임시저장으로 생성하고, `PUBLISHED`이면 정식 기록으로 생성한다.

---

### Request — DRAFT

```json
{
  "status": "DRAFT",
  "workedDate": null,
  "title": "안감 재단 중",
  "workTypes": ["CUTTING"],
  "timeEntries": [],
  "memo": "안감 재단하다가 중단"
}
```

---

### Request — PUBLISHED

```json
{
  "status": "PUBLISHED",
  "workedDate": "2026-07-02",
  "title": "파우치 재단과 재봉",
  "workTypes": ["CUTTING", "SEWING"],
  "timeEntries": [
    {
      "startedAt": "15:00",
      "endedAt": "15:40",
      "memo": "겉감 재단"
    },
    {
      "startedAt": "20:00",
      "endedAt": "21:10",
      "memo": "지퍼 달기"
    }
  ],
  "memo": "지퍼 부분이 어려웠다."
}
```

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "status": "PUBLISHED",
  "workedDate": "2026-07-02",
  "title": "파우치 재단과 재봉",
  "workTypes": ["CUTTING", "SEWING"],
  "durationMinutes": 110,
  "timeEntries": [
    {
      "id": 1,
      "startedAt": "15:00",
      "endedAt": "15:40",
      "durationMinutes": 40,
      "memo": "겉감 재단"
    },
    {
      "id": 2,
      "startedAt": "20:00",
      "endedAt": "21:10",
      "durationMinutes": 70,
      "memo": "지퍼 달기"
    }
  ],
  "memo": "지퍼 부분이 어려웠다.",
  "createdAt": "2026-07-02T21:30:00",
  "updatedAt": "2026-07-02T21:30:00"
}
```

---

### Rules

- DRAFT는 `workedDate`와 `timeEntries` 없이 저장할 수 있다.
    
- PUBLISHED는 `workedDate`가 필수이다.
    
- PUBLISHED는 `timeEntries`가 1개 이상 필요하다.
    
- `durationMinutes`는 TimeEntry 합산값으로 계산한다.
    
- Project가 `ARCHIVED` 상태이면 DailyLog를 생성할 수 없다.
    

---

## 8.2 Project별 DailyLog 목록 조회

### Endpoint

```http
GET /api/v1/projects/{projectId}/daily-logs
```

### Auth

Required

---

### Query Parameters

|이름|필수|설명|
|---|--:|---|
|status|N|DRAFT 또는 PUBLISHED|
|page|N|페이지 번호|
|size|N|페이지 크기|

---

### Response

```json
{
  "items": [
    {
      "id": 1,
      "projectId": 1,
      "status": "PUBLISHED",
      "workedDate": "2026-07-02",
      "title": "파우치 재단과 재봉",
      "workTypes": ["CUTTING", "SEWING"],
      "durationMinutes": 110,
      "thumbnailUrl": "https://cdn.sorosoro.com/...",
      "createdAt": "2026-07-02T21:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "hasNext": false
}
```

---

## 8.3 날짜별 DailyLog 목록 조회

### Endpoint

```http
GET /api/v1/daily-logs
```

### Auth

Required

---

### Query Parameters

|이름|필수|설명|
|---|--:|---|
|date|N|특정 날짜|
|startDate|N|기간 시작일|
|endDate|N|기간 종료일|
|status|N|DRAFT 또는 PUBLISHED|
|page|N|페이지 번호|
|size|N|페이지 크기|

---

### Response

```json
{
  "items": [
    {
      "id": 1,
      "projectId": 1,
      "projectTitle": "딸기 파우치",
      "status": "PUBLISHED",
      "workedDate": "2026-07-02",
      "title": "파우치 재단과 재봉",
      "workTypes": ["CUTTING", "SEWING"],
      "durationMinutes": 110,
      "thumbnailUrl": "https://cdn.sorosoro.com/...",
      "createdAt": "2026-07-02T21:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "hasNext": false
}
```

---

## 8.4 DailyLog 상세 조회

### Endpoint

```http
GET /api/v1/daily-logs/{dailyLogId}
```

### Auth

Required

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "projectTitle": "딸기 파우치",
  "status": "PUBLISHED",
  "workedDate": "2026-07-02",
  "title": "파우치 재단과 재봉",
  "workTypes": ["CUTTING", "SEWING"],
  "durationMinutes": 110,
  "timeEntries": [
    {
      "id": 1,
      "startedAt": "15:00",
      "endedAt": "15:40",
      "durationMinutes": 40,
      "memo": "겉감 재단"
    }
  ],
  "memo": "지퍼 부분이 어려웠다.",
  "photos": [
    {
      "id": 1,
      "status": "READY",
      "isThumbnail": true,
      "thumbnailUrl": "https://cdn.sorosoro.com/...",
      "mediumUrl": "https://cdn.sorosoro.com/...",
      "sortOrder": 0
    }
  ],
  "createdAt": "2026-07-02T21:30:00",
  "updatedAt": "2026-07-02T21:30:00"
}
```

---

## 8.5 DailyLog 수정

### Endpoint

```http
PUT /api/v1/daily-logs/{dailyLogId}
```

### Auth

Required

---

### Request

```json
{
  "workedDate": "2026-07-02",
  "title": "파우치 재단, 재봉, 마감",
  "workTypes": ["CUTTING", "SEWING", "FINISHING"],
  "timeEntries": [
    {
      "startedAt": "15:00",
      "endedAt": "15:40",
      "memo": "겉감 재단"
    },
    {
      "startedAt": "20:00",
      "endedAt": "21:30",
      "memo": "재봉과 마감"
    }
  ],
  "memo": "마감까지 완료했다."
}
```

---

### Response

```json
{
  "id": 1,
  "status": "PUBLISHED",
  "workedDate": "2026-07-02",
  "title": "파우치 재단, 재봉, 마감",
  "workTypes": ["CUTTING", "SEWING", "FINISHING"],
  "durationMinutes": 130,
  "timeEntries": [
    {
      "id": 3,
      "startedAt": "15:00",
      "endedAt": "15:40",
      "durationMinutes": 40,
      "memo": "겉감 재단"
    },
    {
      "id": 4,
      "startedAt": "20:00",
      "endedAt": "21:30",
      "durationMinutes": 90,
      "memo": "재봉과 마감"
    }
  ],
  "memo": "마감까지 완료했다.",
  "updatedAt": "2026-07-02T22:00:00"
}
```

---

### Rules

- DRAFT와 PUBLISHED 모두 수정할 수 있다.
    
- PUBLISHED 수정 시 `workedDate`와 `timeEntries` 1개 이상 조건을 유지해야 한다.
    
- TimeEntry는 개별 수정하지 않고 전체 교체한다.
    
- Project가 `ARCHIVED` 상태이면 수정할 수 없다.
    

---

## 8.6 DailyLog 발행

### Endpoint

```http
POST /api/v1/daily-logs/{dailyLogId}/publish
```

### Auth

Required

---

### Request

```json
{
  "workedDate": "2026-07-02",
  "timeEntries": [
    {
      "startedAt": "20:00",
      "endedAt": "21:00",
      "memo": "재봉"
    }
  ]
}
```

---

### Response

```json
{
  "id": 1,
  "status": "PUBLISHED",
  "workedDate": "2026-07-02",
  "durationMinutes": 60,
  "updatedAt": "2026-07-02T22:00:00"
}
```

---

### Rules

- DRAFT만 발행할 수 있다.
    
- PUBLISHED → DRAFT 전환은 제공하지 않는다.
    
- 발행 시 `workedDate`와 `timeEntries` 1개 이상이 필요하다.
    

---

## 8.7 DailyLog 삭제

### Endpoint

```http
DELETE /api/v1/daily-logs/{dailyLogId}
```

### Auth

Required

---

### Response

```json
{
  "message": "작업 일지가 삭제되었습니다."
}
```

---

# 9. Fabric API

## 9.1 Fabric 등록

### Endpoint

```http
POST /api/v1/fabrics
```

### Auth

Required

---

### Request

```json
{
  "name": "딸기 면 원단",
  "productName": "딸기 패턴 20수 면 원단",
  "productCode": "F-2031",
  "productUrl": "https://shop.example.com/products/2031",
  "storeName": "원단마켓",
  "purchasedAt": "2026-07-01",
  "purchasePrice": 12000,
  "color": "핑크",
  "size": "1마",
  "width": "110cm",
  "materialComposition": "면 100%",
  "memo": "얇고 부드러움. 심지 필요.",
  "rating": 4,
  "repurchaseIntention": "YES"
}
```

---

### Response

```json
{
  "id": 1,
  "name": "딸기 면 원단",
  "productName": "딸기 패턴 20수 면 원단",
  "productCode": "F-2031",
  "productUrl": "https://shop.example.com/products/2031",
  "storeName": "원단마켓",
  "purchasedAt": "2026-07-01",
  "purchasePrice": 12000,
  "color": "핑크",
  "size": "1마",
  "width": "110cm",
  "materialComposition": "면 100%",
  "memo": "얇고 부드러움. 심지 필요.",
  "rating": 4,
  "repurchaseIntention": "YES",
  "thumbnailUrl": null,
  "createdAt": "2026-07-02T10:30:00",
  "updatedAt": "2026-07-02T10:30:00"
}
```

---

### Rules

- `name`은 필수이다.
    
- Fabric은 Project 없이 생성할 수 있다.
    
- `rating`은 1~5 범위이다.
    
- `repurchaseIntention` 기본값은 `UNKNOWN`이다.
    

---

## 9.2 Fabric 목록 조회

### Endpoint

```http
GET /api/v1/fabrics
```

### Auth

Required

---

### Query Parameters

|이름|필수|설명|
|---|--:|---|
|keyword|N|원단명, 상품명, 품번 검색|
|storeName|N|구매처 필터|
|repurchaseIntention|N|재구매 의사 필터|
|page|N|페이지 번호|
|size|N|페이지 크기|

---

### Response

```json
{
  "items": [
    {
      "id": 1,
      "name": "딸기 면 원단",
      "productName": "딸기 패턴 20수 면 원단",
      "productCode": "F-2031",
      "storeName": "원단마켓",
      "color": "핑크",
      "materialComposition": "면 100%",
      "repurchaseIntention": "YES",
      "thumbnailUrl": "https://cdn.sorosoro.com/...",
      "createdAt": "2026-07-02T10:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "hasNext": false
}
```

---

## 9.3 Fabric 상세 조회

### Endpoint

```http
GET /api/v1/fabrics/{fabricId}
```

### Auth

Required

---

### Response

```json
{
  "id": 1,
  "name": "딸기 면 원단",
  "productName": "딸기 패턴 20수 면 원단",
  "productCode": "F-2031",
  "productUrl": "https://shop.example.com/products/2031",
  "storeName": "원단마켓",
  "purchasedAt": "2026-07-01",
  "purchasePrice": 12000,
  "color": "핑크",
  "size": "1마",
  "width": "110cm",
  "materialComposition": "면 100%",
  "memo": "얇고 부드러움. 심지 필요.",
  "rating": 4,
  "repurchaseIntention": "YES",
  "photos": [
    {
      "id": 10,
      "status": "READY",
      "thumbnailUrl": "https://cdn.sorosoro.com/...",
      "mediumUrl": "https://cdn.sorosoro.com/...",
      "sortOrder": 0
    }
  ],
  "usedProjects": [
    {
      "projectFabricId": 1,
      "projectId": 1,
      "projectTitle": "딸기 파우치",
      "memo": "겉감으로 사용"
    }
  ],
  "createdAt": "2026-07-02T10:30:00",
  "updatedAt": "2026-07-02T10:30:00"
}
```

---

## 9.4 Fabric 수정

### Endpoint

```http
PATCH /api/v1/fabrics/{fabricId}
```

### Auth

Required

---

### Request

```json
{
  "name": "딸기 면 원단",
  "productName": "딸기 패턴 20수 면 원단",
  "productCode": "F-2031",
  "productUrl": "https://shop.example.com/products/2031",
  "storeName": "원단마켓",
  "purchasedAt": "2026-07-01",
  "purchasePrice": 12000,
  "color": "핑크",
  "size": "1마",
  "width": "110cm",
  "materialComposition": "면 100%",
  "memo": "얇지만 색감이 예쁘다.",
  "rating": 5,
  "repurchaseIntention": "YES"
}
```

---

### Response

```json
{
  "id": 1,
  "name": "딸기 면 원단",
  "rating": 5,
  "repurchaseIntention": "YES",
  "updatedAt": "2026-07-02T11:00:00"
}
```

---

## 9.5 Fabric 삭제

### Endpoint

```http
DELETE /api/v1/fabrics/{fabricId}
```

### Auth

Required

---

### Response

```json
{
  "message": "원단이 삭제되었습니다."
}
```

---

### Rules

- Fabric 삭제 시 Project는 삭제되지 않는다.
    
- Fabric 삭제 시 ProjectFabric 연결은 삭제된다.
    
- Fabric Photo도 삭제 대상이다.
    

---

# 10. Project-Fabric API

## 10.1 Project에 Fabric 연결

### Endpoint

```http
POST /api/v1/projects/{projectId}/fabrics
```

### Auth

Required

---

### Request

```json
{
  "fabricId": 1,
  "memo": "겉감으로 사용"
}
```

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "fabricId": 1,
  "fabricName": "딸기 면 원단",
  "memo": "겉감으로 사용",
  "createdAt": "2026-07-02T10:30:00"
}
```

---

### Rules

- Project와 Fabric이 모두 현재 User 소유여야 한다.
    
- 동일 Project에 동일 Fabric은 중복 연결할 수 없다.
    
- Project가 `ARCHIVED` 상태이면 연결할 수 없다.
    

---

## 10.2 Project-Fabric 연결 메모 수정

### Endpoint

```http
PATCH /api/v1/projects/{projectId}/fabrics/{projectFabricId}
```

### Auth

Required

---

### Request

```json
{
  "memo": "겉감으로 사용. 다음에는 심지를 붙이는 것이 좋을 듯."
}
```

---

### Response

```json
{
  "id": 1,
  "projectId": 1,
  "fabricId": 1,
  "memo": "겉감으로 사용. 다음에는 심지를 붙이는 것이 좋을 듯.",
  "updatedAt": "2026-07-02T11:00:00"
}
```

---

## 10.3 Project-Fabric 연결 삭제

### Endpoint

```http
DELETE /api/v1/projects/{projectId}/fabrics/{projectFabricId}
```

### Auth

Required

---

### Response

```json
{
  "message": "프로젝트와 원단의 연결이 삭제되었습니다."
}
```

---

# 11. Photo API

## 11.1 Presigned URL 발급

### Endpoint

```http
POST /api/v1/photos/presigned-url
```

### Auth

Required

---

### Request

```json
{
  "ownerType": "DAILY_LOG",
  "ownerId": 1,
  "fileName": "sewing-process.jpg",
  "contentType": "image/jpeg",
  "sizeBytes": 2481024
}
```

---

### Response

```json
{
  "photoId": 1,
  "uploadUrl": "https://s3-presigned-url...",
  "originalKey": "users/1/photos/1/original",
  "expiresIn": 300
}
```

---

### Rules

- ownerType은 `PROJECT`, `DAILY_LOG`, `FABRIC` 중 하나이다.
    
- owner가 실제 존재해야 한다.
    
- owner가 현재 User 소유여야 한다.
    
- owner별 사진 수 제한을 초과할 수 없다.
    
- 허용된 contentType만 업로드할 수 있다.
    
- 파일 크기 제한을 초과할 수 없다.
    
- Photo는 `UPLOADING` 상태로 생성된다.
    

---

## 11.2 업로드 완료 처리

### Endpoint

```http
POST /api/v1/photos/{photoId}/complete-upload
```

### Auth

Required

---

### Request

```json
{}
```

---

### Response

```json
{
  "id": 1,
  "status": "PROCESSING",
  "message": "이미지 처리가 시작되었습니다."
}
```

---

### Rules

- Photo가 현재 User 소유여야 한다.
    
- Photo 상태가 `UPLOADING`이어야 한다.
    
- S3 originalKey object가 존재해야 한다.
    
- 성공 시 Photo 상태는 `PROCESSING`이 된다.
    
- Queue에 photoId를 발행한다.
    

---

## 11.3 Photo 목록 조회

### Endpoint

```http
GET /api/v1/photos
```

### Auth

Required

---

### Query Parameters

|이름|필수|설명|
|---|--:|---|
|ownerType|Y|PROJECT, DAILY_LOG, FABRIC|
|ownerId|Y|owner ID|

---

### Response

```json
{
  "items": [
    {
      "id": 1,
      "ownerType": "DAILY_LOG",
      "ownerId": 1,
      "status": "READY",
      "isThumbnail": true,
      "thumbnailUrl": "https://cdn.sorosoro.com/...",
      "mediumUrl": "https://cdn.sorosoro.com/...",
      "originalUrl": "https://cdn.sorosoro.com/...",
      "sortOrder": 0,
      "createdAt": "2026-07-02T10:30:00"
    }
  ]
}
```

---

## 11.4 대표 사진 설정

### Endpoint

```http
PATCH /api/v1/photos/{photoId}/thumbnail
```

### Auth

Required

---

### Request

```json
{
  "isThumbnail": true
}
```

---

### Response

```json
{
  "id": 1,
  "isThumbnail": true,
  "updatedAt": "2026-07-02T11:00:00"
}
```

---

### Rules

- Project와 DailyLog Photo만 대표 사진으로 설정할 수 있다.
    
- Fabric Photo는 대표 사진 설정을 지원하지 않는다.
    
- READY 상태의 Photo만 대표 사진으로 설정할 수 있다.
    
- 같은 owner의 대표 사진은 1장만 가능하다.
    

---

## 11.5 Photo 정렬 순서 수정

### Endpoint

```http
PATCH /api/v1/photos/sort-order
```

### Auth

Required

---

### Request

```json
{
  "ownerType": "PROJECT",
  "ownerId": 1,
  "photoOrders": [
    {
      "photoId": 1,
      "sortOrder": 0
    },
    {
      "photoId": 2,
      "sortOrder": 1
    }
  ]
}
```

---

### Response

```json
{
  "message": "사진 정렬 순서가 수정되었습니다."
}
```

---

## 11.6 Photo 삭제

### Endpoint

```http
DELETE /api/v1/photos/{photoId}
```

### Auth

Required

---

### Response

```json
{
  "message": "사진이 삭제되었습니다."
}
```

---

### Rules

- Photo가 현재 User 소유여야 한다.
    
- Photo 삭제 시 S3 object도 삭제 대상이다.
    
- Project가 ARCHIVED 상태이면 해당 Project/ DailyLog Photo 삭제가 제한된다.
    

---

# 12. Calendar API

## 12.1 월별 Calendar 조회

### Endpoint

```http
GET /api/v1/calendar
```

### Auth

Required

---

### Query Parameters

|이름|필수|설명|
|---|--:|---|
|year|Y|조회 연도|
|month|Y|조회 월|

---

### Response

```json
{
  "year": 2026,
  "month": 7,
  "days": [
    {
      "date": "2026-07-01",
      "dailyLogCount": 0,
      "totalDurationMinutes": 0,
      "level": 0
    },
    {
      "date": "2026-07-02",
      "dailyLogCount": 2,
      "totalDurationMinutes": 180,
      "level": 3
    }
  ]
}
```

---

### Rules

- PUBLISHED DailyLog만 포함한다.
    
- DRAFT DailyLog는 제외한다.
    
- level은 totalDurationMinutes 기준으로 계산한다.
    

---

## 12.2 특정 날짜 작업 요약 조회

### Endpoint

```http
GET /api/v1/calendar/days/{date}
```

### Auth

Required

---

### Example

```http
GET /api/v1/calendar/days/2026-07-02
```

---

### Response

```json
{
  "date": "2026-07-02",
  "dailyLogCount": 2,
  "totalDurationMinutes": 180,
  "level": 3,
  "dailyLogs": [
    {
      "id": 1,
      "projectId": 1,
      "projectTitle": "딸기 파우치",
      "title": "파우치 재단",
      "workTypes": ["CUTTING"],
      "durationMinutes": 60,
      "thumbnailUrl": "https://cdn.sorosoro.com/..."
    },
    {
      "id": 2,
      "projectId": 2,
      "projectTitle": "카드지갑",
      "title": "재봉",
      "workTypes": ["SEWING"],
      "durationMinutes": 120,
      "thumbnailUrl": null
    }
  ]
}
```

---

## 12.3 Contribution 조회

### Endpoint

```http
GET /api/v1/contributions
```

### Auth

Required

---

### Query Parameters

|이름|필수|설명|
|---|--:|---|
|year|Y|조회 연도|

---

### Response

```json
{
  "year": 2026,
  "totalDurationMinutes": 3200,
  "activeDays": 42,
  "dailyLogCount": 58,
  "days": [
    {
      "date": "2026-01-01",
      "dailyLogCount": 0,
      "totalDurationMinutes": 0,
      "level": 0
    },
    {
      "date": "2026-07-02",
      "dailyLogCount": 2,
      "totalDurationMinutes": 180,
      "level": 3
    }
  ]
}
```

---

## 12.4 Level 기준

|Level|기준|
|---|---|
|0|0분|
|1|1분 이상 30분 이하|
|2|31분 이상 120분 이하|
|3|121분 이상 240분 이하|
|4|241분 이상|

---

# 13. Enum 정리

## 13.1 ProjectStatus

```text
IN_PROGRESS
ON_HOLD
COMPLETED
ARCHIVED
```

---

## 13.2 PatternMethod

```text
SELF_DRAFTED
COPIED
MODIFIED
```

---

## 13.3 DailyLogStatus

```text
DRAFT
PUBLISHED
```

---

## 13.4 WorkType

```text
PATTERN_DRAFTING
CUTTING
INTERFACING
SEWING
PRESSING
FITTING
FIXING
FINISHING
ETC
```

---

## 13.5 RepurchaseIntention

```text
YES
NO
UNKNOWN
```

---

## 13.6 PhotoOwnerType

```text
PROJECT
DAILY_LOG
FABRIC
```

---

## 13.7 PhotoStatus

```text
UPLOADING
PROCESSING
READY
FAILED
```

---

# 14. 주요 에러 코드

## 14.1 Auth

|코드|메시지|Status|
|---|---|--:|
|AUTH-001|인증이 필요합니다.|401|
|AUTH-002|유효하지 않은 토큰입니다.|401|
|AUTH-003|Refresh Token이 유효하지 않습니다.|401|
|AUTH-004|카카오 로그인에 실패했습니다.|400|

---

## 14.2 Project

|코드|메시지|Status|
|---|---|--:|
|PRJ-001|프로젝트명을 입력해주세요.|400|
|PRJ-002|프로젝트를 찾을 수 없습니다.|404|
|PRJ-003|보관된 프로젝트는 수정할 수 없습니다.|409|
|PRJ-004|완료일은 시작일보다 빠를 수 없습니다.|400|

---

## 14.3 DailyLog

|코드|메시지|Status|
|---|---|--:|
|DL-001|작업 날짜를 입력해주세요.|400|
|DL-002|정식 기록에는 작업 시간이 필요합니다.|400|
|DL-003|발행된 기록은 임시저장으로 되돌릴 수 없습니다.|409|
|DL-004|작업 일지를 찾을 수 없습니다.|404|
|DL-005|종료 시각은 시작 시각보다 늦어야 합니다.|400|

---

## 14.4 Fabric

|코드|메시지|Status|
|---|---|--:|
|FAB-001|원단명을 입력해주세요.|400|
|FAB-002|원단을 찾을 수 없습니다.|404|
|FAB-003|평점은 1점에서 5점 사이여야 합니다.|400|
|FAB-004|이미 연결된 원단입니다.|409|

---

## 14.5 Photo

|코드|메시지|Status|
|---|---|--:|
|PHO-001|사진을 업로드할 대상을 찾을 수 없습니다.|404|
|PHO-002|사진 수 제한을 초과했습니다.|409|
|PHO-003|업로드 중 상태의 사진만 완료 처리할 수 있습니다.|409|
|PHO-004|처리 완료된 사진만 대표 사진으로 설정할 수 있습니다.|409|
|PHO-005|Fabric 사진은 대표 사진으로 설정할 수 없습니다.|409|
|PHO-006|지원하지 않는 이미지 형식입니다.|400|
|PHO-007|이미지 파일 크기를 초과했습니다.|400|

---

# 15. 이미지 업로드 전체 시퀀스

## 15.1 Presigned URL 발급

```text
Client
→ POST /api/v1/photos/presigned-url
→ Server
   - 인증 확인
   - owner 확인
   - 사진 수 제한 확인
   - Photo 생성, UPLOADING
   - Presigned URL 발급
→ Client
```

---

## 15.2 S3 직접 업로드

```text
Client
→ S3 Presigned URL
→ 원본 이미지 업로드
```

---

## 15.3 업로드 완료 처리

```text
Client
→ POST /api/v1/photos/{photoId}/complete-upload
→ Server
   - Photo 확인
   - S3 object 존재 확인
   - Photo PROCESSING 변경
   - Queue 발행
→ Client
```

---

## 15.4 Worker 처리

```text
Worker
→ Queue에서 photoId 수신
→ S3 original 다운로드
→ medium.webp 생성
→ thumbnail.webp 생성
→ S3 업로드
→ Photo READY 변경
```

---

# 16. API 요약

## 16.1 Auth/User

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/auth/kakao/login|카카오 로그인|
|POST|/api/v1/auth/reissue|Access Token 재발급|
|POST|/api/v1/auth/logout|로그아웃|
|GET|/api/v1/users/me|내 정보 조회|
|DELETE|/api/v1/users/me|회원 탈퇴|

---

## 16.2 Project

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/projects|Project 생성|
|GET|/api/v1/projects|Project 목록 조회|
|GET|/api/v1/projects/{projectId}|Project 상세 조회|
|PATCH|/api/v1/projects/{projectId}|Project 수정|
|DELETE|/api/v1/projects/{projectId}|Project 삭제|
|PATCH|/api/v1/projects/{projectId}/status|Project 상태 변경|

---

## 16.3 Project Specification / Reference

|Method|Endpoint|설명|
|---|---|---|
|GET|/api/v1/projects/{projectId}/specification|Project 설정 조회|
|PUT|/api/v1/projects/{projectId}/specification|Project 설정 수정|
|POST|/api/v1/projects/{projectId}/references|참고자료 등록|
|PATCH|/api/v1/projects/{projectId}/references/{referenceId}|참고자료 수정|
|DELETE|/api/v1/projects/{projectId}/references/{referenceId}|참고자료 삭제|

---

## 16.4 DailyLog

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/projects/{projectId}/daily-logs|DailyLog 생성|
|GET|/api/v1/projects/{projectId}/daily-logs|Project별 DailyLog 목록|
|GET|/api/v1/daily-logs|날짜별 DailyLog 목록|
|GET|/api/v1/daily-logs/{dailyLogId}|DailyLog 상세|
|PUT|/api/v1/daily-logs/{dailyLogId}|DailyLog 수정|
|POST|/api/v1/daily-logs/{dailyLogId}/publish|DailyLog 발행|
|DELETE|/api/v1/daily-logs/{dailyLogId}|DailyLog 삭제|

---

## 16.5 Fabric / ProjectFabric

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/fabrics|Fabric 등록|
|GET|/api/v1/fabrics|Fabric 목록|
|GET|/api/v1/fabrics/{fabricId}|Fabric 상세|
|PATCH|/api/v1/fabrics/{fabricId}|Fabric 수정|
|DELETE|/api/v1/fabrics/{fabricId}|Fabric 삭제|
|POST|/api/v1/projects/{projectId}/fabrics|Project에 Fabric 연결|
|PATCH|/api/v1/projects/{projectId}/fabrics/{projectFabricId}|연결 메모 수정|
|DELETE|/api/v1/projects/{projectId}/fabrics/{projectFabricId}|연결 삭제|

---

## 16.6 Photo

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/photos/presigned-url|Presigned URL 발급|
|POST|/api/v1/photos/{photoId}/complete-upload|업로드 완료 처리|
|GET|/api/v1/photos|Photo 목록 조회|
|PATCH|/api/v1/photos/{photoId}/thumbnail|대표 사진 설정|
|PATCH|/api/v1/photos/sort-order|Photo 정렬 순서 수정|
|DELETE|/api/v1/photos/{photoId}|Photo 삭제|

---

## 16.7 Calendar / Contribution

|Method|Endpoint|설명|
|---|---|---|
|GET|/api/v1/calendar|월별 Calendar 조회|
|GET|/api/v1/calendar/days/{date}|특정 날짜 작업 요약|
|GET|/api/v1/contributions|연도별 Contribution 조회|

---

# 17. API 문서 요약

소로소로 MVP API는 다음 원칙을 따른다.

- 모든 주요 API는 `/api/v1` 하위에 둔다.
    
- 보호 API는 JWT Access Token을 요구한다.
    
- Project는 재봉 작품을 관리한다.
    
- DailyLog는 Project의 날짜별 작업 기록을 관리한다.
    
- DRAFT DailyLog는 임시저장으로 통계에 포함하지 않는다.
    
- PUBLISHED DailyLog만 Calendar와 Contribution에 반영한다.
    
- DailyLog 작업 시간은 TimeEntry 합산으로 계산한다.
    
- Fabric은 Project와 독립적으로 관리된다.
    
- Project와 Fabric은 ProjectFabric으로 연결된다.
    
- Photo는 Project, DailyLog, Fabric에서 공통으로 사용한다.
    
- 이미지는 Presigned URL로 S3에 직접 업로드한다.
    
- 이미지 리사이징은 Worker가 비동기로 처리한다.
    
- Calendar와 Contribution은 별도 저장 없이 조회 시 계산한다.