# 소로소로(SOROSORO) 문서 목록

이 폴더는 개인 재봉 기록 서비스 소로소로의 제품 기획, 요구사항, 설계, 구현 계획, 테스트 전략, 화면 설계를 정리한 문서를 보관합니다.

문서 번호와 파일명은 현재 기준을 유지합니다.

## 문서 구성

| 번호 | 문서 | 설명 |
| --- | --- | --- |
| 01 | [PRD](01_PRD.md.md) | 서비스의 제품 기획 방향, 문제 정의, 핵심 목표, MVP 범위와 향후 확장 방향을 정의합니다. |
| 02 | [SRS](02_SRS.md.md) | 기능 요구사항, 사용자 흐름, 입력/출력, 비즈니스 규칙, 예외 상황, 권한 정책, 비기능 요구사항을 정의합니다. |
| 03 | [ADR](03_ADR.md.md) | 기술 스택, 도메인 분리, 인증, 이미지 처리, 배포 구조 등 주요 설계 결정과 선택 이유를 기록합니다. |
| 04 | [ERD](04_ERD.md.md) | 핵심 엔티티, 테이블 구조, 관계, 제약조건, 인덱스, 삭제 정책, 상태값을 정의합니다. |
| 05 | [Backend Design](05_Backend_Design.md.md) | 백엔드 계층 구조, 패키지 구조, 도메인별 책임, 트랜잭션 경계, DTO, 예외 처리, 테스트 기준을 정의합니다. |
| 06 | [Architecture](06_Architecture.md.md) | 시스템 구성, API Server와 Worker 실행 구조, Docker Compose, EC2, Nginx, PostgreSQL, Queue, S3, CloudFront, CI/CD 구조를 정의합니다. |
| 07 | [API](07.API.md.md) | REST API 엔드포인트, 요청/응답 형식, 에러 응답, 인증 방식, Pagination, 이미지 업로드 흐름을 정의합니다. |
| 08 | [Implementation Plan](08_Implementation.md.md) | MVP 개발 단계, 구현 우선순위, 작업 단위, 테스트 순서, 배포 순서, 완료 기준을 정리합니다. |
| 09 | [Test Strategy](09_Test%20Strategy.md.md) | 테스트 범위, 테스트 원칙, 도메인별 핵심 테스트 케이스, 배포 전 검증 시나리오를 정의합니다. |
| 10 | [Portfolio README](10_Portfolio%20README.md) | 포트폴리오 제출용 프로젝트 소개, 주요 기능, 기술 스택, 아키텍처, API 요약, 테스트 전략을 정리합니다. |
| 11 | [Wireframe](11_Wireframe.md.md) | MVP 화면 목록, 화면별 목적, 사용자 흐름, 화면 간 이동 구조, 핵심 UI 구성 요소를 정의합니다. |

## 읽는 순서

1. `01_PRD.md.md`에서 서비스의 목적과 MVP 범위를 확인합니다.
2. `02_SRS.md.md`에서 구현해야 할 기능 요구사항과 비즈니스 규칙을 확인합니다.
3. `03_ADR.md.md`, `04_ERD.md.md`, `05_Backend_Design.md.md`, `06_Architecture.md.md`에서 설계 기준을 확인합니다.
4. `07.API.md.md`에서 API 계약을 확인합니다.
5. `08_Implementation.md.md`, `09_Test Strategy.md.md`에서 구현 및 검증 계획을 확인합니다.
6. `11_Wireframe.md.md`에서 화면 흐름과 UI 구성을 확인합니다.
7. `10_Portfolio README.md`는 프로젝트를 외부에 소개할 때 참고합니다.

## 현재 개발 상태

- `backend/`: 구현 코드 생성 전
- `frontend/`: 구현 코드 생성 전
- `docs/`: 기획 및 설계 문서 정리 완료
