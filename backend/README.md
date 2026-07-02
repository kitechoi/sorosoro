# SOROSORO Backend

소로소로(SOROSORO) 개인 재봉 기록 서비스의 Spring Boot 백엔드입니다.

## 기술 스택

- Java 17
- Spring Boot 3.x
- Gradle
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL Driver
- Validation
- Flyway
- Lombok
- Spring Boot Actuator
- Springdoc OpenAPI

## 프로젝트 구조

```text
backend/
├── build.gradle
├── Dockerfile
├── settings.gradle
├── src/
│   ├── main/
│   │   ├── java/com/sorosoro/
│   │   │   ├── SorosoroApplication.java
│   │   │   └── common/
│   │   │       ├── config/
│   │   │       ├── exception/
│   │   │       ├── response/
│   │   │       ├── security/
│   │   │       ├── time/
│   │   │       └── util/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-test.yml
│   │       └── application-prod.yml
│   └── test/java/com/sorosoro/
│       └── SorosoroApplicationTests.java
└── README.md
```

아직 도메인 Entity는 생성하지 않았습니다. 도메인별 패키지와 Entity는 문서의 구현 순서에 맞춰 추가할 예정입니다.

## Profile

- `local`: 로컬 개발용 PostgreSQL, Flyway 사용
- `test`: 기본 contextLoads 테스트용 profile. 외부 PostgreSQL 없이 테스트가 통과하도록 DB/JPA/Flyway 자동설정을 제외합니다.
- `prod`: 운영 환경용 profile. DB 접속 정보는 환경 변수로 주입합니다.

## 환경 변수

`local`, `prod` profile에서 사용하는 주요 환경 변수는 다음과 같습니다.

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

`local` profile은 기본값을 제공합니다.

```text
DB_URL=jdbc:postgresql://localhost:5432/sorosoro
DB_USERNAME=sorosoro
DB_PASSWORD=sorosoro
```

## Docker Compose 실행

로컬 개발에서는 루트의 Docker Compose로 PostgreSQL과 Spring Boot 애플리케이션을 함께 실행합니다.
먼저 Docker Desktop 또는 Docker 데몬이 실행 중이어야 합니다.

```bash
cd ..
docker compose up -d --build
```

기본 DB 이름, 사용자, 비밀번호는 모두 `sorosoro`입니다. 값을 바꾸고 싶다면 루트에서 `.env.example`을 복사해 `.env`를 만든 뒤 수정합니다.

```bash
cp .env.example .env
```

상태를 확인합니다.

```bash
docker compose ps
docker compose logs app
docker compose logs postgres
```

애플리케이션과 PostgreSQL을 중지합니다.

```bash
docker compose down
```

데이터 볼륨까지 삭제하려면 다음 명령을 사용합니다.

```bash
docker compose down -v
```

## 테스트 실행

```bash
cd backend
./gradlew test
```

Repository 매핑 테스트는 Testcontainers 기반 PostgreSQL을 사용합니다. 테스트 실행 전 Docker Desktop 또는 Docker 데몬이 실행 중이어야 합니다.

Gradle Wrapper가 없는 환경에서는 다음 명령을 사용합니다.

```bash
cd backend
gradle test
```

## Health Check

애플리케이션 실행 후 actuator health endpoint를 확인할 수 있습니다.

```text
GET http://localhost:8080/actuator/health
```

Docker 컨테이너 내부에서 직접 확인하려면 다음 명령을 사용합니다.

```bash
docker compose exec app wget -qO- http://localhost:8080/actuator/health
```

## Swagger UI

애플리케이션 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```
