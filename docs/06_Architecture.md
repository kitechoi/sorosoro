# 06. Architecture — 소로소로(SOROSORO)

> 소로소로의 시스템 구성, 실행 구조, 배포 구조, 외부 서비스 연동 방식을 정의하는 문서

---

# 1. 문서 개요

## 1.1 문서 목적

이 문서는 소로소로(SOROSORO)의 전체 아키텍처를 정의한다.

소로소로는 Java 17, Spring Boot, PostgreSQL, S3, CloudFront 기반의 개인 재봉 기록 서비스이다.  
본 문서는 백엔드 애플리케이션이 실제 배포 환경에서 어떻게 실행되고, 각 컴포넌트가 어떻게 연결되는지 설명한다.

본 문서에서는 다음 내용을 정의한다.

- 전체 시스템 구성
    
- API Server와 Worker 실행 구조
    
- Docker Compose 기반 실행 구조
    
- EC2 배포 구조
    
- Nginx Reverse Proxy 구조
    
- PostgreSQL 연결 구조
    
- Queue 기반 비동기 처리 구조
    
- S3와 CloudFront 이미지 처리 구조
    
- GitHub Actions 배포 흐름
    
- 환경 변수 관리
    
- 로그와 장애 대응 기준
    
- 향후 확장 방향
    

---

## 1.2 문서 범위

본 문서는 MVP 배포 구조를 기준으로 작성한다.

포함 범위는 다음과 같다.

- Spring Boot API Server
    
- Image Processing Worker
    
- PostgreSQL
    
- Queue
    
- Nginx
    
- AWS EC2
    
- AWS S3
    
- CloudFront
    
- GitHub Actions
    
- Docker
    
- Docker Compose
    

제외 범위는 다음과 같다.

- Kubernetes
    
- AWS ECS
    
- AWS RDS
    
- AWS SQS
    
- ALB
    
- CloudWatch 기반 고도화 모니터링
    
- AI 원단 자동 기록 인프라
    
- Multi-region 배포
    
- Auto Scaling
    

위 항목들은 MVP 이후 확장 대상으로 관리한다.

---

# 2. 전체 시스템 구성

## 2.1 High-level Architecture

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
                    AWS S3
                         |
                         v
                    CloudFront
                         |
                         v
                      Client
```

---

## 2.2 구성 요소 요약

|구성 요소|역할|
|---|---|
|Client|웹 클라이언트, API 호출, S3 직접 업로드|
|Nginx|외부 요청 수신, API Server로 Reverse Proxy|
|API Server|인증, 도메인 API, Presigned URL 발급, Queue 발행|
|Worker|이미지 리사이징 비동기 처리|
|PostgreSQL|서비스 데이터 저장|
|Queue|비동기 이미지 처리 작업 전달|
|S3|원본/변환 이미지 저장|
|CloudFront|이미지 CDN 제공|
|GitHub Actions|빌드, 테스트, 배포 자동화|
|EC2|Docker Compose 기반 애플리케이션 실행 서버|

---

# 3. 실행 단위

## 3.1 실행 단위 개요

소로소로 MVP는 다음 실행 단위로 구성된다.

```text
app
worker
postgres
queue
nginx
```

|실행 단위|설명|
|---|---|
|app|Spring Boot API Server|
|worker|이미지 리사이징 Worker|
|postgres|PostgreSQL Database|
|queue|이미지 처리 작업 Queue|
|nginx|Reverse Proxy|

---

## 3.2 API Server

API Server는 외부 HTTP 요청을 처리한다.

주요 책임은 다음과 같다.

- 카카오 로그인
    
- JWT 인증
    
- Project API
    
- DailyLog API
    
- Fabric API
    
- Photo API
    
- Calendar API
    
- Presigned URL 발급
    
- Photo 업로드 완료 처리
    
- Queue에 이미지 처리 작업 발행
    

API Server는 외부 요청을 받는 유일한 애플리케이션 실행 단위이다.

---

## 3.3 Worker

Worker는 외부 HTTP 요청을 받지 않는다.

Worker의 책임은 이미지 처리이다.

주요 작업은 다음과 같다.

- Queue에서 photoId 수신
    
- Photo 조회
    
- S3 원본 이미지 다운로드
    
- Medium 이미지 생성
    
- Thumbnail 이미지 생성
    
- 변환 이미지 S3 업로드
    
- Photo 상태 변경
    

Worker는 API Server와 독립적으로 실행된다.

---

## 3.4 API Server와 Worker 분리 이유

이미지 리사이징은 CPU와 메모리를 사용하는 작업이다.

이를 API 요청 안에서 처리하면 다음 문제가 생길 수 있다.

- API 응답 지연
    
- 서버 부하 증가
    
- 요청 타임아웃
    
- 동시 업로드 시 처리 병목
    
- 이미지 처리 실패가 API 실패로 직접 연결
    

따라서 API Server는 요청 처리와 Queue 발행까지만 담당하고, Worker가 실제 이미지 처리를 담당한다.

---

# 4. Docker Compose 구조

## 4.1 Docker Compose 사용 이유

MVP에서는 Docker Compose를 사용해 단일 EC2 서버 안에서 여러 실행 단위를 관리한다.

Docker Compose를 사용하는 이유는 다음과 같다.

- 로컬과 운영 환경을 유사하게 구성할 수 있다.
    
- API Server와 Worker를 분리 실행할 수 있다.
    
- PostgreSQL과 Queue를 함께 실행할 수 있다.
    
- 1인 개발자가 관리하기에 복잡도가 적절하다.
    
- 향후 ECS로 이전할 때 컨테이너 기반 실행 구조를 재사용할 수 있다.
    

---

## 4.2 예상 docker-compose 구성

```yaml
services:
  app:
    image: sorosoro-app:latest
    container_name: sorosoro-app
    env_file:
      - .env
    environment:
      - APP_MODE=api
      - SPRING_PROFILES_ACTIVE=prod
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - queue
    restart: always

  worker:
    image: sorosoro-app:latest
    container_name: sorosoro-worker
    env_file:
      - .env
    environment:
      - APP_MODE=worker
      - SPRING_PROFILES_ACTIVE=worker
    depends_on:
      - postgres
      - queue
    restart: always

  postgres:
    image: postgres:16
    container_name: sorosoro-postgres
    env_file:
      - .env
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: always

  queue:
    image: redis:7
    container_name: sorosoro-queue
    ports:
      - "6379:6379"
    restart: always

  nginx:
    image: nginx:stable
    container_name: sorosoro-nginx
    volumes:
      - ./nginx/default.conf:/etc/nginx/conf.d/default.conf
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - app
    restart: always

volumes:
  postgres_data:
```

---

## 4.3 Queue 선택 기준

MVP에서는 운영 복잡도를 줄이기 위해 Redis 기반 Queue를 우선 검토한다.

|선택지|장점|단점|
|---|---|---|
|Redis Queue|Docker Compose로 쉽게 실행 가능, 구현 단순|메시지 내구성은 SQS보다 약함|
|AWS SQS|관리형 Queue, 안정성 높음|AWS 설정과 비용, 로컬 개발 복잡도 증가|
|DB Polling|별도 Queue 불필요|DB 부하, 처리 지연, 구조적 명확성 부족|

MVP에서는 Redis Queue를 사용하고, 향후 AWS 환경 확장 시 SQS로 이전할 수 있다.

---

## 4.4 로컬 개발 환경

로컬에서는 다음 구성을 사용할 수 있다.

```text
app
worker
postgres
queue
```

Nginx는 로컬 개발에서 필수는 아니다.

로컬 개발 시에는 다음 방식으로 접근할 수 있다.

```text
http://localhost:8080
```

운영 환경에서는 Nginx를 통해 접근한다.

---

# 5. 애플리케이션 실행 모드

## 5.1 실행 모드 개요

소로소로는 같은 코드베이스를 사용하되, 실행 모드에 따라 API Server와 Worker를 분리한다.

```text
APP_MODE=api
APP_MODE=worker
```

또는 Spring Profile을 사용할 수 있다.

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_PROFILES_ACTIVE=worker
```

---

## 5.2 API 모드

API 모드는 HTTP 요청을 처리한다.

활성화되는 구성은 다음과 같다.

- Controller
    
- Security Filter
    
- Service
    
- Repository
    
- S3 Presigned URL Client
    
- Queue Producer
    
- Swagger/OpenAPI
    

API 모드에서는 Worker consumer가 실행되지 않아야 한다.

---

## 5.3 Worker 모드

Worker 모드는 이미지 처리 작업을 수행한다.

활성화되는 구성은 다음과 같다.

- Queue Consumer
    
- Photo Repository
    
- S3 Storage Client
    
- Image Resize Processor
    
- Photo 상태 변경 Service
    

Worker 모드에서는 외부 HTTP Controller가 필요하지 않다.

MVP에서는 구현 편의를 위해 HTTP Server가 떠 있더라도 외부에 노출하지 않는다.  
향후에는 Worker 전용 Application 또는 profile 기반 Bean 분리를 적용할 수 있다.

---

## 5.4 Bean 분리 기준

Spring Bean은 실행 모드에 따라 분리할 수 있다.

예시는 다음과 같다.

```java
@Profile("prod")
@RestController
public class PhotoController {
}
```

Worker Consumer는 다음처럼 분리할 수 있다.

```java
@Profile("worker")
@Component
public class PhotoResizeWorker {
}
```

또는 조건부 Bean을 사용할 수 있다.

```java
@ConditionalOnProperty(name = "app.mode", havingValue = "worker")
```

MVP에서는 `app.mode` 기반 조건부 실행을 우선 검토한다.

---

# 6. Nginx 구조

## 6.1 Nginx 역할

Nginx는 외부 요청을 API Server로 전달한다.

역할은 다음과 같다.

- HTTP/HTTPS 요청 수신
    
- API Server로 Reverse Proxy
    
- 요청 크기 제한
    
- 프록시 헤더 전달
    
- 헬스 체크 경로 제공
    
- TLS 인증서 적용
    

---

## 6.2 요청 흐름

```text
Client
→ Nginx: https://api.sorosoro.com
→ app:8080
→ Spring Boot API Server
```

---

## 6.3 예상 Nginx 설정

```nginx
server {
    listen 80;
    server_name api.sorosoro.com;

    location / {
        proxy_pass http://app:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        client_max_body_size 10m;
    }

    location /health {
        proxy_pass http://app:8080/actuator/health;
    }
}
```

HTTPS 적용 시 Certbot 또는 별도 인증서 설정을 추가한다.

---

## 6.4 이미지 요청은 Nginx를 거치지 않는다

이미지 파일은 API Server나 Nginx가 직접 제공하지 않는다.

이미지 조회 흐름은 다음과 같다.

```text
Client
→ CloudFront
→ S3
```

API Server는 Photo의 CloudFront URL만 반환한다.

---

# 7. Database Architecture

## 7.1 PostgreSQL 실행 구조

MVP에서는 PostgreSQL을 Docker Compose의 `postgres` 서비스로 실행한다.

```text
app
→ postgres:5432
worker
→ postgres:5432
```

API Server와 Worker는 같은 PostgreSQL을 사용한다.

---

## 7.2 데이터 저장 범위

PostgreSQL에는 다음 데이터를 저장한다.

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
    

이미지 파일 자체는 PostgreSQL에 저장하지 않는다.

이미지 파일은 S3에 저장하고, PostgreSQL에는 S3 Key만 저장한다.

---

## 7.3 Flyway Migration

스키마 변경은 Flyway로 관리한다.

Migration 파일 예시는 다음과 같다.

```text
V1__create_users.sql
V2__create_projects.sql
V3__create_daily_logs.sql
V4__create_fabrics.sql
V5__create_photos.sql
```

Flyway를 사용하는 이유는 다음과 같다.

- DB 스키마 변경 이력 관리
    
- 로컬과 운영 환경 스키마 일관성 유지
    
- 배포 시 자동 마이그레이션 가능
    
- 포트폴리오에서 운영 친화적 설계 설명 가능
    

---

## 7.4 PostgreSQL 백업 정책

MVP에서는 최소한 다음 백업 정책을 고려한다.

- PostgreSQL volume 유지
    
- 주기적 `pg_dump`
    
- dump 파일을 S3 또는 외부 저장소에 저장
    
- 배포 전 수동 백업 가능
    

향후 RDS로 이전하면 자동 백업과 스냅샷을 활용할 수 있다.

---

# 8. Image Storage Architecture

## 8.1 이미지 저장 구조

이미지 파일은 S3에 저장한다.

Photo 테이블에는 S3 Key만 저장한다.

```text
Photo
 ├── originalKey
 ├── mediumKey
 └── thumbnailKey
```

---

## 8.2 S3 Key 규칙

S3 Key는 User와 Photo 기준으로 생성한다.

```text
users/{userId}/photos/{photoId}/original
users/{userId}/photos/{photoId}/medium.webp
users/{userId}/photos/{photoId}/thumbnail.webp
```

예시는 다음과 같다.

```text
users/1/photos/100/original
users/1/photos/100/medium.webp
users/1/photos/100/thumbnail.webp
```

---

## 8.3 이미지 업로드 흐름

```text
1. Client
   → API Server: Presigned URL 요청

2. API Server
   → owner 검증
   → Photo 생성
   → status = UPLOADING
   → S3 Presigned URL 발급

3. Client
   → S3: 원본 이미지 직접 업로드

4. Client
   → API Server: complete-upload 요청

5. API Server
   → S3 object 존재 확인
   → Photo status = PROCESSING
   → Queue에 photoId 발행

6. Worker
   → S3 원본 다운로드
   → medium.webp, thumbnail.webp 생성
   → S3 업로드
   → Photo status = READY
```

---

## 8.4 이미지 조회 흐름

```text
Client
→ API Server: Photo 목록 조회
→ API Server: CloudFront URL 포함 응답
→ Client
→ CloudFront: 이미지 요청
→ S3
```

API 응답에는 다음 URL을 포함할 수 있다.

- mediumUrl
    
- thumbnailUrl
    
- originalUrl
    

단, Photo가 READY 상태일 때만 변환 이미지 URL을 제공한다.

---

## 8.5 CloudFront 사용 이유

CloudFront를 사용하는 이유는 다음과 같다.

- 이미지 조회 속도 개선
    
- S3 직접 접근 최소화
    
- CDN 캐싱 활용
    
- 목록/카드 화면의 이미지 로딩 최적화
    
- 향후 Signed URL 또는 접근 제한 확장 가능
    

---

# 9. Queue / Worker Architecture

## 9.1 Queue 역할

Queue는 API Server와 Worker 사이에서 이미지 처리 작업을 전달한다.

API Server는 이미지 처리를 직접 수행하지 않고 Queue에 작업을 넣는다.

Worker는 Queue에서 작업을 꺼내 처리한다.

---

## 9.2 메시지 구조

MVP의 Queue 메시지는 단순하게 `photoId`만 포함할 수 있다.

```json
{
  "photoId": 100
}
```

Worker는 photoId를 기준으로 DB에서 Photo를 조회한다.

---

## 9.3 Worker 처리 흐름

```text
1. Queue 메시지 수신
2. photoId 추출
3. Photo 조회
4. Photo.status == PROCESSING 확인
5. S3 originalKey 다운로드
6. 이미지 메타데이터 추출
7. medium.webp 생성
8. thumbnail.webp 생성
9. S3 업로드
10. Photo 상태 READY 변경
```

실패 시 다음 흐름을 따른다.

```text
1. 예외 발생
2. 실패 로그 기록
3. Photo 상태 FAILED 변경
```

---

## 9.4 중복 처리 방지

Queue 메시지는 중복 전달될 수 있다고 가정한다.

Worker는 작업 시작 전 Photo 상태를 확인한다.

```text
if photo.status != PROCESSING:
    skip
```

이를 통해 이미 처리된 Photo를 중복 처리하지 않는다.

---

## 9.5 실패 처리

MVP에서는 실패 시 Photo 상태를 `FAILED`로 변경한다.

향후 다음 정책을 추가할 수 있다.

- 재시도 횟수 관리
    
- 실패 사유 저장
    
- 재처리 API
    
- 오래된 PROCESSING Photo 정리
    
- Dead Letter Queue
    

---

# 10. CI/CD Architecture

## 10.1 배포 목표

GitHub Actions를 사용해 배포 과정을 자동화한다.

목표는 다음과 같다.

- main branch push 시 자동 빌드
    
- 테스트 실행
    
- Docker image build
    
- EC2 서버에 배포
    
- Docker Compose로 서비스 재시작
    

---

## 10.2 배포 흐름

```text
Developer
→ GitHub push
→ GitHub Actions
→ Test
→ Build
→ Docker image build
→ Docker image push
→ EC2 SSH 접속
→ docker compose pull
→ docker compose up -d
```

---

## 10.3 GitHub Actions 예시

```yaml
name: Deploy

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Build
        run: ./gradlew clean build

      - name: Docker build
        run: docker build -t sorosoro-app:latest .

      - name: Deploy to EC2
        run: |
          ssh ${{ secrets.EC2_USER }}@${{ secrets.EC2_HOST }} "
            cd /home/ubuntu/sorosoro &&
            docker compose pull &&
            docker compose up -d
          "
```

실제 운영에서는 Docker image를 Docker Hub, GHCR, ECR 중 하나에 push한 뒤 EC2에서 pull하는 방식을 사용한다.

---

## 10.4 배포 방식 후보

|방식|설명|판단|
|---|---|---|
|EC2에서 git pull 후 build|단순하지만 서버 빌드 부담 큼|MVP 초기 가능|
|GitHub Actions에서 jar build 후 전송|서버 빌드 부담 낮음|가능|
|Docker image registry 사용|가장 명확한 컨테이너 배포|우선 검토|
|ECR 사용|AWS 연동 좋음|향후 검토|

MVP에서는 GitHub Container Registry 또는 Docker Hub를 우선 검토한다.

---

# 11. 환경 변수 관리

## 11.1 환경 변수 원칙

민감 정보는 코드에 하드코딩하지 않는다.

운영 환경 설정은 `.env` 또는 GitHub Actions Secrets로 관리한다.

`.env` 파일은 Git에 포함하지 않는다.

---

## 11.2 주요 환경 변수

```text
SPRING_PROFILES_ACTIVE=prod
APP_MODE=api

DB_HOST=postgres
DB_PORT=5432
DB_NAME=sorosoro
DB_USERNAME=sorosoro
DB_PASSWORD=secret

JWT_SECRET=secret
JWT_ACCESS_TOKEN_EXPIRE_SECONDS=3600
JWT_REFRESH_TOKEN_EXPIRE_SECONDS=1209600

KAKAO_CLIENT_ID=secret
KAKAO_CLIENT_SECRET=secret
KAKAO_REDIRECT_URI=https://api.sorosoro.com/oauth/kakao/callback

AWS_ACCESS_KEY_ID=secret
AWS_SECRET_ACCESS_KEY=secret
AWS_REGION=ap-northeast-2
S3_BUCKET=sorosoro-images
CLOUDFRONT_DOMAIN=https://cdn.sorosoro.com

QUEUE_HOST=queue
QUEUE_PORT=6379
```

---

## 11.3 환경 분리

환경은 다음처럼 분리한다.

|환경|설명|
|---|---|
|local|로컬 개발|
|test|테스트|
|prod|운영|
|worker|Worker 실행|

MVP에서는 `prod`와 `worker`를 조합하거나, `APP_MODE`로 실행 모드를 구분한다.

---

# 12. Network Architecture

## 12.1 외부 접근

외부에서 접근 가능한 컴포넌트는 Nginx뿐이다.

```text
Internet
→ Nginx
→ app
```

PostgreSQL, Redis Queue, Worker는 외부에 직접 노출하지 않는다.

---

## 12.2 내부 통신

Docker Compose 네트워크 안에서 서비스 이름으로 통신한다.

|출발|도착|주소|
|---|---|---|
|nginx|app|[http://app:8080](http://app:8080/)|
|app|postgres|postgres:5432|
|app|queue|queue:6379|
|worker|postgres|postgres:5432|
|worker|queue|queue:6379|
|app/worker|S3|AWS endpoint|

---

## 12.3 보안 그룹 기준

EC2 보안 그룹에서는 다음 포트만 외부에 개방한다.

|포트|용도|
|--:|---|
|22|SSH|
|80|HTTP|
|443|HTTPS|

다음 포트는 외부에 개방하지 않는다.

|포트|용도|
|--:|---|
|8080|Spring Boot app|
|5432|PostgreSQL|
|6379|Redis Queue|

---

# 13. Logging & Monitoring

## 13.1 로그 기준

MVP에서는 애플리케이션 로그를 표준 출력으로 남긴다.

Docker Compose 환경에서는 다음 명령으로 로그를 확인한다.

```bash
docker compose logs -f app
docker compose logs -f worker
```

---

## 13.2 주요 로그 대상

|대상|로그 내용|
|---|---|
|API Server|요청 처리, 주요 예외, 인증 실패|
|Worker|photoId 처리 시작/성공/실패|
|Photo|상태 변경|
|Queue|메시지 발행/수신|
|S3|업로드 완료 확인 실패, 삭제 실패|
|Auth|로그인, 토큰 재발급, 로그아웃|

---

## 13.3 에러 로그 기준

다음 상황은 반드시 에러 로그를 남긴다.

- Worker 이미지 처리 실패
    
- S3 object 존재 확인 실패
    
- S3 업로드 실패
    
- S3 삭제 실패
    
- Queue 발행 실패
    
- DB 트랜잭션 실패
    
- 인증 토큰 파싱 실패
    

민감 정보는 로그에 남기지 않는다.

---

## 13.4 향후 모니터링 확장

MVP 이후 다음을 검토한다.

- CloudWatch Logs
    
- Sentry
    
- Prometheus + Grafana
    
- Nginx access log 분석
    
- Worker 실패 작업 대시보드
    
- Health Check 알림
    

---

# 14. Health Check

## 14.1 API Health Check

API Server는 Health Check 엔드포인트를 제공한다.

```text
GET /actuator/health
```

Nginx 또는 배포 스크립트에서 이 경로를 사용할 수 있다.

---

## 14.2 Worker Health Check

Worker는 HTTP 요청을 받지 않을 수 있으므로 다음 방식 중 하나를 고려한다.

|방식|설명|
|---|---|
|프로세스 상태 확인|컨테이너가 실행 중인지 확인|
|Queue heartbeat|Worker가 주기적으로 heartbeat 기록|
|로그 기반 확인|Worker 로그에서 처리 여부 확인|
|Actuator 노출|Worker도 내부용 actuator 제공|

MVP에서는 컨테이너 상태와 로그 확인을 우선 사용한다.

---

# 15. 장애 대응 기준

## 15.1 API Server 장애

API Server 장애 시 영향은 다음과 같다.

- 사용자는 API 요청을 수행할 수 없다.
    
- 이미지 업로드 URL 발급이 불가능하다.
    
- complete-upload 처리가 불가능하다.
    

대응은 다음과 같다.

```bash
docker compose restart app
```

---

## 15.2 Worker 장애

Worker 장애 시 영향은 다음과 같다.

- 이미지 업로드는 가능하다.
    
- complete-upload 후 Photo가 PROCESSING 상태로 남을 수 있다.
    
- 이미지 변환이 지연된다.
    

대응은 다음과 같다.

```bash
docker compose restart worker
```

Worker 재시작 후 Queue에 남은 작업을 처리한다.

---

## 15.3 PostgreSQL 장애

PostgreSQL 장애 시 영향은 다음과 같다.

- 대부분의 API 요청이 실패한다.
    
- Worker도 Photo 상태를 변경할 수 없다.
    

대응은 다음과 같다.

```bash
docker compose restart postgres
```

데이터 손상 또는 유실이 발생한 경우 백업에서 복구해야 한다.

---

## 15.4 Queue 장애

Queue 장애 시 영향은 다음과 같다.

- complete-upload 이후 이미지 처리 작업을 발행할 수 없다.
    
- Photo가 PROCESSING으로 변경되었지만 Worker에 전달되지 않을 수 있다.
    

MVP에서는 Queue 발행 실패 시 complete-upload 요청을 실패로 처리하는 것을 우선 검토한다.

향후에는 다음 방식을 검토한다.

- DB 기반 outbox
    
- 재처리 배치
    
- SQS 이전
    

---

## 15.5 S3 장애 또는 권한 오류

S3 연동 실패 시 영향은 다음과 같다.

- Presigned URL 발급 실패
    
- original object 확인 실패
    
- 이미지 다운로드 실패
    
- medium/thumbnail 업로드 실패
    
- 이미지 삭제 실패
    

대응은 다음과 같다.

- AWS 권한 확인
    
- S3 bucket policy 확인
    
- IAM key 확인
    
- 실패한 Photo를 FAILED로 변경
    
- 필요 시 재처리
    

---

# 16. 보안 기준

## 16.1 인증 보안

- Access Token은 짧은 만료 시간을 가진다.
    
- Refresh Token은 DB에 저장한다.
    
- 로그아웃 시 Refresh Token을 삭제한다.
    
- 탈퇴 시 Refresh Token을 삭제한다.
    
- JWT Secret은 환경 변수로 관리한다.
    

---

## 16.2 S3 보안

- S3 Bucket은 public write를 허용하지 않는다.
    
- 업로드는 Presigned URL을 통해서만 수행한다.
    
- Presigned URL은 짧은 만료 시간을 가진다.
    
- 업로드 가능한 contentType과 size를 제한한다.
    
- 이미지 조회는 CloudFront를 통해 제공한다.
    

---

## 16.3 서버 보안

- EC2 보안 그룹은 22, 80, 443만 개방한다.
    
- PostgreSQL과 Redis는 외부에 노출하지 않는다.
    
- `.env` 파일은 Git에 포함하지 않는다.
    
- SSH Key는 안전하게 관리한다.
    
- 운영 로그에 토큰, 비밀번호, AWS Key를 남기지 않는다.
    

---

# 17. 배포 후 점검 항목

배포 후 다음 항목을 확인한다.

## 17.1 기본 서비스 점검

```text
GET /actuator/health
```

확인 항목은 다음과 같다.

- API Server가 정상 실행되는지
    
- PostgreSQL 연결이 정상인지
    
- Nginx가 app으로 프록시하는지
    
- HTTPS가 정상 동작하는지
    

---

## 17.2 인증 점검

- 카카오 로그인 요청
    
- Access Token 발급
    
- Refresh Token 저장
    
- Access Token 재발급
    
- 로그아웃
    

---

## 17.3 이미지 업로드 점검

- Presigned URL 발급
    
- S3 원본 업로드
    
- complete-upload 호출
    
- Queue 메시지 발행
    
- Worker 처리
    
- medium/thumbnail 생성
    
- CloudFront URL 조회
    
- Photo READY 상태 확인
    

---

## 17.4 도메인 기능 점검

- Project 생성
    
- DailyLog DRAFT 저장
    
- DailyLog PUBLISHED 발행
    
- Fabric 생성
    
- Project-Fabric 연결
    
- Calendar 조회
    
- Contribution 조회
    

---

# 18. 향후 확장 방향

## 18.1 ECS 전환

현재 구조는 Docker Compose 기반 단일 EC2 배포이다.

향후 확장 구조는 다음과 같다.

|현재|확장|
|---|---|
|EC2 Docker Compose|ECS Fargate|
|app container|API Service|
|worker container|Worker Service|
|Nginx|ALB|
|Docker Hub/GHCR|ECR|

---

## 18.2 RDS 전환

현재 PostgreSQL은 Docker Compose 내부에서 실행한다.

향후에는 RDS PostgreSQL로 이전할 수 있다.

장점은 다음과 같다.

- 자동 백업
    
- 스냅샷
    
- 모니터링
    
- 장애 복구
    
- 운영 안정성 향상
    

---

## 18.3 SQS 전환

현재 Queue는 Redis 기반으로 시작할 수 있다.

향후 AWS SQS로 이전하면 다음 장점이 있다.

- 관리형 Queue
    
- 메시지 내구성 향상
    
- Dead Letter Queue 구성 가능
    
- Worker 확장 용이
    

---

## 18.4 이미지 처리 고도화

향후 이미지 처리 기능은 다음처럼 확장할 수 있다.

- 실패한 Photo 재처리
    
- 이미지 삭제 Queue
    
- HEIC 지원
    
- EXIF 회전 보정
    
- WebP 품질 조정
    
- 원본 이미지 접근 제한
    
- Signed CloudFront URL
    

---

## 18.5 AI 원단 자동 기록 인프라

AI 기능을 도입할 경우 다음 실행 단위가 추가될 수 있다.

```text
ai-worker
```

예상 흐름은 다음과 같다.

```text
FabricExtractionJob 생성
→ Queue 발행
→ AI Worker 처리
→ ExtractedFabricItem 저장
→ 사용자 검수
→ Fabric 생성
```

MVP에서는 AI Worker를 배포 구조에 포함하지 않는다.

---

# 19. Architecture 요약

소로소로 MVP 아키텍처는 다음 원칙을 따른다.

- 단일 EC2 서버에서 Docker Compose로 실행한다.
    
- Nginx가 외부 요청을 받아 API Server로 전달한다.
    
- API Server와 Worker는 별도 컨테이너로 실행한다.
    
- API Server는 HTTP 요청 처리와 Queue 발행을 담당한다.
    
- Worker는 이미지 리사이징을 비동기로 처리한다.
    
- PostgreSQL은 서비스 데이터를 저장한다.
    
- 이미지 파일은 S3에 저장한다.
    
- 이미지 조회는 CloudFront를 통해 제공한다.
    
- Queue는 API Server와 Worker 사이의 비동기 작업 전달을 담당한다.
    
- GitHub Actions로 빌드와 배포를 자동화한다.
    
- 환경 변수와 민감 정보는 코드에 포함하지 않는다.
    
- MVP에서는 단순성과 운영 가능성을 우선하고, 향후 ECS, RDS, SQS, ALB로 확장한다.