# 10. Portfolio README — 소로소로(SOROSORO)

> 하루하루 재봉 과정을 기록하는 개인 재봉일기 서비스

---

# 소로소로(SOROSORO)

소로소로는 개인 재봉 작업을 프로젝트 단위로 관리하고, 날짜별 작업 일지와 원단 정보를 함께 기록할 수 있는 재봉 기록 서비스입니다.

재봉 작업은 결과물만 남기기보다, 어떤 원단을 사용했고 어떤 과정을 거쳐 완성했는지가 중요합니다.  
하지만 실제 작업 과정은 사진, 메모, 쇼핑몰 링크, 원단 정보, 작업 시간이 여러 곳에 흩어지기 쉽습니다.

소로소로는 이러한 기록을 하나의 서비스 안에서 관리할 수 있도록 설계했습니다.

---

# 1. 프로젝트 개요

## 1.1 한 줄 소개

> 하루하루 재봉 과정을 기록하는 개인 재봉일기 서비스

---

## 1.2 개발 배경

재봉 작업을 하다 보면 다음과 같은 불편함이 발생합니다.

- 어떤 원단을 어디서 샀는지 다시 찾기 어렵다.
    
- 특정 작품에 어떤 원단을 사용했는지 기억하기 어렵다.
    
- 작업 과정 사진과 메모가 여러 곳에 흩어진다.
    
- 하루에 얼마나 작업했는지 기록하기 어렵다.
    
- 완성 작품만 남고 제작 과정은 사라진다.
    
- 같은 원단을 다른 작품에 재사용했는지 확인하기 어렵다.
    

소로소로는 이러한 문제를 해결하기 위해, 작품·작업일지·원단·사진을 연결해 기록할 수 있는 개인용 재봉 기록 서비스로 기획했습니다.

---

## 1.3 핵심 목표

소로소로의 목표는 다음과 같습니다.

- 재봉 작품을 Project 단위로 관리한다.
    
- 날짜별 작업 과정을 DailyLog로 기록한다.
    
- 원단 구매 정보를 Fabric으로 독립 관리한다.
    
- Project와 Fabric을 연결해 어떤 작품에 어떤 원단을 사용했는지 추적한다.
    
- 사진을 중심으로 제작 과정을 남긴다.
    
- Calendar와 Contribution으로 작업 습관을 시각화한다.
    
- 이미지 업로드와 리사이징을 안정적으로 처리한다.
    

---

# 2. 주요 기능

## 2.1 카카오 로그인

사용자는 카카오 계정으로 로그인할 수 있습니다.

- Kakao OAuth 로그인
    
- JWT Access Token 발급
    
- Refresh Token 기반 재발급
    
- 로그아웃
    
- 회원 탈퇴
    

---

## 2.2 Project 관리

Project는 하나의 재봉 작품을 의미합니다.

사용자는 Project를 생성하고, 진행 상태를 관리할 수 있습니다.

지원 상태는 다음과 같습니다.

|상태|설명|
|---|---|
|IN_PROGRESS|진행 중|
|ON_HOLD|보류|
|COMPLETED|완료|
|ARCHIVED|보관|

Project에는 다음 정보를 기록할 수 있습니다.

- 작품명
    
- 설명
    
- 시작일
    
- 완료일
    
- 회고
    
- 제작 설정
    
- 참고 URL
    
- 연결된 원단
    
- 대표 사진
    

---

## 2.3 DailyLog 관리

DailyLog는 특정 날짜의 작업 기록입니다.

사용자는 작업 내용을 임시저장하거나 정식 기록으로 발행할 수 있습니다.

DailyLog 상태는 다음과 같습니다.

|상태|설명|
|---|---|
|DRAFT|임시저장|
|PUBLISHED|정식 기록|

DRAFT는 작성 중인 기록으로, 통계에 포함하지 않습니다.  
PUBLISHED는 정식 기록으로, Calendar와 Contribution에 반영됩니다.

DailyLog에는 다음 정보를 기록할 수 있습니다.

- 작업 날짜
    
- 작업 제목
    
- 작업 종류
    
- 작업 시간 구간
    
- 작업 메모
    
- 작업 사진
    

---

## 2.4 TimeEntry 기반 작업 시간 계산

DailyLog의 작업 시간은 사용자가 총합을 직접 입력하지 않습니다.

여러 작업 시간 구간인 `DailyLogTimeEntry`를 입력하면, 시스템이 자동으로 총 작업 시간을 계산합니다.

예시는 다음과 같습니다.

```text
15:00 ~ 15:40 재단
20:00 ~ 21:10 재봉
22:30 ~ 23:00 마감
```

위 기록은 다음처럼 계산됩니다.

```text
40분 + 70분 + 30분 = 140분
```

이 합산값은 다음 기능에 사용됩니다.

- Project 총 작업 시간
    
- Calendar 날짜별 작업 시간
    
- Contribution 재봉 잔디
    

---

## 2.5 Fabric 관리

Fabric은 원단 구매 기록입니다.

Fabric은 Project와 독립적으로 존재할 수 있습니다.

즉, 사용자는 아직 어떤 작품에 사용할지 정하지 않은 원단도 먼저 등록할 수 있습니다.

Fabric에는 다음 정보를 기록할 수 있습니다.

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
    
- 원단 사진
    

---

## 2.6 Project-Fabric 연결

Project와 Fabric은 다대다 관계입니다.

하나의 Project는 여러 Fabric을 사용할 수 있고, 하나의 Fabric은 여러 Project에 사용될 수 있습니다.

이를 위해 `ProjectFabric` 연결 엔티티를 두었습니다.

연결 엔티티를 사용한 이유는 다음과 같습니다.

- 동일 Fabric을 여러 Project에 재사용할 수 있다.
    
- 하나의 Project에 여러 Fabric을 연결할 수 있다.
    
- 연결 관계에 메모를 남길 수 있다.
    
- 동일 Project-Fabric 중복 연결을 막을 수 있다.
    
- 향후 사용량, 용도, 정렬 등으로 확장할 수 있다.
    

---

## 2.7 Photo 관리

Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 엔티티입니다.

각 도메인마다 별도 이미지 테이블을 만들지 않고, `ownerType`과 `ownerId`로 연결 대상을 구분합니다.

지원 ownerType은 다음과 같습니다.

|ownerType|설명|
|---|---|
|PROJECT|Project 사진|
|DAILY_LOG|DailyLog 사진|
|FABRIC|Fabric 사진|

사진 수 제한은 다음과 같습니다.

|대상|최대 사진 수|
|---|--:|
|Project|15장|
|DailyLog|15장|
|Fabric|2장|

Project와 DailyLog는 대표 사진을 1장 설정할 수 있습니다.  
Fabric은 별도 대표 사진 설정 없이 첫 번째 사진을 대표 이미지처럼 사용합니다.

---

## 2.8 이미지 업로드

이미지 업로드는 Presigned URL 기반 S3 직접 업로드 방식을 사용합니다.

업로드 흐름은 다음과 같습니다.

```text
Client
→ Server: Presigned URL 요청
→ Server: Photo 생성, UPLOADING 상태
→ Client
→ S3: 원본 이미지 직접 업로드
→ Client
→ Server: 업로드 완료 요청
→ Server: Photo PROCESSING 상태 변경
→ Queue 발행
→ Worker: 이미지 리사이징
→ Photo READY 상태 변경
```

이 구조를 선택한 이유는 다음과 같습니다.

- 이미지 파일이 백엔드 서버를 거치지 않는다.
    
- 서버의 네트워크와 메모리 부담을 줄일 수 있다.
    
- 업로드 권한은 서버가 통제할 수 있다.
    
- S3와 CloudFront 기반 이미지 제공 구조와 잘 맞는다.
    
- 이미지 리사이징 Worker와 자연스럽게 연결된다.
    

---

## 2.9 이미지 리사이징 Worker

업로드된 원본 이미지는 Worker가 비동기로 처리합니다.

Worker는 원본 이미지를 다운로드한 뒤 다음 이미지를 생성합니다.

|이미지|용도|
|---|---|
|Original|원본 이미지|
|Medium|상세 화면|
|Thumbnail|목록, 카드, 캘린더|

Photo 상태는 다음 흐름을 따릅니다.

```text
UPLOADING
→ PROCESSING
→ READY
```

실패 시 다음 상태가 됩니다.

```text
PROCESSING
→ FAILED
```

---

## 2.10 Calendar

Calendar는 월별 작업 기록을 보여줍니다.

Calendar는 별도 테이블에 저장하지 않고, PUBLISHED DailyLog를 조회 시점에 집계합니다.

조회 기준은 다음과 같습니다.

```text
status = PUBLISHED
workedDate between monthStart and monthEnd
```

날짜별 응답에는 다음 정보가 포함됩니다.

- 날짜
    
- DailyLog 개수
    
- 총 작업 시간
    
- Level
    

---

## 2.11 Contribution

Contribution은 연도별 재봉 잔디입니다.

GitHub Contribution처럼 날짜별 작업 시간을 단계로 표현합니다.

Level 기준은 다음과 같습니다.

|Level|기준|
|---|---|
|0|0분|
|1|1분 이상 30분 이하|
|2|31분 이상 120분 이하|
|3|121분 이상 240분 이하|
|4|241분 이상|

Contribution 역시 별도 저장하지 않고, PUBLISHED DailyLog를 기준으로 조회 시 계산합니다.

---

# 3. 기술 스택

## 3.1 Backend

|영역|기술|
|---|---|
|Language|Java 17|
|Framework|Spring Boot|
|Security|Spring Security|
|Authentication|JWT, Refresh Token|
|ORM|Spring Data JPA|
|Query|QueryDSL|
|Migration|Flyway|
|Test|JUnit 5|
|API Docs|Springdoc OpenAPI|

---

## 3.2 Database / Infra

|영역|기술|
|---|---|
|Database|PostgreSQL|
|Storage|AWS S3|
|CDN|CloudFront|
|Queue|Redis Queue|
|Async Processing|Worker|
|Deployment|Docker, Docker Compose|
|Server|AWS EC2|
|Reverse Proxy|Nginx|
|CI/CD|GitHub Actions|

---

# 4. 아키텍처

## 4.1 전체 구조

```text
Client
  |
  | HTTPS
  v
Nginx on EC2
  |
  | Reverse Proxy
  v
Spring Boot API Server
  |
  |----------------------|
  |                      |
  v                      v
PostgreSQL             Queue
                         |
                         v
                  Image Processing Worker
                         |
                         v
                       S3
                         |
                         v
                   CloudFront
                         |
                         v
                      Client
```

---

## 4.2 실행 단위

MVP는 Docker Compose 기반으로 다음 컨테이너를 실행합니다.

```text
app
worker
postgres
queue
nginx
```

|실행 단위|역할|
|---|---|
|app|Spring Boot API Server|
|worker|이미지 리사이징 Worker|
|postgres|PostgreSQL Database|
|queue|이미지 처리 작업 Queue|
|nginx|Reverse Proxy|

---

## 4.3 API Server와 Worker 분리

API Server는 HTTP 요청을 처리합니다.

Worker는 이미지 리사이징 작업만 담당합니다.

이렇게 분리한 이유는 다음과 같습니다.

- 이미지 변환 작업이 API 응답 시간을 지연시키지 않도록 하기 위해
    
- 무거운 CPU 작업을 API Server에서 분리하기 위해
    
- 이미지 처리 실패를 Photo 상태로 관리하기 위해
    
- 향후 Worker만 별도로 확장할 수 있도록 하기 위해
    

---

# 5. ERD

## 5.1 핵심 도메인 관계

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

---

## 5.2 주요 테이블

|테이블|설명|
|---|---|
|users|사용자|
|refresh_tokens|Refresh Token|
|projects|재봉 작품|
|project_specifications|작품 제작 설정|
|project_references|참고 URL|
|daily_logs|날짜별 작업 일지|
|daily_log_time_entries|작업 시간 구간|
|fabrics|원단 구매 기록|
|project_fabrics|Project-Fabric 연결|
|photos|공통 이미지|

---

# 6. 핵심 설계 결정

## 6.1 Fabric을 Project와 독립 도메인으로 분리

Fabric은 Project의 속성이 아니라 독립 도메인으로 설계했습니다.

이유는 다음과 같습니다.

- 원단은 Project보다 먼저 등록될 수 있다.
    
- 하나의 원단은 여러 Project에 사용될 수 있다.
    
- 하나의 Project는 여러 원단을 사용할 수 있다.
    
- 원단 구매 기록은 작품 제작 기록과 성격이 다르다.
    
- 원단 검색, 재구매 의사, 사용 Project 조회가 필요하다.
    
- 향후 AI 원단 자동 기록 기능과 연결하기 쉽다.
    

---

## 6.2 Project-Fabric을 연결 엔티티로 설계

Project와 Fabric은 다대다 관계입니다.

JPA의 직접 `@ManyToMany`를 사용하지 않고, `ProjectFabric` 연결 엔티티를 두었습니다.

이유는 다음과 같습니다.

- 연결 관계에 memo를 남길 수 있다.
    
- 동일 Project-Fabric 중복 연결을 막을 수 있다.
    
- 향후 사용량, 용도, 정렬 등으로 확장할 수 있다.
    
- Project와 Fabric의 생명주기를 분리할 수 있다.
    

---

## 6.3 DailyLog에 DRAFT/PUBLISHED 상태 도입

DailyLog에는 DRAFT와 PUBLISHED 상태를 두었습니다.

DRAFT는 임시저장 기록이고, PUBLISHED는 정식 기록입니다.

통계 반영 기준은 다음과 같습니다.

|상태|Calendar/Contribution 반영|
|---|---|
|DRAFT|제외|
|PUBLISHED|포함|

PUBLISHED DailyLog도 수정할 수 있습니다.  
단, PUBLISHED 상태에서는 workedDate와 TimeEntry가 필수입니다.

---

## 6.4 TimeEntry 기반 작업 시간 계산

DailyLog의 작업 시간은 직접 입력하지 않고, 여러 TimeEntry의 합산값으로 계산합니다.

이유는 다음과 같습니다.

- 실제 재봉 작업은 여러 구간으로 나뉠 수 있다.
    
- 사용자가 총 시간을 직접 계산하지 않아도 된다.
    
- 작업 구간별 메모를 남길 수 있다.
    
- Calendar와 Contribution 집계 기준이 명확해진다.
    
- 향후 시간대별 작업 패턴 분석으로 확장할 수 있다.
    

---

## 6.5 Photo를 공통 이미지 엔티티로 설계

Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 엔티티입니다.

이유는 다음과 같습니다.

- 이미지 업로드 로직을 통합할 수 있다.
    
- Presigned URL 발급 로직을 재사용할 수 있다.
    
- 이미지 처리 상태를 일관되게 관리할 수 있다.
    
- Original, Medium, Thumbnail 구조를 공통으로 사용할 수 있다.
    
- Worker가 도메인별 이미지 테이블을 알 필요가 없다.
    

---

## 6.6 Calendar/Contribution을 조회 시 계산

Calendar와 Contribution은 별도 테이블에 저장하지 않습니다.

PUBLISHED DailyLog를 기준으로 조회 시점에 계산합니다.

이유는 다음과 같습니다.

- Calendar와 Contribution은 원본 데이터가 아니라 파생 데이터이다.
    
- PUBLISHED DailyLog 수정 시 별도 집계 테이블 동기화가 필요 없다.
    
- DRAFT/PUBLISHED 기준을 일관되게 적용할 수 있다.
    
- MVP 데이터 규모에서는 조회 시 계산으로 충분하다.
    

---

## 6.7 AI 원단 자동 기록을 MVP에서 제외

AI 기반 원단 자동 기록은 Future Scope로 분리했습니다.

이유는 다음과 같습니다.

- MVP의 핵심은 AI가 아니라 기록 구조 검증이다.
    
- 쇼핑몰마다 원단 정보 표현 방식이 다르다.
    
- OCR/Vision LLM 결과는 검수가 필요하다.
    
- AI 기능을 포함하면 MVP 범위가 과도하게 커진다.
    
- Fabric 데이터 모델만으로도 향후 AI 확장이 가능하다.
    

---

# 7. API 요약

## 7.1 Auth/User

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/auth/kakao/login|카카오 로그인|
|POST|/api/v1/auth/reissue|Access Token 재발급|
|POST|/api/v1/auth/logout|로그아웃|
|GET|/api/v1/users/me|내 정보 조회|
|DELETE|/api/v1/users/me|회원 탈퇴|

---

## 7.2 Project

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/projects|Project 생성|
|GET|/api/v1/projects|Project 목록 조회|
|GET|/api/v1/projects/{projectId}|Project 상세 조회|
|PATCH|/api/v1/projects/{projectId}|Project 수정|
|DELETE|/api/v1/projects/{projectId}|Project 삭제|
|PATCH|/api/v1/projects/{projectId}/status|Project 상태 변경|

---

## 7.3 DailyLog

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

## 7.4 Fabric / ProjectFabric

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/fabrics|Fabric 등록|
|GET|/api/v1/fabrics|Fabric 목록 조회|
|GET|/api/v1/fabrics/{fabricId}|Fabric 상세 조회|
|PATCH|/api/v1/fabrics/{fabricId}|Fabric 수정|
|DELETE|/api/v1/fabrics/{fabricId}|Fabric 삭제|
|POST|/api/v1/projects/{projectId}/fabrics|Project에 Fabric 연결|
|PATCH|/api/v1/projects/{projectId}/fabrics/{projectFabricId}|연결 메모 수정|
|DELETE|/api/v1/projects/{projectId}/fabrics/{projectFabricId}|연결 삭제|

---

## 7.5 Photo

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/photos/presigned-url|Presigned URL 발급|
|POST|/api/v1/photos/{photoId}/complete-upload|업로드 완료 처리|
|GET|/api/v1/photos|Photo 목록 조회|
|PATCH|/api/v1/photos/{photoId}/thumbnail|대표 사진 설정|
|PATCH|/api/v1/photos/sort-order|Photo 정렬 순서 수정|
|DELETE|/api/v1/photos/{photoId}|Photo 삭제|

---

## 7.6 Calendar / Contribution

|Method|Endpoint|설명|
|---|---|---|
|GET|/api/v1/calendar|월별 Calendar 조회|
|GET|/api/v1/calendar/days/{date}|특정 날짜 작업 요약|
|GET|/api/v1/contributions|연도별 Contribution 조회|

---

# 8. 테스트 전략

## 8.1 테스트 우선순위

테스트는 다음 도메인을 우선 검증합니다.

|우선순위|대상|이유|
|--:|---|---|
|1|DailyLog|상태, 시간 계산, 통계 반영 기준이 핵심|
|2|Photo|업로드 상태 전이와 이미지 처리 흐름이 복잡|
|3|ProjectFabric|소유권, 중복 연결, 독립 도메인 관계 검증|
|4|Calendar/Contribution|조회 시 계산되는 집계 정확성 필요|
|5|Auth/Security|보호 API 접근 제어 필요|

---

## 8.2 핵심 테스트 케이스

### DailyLog

- DRAFT는 workedDate 없이 저장 가능하다.
    
- DRAFT는 TimeEntry 없이 저장 가능하다.
    
- PUBLISHED는 workedDate가 필수이다.
    
- PUBLISHED는 TimeEntry가 1개 이상 필요하다.
    
- PUBLISHED 수정 시 durationMinutes가 재계산된다.
    
- PUBLISHED → DRAFT 전환은 불가능하다.
    

### Photo

- owner 권한이 있어야 Presigned URL을 발급할 수 있다.
    
- owner별 사진 수 제한을 초과할 수 없다.
    
- complete-upload 후 Photo는 PROCESSING 상태가 된다.
    
- Worker 처리 성공 시 READY 상태가 된다.
    
- Worker 처리 실패 시 FAILED 상태가 된다.
    
- READY 상태 Photo만 대표 사진으로 설정할 수 있다.
    

### Calendar/Contribution

- DRAFT DailyLog는 집계에서 제외된다.
    
- PUBLISHED DailyLog는 집계에 포함된다.
    
- 같은 날짜의 DailyLog 작업 시간은 합산된다.
    
- Level은 totalDurationMinutes 기준으로 계산된다.
    

---

# 9. 실행 방법

## 9.1 로컬 실행

```bash
git clone https://github.com/{username}/sorosoro.git
cd sorosoro
```

환경 변수 파일을 생성합니다.

```bash
cp .env.example .env
```

Docker Compose를 실행합니다.

```bash
docker compose up -d
```

애플리케이션을 실행합니다.

```bash
./gradlew bootRun
```

---

## 9.2 테스트 실행

```bash
./gradlew test
```

---

## 9.3 API 문서 확인

로컬 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

---

# 10. 배포 구조

## 10.1 운영 배포

MVP는 EC2 + Docker Compose + Nginx 기반으로 배포합니다.

배포 구성은 다음과 같습니다.

```text
EC2
 ├── nginx
 ├── app
 ├── worker
 ├── postgres
 └── queue
```

---

## 10.2 CI/CD

GitHub Actions를 사용해 배포를 자동화합니다.

배포 흐름은 다음과 같습니다.

```text
main branch push
→ Test
→ Build
→ Docker image build
→ Registry push
→ EC2 SSH
→ docker compose pull
→ docker compose up -d
```

---

# 11. 트러블슈팅 정리 예정

구현 과정에서 발생한 문제는 다음 형식으로 정리합니다.

```text
문제 상황
원인 분석
해결 방법
대안 검토
배운 점
```

예상 트러블슈팅 주제는 다음과 같습니다.

- Presigned URL 업로드 완료 전 Photo 상태 관리
    
- UPLOADING 상태 Photo 정리 정책
    
- Worker 실패 시 Photo FAILED 처리
    
- S3 object 삭제 실패 처리
    
- Project 삭제 시 Photo와 S3 object 정리
    
- PUBLISHED DailyLog 수정 시 Calendar 집계 반영
    
- ownerType/ownerId 구조의 소유권 검증
    
- Docker Compose에서 app/worker 실행 모드 분리
    

---

# 12. 향후 확장 방향

## 12.1 AI 원단 자동 기록

향후 상품 URL 또는 주문 내역 캡처를 기반으로 원단 정보를 자동 추출하는 기능을 추가할 수 있습니다.

예상 흐름은 다음과 같습니다.

```text
상품 URL 또는 주문 내역 캡처 입력
→ OCR / Vision LLM / DOM Extraction
→ 원단 정보 후보 추출
→ 사용자 검수
→ Fabric 생성
```

---

## 12.2 통계 대시보드

향후 다음 통계를 추가할 수 있습니다.

- 월별 총 작업 시간
    
- Project별 작업 시간
    
- WorkType별 작업 시간
    
- 연속 작업일
    
- 가장 오래 작업한 Project
    
- 시간대별 작업 패턴
    

---

## 12.3 원단 재고 관리

향후 Fabric에 재고 관리 기능을 추가할 수 있습니다.

예상 항목은 다음과 같습니다.

- 구매 길이
    
- 사용 길이
    
- 남은 길이
    
- 단위
    
- 재고 상태
    

---

## 12.4 인프라 확장

MVP 이후 서비스 규모가 커지면 다음 구조로 확장할 수 있습니다.

|현재 구조|확장 구조|
|---|---|
|EC2 Docker Compose|ECS Fargate|
|EC2 PostgreSQL|RDS PostgreSQL|
|Redis Queue|AWS SQS|
|Nginx|ALB|
|Docker Hub/GHCR|ECR|
|Docker logs|CloudWatch Logs|

---

# 13. 문서 목록

이 프로젝트는 다음 문서를 기반으로 설계했습니다.

|문서|설명|
|---|---|
|01_PRD.md|제품 요구사항|
|02_SRS.md|소프트웨어 요구사항|
|03_ADR.md|주요 아키텍처 의사결정|
|04_ERD.md|데이터베이스 설계|
|05_Backend_Design.md|백엔드 설계|
|06_Architecture.md|시스템 아키텍처|
|07_API.md|API 명세|
|08_Implementation_Plan.md|구현 계획|
|09_Test_Strategy.md|테스트 전략|

---

# 14. 프로젝트 요약

소로소로는 재봉 작품, 날짜별 작업 일지, 원단 구매 기록, 사진을 연결해 관리하는 개인 재봉 기록 서비스입니다.

이 프로젝트에서 중점적으로 설계한 부분은 다음과 같습니다.

- Fabric을 Project와 독립 도메인으로 분리
    
- Project와 Fabric을 연결 엔티티로 다대다 관계 구성
    
- DailyLog에 DRAFT/PUBLISHED 상태 도입
    
- TimeEntry 합산 기반 작업 시간 계산
    
- Photo 공통 엔티티 설계
    
- Presigned URL 기반 S3 직접 업로드
    
- Worker 기반 비동기 이미지 리사이징
    
- Calendar/Contribution 조회 시 계산
    
- Docker Compose + EC2 + Nginx 기반 배포 구조
    

단순 CRUD 서비스를 넘어서, 기록 도메인의 관계 설계, 이미지 업로드 처리, 비동기 작업 분리, 배포 구조까지 고려한 백엔드 포트폴리오 프로젝트입니다.