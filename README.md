# 소로소로(SOROSORO)

[![CI](https://github.com/kitechoi/sorosoro/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/kitechoi/sorosoro/actions/workflows/ci.yml)

> 하루하루 재봉 과정을 기록하는 개인 재봉일기 서비스

소로소로는 개인 재봉 작업을 프로젝트 단위로 관리하고, 날짜별 작업 일지와 원단 정보를 함께 기록할 수 있는 재봉 기록 서비스입니다.

재봉 작업은 결과물만 남기기보다, 어떤 원단을 사용했고 어떤 과정을 거쳐 완성했는지가 중요합니다. 하지만 실제 작업 과정은 사진, 메모, 쇼핑몰 링크, 원단 정보, 작업 시간이 여러 곳에 흩어지기 쉽습니다.

소로소로는 이러한 기록을 하나의 서비스 안에서 관리할 수 있도록 설계했습니다.

## 프로젝트 개요

### 한 줄 소개

하루하루 재봉 과정을 기록하고, 작품과 원단을 연결해 관리하는 개인 재봉일기 서비스

### 개발 배경

재봉 작업을 하다 보면 다음과 같은 불편함이 발생합니다.

- 어떤 원단을 어디서 샀는지 다시 찾기 어렵다.
- 특정 작품에 어떤 원단을 사용했는지 기억하기 어렵다.
- 작업 과정 사진과 메모가 여러 곳에 흩어진다.
- 하루에 얼마나 작업했는지 기록하기 어렵다.
- 완성 작품만 남고 제작 과정은 사라진다.
- 같은 원단을 다른 작품에 재사용했는지 확인하기 어렵다.

소로소로는 이러한 문제를 해결하기 위해, 작품·작업일지·원단·사진을 연결해 기록할 수 있는 개인용 재봉 기록 서비스로 기획했습니다.

### 핵심 목표

- 재봉 작품을 Project 단위로 관리한다.
- 날짜별 작업 과정을 DailyLog로 기록한다.
- 원단 구매 정보를 Fabric으로 독립 관리한다.
- Project와 Fabric을 연결해 어떤 작품에 어떤 원단을 사용했는지 추적한다.
- 사진을 중심으로 제작 과정을 남긴다.
- Calendar와 Contribution으로 작업 습관을 시각화한다.
- 이미지 업로드와 리사이징을 안정적으로 처리한다.

## 주요 기능

### 카카오 로그인

- Kakao OAuth 로그인
- JWT Access Token 발급
- Refresh Token 기반 재발급
- 로그아웃
- 회원 탈퇴

### Project 관리

Project는 하나의 재봉 작품을 의미합니다.

지원 상태는 다음과 같습니다.

| 상태 | 설명 |
| --- | --- |
| IN_PROGRESS | 진행 중 |
| ON_HOLD | 보류 |
| COMPLETED | 완료 |
| ARCHIVED | 보관 |

Project에는 작품명, 설명, 시작일, 완료일, 회고, 제작 설정, 참고 URL, 연결된 원단, 대표 사진을 기록할 수 있습니다.

### DailyLog 관리

DailyLog는 특정 날짜의 작업 기록입니다. 사용자는 작업 내용을 임시저장하거나 정식 기록으로 발행할 수 있습니다.

| 상태 | 설명 |
| --- | --- |
| DRAFT | 임시저장 |
| PUBLISHED | 정식 기록 |

DRAFT는 작성 중인 기록으로 통계에 포함하지 않고, PUBLISHED는 Calendar와 Contribution에 반영합니다.

### TimeEntry 기반 작업 시간 계산

DailyLog의 작업 시간은 사용자가 총합을 직접 입력하지 않습니다. 여러 작업 시간 구간인 `DailyLogTimeEntry`를 입력하면 시스템이 자동으로 총 작업 시간을 계산합니다.

```text
15:00 ~ 15:40 재단
20:00 ~ 21:10 재봉
22:30 ~ 23:00 마감
```

위 기록은 `40분 + 70분 + 30분 = 140분`으로 계산되며, Project 총 작업 시간, Calendar 날짜별 작업 시간, Contribution 재봉 잔디에 사용됩니다.

### Fabric 관리

Fabric은 원단 구매 기록입니다. Project와 독립적으로 존재할 수 있으므로, 사용자는 아직 어떤 작품에 사용할지 정하지 않은 원단도 먼저 등록할 수 있습니다.

Fabric에는 원단명, 쇼핑몰 상품명, 품번, 상품 URL, 구매처, 구매일, 구매 가격, 색상, 규격, 원단폭, 혼용률, 메모, 평점, 재구매 의사, 원단 사진을 기록할 수 있습니다.

### Project-Fabric 연결

Project와 Fabric은 다대다 관계입니다. 하나의 Project는 여러 Fabric을 사용할 수 있고, 하나의 Fabric은 여러 Project에 사용될 수 있습니다.

이를 위해 `ProjectFabric` 연결 엔티티를 두었습니다.

- 동일 Fabric을 여러 Project에 재사용할 수 있다.
- 하나의 Project에 여러 Fabric을 연결할 수 있다.
- 연결 관계에 메모를 남길 수 있다.
- 동일 Project-Fabric 중복 연결을 막을 수 있다.
- 향후 사용량, 용도, 정렬 등으로 확장할 수 있다.

### Photo 관리

Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 엔티티입니다.

| ownerType | 설명 |
| --- | --- |
| PROJECT | Project 사진 |
| DAILY_LOG | DailyLog 사진 |
| FABRIC | Fabric 사진 |

사진 수 제한은 다음과 같습니다.

| 대상 | 최대 사진 수 |
| --- | --: |
| Project | 15장 |
| DailyLog | 15장 |
| Fabric | 2장 |

### 이미지 업로드와 리사이징

이미지 업로드는 Presigned URL 기반 S3 직접 업로드 방식을 사용합니다.

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

업로드된 원본 이미지는 Worker가 비동기로 처리하며 Original, Medium, Thumbnail 이미지를 생성합니다.

### Calendar / Contribution

Calendar는 월별 작업 기록을 보여주고, Contribution은 GitHub Contribution처럼 날짜별 작업 시간을 단계로 표현합니다.

두 기능 모두 별도 테이블에 저장하지 않고, PUBLISHED DailyLog를 기준으로 조회 시 계산합니다.

| Level | 기준 |
| --- | --- |
| 0 | 0분 |
| 1 | 1분 이상 30분 이하 |
| 2 | 31분 이상 120분 이하 |
| 3 | 121분 이상 240분 이하 |
| 4 | 241분 이상 |

## 기술 스택

### Backend

| 영역 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot |
| Security | Spring Security |
| Authentication | JWT, Refresh Token |
| ORM | Spring Data JPA |
| Query | QueryDSL |
| Migration | Flyway |
| Test | JUnit 5 |
| API Docs | Springdoc OpenAPI |

### Database / Infra

| 영역 | 기술 |
| --- | --- |
| Database | PostgreSQL |
| Storage | AWS S3 |
| CDN | CloudFront |
| Queue | Redis Queue |
| Async Processing | Worker |
| Deployment | Docker, Docker Compose |
| Server | AWS EC2 |
| Reverse Proxy | Nginx |
| CI/CD | GitHub Actions |

## 아키텍처

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

MVP는 Docker Compose 기반으로 `app`, `worker`, `postgres`, `queue`, `nginx` 실행 단위를 분리할 계획입니다.

## 핵심 도메인 관계

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

## 핵심 설계 결정

- Fabric은 Project의 속성이 아니라 독립 도메인으로 설계한다.
- Project와 Fabric은 `ProjectFabric` 연결 엔티티로 연결한다.
- DailyLog는 DRAFT/PUBLISHED 상태를 가진다.
- DailyLog 작업 시간은 TimeEntry 합산으로 계산한다.
- Photo는 Project, DailyLog, Fabric에서 공통으로 사용하는 이미지 엔티티로 설계한다.
- Calendar와 Contribution은 별도 저장 없이 조회 시점에 계산한다.
- AI 기반 원단 자동 기록은 MVP가 아닌 Future Scope로 분리한다.

## API 요약

| 도메인 | 주요 API |
| --- | --- |
| Auth/User | 카카오 로그인, 토큰 재발급, 로그아웃, 내 정보 조회, 회원 탈퇴 |
| Project | 생성, 목록 조회, 상세 조회, 수정, 삭제, 상태 변경 |
| DailyLog | 생성, 프로젝트별 목록, 날짜별 목록, 상세 조회, 수정, 발행, 삭제 |
| Fabric / ProjectFabric | 원단 등록/조회/수정/삭제, 프로젝트-원단 연결/수정/삭제 |
| Photo | Presigned URL 발급, 업로드 완료 처리, 목록 조회, 대표 사진 설정, 정렬, 삭제 |
| Calendar / Contribution | 월별 Calendar 조회, 특정 날짜 요약, 연도별 Contribution 조회 |

## 현재 저장소 상태

이 저장소는 현재 문서와 개발 디렉터리 구조를 먼저 정리한 상태입니다. `backend/`와 `frontend/` 폴더는 유지하지만, 아직 실제 구현 코드는 생성하지 않았습니다.

```text
sorosoro/
├── README.md
├── .gitignore
├── .env.example
├── docker-compose.yml
├── backend/
├── frontend/
└── docs/
    ├── README.md
    ├── 01_PRD.md.md
    ├── 02_SRS.md.md
    ├── 03_ADR.md.md
    ├── 04_ERD.md.md
    ├── 05_Backend_Design.md.md
    ├── 06_Architecture.md.md
    ├── 07.API.md.md
    ├── 08_Implementation.md.md
    ├── 09_Test Strategy.md.md
    ├── 10_Portfolio README.md
    └── 11_Wireframe.md.md
```

## 문서

프로젝트 문서는 `docs/` 폴더에서 관리합니다.

- [문서 목록](docs/README.md)
- [포트폴리오 README 원문](docs/10_Portfolio%20README.md)

## 개발 예정

구현 단계에서는 문서에 정의된 설계를 기준으로 백엔드와 프론트엔드 코드를 추가할 예정입니다.

- `backend/`: Java 17, Spring Boot 기반 API Server 및 Worker
- `frontend/`: 소로소로 MVP 화면 구현

현재 단계에서는 문서와 저장소 구조 정리만 완료했습니다.
