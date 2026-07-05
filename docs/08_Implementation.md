# 08. Implementation Plan — 소로소로(SOROSORO)

> 소로소로 MVP 개발 순서, 작업 단위, 우선순위, 테스트 기준을 정의하는 문서

---

# 1. 문서 개요

## 1.1 문서 목적

이 문서는 소로소로(SOROSORO)의 MVP 구현 계획을 정의한다.

소로소로는 개인 재봉 기록 서비스로, Project, DailyLog, Fabric, Photo, Calendar 기능을 중심으로 구성된다.

본 문서는 앞서 작성한 PRD, SRS, ADR, ERD, Backend Design, Architecture, API 문서를 실제 개발 작업으로 전환하기 위한 기준을 제공한다.

본 문서에서는 다음 내용을 정의한다.

- 개발 단계
    
- 구현 우선순위
    
- 브랜치 전략
    
- 작업 단위
    
- 도메인별 구현 순서
    
- 테스트 순서
    
- 배포 순서
    
- MVP 완료 기준
    
- 향후 확장 작업
    

---

## 1.2 구현 범위

MVP에서 구현하는 기능은 다음과 같다.

- 카카오 로그인
    
- JWT 인증
    
- Refresh Token
    
- Project CRUD
    
- Project 상태 관리
    
- ProjectSpecification 관리
    
- ProjectReference 관리
    
- DailyLog CRUD
    
- DailyLog DRAFT/PUBLISHED
    
- DailyLogTimeEntry 기반 작업 시간 계산
    
- Fabric CRUD
    
- Project-Fabric 연결
    
- Photo Presigned URL 업로드
    
- Photo 상태 관리
    
- 이미지 리사이징 Worker
    
- Calendar 조회
    
- Contribution 조회
    
- Docker Compose 기반 실행
    
- EC2 배포
    
- GitHub Actions 배포 자동화
    

---

## 1.3 구현 제외 범위

MVP에서는 다음 기능을 구현하지 않는다.

- AI 기반 원단 자동 기록
    
- 커뮤니티
    
- 댓글
    
- 좋아요
    
- 팔로우
    
- 알림
    
- 원단 재고 관리
    
- 통계 대시보드
    
- 고도화 검색
    
- 관리자 기능
    
- 공개 프로젝트
    
- 다중 사용자 공유
    
- PUBLISHED → DRAFT 전환
    
- TimeEntry 개별 수정 API
    
- HEIC 이미지 지원
    
- 이미지 재처리 API
    

---

# 2. 개발 원칙

## 2.1 구현 우선순위 원칙

개발은 다음 원칙을 따른다.

1. 인증보다 도메인 골격을 먼저 만든다.
    
2. DB 스키마와 Entity를 먼저 고정한다.
    
3. 핵심 도메인 CRUD를 먼저 완성한다.
    
4. 이미지 업로드는 도메인 CRUD 이후 구현한다.
    
5. Calendar와 Contribution은 DailyLog가 안정화된 후 구현한다.
    
6. 배포는 로컬 실행이 안정화된 후 진행한다.
    
7. AI 기능은 MVP 완료 이후로 미룬다.
    

---

## 2.2 구현 방식 원칙

- 한 번에 모든 기능을 구현하지 않는다.
    
- 도메인 단위로 수직 슬라이스를 완성한다.
    
- Entity → Repository → Service → Controller → Test 순서로 구현한다.
    
- API 응답은 문서와 다를 경우 문서를 업데이트한다.
    
- 테스트 가능한 단위부터 구현한다.
    
- 복잡한 기능은 먼저 동기 방식으로 검증한 뒤 비동기 처리로 확장한다.
    
- MVP에서는 완벽한 추상화보다 명확성과 완성도를 우선한다.
    

---

# 3. 브랜치 전략

## 3.1 기본 브랜치

```text
main
dev
pre-dev
feature/ticket-*
fix/*
docs/*
```

|브랜치|역할|
|---|---|
|main|배포 가능한 안정 버전. MVP 개발 중에는 사람이 필요 시 반영한다.|
|dev|AI 리뷰를 통과한 개발 통합 브랜치|
|pre-dev|Gemini 명세 검증을 통과한 티켓 브랜치를 임시 통합하는 브랜치|
|feature/ticket-*|티켓 단위 기능 개발|
|fix/*|사람 주도 ad-hoc 버그 수정. 티켓 작업에는 사용하지 않으며 `needs-human-review` 경로로 처리한다.|
|docs/*|사람 주도 ad-hoc 문서 수정. 티켓 작업에는 사용하지 않으며 `needs-human-review` 경로로 처리한다.|

---

## 3.2 브랜치 사용 방식

기능 개발은 하나의 티켓을 기준으로 `feature/ticket-N` 브랜치에서 진행한다.

```bash
git checkout dev
git pull origin dev
git checkout -b feature/ticket-N
```

기능 완료 후 `pre-dev`로 Pull Request를 생성한다.

`feature/ticket-N` → `pre-dev` PR은 Gemini가 문서와 티켓 기준으로 명세 검증을 수행한다.

Gemini 검증과 CI를 통과하면 `pre-dev`로 병합한다.

`pre-dev` 병합 후 `pre-dev` → `dev` Pull Request를 생성한다.

`pre-dev` → `dev` PR은 Claude Code GitHub Action이 코드 품질, 구조, 보안, 테스트 관점에서 리뷰한다.

Claude 리뷰와 CI를 통과하면 `dev`로 병합한다.

`main` 반영은 별도 배포 판단에 따라 사람이 수행한다.

---

## 3.3 커밋 메시지 규칙

커밋 메시지는 다음 형식을 사용한다.

```text
type: message
```

|type|설명|
|---|---|
|feat|기능 추가|
|fix|버그 수정|
|refactor|리팩터링|
|test|테스트 추가/수정|
|docs|문서 수정|
|chore|설정, 빌드, 의존성|
|infra|배포, Docker, CI/CD|
|style|코드 포맷팅|

예시는 다음과 같다.

```bash
feat: 프로젝트 생성 API 구현
test: DailyLog 발행 검증 테스트 추가
infra: docker-compose 운영 설정 추가
docs: API 명세 업데이트
```

---

# 4. 전체 구현 로드맵

## 4.1 단계 요약

|단계|이름|목표|
|--:|---|---|
|0|프로젝트 초기 세팅|Spring Boot 프로젝트와 기본 설정 구성|
|1|DB/Entity 기반 구축|ERD 기반 Entity와 Migration 작성|
|2|Auth/User 구현|카카오 로그인, JWT, Refresh Token|
|3|Project 구현|Project, Specification, Reference|
|4|Fabric 구현|Fabric CRUD|
|5|ProjectFabric 구현|Project와 Fabric 연결|
|6|DailyLog 구현|DRAFT/PUBLISHED, TimeEntry|
|7|Calendar 구현|Calendar, Contribution 집계|
|8|Photo 구현|Presigned URL, Photo 상태|
|9|Worker 구현|이미지 리사이징 비동기 처리|
|10|통합 테스트|주요 시나리오 검증|
|11|Docker/배포|Docker Compose, EC2, Nginx|
|12|README/포트폴리오 정리|문서, 실행 방법, 회고 정리|

---

# 5. Phase 0. 프로젝트 초기 세팅

## 5.1 목표

Spring Boot 백엔드 프로젝트의 기본 구조를 만든다.

---

## 5.2 작업 목록

- Spring Boot 프로젝트 생성
    
- Java 17 설정
    
- Gradle 설정
    
- 기본 패키지 구조 생성
    
- application.yml 분리
    
- local/test/prod profile 구성
    
- 공통 응답/예외 구조 생성
    
- Springdoc OpenAPI 설정
    
- Health Check 설정
    
- Git ignore 설정
    
- README 초기 작성
    

---

## 5.3 패키지 생성

```text
com.sorosoro
 ├── auth
 ├── user
 ├── project
 ├── dailylog
 ├── fabric
 ├── photo
 ├── calendar
 ├── worker
 └── common
```

---

## 5.4 완료 기준

- 애플리케이션이 로컬에서 실행된다.
    
- `/actuator/health`가 정상 응답한다.
    
- 기본 예외 응답 형식이 동작한다.
    
- Swagger UI가 열린다.
    

---

# 6. Phase 1. DB/Entity 기반 구축

## 6.1 목표

ERD 기반으로 테이블과 Entity를 구현한다.

---

## 6.2 작업 목록

- PostgreSQL local 설정
    
- Flyway 설정
    
- users 테이블 생성
    
- refresh_tokens 테이블 생성
    
- projects 테이블 생성
    
- project_specifications 테이블 생성
    
- project_references 테이블 생성
    
- daily_logs 테이블 생성
    
- daily_log_time_entries 테이블 생성
    
- fabrics 테이블 생성
    
- project_fabrics 테이블 생성
    
- photos 테이블 생성
    
- 공통 BaseEntity 생성
    
- Enum 정의
    
- JPA Entity 작성
    
- Repository 기본 인터페이스 작성
    

---

## 6.3 Migration 작성 순서

```text
V1__create_users.sql
V2__create_refresh_tokens.sql
V3__create_projects.sql
V4__create_project_specifications.sql
V5__create_project_references.sql
V6__create_daily_logs.sql
V7__create_daily_log_time_entries.sql
V8__create_fabrics.sql
V9__create_project_fabrics.sql
V10__create_photos.sql
```

---

## 6.4 완료 기준

- 모든 테이블이 Flyway로 생성된다.
    
- 애플리케이션 실행 시 Migration이 정상 적용된다.
    
- Entity와 DB 컬럼 매핑이 정상 동작한다.
    
- 기본 Repository 테스트가 통과한다.
    

---

# 7. Phase 2. Auth/User 구현

## 7.1 목표

카카오 로그인과 JWT 인증 기반을 구현한다.

---

## 7.2 작업 목록

- User Entity 구현
    
- RefreshToken Entity 구현
    
- KakaoOAuthClient 구현
    
- JwtTokenProvider 구현
    
- SecurityConfig 구현
    
- CustomUserPrincipal 구현
    
- JWT 인증 필터 구현
    
- 카카오 로그인 API 구현
    
- Access Token 재발급 API 구현
    
- 로그아웃 API 구현
    
- 내 정보 조회 API 구현
    
- 회원 탈퇴 API 구현
    

---

## 7.3 주요 API

|Method|Endpoint|설명|
|---|---|---|
|POST|/api/v1/auth/kakao/login|카카오 로그인|
|POST|/api/v1/auth/reissue|토큰 재발급|
|POST|/api/v1/auth/logout|로그아웃|
|GET|/api/v1/users/me|내 정보 조회|
|DELETE|/api/v1/users/me|회원 탈퇴|

---

## 7.4 테스트 기준

- 카카오 사용자 정보로 User가 생성된다.
    
- 기존 User는 중복 생성되지 않는다.
    
- Access Token이 발급된다.
    
- Refresh Token이 저장된다.
    
- Refresh Token으로 Access Token을 재발급할 수 있다.
    
- 로그아웃 시 Refresh Token이 삭제된다.
    
- 탈퇴 시 User 상태가 DELETED가 된다.
    
- 인증이 없는 보호 API는 401을 반환한다.
    

---

## 7.5 완료 기준

- 인증이 필요한 API에서 현재 User를 식별할 수 있다.
    
- `@AuthenticationPrincipal` 또는 `@CurrentUser`로 userId를 사용할 수 있다.
    

---

# 8. Phase 3. Project 구현

## 8.1 목표

Project와 제작 설정, 참고자료 기능을 구현한다.

---

## 8.2 작업 목록

- Project Entity 상태 변경 메서드 구현
    
- ProjectSpecification Entity 구현
    
- ProjectReference Entity 구현
    
- ProjectRepository 구현
    
- ProjectQueryRepository 구현
    
- ProjectService 구현
    
- ProjectController 구현
    
- Project 생성 API
    
- Project 목록 API
    
- Project 상세 API
    
- Project 수정 API
    
- Project 삭제 API
    
- Project 상태 변경 API
    
- ProjectSpecification 조회/수정 API
    
- ProjectReference 등록/수정/삭제 API
    

---

## 8.3 주요 규칙

- Project는 title만으로 생성 가능하다.
    
- 생성 시 기본 상태는 `IN_PROGRESS`이다.
    
- Project 생성 시 ProjectSpecification도 생성한다.
    
- `ARCHIVED` 상태에서는 수정성 작업을 제한한다.
    
- `ON_HOLD` 상태에서는 DailyLog 작성이 가능하다.
    
- Project 삭제 시 Fabric은 삭제하지 않는다.
    

---

## 8.4 테스트 기준

- Project 생성 시 Specification도 생성된다.
    
- Project title이 없으면 실패한다.
    
- 다른 사용자의 Project는 조회/수정할 수 없다.
    
- Project 상태를 변경할 수 있다.
    
- ARCHIVED Project는 수정할 수 없다.
    
- Project 삭제 시 관련 Specification, Reference가 삭제된다.
    

---

## 8.5 완료 기준

- Project 관련 API가 Swagger에서 정상 호출된다.
    
- Project 상세에서 Specification과 Reference를 함께 조회할 수 있다.
    

---

# 9. Phase 4. Fabric 구현

## 9.1 목표

원단을 Project와 독립적으로 등록하고 관리할 수 있도록 한다.

---

## 9.2 작업 목록

- Fabric Entity 구현
    
- FabricRepository 구현
    
- FabricQueryRepository 구현
    
- FabricService 구현
    
- FabricController 구현
    
- Fabric 등록 API
    
- Fabric 목록 조회 API
    
- Fabric 상세 조회 API
    
- Fabric 수정 API
    
- Fabric 삭제 API
    

---

## 9.3 주요 규칙

- Fabric은 Project 없이 생성 가능하다.
    
- name은 필수이다.
    
- repurchaseIntention 기본값은 `UNKNOWN`이다.
    
- rating은 1~5 범위이다.
    
- purchasePrice는 0 이상이다.
    
- Fabric 삭제 시 Project는 삭제하지 않는다.
    

---

## 9.4 테스트 기준

- name만으로 Fabric을 생성할 수 있다.
    
- 다른 사용자의 Fabric은 수정할 수 없다.
    
- rating이 1~5 범위를 벗어나면 실패한다.
    
- Fabric 삭제 시 Project는 유지된다.
    

---

## 9.5 완료 기준

- Fabric CRUD API가 정상 동작한다.
    
- Fabric 목록에서 검색 조건을 사용할 수 있다.
    

---

# 10. Phase 5. ProjectFabric 구현

## 10.1 목표

Project와 Fabric을 연결하고 연결 메모를 관리한다.

---

## 10.2 작업 목록

- ProjectFabric Entity 구현
    
- ProjectFabricRepository 구현
    
- ProjectFabricService 구현
    
- Project에 Fabric 연결 API
    
- 연결 메모 수정 API
    
- 연결 삭제 API
    
- Project 상세에 연결 Fabric 포함
    
- Fabric 상세에 사용 Project 포함
    

---

## 10.3 주요 규칙

- Project와 Fabric 모두 현재 User 소유여야 한다.
    
- 동일 Project에 동일 Fabric을 중복 연결할 수 없다.
    
- Project가 ARCHIVED 상태이면 연결, 수정, 삭제할 수 없다.
    
- ProjectFabric 삭제 시 Project와 Fabric은 유지된다.
    

---

## 10.4 테스트 기준

- Project와 Fabric을 연결할 수 있다.
    
- 다른 사용자의 Fabric은 연결할 수 없다.
    
- 같은 Fabric을 같은 Project에 중복 연결할 수 없다.
    
- 연결 메모를 수정할 수 있다.
    
- 연결 삭제 시 Project와 Fabric은 유지된다.
    
- ARCHIVED Project에는 Fabric을 연결할 수 없다.
    

---

## 10.5 완료 기준

- Project 상세에서 연결된 Fabric을 확인할 수 있다.
    
- Fabric 상세에서 사용된 Project를 확인할 수 있다.
    

---

# 11. Phase 6. DailyLog 구현

## 11.1 목표

Project의 날짜별 작업 기록과 작업 시간 계산을 구현한다.

---

## 11.2 작업 목록

- DailyLog Entity 구현
    
- DailyLogTimeEntry Entity 구현
    
- DailyLogRepository 구현
    
- DailyLogQueryRepository 구현
    
- DailyLogService 구현
    
- DailyLogController 구현
    
- DailyLog 생성 API
    
- Project별 DailyLog 목록 API
    
- 날짜별 DailyLog 목록 API
    
- DailyLog 상세 API
    
- DailyLog 수정 API
    
- DailyLog 발행 API
    
- DailyLog 삭제 API
    
- TimeEntry 전체 교체 로직
    
- durationMinutes 계산 로직
    

---

## 11.3 주요 규칙

- DailyLog는 DRAFT 또는 PUBLISHED 상태를 가진다.
    
- DRAFT는 workedDate 없이 저장할 수 있다.
    
- DRAFT는 TimeEntry 없이 저장할 수 있다.
    
- PUBLISHED는 workedDate가 필수이다.
    
- PUBLISHED는 TimeEntry가 1개 이상 필요하다.
    
- PUBLISHED도 수정 가능하다.
    
- PUBLISHED → DRAFT 전환은 제공하지 않는다.
    
- TimeEntry는 개별 API 없이 DailyLog 수정 시 전체 교체한다.
    
- durationMinutes는 TimeEntry 합산값이다.
    
- Project가 ARCHIVED 상태이면 DailyLog 수정성 작업을 제한한다.
    

---

## 11.4 테스트 기준

- DRAFT DailyLog는 workedDate 없이 저장된다.
    
- DRAFT DailyLog는 TimeEntry 없이 저장된다.
    
- PUBLISHED DailyLog는 workedDate 없으면 실패한다.
    
- PUBLISHED DailyLog는 TimeEntry 없으면 실패한다.
    
- TimeEntry durationMinutes가 계산된다.
    
- DailyLog durationMinutes가 TimeEntry 합산값으로 계산된다.
    
- DRAFT를 PUBLISHED로 발행할 수 있다.
    
- PUBLISHED를 DRAFT로 되돌릴 수 없다.
    
- PUBLISHED 수정 시 durationMinutes가 재계산된다.
    
- ARCHIVED Project에는 DailyLog를 추가할 수 없다.
    

---

## 11.5 완료 기준

- DailyLog 작성, 임시저장, 발행, 수정, 삭제가 가능하다.
    
- 작업 시간이 정확히 계산된다.
    

---

# 12. Phase 7. Calendar/Contribution 구현

## 12.1 목표

PUBLISHED DailyLog를 기준으로 Calendar와 Contribution을 조회한다.

---

## 12.2 작업 목록

- CalendarQueryRepository 구현
    
- CalendarService 구현
    
- CalendarController 구현
    
- 월별 Calendar 조회 API
    
- 특정 날짜 작업 요약 API
    
- 연도별 Contribution 조회 API
    
- Level 계산 로직 구현
    

---

## 12.3 주요 규칙

- DRAFT DailyLog는 집계에서 제외한다.
    
- PUBLISHED DailyLog만 집계한다.
    
- 날짜별 totalDurationMinutes를 합산한다.
    
- level은 totalDurationMinutes 기준으로 계산한다.
    
- Calendar와 Contribution은 별도 테이블로 저장하지 않는다.
    

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

## 12.5 테스트 기준

- DRAFT DailyLog는 Calendar에 포함되지 않는다.
    
- PUBLISHED DailyLog는 Calendar에 포함된다.
    
- 같은 날짜의 PUBLISHED DailyLog 시간이 합산된다.
    
- DRAFT DailyLog는 Contribution에 포함되지 않는다.
    
- PUBLISHED DailyLog는 Contribution에 포함된다.
    
- level이 기준대로 계산된다.
    
- 다른 사용자의 DailyLog는 집계에 포함되지 않는다.
    

---

## 12.6 완료 기준

- 월별 Calendar가 정상 응답한다.
    
- 특정 날짜 작업 요약이 정상 응답한다.
    
- 연도별 Contribution이 정상 응답한다.
    

---

# 13. Phase 8. Photo 구현

## 13.1 목표

Photo 공통 엔티티와 Presigned URL 기반 이미지 업로드 기능을 구현한다.

---

## 13.2 작업 목록

- Photo Entity 구현
    
- PhotoRepository 구현
    
- PhotoQueryRepository 구현
    
- S3StorageClient 구현
    
- CloudFrontUrlResolver 구현
    
- PhotoService 구현
    
- PhotoController 구현
    
- Presigned URL 발급 API
    
- complete-upload API
    
- Photo 목록 조회 API
    
- 대표 사진 설정 API
    
- Photo 정렬 순서 수정 API
    
- Photo 삭제 API
    

---

## 13.3 주요 규칙

- Photo는 ownerType과 ownerId를 가진다.
    
- ownerType은 PROJECT, DAILY_LOG, FABRIC 중 하나이다.
    
- owner 존재 여부와 소유권은 Service에서 검증한다.
    
- Project와 DailyLog는 최대 15장까지 Photo를 가질 수 있다.
    
- Fabric은 최대 2장까지 Photo를 가질 수 있다.
    
- Project와 DailyLog는 대표 사진을 1장만 가질 수 있다.
    
- Fabric은 대표 사진 설정을 지원하지 않는다.
    
- READY 상태 Photo만 대표 사진으로 설정할 수 있다.
    
- Presigned URL 발급 시 Photo는 UPLOADING 상태가 된다.
    
- complete-upload 성공 시 PROCESSING 상태가 된다.
    

---

## 13.4 테스트 기준

- owner가 없으면 Presigned URL 발급에 실패한다.
    
- 다른 사용자의 owner에는 Photo를 추가할 수 없다.
    
- 사진 수 제한을 초과하면 실패한다.
    
- Presigned URL 발급 시 Photo가 UPLOADING 상태가 된다.
    
- complete-upload 시 Photo가 PROCESSING 상태가 된다.
    
- READY 상태가 아닌 Photo는 대표 사진으로 설정할 수 없다.
    
- Fabric Photo는 대표 사진으로 설정할 수 없다.
    
- Project와 DailyLog는 대표 사진이 1장만 유지된다.
    

---

## 13.5 완료 기준

- Presigned URL 발급부터 complete-upload까지 흐름이 동작한다.
    
- Photo 상태가 정상 전이된다.
    
- Photo 목록에서 CloudFront URL을 받을 수 있다.
    

---

# 14. Phase 9. Image Worker 구현

## 14.1 목표

Queue 기반 이미지 리사이징 Worker를 구현한다.

---

## 14.2 작업 목록

- Queue Producer 구현
    
- Queue Consumer 구현
    
- PhotoResizeWorker 구현
    
- ImageProcessor 구현
    
- S3 원본 다운로드
    
- Medium 이미지 생성
    
- Thumbnail 이미지 생성
    
- S3 변환 이미지 업로드
    
- Photo READY 상태 변경
    
- 실패 시 Photo FAILED 상태 변경
    
- Worker 실행 모드 구성
    

---

## 14.3 주요 규칙

- Worker는 photoId를 기준으로 작업한다.
    
- Photo 상태가 PROCESSING이 아니면 skip한다.
    
- 원본 이미지가 없으면 FAILED 처리한다.
    
- 이미지 변환 실패 시 FAILED 처리한다.
    
- 성공 시 mediumKey, thumbnailKey, processedAt을 저장한다.
    
- Worker는 HTTP 요청을 직접 처리하지 않는다.
    

---

## 14.4 테스트 기준

- Queue 메시지를 수신한다.
    
- photoId로 Photo를 조회한다.
    
- PROCESSING 상태 Photo만 처리한다.
    
- 성공 시 READY 상태가 된다.
    
- mediumKey와 thumbnailKey가 저장된다.
    
- 실패 시 FAILED 상태가 된다.
    
- 중복 메시지는 안전하게 skip된다.
    

---

## 14.5 완료 기준

- 실제 이미지 업로드 후 Worker가 medium/thumbnail을 생성한다.
    
- CloudFront URL로 변환 이미지를 조회할 수 있다.
    

---

# 15. Phase 10. 통합 테스트

## 15.1 목표

MVP 핵심 사용자 흐름을 통합 검증한다.

---

## 15.2 핵심 시나리오

### Scenario 1. 사용자 가입 및 로그인

```text
카카오 로그인
→ User 생성
→ Access Token 발급
→ 내 정보 조회
```

---

### Scenario 2. Project 생성과 DailyLog 작성

```text
Project 생성
→ DRAFT DailyLog 작성
→ TimeEntry 추가
→ PUBLISHED 발행
→ Project 상세에서 총 작업 시간 확인
```

---

### Scenario 3. Fabric 등록과 Project 연결

```text
Fabric 등록
→ Project에 Fabric 연결
→ 연결 메모 작성
→ Project 상세에서 Fabric 확인
→ Fabric 상세에서 사용 Project 확인
```

---

### Scenario 4. 이미지 업로드

```text
Presigned URL 발급
→ S3 업로드
→ complete-upload
→ Worker 처리
→ Photo READY
→ Thumbnail URL 조회
```

---

### Scenario 5. Calendar와 Contribution

```text
PUBLISHED DailyLog 생성
→ Calendar 조회
→ 특정 날짜 요약 조회
→ Contribution 조회
```

---

## 15.3 완료 기준

- 핵심 시나리오가 로컬 환경에서 모두 성공한다.
    
- 주요 API가 Swagger에서 수동 테스트 가능하다.
    
- 실패 케이스가 의도한 에러 코드로 반환된다.
    

---

# 16. Phase 11. Docker/배포 구현

## 16.1 목표

소로소로를 Docker Compose 기반으로 EC2에 배포한다.

---

## 16.2 작업 목록

- Dockerfile 작성
    
- docker-compose.yml 작성
    
- docker-compose.prod.yml 작성
    
- Nginx 설정 작성
    
- EC2 서버 생성
    
- 보안 그룹 설정
    
- Docker 설치
    
- Docker Compose 설치
    
- .env 운영 설정
    
- PostgreSQL volume 설정
    
- S3 bucket 생성
    
- CloudFront 설정
    
- GitHub Actions 배포 workflow 작성
    
- 배포 후 Health Check 확인
    

---

## 16.3 배포 순서

```text
1. EC2 생성
2. 보안 그룹 설정
3. Docker 설치
4. 소스 코드 또는 compose 파일 배치
5. .env 설정
6. docker compose up -d
7. Nginx 연결 확인
8. API Health Check
9. S3/CloudFront 연결 확인
10. GitHub Actions 배포 자동화 연결
```

---

## 16.4 완료 기준

- EC2에서 API Server가 실행된다.
    
- Nginx를 통해 API에 접근할 수 있다.
    
- PostgreSQL이 정상 연결된다.
    
- Worker가 실행된다.
    
- 이미지 업로드와 리사이징이 운영 환경에서 동작한다.
    
- GitHub Actions로 배포할 수 있다.
    

---

# 17. Phase 12. README/포트폴리오 정리

## 17.1 목표

프로젝트를 포트폴리오로 설명할 수 있도록 문서와 결과물을 정리한다.

---

## 17.2 작업 목록

- README 작성
    
- 프로젝트 소개 작성
    
- 주요 기능 정리
    
- 아키텍처 다이어그램 추가
    
- ERD 다이어그램 추가
    
- API 문서 링크 추가
    
- 기술 선택 이유 정리
    
- 트러블슈팅 정리
    
- 테스트 방법 정리
    
- 배포 방법 정리
    
- 향후 확장 방향 정리
    
- 회고 작성
    

---

## 17.3 README 구성

```text
1. 프로젝트 소개
2. 주요 기능
3. 기술 스택
4. 아키텍처
5. ERD
6. API 문서
7. 핵심 설계 결정
8. 실행 방법
9. 테스트 방법
10. 트러블슈팅
11. 향후 확장 방향
```

---

## 17.4 포트폴리오 강조 포인트

포트폴리오에서는 다음 내용을 강조한다.

- Project, DailyLog, Fabric 도메인 분리
    
- DRAFT/PUBLISHED 상태 설계
    
- TimeEntry 기반 작업 시간 계산
    
- Project-Fabric 연결 엔티티 설계
    
- Photo 공통 엔티티 설계
    
- Presigned URL 기반 이미지 업로드
    
- Worker 기반 비동기 이미지 처리
    
- Calendar/Contribution 조회 시 계산
    
- Docker Compose + EC2 + Nginx 배포
    
- AI 기능을 MVP에서 제외한 범위 조절 판단
    

---

# 18. 우선순위 기준

## 18.1 Must Have

MVP에서 반드시 구현해야 한다.

- Auth/User
    
- Project CRUD
    
- DailyLog CRUD
    
- DRAFT/PUBLISHED
    
- TimeEntry 합산
    
- Fabric CRUD
    
- ProjectFabric 연결
    
- Photo 업로드
    
- Image Worker
    
- Calendar
    
- Contribution
    
- Docker Compose 실행
    

---

## 18.2 Should Have

MVP에 포함하면 좋지만, 일정에 따라 조정 가능하다.

- ProjectReference 정렬
    
- Photo 정렬 순서 수정
    
- Project 상세의 총 작업 시간
    
- Fabric 검색 조건
    
- Contribution 전체 연도 채우기
    
- GitHub Actions 자동 배포
    
- CloudFront URL 연결
    

---

## 18.3 Could Have

MVP 이후로 미뤄도 된다.

- Photo 삭제 재시도 Queue
    
- 오래된 UPLOADING Photo 정리
    
- 오래된 PROCESSING Photo 정리
    
- Worker 실패 사유 저장
    
- Cursor Pagination
    
- 상세 검색 필터
    
- 통계 대시보드
    

---

## 18.4 Won’t Have in MVP

MVP에서 하지 않는다.

- AI 원단 자동 기록
    
- Community
    
- Like
    
- Comment
    
- Follow
    
- Notification
    
- Fabric Inventory
    
- Public Project
    
- Admin
    
- PUBLISHED → DRAFT
    
- TimeEntry 개별 수정 API
    

---

# 19. 예상 개발 순서 요약

실제 개발 순서는 다음처럼 진행한다.

```text
0. Spring Boot 초기 세팅
1. DB Migration + Entity
2. Auth/User
3. Project
4. Fabric
5. ProjectFabric
6. DailyLog
7. Calendar/Contribution
8. Photo Presigned URL
9. Image Worker
10. 통합 테스트
11. Docker Compose
12. EC2 배포
13. README/포트폴리오 정리
```

---

# 20. MVP 완료 기준

소로소로 MVP는 다음 조건을 만족하면 완료로 본다.

## 20.1 기능 완료 기준

- 사용자는 카카오 로그인을 할 수 있다.
    
- 사용자는 Project를 생성, 조회, 수정, 삭제할 수 있다.
    
- 사용자는 Project 제작 설정과 참고자료를 관리할 수 있다.
    
- 사용자는 Fabric을 등록, 조회, 수정, 삭제할 수 있다.
    
- 사용자는 Project에 Fabric을 연결할 수 있다.
    
- 사용자는 DailyLog를 DRAFT로 저장할 수 있다.
    
- 사용자는 DailyLog를 PUBLISHED로 발행할 수 있다.
    
- PUBLISHED DailyLog의 작업 시간이 TimeEntry 기준으로 계산된다.
    
- 사용자는 Project, DailyLog, Fabric에 사진을 업로드할 수 있다.
    
- 이미지는 Worker를 통해 리사이징된다.
    
- 사용자는 Calendar에서 날짜별 작업 시간을 확인할 수 있다.
    
- 사용자는 Contribution에서 연도별 재봉 잔디를 확인할 수 있다.
    

---

## 20.2 품질 완료 기준

- 주요 Service 테스트가 작성되어 있다.
    
- 주요 API가 Swagger에서 테스트 가능하다.
    
- 인증과 소유권 검증이 동작한다.
    
- 에러 응답 형식이 통일되어 있다.
    
- README에 실행 방법이 정리되어 있다.
    
- Docker Compose로 로컬 실행이 가능하다.
    
- 운영 환경에 배포 가능하다.
    

---

## 20.3 포트폴리오 완료 기준

- PRD, SRS, ADR, ERD, API 문서가 정리되어 있다.
    
- README에 핵심 설계 결정이 요약되어 있다.
    
- 아키텍처 다이어그램이 포함되어 있다.
    
- ERD 다이어그램이 포함되어 있다.
    
- 주요 트러블슈팅이 정리되어 있다.
    
- MVP에서 제외한 기능과 향후 확장 방향이 명확하다.
    

---

# 21. 구현 계획 요약

소로소로 MVP 구현은 다음 원칙으로 진행한다.

- 먼저 DB와 도메인 모델을 안정화한다.
    
- 인증 기반을 만든 뒤 도메인 API를 구현한다.
    
- Project, Fabric, DailyLog 순서로 핵심 기록 기능을 만든다.
    
- DailyLog가 안정화된 후 Calendar와 Contribution을 구현한다.
    
- Photo와 Worker는 도메인 CRUD 이후 연결한다.
    
- 로컬에서 통합 시나리오를 검증한 뒤 Docker Compose로 배포한다.
    
- MVP에서는 기능 범위를 과도하게 늘리지 않고, 핵심 기록 경험 완성을 우선한다.
    

최종 목표는 단순 CRUD 프로젝트가 아니라, 도메인 설계·이미지 업로드·비동기 처리·배포 구조까지 설명 가능한 백엔드 포트폴리오를 완성하는 것이다.
