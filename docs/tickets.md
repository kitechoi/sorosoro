# Tickets — 소로소로(SOROSORO)

> Docs 기반 티켓 쪼개기 → 기능별 브랜치 분리 → 구현 → GitHub PR 흐름을 위한 작업 목록

---

## [BE-001] User 내 정보 조회 및 회원 탈퇴 API 구현

* **추천 브랜치명:** feature/ticket-be-001
* **상태:** DONE
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md
* **비고:** AI 리뷰 파이프라인 도입 전 구현·merge됨. 구현 커밋: `241ebed`
* **체크리스트:**

  * [x] `GET /api/v1/users/me` API 구현
  * [x] `DELETE /api/v1/users/me` API 구현
  * [x] `UserController` 구현
  * [x] `UserService` 구현
  * [x] `UserMeResponse` DTO 구현
  * [x] `@AuthenticationPrincipal AuthUserPrincipal` 기반 현재 사용자 조회 구현
  * [x] 회원 탈퇴 시 `User.status = DELETED` 처리
  * [x] 회원 탈퇴 시 해당 User의 RefreshToken 전체 삭제
  * [x] 탈퇴 처리용 `User.withdraw()` 또는 `User.delete()` 메서드 추가
  * [x] UserService 테스트 작성
  * [x] 인증 없는 요청 401 테스트 작성
  * [x] 탈퇴 후 User 상태 변경 테스트 작성

---

## [BE-002] Project 생성 API 구현

* **추천 브랜치명:** feature/ticket-be-002
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `POST /api/v1/projects` API 구현
  * [ ] `ProjectController` 기본 구조 생성
  * [ ] `ProjectService` 기본 구조 생성
  * [ ] `ProjectCreateRequest` DTO 구현
  * [ ] `ProjectResponse` DTO 구현
  * [ ] Project 생성 시 기본 상태 `IN_PROGRESS` 적용
  * [ ] Project 생성 시 빈 `ProjectSpecification` 함께 생성
  * [ ] title 필수 검증 구현
  * [ ] 현재 인증 User 소유로 Project 생성
  * [ ] Project 생성 성공 테스트 작성
  * [ ] title 누락 실패 테스트 작성
  * [ ] ProjectSpecification 함께 생성되는지 테스트 작성

---

## [BE-003] Project 목록 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-003
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `GET /api/v1/projects` API 구현
  * [ ] status 필터 조회 구현
  * [ ] page, size 기반 페이지 조회 구현
  * [ ] `ProjectQueryRepository` 구현
  * [ ] `ProjectListResponse` DTO 구현
  * [ ] `PageResponse<ProjectListResponse>` 반환
  * [ ] 현재 User의 Project만 조회되도록 구현
  * [ ] 최신순 정렬 기준 적용
  * [ ] Project 목록 조회 테스트 작성
  * [ ] status 필터 테스트 작성
  * [ ] 다른 사용자의 Project 제외 테스트 작성

---

## [BE-004] Project 상세 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-004
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `GET /api/v1/projects/{projectId}` API 구현
  * [ ] Project 기본 정보 조회 구현
  * [ ] ProjectSpecification 함께 조회
  * [ ] ProjectReference 목록 함께 조회
  * [ ] 연결된 Fabric 목록 함께 조회
  * [ ] Project 총 작업 시간 계산 값 포함
  * [ ] 대표 Photo thumbnailUrl 포함
  * [ ] 현재 User 소유권 검증 구현
  * [ ] Project 없음 404 처리
  * [ ] 다른 사용자 Project 접근 403 처리
  * [ ] Project 상세 조회 테스트 작성

---

## [BE-005] Project 수정 API 구현

* **추천 브랜치명:** feature/ticket-be-005
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `PATCH /api/v1/projects/{projectId}` API 구현
  * [ ] `ProjectUpdateRequest` DTO 구현
  * [ ] Project 기본 정보 수정 로직 구현
  * [ ] title 필수 검증 구현
  * [ ] `completedAt >= startedAt` 검증 구현
  * [ ] 현재 User 소유권 검증 구현
  * [ ] `ARCHIVED` 상태 Project 수정 제한 구현
  * [ ] Project 수정 성공 테스트 작성
  * [ ] 다른 사용자 Project 수정 403 테스트 작성
  * [ ] ARCHIVED Project 수정 409 테스트 작성
  * [ ] completedAt이 startedAt보다 빠른 경우 400 테스트 작성

---

## [BE-006] Project 상태 변경 API 구현

* **추천 브랜치명:** feature/ticket-be-006
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `PATCH /api/v1/projects/{projectId}/status` API 구현
  * [ ] `ProjectStatusUpdateRequest` DTO 구현
  * [ ] Project 상태 변경 로직 구현
  * [ ] `COMPLETED` 변경 시 completedAt 저장 처리
  * [ ] `ARCHIVED` 상태에서도 상태 변경 가능하도록 구현
  * [ ] 현재 User 소유권 검증 구현
  * [ ] Project 상태 변경 성공 테스트 작성
  * [ ] 다른 사용자 Project 상태 변경 403 테스트 작성
  * [ ] 유효하지 않은 상태값 요청 실패 테스트 작성

---

## [BE-007] Project 삭제 API 구현

* **추천 브랜치명:** feature/ticket-be-007
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `DELETE /api/v1/projects/{projectId}` API 구현
  * [ ] Project 삭제 로직 구현
  * [ ] ProjectSpecification 삭제 처리
  * [ ] ProjectReference 삭제 처리
  * [ ] DailyLog 삭제 처리
  * [ ] DailyLogTimeEntry 삭제 처리
  * [ ] ProjectFabric 삭제 처리
  * [ ] Project Photo 삭제 대상 처리
  * [ ] DailyLog Photo 삭제 대상 처리
  * [ ] Fabric 자체는 삭제되지 않도록 구현
  * [ ] Project 삭제 테스트 작성
  * [ ] Project 삭제 시 Fabric 유지 테스트 작성
  * [ ] 다른 사용자 Project 삭제 403 테스트 작성

---

## [BE-008] ProjectSpecification 조회 및 수정 API 구현

* **추천 브랜치명:** feature/ticket-be-008
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `GET /api/v1/projects/{projectId}/specification` API 구현
  * [ ] `PUT /api/v1/projects/{projectId}/specification` API 구현
  * [ ] `ProjectSpecificationResponse` DTO 구현
  * [ ] `ProjectSpecificationUpdateRequest` DTO 구현
  * [ ] patternMethod enum 검증 구현
  * [ ] Project 소유권 검증 구현
  * [ ] `ARCHIVED` Project 설정 수정 제한 구현
  * [ ] ProjectSpecification 조회 테스트 작성
  * [ ] ProjectSpecification 수정 테스트 작성
  * [ ] 다른 사용자 Project 설정 접근 403 테스트 작성

---

## [BE-009] ProjectReference 등록, 수정, 삭제 API 구현

* **추천 브랜치명:** feature/ticket-be-009
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `POST /api/v1/projects/{projectId}/references` API 구현
  * [ ] `PATCH /api/v1/projects/{projectId}/references/{referenceId}` API 구현
  * [ ] `DELETE /api/v1/projects/{projectId}/references/{referenceId}` API 구현
  * [ ] `ProjectReferenceCreateRequest` DTO 구현
  * [ ] `ProjectReferenceUpdateRequest` DTO 구현
  * [ ] `ProjectReferenceResponse` DTO 구현
  * [ ] url 필수 검증 구현
  * [ ] sortOrder 기본값 또는 필수 정책 구현
  * [ ] Project 소유권 검증 구현
  * [ ] Reference가 해당 Project에 속하는지 검증
  * [ ] `ARCHIVED` Project 참고자료 수정 제한 구현
  * [ ] 참고자료 등록 테스트 작성
  * [ ] 참고자료 수정 테스트 작성
  * [ ] 참고자료 삭제 테스트 작성

---

## [BE-010] Fabric 등록 API 구현

* **추천 브랜치명:** feature/ticket-be-010
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `POST /api/v1/fabrics` API 구현
  * [ ] `FabricController` 기본 구조 생성
  * [ ] `FabricService` 기본 구조 생성
  * [ ] `FabricCreateRequest` DTO 구현
  * [ ] `FabricResponse` DTO 구현
  * [ ] name 필수 검증 구현
  * [ ] repurchaseIntention 기본값 `UNKNOWN` 적용
  * [ ] rating 1~5 검증 구현
  * [ ] purchasePrice 0 이상 검증 구현
  * [ ] 현재 User 소유로 Fabric 생성
  * [ ] Fabric 생성 성공 테스트 작성
  * [ ] name 누락 실패 테스트 작성
  * [ ] rating 범위 실패 테스트 작성

---

## [BE-011] Fabric 목록 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-011
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `GET /api/v1/fabrics` API 구현
  * [ ] keyword 검색 구현
  * [ ] storeName 필터 구현
  * [ ] repurchaseIntention 필터 구현
  * [ ] page, size 기반 페이지 조회 구현
  * [ ] `FabricQueryRepository` 구현
  * [ ] `FabricListResponse` DTO 구현
  * [ ] `PageResponse<FabricListResponse>` 반환
  * [ ] 현재 User의 Fabric만 조회되도록 구현
  * [ ] Fabric 목록 조회 테스트 작성
  * [ ] keyword 검색 테스트 작성
  * [ ] storeName 필터 테스트 작성
  * [ ] repurchaseIntention 필터 테스트 작성

---

## [BE-012] Fabric 상세 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-012
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `GET /api/v1/fabrics/{fabricId}` API 구현
  * [ ] Fabric 기본 정보 조회 구현
  * [ ] Fabric Photo 목록 포함
  * [ ] Fabric이 사용된 Project 목록 포함
  * [ ] 현재 User 소유권 검증 구현
  * [ ] Fabric 없음 404 처리
  * [ ] 다른 사용자 Fabric 접근 403 처리
  * [ ] Fabric 상세 조회 테스트 작성
  * [ ] 사용 Project 목록 포함 테스트 작성

---

## [BE-013] Fabric 수정 API 구현

* **추천 브랜치명:** feature/ticket-be-013
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `PATCH /api/v1/fabrics/{fabricId}` API 구현
  * [ ] `FabricUpdateRequest` DTO 구현
  * [ ] Fabric 수정 로직 구현
  * [ ] name 필수 검증 구현
  * [ ] rating 1~5 검증 구현
  * [ ] purchasePrice 0 이상 검증 구현
  * [ ] 현재 User 소유권 검증 구현
  * [ ] Fabric 수정 성공 테스트 작성
  * [ ] 다른 사용자 Fabric 수정 403 테스트 작성
  * [ ] rating 범위 실패 테스트 작성

---

## [BE-014] Fabric 삭제 API 구현

* **추천 브랜치명:** feature/ticket-be-014
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `DELETE /api/v1/fabrics/{fabricId}` API 구현
  * [ ] Fabric 삭제 로직 구현
  * [ ] 연결된 ProjectFabric 삭제 처리
  * [ ] Fabric Photo 삭제 대상 처리
  * [ ] Project 자체는 삭제되지 않도록 구현
  * [ ] 현재 User 소유권 검증 구현
  * [ ] Fabric 삭제 테스트 작성
  * [ ] Fabric 삭제 시 Project 유지 테스트 작성
  * [ ] Fabric 삭제 시 ProjectFabric 삭제 테스트 작성
  * [ ] 다른 사용자 Fabric 삭제 403 테스트 작성

---

## [BE-015] Project-Fabric 연결 API 구현

* **추천 브랜치명:** feature/ticket-be-015
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `POST /api/v1/projects/{projectId}/fabrics` API 구현
  * [ ] `ProjectFabricService` 구현
  * [ ] `ProjectFabricConnectRequest` DTO 구현
  * [ ] `ProjectFabricResponse` DTO 구현
  * [ ] Project 소유권 검증 구현
  * [ ] Fabric 소유권 검증 구현
  * [ ] Project가 `ARCHIVED` 상태인지 검증
  * [ ] 동일 Project-Fabric 중복 연결 방지
  * [ ] 중복 연결 시 409 처리
  * [ ] Project-Fabric 연결 성공 테스트 작성
  * [ ] 다른 사용자 Fabric 연결 403 테스트 작성
  * [ ] 중복 연결 409 테스트 작성
  * [ ] ARCHIVED Project 연결 실패 테스트 작성

---

## [BE-016] Project-Fabric 연결 메모 수정 및 삭제 API 구현

* **추천 브랜치명:** feature/ticket-be-016
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `PATCH /api/v1/projects/{projectId}/fabrics/{projectFabricId}` API 구현
  * [ ] `DELETE /api/v1/projects/{projectId}/fabrics/{projectFabricId}` API 구현
  * [ ] `ProjectFabricUpdateRequest` DTO 구현
  * [ ] 연결 메모 수정 로직 구현
  * [ ] 연결 삭제 로직 구현
  * [ ] ProjectFabric이 해당 Project에 속하는지 검증
  * [ ] Project 소유권 검증 구현
  * [ ] Project가 `ARCHIVED` 상태인지 검증
  * [ ] 연결 수정 테스트 작성
  * [ ] 연결 삭제 테스트 작성
  * [ ] 연결 삭제 시 Project와 Fabric 유지 테스트 작성
  * [ ] ARCHIVED Project 연결 수정/삭제 실패 테스트 작성

---

## [BE-017] DailyLog 생성 API 구현

* **추천 브랜치명:** feature/ticket-be-017
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `POST /api/v1/projects/{projectId}/daily-logs` API 구현
  * [ ] `DailyLogController` 기본 구조 생성
  * [ ] `DailyLogService` 기본 구조 생성
  * [ ] `DailyLogCreateRequest` DTO 구현
  * [ ] `DailyLogResponse` DTO 구현
  * [ ] DRAFT DailyLog 생성 로직 구현
  * [ ] PUBLISHED DailyLog 생성 로직 구현
  * [ ] Project 소유권 검증 구현
  * [ ] Project `ARCHIVED` 상태 검증 구현
  * [ ] PUBLISHED 생성 시 workedDate 필수 검증
  * [ ] PUBLISHED 생성 시 TimeEntry 1개 이상 검증
  * [ ] TimeEntry durationMinutes 계산 구현
  * [ ] DailyLog durationMinutes 합산 구현
  * [ ] DRAFT 생성 테스트 작성
  * [ ] PUBLISHED 생성 테스트 작성
  * [ ] PUBLISHED 필수값 누락 실패 테스트 작성
  * [ ] ARCHIVED Project 생성 실패 테스트 작성

---

## [BE-018] DailyLog 목록 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-018
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `GET /api/v1/projects/{projectId}/daily-logs` API 구현
  * [ ] `GET /api/v1/daily-logs` API 구현
  * [ ] Project별 DailyLog 목록 조회 구현
  * [ ] 날짜별 DailyLog 목록 조회 구현
  * [ ] status 필터 구현
  * [ ] date 단일 조회 구현
  * [ ] startDate, endDate 기간 조회 구현
  * [ ] page, size 기반 페이지 조회 구현
  * [ ] `DailyLogQueryRepository` 구현
  * [ ] `DailyLogListResponse` DTO 구현
  * [ ] 현재 User의 DailyLog만 조회되도록 구현
  * [ ] Project별 DailyLog 목록 테스트 작성
  * [ ] 날짜별 DailyLog 목록 테스트 작성
  * [ ] status 필터 테스트 작성
  * [ ] 다른 사용자 DailyLog 제외 테스트 작성

---

## [BE-019] DailyLog 상세 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-019
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `GET /api/v1/daily-logs/{dailyLogId}` API 구현
  * [ ] DailyLog 기본 정보 조회 구현
  * [ ] Project title 포함
  * [ ] TimeEntry 목록 포함
  * [ ] Photo 목록 포함
  * [ ] 현재 User 소유권 검증 구현
  * [ ] DailyLog 없음 404 처리
  * [ ] 다른 사용자 DailyLog 접근 403 처리
  * [ ] DailyLog 상세 조회 테스트 작성
  * [ ] TimeEntry 포함 테스트 작성
  * [ ] Photo 포함 테스트 작성

---

## [BE-020] DailyLog 수정 API 구현

* **추천 브랜치명:** feature/ticket-be-020
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `PUT /api/v1/daily-logs/{dailyLogId}` API 구현
  * [ ] `DailyLogUpdateRequest` DTO 구현
  * [ ] DRAFT 수정 로직 구현
  * [ ] PUBLISHED 수정 로직 구현
  * [ ] PUBLISHED 수정 시 workedDate 필수 유지
  * [ ] PUBLISHED 수정 시 TimeEntry 1개 이상 필수 유지
  * [ ] TimeEntry 전체 교체 로직 구현
  * [ ] durationMinutes 재계산 구현
  * [ ] DailyLog 소유권 검증 구현
  * [ ] Project `ARCHIVED` 상태 검증 구현
  * [ ] DRAFT 수정 테스트 작성
  * [ ] PUBLISHED 수정 테스트 작성
  * [ ] TimeEntry 전체 교체 테스트 작성
  * [ ] durationMinutes 재계산 테스트 작성
  * [ ] ARCHIVED Project의 DailyLog 수정 실패 테스트 작성

---

## [BE-021] DailyLog 발행 API 구현

* **추천 브랜치명:** feature/ticket-be-021
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `POST /api/v1/daily-logs/{dailyLogId}/publish` API 구현
  * [ ] `DailyLogPublishRequest` DTO 구현
  * [ ] DRAFT → PUBLISHED 상태 전환 구현
  * [ ] PUBLISHED 상태 DailyLog 재발행 방지
  * [ ] PUBLISHED → DRAFT 전환 불가 정책 반영
  * [ ] 발행 시 workedDate 필수 검증
  * [ ] 발행 시 TimeEntry 1개 이상 검증
  * [ ] 발행 시 durationMinutes 재계산
  * [ ] DailyLog 소유권 검증 구현
  * [ ] Project `ARCHIVED` 상태 검증 구현
  * [ ] DRAFT 발행 성공 테스트 작성
  * [ ] PUBLISHED 재발행 409 테스트 작성
  * [ ] workedDate 누락 실패 테스트 작성
  * [ ] TimeEntry 누락 실패 테스트 작성

---

## [BE-022] DailyLog 삭제 API 구현

* **추천 브랜치명:** feature/ticket-be-022
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `DELETE /api/v1/daily-logs/{dailyLogId}` API 구현
  * [ ] DailyLog 삭제 로직 구현
  * [ ] DailyLogTimeEntry 함께 삭제
  * [ ] DailyLog Photo 삭제 대상 처리
  * [ ] DailyLog 소유권 검증 구현
  * [ ] Project `ARCHIVED` 상태 검증 구현
  * [ ] DailyLog 삭제 테스트 작성
  * [ ] TimeEntry 함께 삭제 테스트 작성
  * [ ] 다른 사용자 DailyLog 삭제 403 테스트 작성
  * [ ] ARCHIVED Project의 DailyLog 삭제 실패 테스트 작성

---

## [BE-023] Calendar 월별 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-023
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `GET /api/v1/calendar` API 구현
  * [ ] `CalendarController` 구현
  * [ ] `CalendarService` 구현
  * [ ] `CalendarQueryRepository` 구현
  * [ ] year, month 요청값 검증 구현
  * [ ] PUBLISHED DailyLog만 집계
  * [ ] DRAFT DailyLog 제외
  * [ ] 날짜별 dailyLogCount 계산
  * [ ] 날짜별 totalDurationMinutes 계산
  * [ ] level 계산 구현
  * [ ] 월의 모든 날짜를 응답에 포함
  * [ ] Calendar 조회 테스트 작성
  * [ ] DRAFT 제외 테스트 작성
  * [ ] PUBLISHED 포함 테스트 작성
  * [ ] level 계산 테스트 작성

---

## [BE-024] 날짜별 작업 요약 API 구현

* **추천 브랜치명:** feature/ticket-be-024
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `GET /api/v1/calendar/days/{date}` API 구현
  * [ ] 특정 날짜의 PUBLISHED DailyLog 목록 조회
  * [ ] totalDurationMinutes 계산
  * [ ] dailyLogCount 계산
  * [ ] level 계산
  * [ ] DailyLog summary list 응답 구현
  * [ ] 다른 사용자의 DailyLog 제외
  * [ ] 날짜별 작업 요약 조회 테스트 작성
  * [ ] 같은 날짜의 여러 DailyLog 합산 테스트 작성
  * [ ] DRAFT 제외 테스트 작성

---

## [BE-025] Contribution 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-025
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `GET /api/v1/contributions` API 구현
  * [ ] year 요청값 검증 구현
  * [ ] 연도별 PUBLISHED DailyLog 집계 구현
  * [ ] 연도 전체 날짜 목록 생성
  * [ ] activeDays 계산
  * [ ] yearly totalDurationMinutes 계산
  * [ ] dailyLogCount 계산
  * [ ] 날짜별 level 계산
  * [ ] DRAFT DailyLog 제외
  * [ ] Contribution 조회 테스트 작성
  * [ ] activeDays 계산 테스트 작성
  * [ ] 연도 총 작업 시간 계산 테스트 작성
  * [ ] level 계산 테스트 작성

---

## [BE-026] Photo Presigned URL 발급 API 구현

* **추천 브랜치명:** feature/ticket-be-026
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/06_Architecture.md, docs/07_API.md
* **체크리스트:**

  * [ ] `POST /api/v1/photos/presigned-url` API 구현
  * [ ] `PhotoController` 기본 구조 생성
  * [ ] `PhotoService` 기본 구조 생성
  * [ ] `PhotoPresignedUrlRequest` DTO 구현
  * [ ] `PhotoPresignedUrlResponse` DTO 구현
  * [ ] ownerType 검증 구현
  * [ ] ownerId 존재 여부 검증 구현
  * [ ] owner 소유권 검증 구현
  * [ ] owner별 사진 수 제한 검증 구현
  * [ ] contentType 허용 검증 구현
  * [ ] sizeBytes 제한 검증 구현
  * [ ] Photo `UPLOADING` 상태 생성
  * [ ] S3 originalKey 생성 규칙 구현
  * [ ] `S3StorageClient` 인터페이스 구현
  * [ ] 테스트용 Fake S3StorageClient 구현
  * [ ] Presigned URL 발급 테스트 작성
  * [ ] owner 없음 404 테스트 작성
  * [ ] 다른 사용자 owner 접근 403 테스트 작성
  * [ ] 사진 수 제한 409 테스트 작성

---

## [BE-027] Photo 업로드 완료 처리 API 구현

* **추천 브랜치명:** feature/ticket-be-027
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/05_Backend_Design.md, docs/06_Architecture.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `POST /api/v1/photos/{photoId}/complete-upload` API 구현
  * [ ] Photo 소유권 검증 구현
  * [ ] Photo 상태가 `UPLOADING`인지 검증
  * [ ] S3 original object 존재 확인 구현
  * [ ] Photo 상태를 `PROCESSING`으로 변경
  * [ ] Queue에 photoId 발행
  * [ ] `PhotoQueueProducer` 인터페이스 구현
  * [ ] 테스트용 Fake PhotoQueueProducer 구현
  * [ ] complete-upload 성공 테스트 작성
  * [ ] UPLOADING이 아닌 상태 실패 테스트 작성
  * [ ] S3 object 없음 실패 테스트 작성
  * [ ] Queue 발행 여부 테스트 작성

---

## [BE-028] Photo 목록 조회 API 구현

* **추천 브랜치명:** feature/ticket-be-028
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/07_API.md
* **체크리스트:**

  * [ ] `GET /api/v1/photos` API 구현
  * [ ] ownerType, ownerId 요청값 검증 구현
  * [ ] owner 존재 여부 검증 구현
  * [ ] owner 소유권 검증 구현
  * [ ] owner별 Photo 목록 조회 구현
  * [ ] sortOrder 기준 정렬 구현
  * [ ] READY 상태 Photo에 CloudFront URL 포함
  * [ ] `CloudFrontUrlResolver` 구현
  * [ ] `PhotoResponse` DTO 구현
  * [ ] Photo 목록 조회 테스트 작성
  * [ ] 다른 사용자 owner 접근 403 테스트 작성
  * [ ] sortOrder 정렬 테스트 작성

---

## [BE-029] Photo 대표 사진 설정 API 구현

* **추천 브랜치명:** feature/ticket-be-029
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/03_ADR.md, docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `PATCH /api/v1/photos/{photoId}/thumbnail` API 구현
  * [ ] Project Photo 대표 설정 구현
  * [ ] DailyLog Photo 대표 설정 구현
  * [ ] Fabric Photo 대표 설정 불가 처리
  * [ ] Photo 소유권 검증 구현
  * [ ] Photo 상태가 `READY`인지 검증
  * [ ] 같은 owner의 기존 대표 사진 해제 구현
  * [ ] 새 대표 사진 설정 구현
  * [ ] READY Photo 대표 설정 테스트 작성
  * [ ] PROCESSING Photo 대표 설정 실패 테스트 작성
  * [ ] Fabric Photo 대표 설정 실패 테스트 작성
  * [ ] 대표 사진 1장 유지 테스트 작성

---

## [BE-030] Photo 정렬 순서 수정 및 삭제 API 구현

* **추천 브랜치명:** feature/ticket-be-030
* **상태:** TODO
* **관련 명세서:** docs/02_SRS.md, docs/05_Backend_Design.md, docs/06_Architecture.md, docs/07_API.md
* **체크리스트:**

  * [ ] `PATCH /api/v1/photos/sort-order` API 구현
  * [ ] `DELETE /api/v1/photos/{photoId}` API 구현
  * [ ] Photo 정렬 순서 수정 로직 구현
  * [ ] 요청된 Photo들이 같은 owner에 속하는지 검증
  * [ ] Photo 소유권 검증 구현
  * [ ] Photo 삭제 로직 구현
  * [ ] S3 originalKey 삭제 요청 구현
  * [ ] S3 mediumKey 삭제 요청 구현
  * [ ] S3 thumbnailKey 삭제 요청 구현
  * [ ] Project `ARCHIVED` 상태에서 관련 Photo 수정/삭제 제한 구현
  * [ ] Photo 정렬 수정 테스트 작성
  * [ ] Photo 삭제 테스트 작성
  * [ ] S3 삭제 요청 테스트 작성

---

## [BE-031] Image Processing Worker 구현

* **추천 브랜치명:** feature/ticket-be-031
* **상태:** TODO
* **관련 명세서:** docs/03_ADR.md, docs/05_Backend_Design.md, docs/06_Architecture.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] Worker 실행 모드 구성
  * [ ] Queue Consumer 구현
  * [ ] `PhotoResizeWorker` 구현
  * [ ] `ImageProcessor` 구현
  * [ ] photoId 메시지 수신 처리
  * [ ] Photo 상태가 `PROCESSING`인지 확인
  * [ ] S3 originalKey 다운로드 구현
  * [ ] medium.webp 생성 구현
  * [ ] thumbnail.webp 생성 구현
  * [ ] 변환 이미지 S3 업로드 구현
  * [ ] Photo mediumKey, thumbnailKey 저장
  * [ ] Photo width, height, sizeBytes 저장
  * [ ] Photo `READY` 상태 변경
  * [ ] 실패 시 Photo `FAILED` 상태 변경
  * [ ] 중복 메시지 skip 처리
  * [ ] Worker 성공 테스트 작성
  * [ ] READY/FAILED 상태 skip 테스트 작성
  * [ ] S3 다운로드 실패 시 FAILED 테스트 작성
  * [ ] 이미지 변환 실패 시 FAILED 테스트 작성

---

## [BE-032] Redis Queue 기반 비동기 처리 연결

* **추천 브랜치명:** feature/ticket-be-032
* **상태:** TODO
* **관련 명세서:** docs/06_Architecture.md, docs/08_Implementation.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] docker-compose.yml에 Redis queue 서비스 추가
  * [ ] application-local.yml Redis 설정 추가
  * [ ] application-prod.yml Redis 설정 추가
  * [ ] Queue Producer Redis 구현체 추가
  * [ ] Queue Consumer Redis 구현체 추가
  * [ ] app 모드에서는 Producer만 활성화
  * [ ] worker 모드에서는 Consumer만 활성화
  * [ ] Redis 연결 실패 예외 처리 구현
  * [ ] complete-upload 후 Redis 메시지 발행 통합 테스트 작성
  * [ ] Worker가 Redis 메시지를 수신하는 통합 테스트 작성

---

## [BE-033] S3 연동 구현

* **추천 브랜치명:** feature/ticket-be-033
* **상태:** TODO
* **관련 명세서:** docs/03_ADR.md, docs/05_Backend_Design.md, docs/06_Architecture.md, docs/07_API.md
* **체크리스트:**

  * [ ] AWS S3 SDK 의존성 추가
  * [ ] S3 설정 Properties 구현
  * [ ] `S3StorageClient` 실제 구현체 작성
  * [ ] Presigned URL 발급 구현
  * [ ] S3 object 존재 확인 구현
  * [ ] S3 object 다운로드 구현
  * [ ] S3 object 업로드 구현
  * [ ] S3 object 삭제 구현
  * [ ] S3 Key 생성 규칙 검증
  * [ ] 로컬 테스트에서는 Fake S3StorageClient 사용하도록 구성
  * [ ] S3 연동 단위 테스트 작성
  * [ ] Presigned URL 발급 수동 검증 절차 문서화

---

## [BE-034] Security 통합 테스트 보강

* **추천 브랜치명:** feature/ticket-be-034
* **상태:** TODO
* **관련 명세서:** docs/05_Backend_Design.md, docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] 인증 없는 보호 API 요청 401 테스트 작성
  * [ ] 유효하지 않은 Access Token 요청 401 테스트 작성
  * [ ] 다른 사용자 Project 접근 403 테스트 작성
  * [ ] 다른 사용자 DailyLog 접근 403 테스트 작성
  * [ ] 다른 사용자 Fabric 접근 403 테스트 작성
  * [ ] 다른 사용자 Photo 접근 403 테스트 작성
  * [ ] ProjectFabric 연결 시 양쪽 소유권 검증 테스트 작성
  * [ ] 에러 응답이 `code`, `message`, `details` 형식인지 검증

---

## [BE-035] API 통합 테스트 작성

* **추천 브랜치명:** feature/ticket-be-035
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] Auth API 통합 테스트 작성
  * [ ] User API 통합 테스트 작성
  * [ ] Project API 통합 테스트 작성
  * [ ] Fabric API 통합 테스트 작성
  * [ ] ProjectFabric API 통합 테스트 작성
  * [ ] DailyLog API 통합 테스트 작성
  * [ ] Calendar API 통합 테스트 작성
  * [ ] Photo API 통합 테스트 작성
  * [ ] 성공 응답 JSON 필드 검증
  * [ ] 실패 응답 JSON 필드 검증
  * [ ] HTTP Status 검증
  * [ ] 문서와 API 응답 불일치 항목 수정

---

## [BE-036] Docker Compose app + worker + postgres + redis 구조 정리

* **추천 브랜치명:** feature/ticket-be-036
* **상태:** TODO
* **관련 명세서:** docs/06_Architecture.md, docs/08_Implementation.md
* **체크리스트:**

  * [ ] docker-compose.yml에 worker 서비스 추가
  * [ ] docker-compose.yml에 redis queue 서비스 추가
  * [ ] app 컨테이너 환경 변수 `APP_MODE=api` 설정
  * [ ] worker 컨테이너 환경 변수 `APP_MODE=worker` 설정
  * [ ] app과 worker가 같은 Docker image를 사용하도록 구성
  * [ ] app은 HTTP API를 처리하도록 구성
  * [ ] worker는 Queue Consumer만 실행하도록 구성
  * [ ] postgres volume 유지 확인
  * [ ] redis 외부 포트 노출 여부 검토
  * [ ] docker compose up -d --build 검증
  * [ ] app health check 검증
  * [ ] worker 로그 검증

---

## [BE-037] Nginx Reverse Proxy 설정

* **추천 브랜치명:** feature/ticket-be-037
* **상태:** TODO
* **관련 명세서:** docs/06_Architecture.md, docs/08_Implementation.md
* **체크리스트:**

  * [ ] nginx 설정 파일 추가
  * [ ] docker-compose.yml에 nginx 서비스 추가
  * [ ] `/` 요청을 app:8080으로 proxy pass
  * [ ] `/health` 요청을 actuator health로 proxy pass
  * [ ] proxy header 설정 추가
  * [ ] client_max_body_size 설정 추가
  * [ ] 80 포트 외부 노출 설정
  * [ ] app 8080 포트 외부 노출 제거 검토
  * [ ] nginx를 통한 health check 검증
  * [ ] nginx access/error 로그 확인

---

## [BE-038] 운영 환경 설정 및 배포 문서 정리

* **추천 브랜치명:** feature/ticket-be-038
* **상태:** TODO
* **관련 명세서:** docs/06_Architecture.md, docs/08_Implementation.md, docs/10_Portfolio_README.md
* **체크리스트:**

  * [ ] `.env.example` 최신화
  * [ ] 운영 환경 변수 목록 정리
  * [ ] backend/README.md 실행 방법 최신화
  * [ ] 루트 README.md 실행 방법 최신화
  * [ ] Docker Compose 실행 명령 정리
  * [ ] Health check 명령 정리
  * [ ] 테스트 실행 명령 정리
  * [ ] Redis/S3/Worker 추가 이후 구조 반영
  * [ ] 배포 전 체크리스트 추가
  * [ ] 트러블슈팅 섹션 추가

---

## [BE-039] GitHub Actions CI 구성

* **추천 브랜치명:** feature/ticket-be-039
* **상태:** TODO
* **관련 명세서:** docs/06_Architecture.md, docs/08_Implementation.md, docs/09_Test_Strategy.md, docs/12_AI_Review_Flow.md
* **체크리스트:**

  * [ ] GitHub Actions workflow 파일 추가
  * [ ] job/check 이름을 `ci`로 고정
  * [ ] `ci` 이름은 branch protection required status check에서 참조하므로 이후 rename 금지 주석 추가
  * [ ] `pull_request` 트리거 대상 base branch를 `pre-dev`, `dev`로 제한
  * [ ] `push` 트리거 대상 branch를 `pre-dev`, `dev`로 제한
  * [ ] Java 17 setup 구성
  * [ ] Gradle cache 구성
  * [ ] `backend` working-directory에서 `./gradlew test` 실행
  * [ ] Testcontainers가 `ubuntu-latest` Docker daemon에서 동작하는지 CI 실행으로 확인
  * [ ] 테스트 실패 시 PR merge 방지 기준 문서화
  * [ ] workflow badge README에 추가
  * [ ] CI 실행 결과 확인

---

## [BE-040] GitHub Actions 배포 Workflow 초안 구성

* **추천 브랜치명:** feature/ticket-be-040
* **상태:** TODO
* **관련 명세서:** docs/06_Architecture.md, docs/08_Implementation.md, docs/10_Portfolio_README.md
* **체크리스트:**

  * [ ] main branch push 시 배포 workflow 작성
  * [ ] Docker image build 단계 작성
  * [ ] Docker image registry push 단계 작성
  * [ ] EC2 SSH 접속 단계 작성
  * [ ] EC2에서 docker compose pull 실행
  * [ ] EC2에서 docker compose up -d 실행
  * [ ] GitHub Secrets 목록 문서화
  * [ ] 실제 운영 적용 전 dry-run 방식 검토
  * [ ] 배포 실패 시 롤백 또는 수동 복구 절차 문서화

---

## [FE-001] Frontend 프로젝트 초기 세팅

* **추천 브랜치명:** feature/ticket-fe-001
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/08_Implementation.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] frontend/에 React 또는 Next.js 프로젝트 생성
  * [ ] TypeScript 설정
  * [ ] Tailwind CSS 설정
  * [ ] 기본 라우팅 구조 생성
  * [ ] 공통 layout 구성
  * [ ] 모바일 우선 viewport 기준 적용
  * [ ] API client 기본 구조 생성
  * [ ] 환경 변수 설정 파일 구성
  * [ ] frontend/README.md 작성
  * [ ] 로컬 실행 확인

---

## [FE-002] 공통 UI 컴포넌트 구현

* **추천 브랜치명:** feature/ticket-fe-002
* **상태:** TODO
* **관련 명세서:** docs/11_Wireframe.md, docs/10_Portfolio_README.md
* **체크리스트:**

  * [ ] Button 컴포넌트 구현
  * [ ] Input 컴포넌트 구현
  * [ ] Textarea 컴포넌트 구현
  * [ ] StatusBadge 컴포넌트 구현
  * [ ] BottomNav 컴포넌트 구현
  * [ ] ProjectCard 컴포넌트 구현
  * [ ] DailyLogCard 컴포넌트 구현
  * [ ] FabricCard 컴포넌트 구현
  * [ ] PhotoCard 컴포넌트 구현
  * [ ] EmptyState 컴포넌트 구현
  * [ ] ConfirmModal 컴포넌트 구현

---

## [FE-003] Auth/Login 화면 구현

* **추천 브랜치명:** feature/ticket-fe-003
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] Onboarding/Login 화면 구현
  * [ ] 카카오 로그인 버튼 UI 구현
  * [ ] 로그인 성공 후 token 저장 구조 구현
  * [ ] Access Token 저장 방식 결정
  * [ ] Refresh Token 저장 방식 결정
  * [ ] 로그인 후 Home 이동 처리
  * [ ] 로그아웃 처리
  * [ ] 인증 필요한 라우트 보호 처리

---

## [FE-004] Home 화면 구현

* **추천 브랜치명:** feature/ticket-fe-004
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] Home 화면 레이아웃 구현
  * [ ] 오늘의 작업 요약 카드 구현
  * [ ] 빠른 작성 버튼 구현
  * [ ] 최근 Project 섹션 구현
  * [ ] 최근 DailyLog 섹션 구현
  * [ ] Contribution 요약 카드 구현
  * [ ] BottomNav 연결
  * [ ] mock data 기반 화면 표시
  * [ ] API 연결 예정 주석 또는 TODO 정리

---

## [FE-005] Project 화면 구현

* **추천 브랜치명:** feature/ticket-fe-005
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] Project 목록 화면 구현
  * [ ] Project 상태 필터 UI 구현
  * [ ] Project 상세 화면 구현
  * [ ] Project 생성 화면 구현
  * [ ] Project 수정 화면 구현
  * [ ] Project 상태 변경 UI 구현
  * [ ] Project 설정 화면 구현
  * [ ] 참고자료 관리 UI 구현
  * [ ] Project API 연동
  * [ ] 로딩 상태 구현
  * [ ] Empty State 구현
  * [ ] 에러 상태 구현

---

## [FE-006] Fabric 화면 구현

* **추천 브랜치명:** feature/ticket-fe-006
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] Fabric 목록 화면 구현
  * [ ] Fabric 검색 UI 구현
  * [ ] Fabric 필터 UI 구현
  * [ ] Fabric 상세 화면 구현
  * [ ] Fabric 등록 화면 구현
  * [ ] Fabric 수정 화면 구현
  * [ ] 사용 Project 목록 UI 구현
  * [ ] Fabric API 연동
  * [ ] Empty State 구현
  * [ ] 에러 상태 구현

---

## [FE-007] Project-Fabric 연결 화면 구현

* **추천 브랜치명:** feature/ticket-fe-007
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] Project-Fabric 연결 화면 구현
  * [ ] Fabric 선택 목록 UI 구현
  * [ ] Fabric 검색 UI 구현
  * [ ] 연결 메모 입력 UI 구현
  * [ ] Project에 Fabric 연결 API 연동
  * [ ] 연결 메모 수정 UI 구현
  * [ ] 연결 삭제 UI 구현
  * [ ] 이미 연결된 Fabric 처리 UI 구현
  * [ ] ARCHIVED Project일 때 액션 제한 UI 구현

---

## [FE-008] DailyLog 화면 구현

* **추천 브랜치명:** feature/ticket-fe-008
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] DailyLog 작성 화면 구현
  * [ ] DailyLog 수정 화면 구현
  * [ ] DailyLog 상세 화면 구현
  * [ ] 작업 날짜 입력 UI 구현
  * [ ] WorkType 선택 UI 구현
  * [ ] TimeEntry 추가/삭제 UI 구현
  * [ ] TimeEntry durationMinutes 계산 UI 구현
  * [ ] 총 작업 시간 표시 구현
  * [ ] 임시저장 버튼 구현
  * [ ] 발행하기 버튼 구현
  * [ ] DailyLog API 연동
  * [ ] PUBLISHED 필수값 검증 UI 구현
  * [ ] 에러 메시지 표시 구현

---

## [FE-009] Photo 업로드 UI 구현

* **추천 브랜치명:** feature/ticket-fe-009
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] Photo 업로드 컴포넌트 구현
  * [ ] ownerType, ownerId 기반 업로드 처리
  * [ ] Presigned URL 발급 API 연동
  * [ ] S3 직접 업로드 처리
  * [ ] complete-upload API 연동
  * [ ] UPLOADING 상태 UI 구현
  * [ ] PROCESSING 상태 UI 구현
  * [ ] READY 상태 UI 구현
  * [ ] FAILED 상태 UI 구현
  * [ ] Photo 삭제 UI 구현
  * [ ] 대표 사진 설정 UI 구현
  * [ ] Photo 정렬 UI 구현

---

## [FE-010] Calendar 및 Contribution 화면 구현

* **추천 브랜치명:** feature/ticket-fe-010
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] Calendar 월별 화면 구현
  * [ ] 월 이동 UI 구현
  * [ ] 날짜별 level 표시 구현
  * [ ] 날짜 선택 UI 구현
  * [ ] 선택 날짜 작업 요약 표시
  * [ ] 날짜별 DailyLog 목록 표시
  * [ ] Contribution 화면 구현
  * [ ] 연도 이동 UI 구현
  * [ ] Contribution grid 구현
  * [ ] level legend 구현
  * [ ] Calendar API 연동
  * [ ] Contribution API 연동

---

## [FE-011] My Page 구현

* **추천 브랜치명:** feature/ticket-fe-011
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] My Page 화면 구현
  * [ ] 내 정보 조회 API 연동
  * [ ] 프로필 이미지 표시
  * [ ] 닉네임 표시
  * [ ] 로그아웃 버튼 구현
  * [ ] 회원 탈퇴 버튼 구현
  * [ ] 회원 탈퇴 확인 모달 구현
  * [ ] 로그아웃 후 로그인 화면 이동 처리
  * [ ] 탈퇴 후 로그인 화면 이동 처리

---

## [FE-012] Frontend API 연동 및 인증 흐름 정리

* **추천 브랜치명:** feature/ticket-fe-012
* **상태:** TODO
* **관련 명세서:** docs/07_API.md, docs/05_Backend_Design.md
* **체크리스트:**

  * [ ] API client 공통 모듈 구현
  * [ ] Authorization header 자동 첨부 구현
  * [ ] 401 응답 처리 구현
  * [ ] Access Token 재발급 흐름 구현
  * [ ] Refresh Token 만료 시 로그아웃 처리
  * [ ] 공통 에러 응답 처리 구현
  * [ ] PageResponse 타입 정의
  * [ ] 도메인별 API 함수 분리
  * [ ] API 타입 정의

---

## [DOC-001] 문서와 구현 결과 동기화

* **추천 브랜치명:** feature/ticket-doc-001
* **상태:** TODO
* **관련 명세서:** docs/01_PRD.md, docs/02_SRS.md, docs/03_ADR.md, docs/04_ERD.md, docs/05_Backend_Design.md, docs/06_Architecture.md, docs/07_API.md, docs/08_Implementation.md, docs/09_Test_Strategy.md, docs/10_Portfolio_README.md, docs/11_Wireframe.md
* **체크리스트:**

  * [ ] 구현된 API와 docs/07_API.md 비교
  * [ ] 실제 Entity와 docs/04_ERD.md 비교
  * [ ] 실제 패키지 구조와 docs/05_Backend_Design.md 비교
  * [ ] 실제 Docker 구조와 docs/06_Architecture.md 비교
  * [ ] 구현 순서 변경 사항 docs/08_Implementation.md 반영
  * [ ] 테스트 작성 결과 docs/09_Test_Strategy.md 반영
  * [ ] README의 기술 스택과 실행 방법 최신화
  * [ ] 변경된 결정이 있으면 docs/03_ADR.md에 추가

---

## [DOC-002] 트러블슈팅 문서 작성

* **추천 브랜치명:** feature/ticket-doc-002
* **상태:** TODO
* **관련 명세서:** docs/10_Portfolio_README.md, docs/06_Architecture.md, docs/09_Test_Strategy.md
* **체크리스트:**

  * [ ] `13_Troubleshooting.md` 생성
  * [ ] Docker Compose 실행 이슈 정리
  * [ ] Flyway migration 이슈 정리
  * [ ] JWT 인증 구현 이슈 정리
  * [ ] Testcontainers 설정 이슈 정리
  * [ ] Photo 상태 전이 이슈 정리
  * [ ] S3 연동 이슈 정리
  * [ ] Worker 처리 실패 이슈 정리
  * [ ] 문제 상황, 원인, 해결, 배운 점 형식으로 작성

---

## [QA-001] MVP 핵심 시나리오 수동 검증

* **추천 브랜치명:** feature/ticket-qa-001
* **상태:** TODO
* **관련 명세서:** docs/08_Implementation.md, docs/09_Test_Strategy.md, docs/10_Portfolio_README.md
* **체크리스트:**

  * [ ] 카카오 로그인 흐름 검증
  * [ ] 내 정보 조회 검증
  * [ ] Project 생성 검증
  * [ ] Fabric 생성 검증
  * [ ] Project에 Fabric 연결 검증
  * [ ] DailyLog DRAFT 생성 검증
  * [ ] DailyLog PUBLISHED 발행 검증
  * [ ] Calendar 반영 검증
  * [ ] Contribution 반영 검증
  * [ ] Photo Presigned URL 발급 검증
  * [ ] S3 업로드 검증
  * [ ] complete-upload 검증
  * [ ] Worker READY 처리 검증
  * [ ] README에 수동 검증 결과 정리
