# AI-001 주문 캡처 기반 원단 가져오기

2026-10-04 사용자 결정: 본인 사용부터 시작한다. 상품 URL을 사용자가 하나씩 입력하거나 모든 항목을 확인하는 절차를 요구하지 않는다. 캡처에서 구매 기록을 자동 등록하고 필요한 항목만 수정한다. 완성도가 낮은 판매처의 상세 보강은 제외해도 된다.

이 티켓 범위에서 기존 PRD/ADR-011의 AI Future Scope 및 필수 검수 정책을 위 결정으로 변경한다. 프로젝트/일지/사진 Worker 전체 구현을 선행 조건으로 삼지 않는다.

## 제품/도메인

- ImportJob은 로그인 사용자와 원본 이미지 하나에 속한다. 여러 이미지는 화면에서 개별 작업으로 올린다.
- ImportItem은 캡처에서 읽은 구매 항목이다. 원문, 수량, 가격 종류/문구, 주문번호와 출처를 보존한다.
- 이름을 읽은 정상 항목은 Fabric으로 자동 등록한다. 읽지 못한 항목/겹치는 구매 의심 항목은 NEEDS_REVIEW로 보존하고 다른 항목은 계속 등록한다.
- Fabric.purchasePrice는 KRW 항목 합계만 사용한다. 단가/주문 전체 금액/불명확한 금액을 구매 합계로 추정하지 않는다. 구매일을 오늘로 대체하지 않는다.
- 같은 URL의 다른 옵션과 재구매는 허용한다. 같은 사용자+이미지 SHA-256은 동일 Job을 반환한다. 같은 판매처+주문번호+상품/옵션의 기존 항목은 중복 가능성으로 표시하고 별도 등록은 사용자 결정으로 한다. 주문번호가 없으면 별개 구매를 임의로 합치지 않는다.
- 추출 실패/비활성 판매처/상품 검색 실패는 기존 구매 기록을 삭제하지 않는다. 상품 페이지 가격은 구매 가격을 변경하지 않는다. 보강은 사용자가 수정하지 않은 빈 상세 필드만 채운다.
- 모든 결과는 수정/삭제 가능하다. 가져오기 취소는 해당 작업에서 새로 만든 Fabric만 삭제한다. ProjectFabric 또는 Photo가 연결된 기록이 있으면 취소를 거부한다.

## 구조/책임

Spring가 인증, 소유권, PostgreSQL 작업/원본/결과, 중복 처리와 Fabric 쓰기를 담당한다. Python은 DB/Notion 접근 없이 JSON 결과를 반환한다. 기존 PoC의 Gemini Vision, 상품코드 기반 검색, 페이지 파싱 흐름을 재사용한다.

개인용 1개 Spring 인스턴스의 DB 폴링 작업자와 내부 Python HTTP 서비스로 시작한다. 접수 트랜잭션과 외부 호출을 분리한다. 원자적 작업 claim/lease를 사용하고 만료 작업은 제한된 횟수로 재시도한다. 완료 기록과 원단 저장은 같은 트랜잭션으로 처리한다. 오래된 실행 결과는 claim token으로 거부한다. 모델/네트워크 실패를 무한 재시도하지 않는다.

원본 이미지는 최대 10 MiB, JPEG/PNG/WebP만 허용하며 private bytea로 보관한다. 완료 후 7일에 원본만 지운다. 작업 취소 시 즉시 지운다. 응답/로그에 이미지·키·주문 원문 전체를 출력하지 않는다. Python 내부 API는 별도 token으로 보호하고 Docker 외부 포트를 열지 않는다.

Fabric.purchaseQuantity는 캡처의 수량 문자열(예: `2마`)이며 수정 가능하다.

## API

모든 사용자 API는 기존 JWT 사용자로 소유권을 확인한다.

- POST /api/v1/fabric-imports : multipart `image`, optional `seller`. 202 Job (중복도 기존 Job), 400 형식/용량 오류.
- GET /api/v1/fabric-imports : 최근 작업 목록.
- GET /api/v1/fabric-imports/{id} : 상태/항목/오류/등록 원단 IDs.
- GET /api/v1/fabric-imports/{id}/image : 소유자만 원본 조회. 보관 만료 시 404.
- POST /api/v1/fabric-imports/{id}/retry : 실패한 작업만 재시도. 완료 작업은 재등록하지 않음.
- POST /api/v1/fabric-imports/{id}/items/{itemId}/register : 확인 필요 항목 수정 후 등록. 반복 호출은 기존 결과.
- DELETE /api/v1/fabric-imports/{id} : 작업 취소 및 해당 작업이 만든 연결되지 않은 원단만 삭제.
- GET /api/v1/fabrics : 기존 페이지 계약/keyword,storeName,repurchaseIntention 필터.
- DELETE /api/v1/fabric-imports/{id}/items/{itemId} : 항목 하나만 제외.
- DELETE /api/v1/fabrics/{id} : 원단과 프로젝트 연결을 삭제, 프로젝트 자체는 유지. 사진이 연결된 경우 이 티켓에서는 삭제 거부.
- GET, PUT /api/v1/fabrics/{id} : 개인 원단 조회/수정. PUT은 기존 Fabric 필드 계약.
- 공개 /api/v1/import-ui-config : Kakao public client ID, demo 활성 여부만 제공.

상태: QUEUED → PROCESSING → COMPLETED 또는 FAILED; 취소 시 CANCELLED. 항목: REGISTERED / NEEDS_REVIEW / DISMISSED. 제외한 항목은 재전송으로 복원되지 않는다. 보강: PENDING → COMPLETE / SKIPPED / FAILED. UI는 구매 등록 상태와 보강 상태를 구분한다.

## 실행/검증

정상 입력, 구매금액 의미, 날짜 누락, 사이트 제외, 이름 누락, 같은 이미지 재전송, 겹치는 주문, 재구매, 소유권, 작업 lease, 재시작, 취소, 사용자 수정 보존을 자동 테스트한다. Java 자동 테스트는 실제 PostgreSQL/Flyway와 내부 HTTP stub을 사용하고, Python HTTP 테스트는 모델 호출을 대체한다. 실제 Python/Gemini 연결은 아래 별도 실행으로 검증했다.

사이트별 보강은 allowlist로 개별 활성화한다. 네 기존 판매처는 구매 기록 판매처로 인식하지만, 실제 검색/일치 검증을 통과한 판매처만 자동 보강한다. 불명확한 매칭은 후보를 임의로 선택하지 않는다.

### 2026-10-04 실행 결과

- Java 17 `./gradlew test`: 26 tests, 0 failures/errors/skipped. Python: 11 passed.
- Docker Compose 설정 검사 및 app/receipt-worker 이미지 빌드 성공. JS 구문 검사 통과.
- 별도 로컬 PostgreSQL, Spring, Python, 실제 Gemini `gemini-3.1-flash-lite`로 검증용 가상 주문 이미지 2개 항목을 자동 등록했다. 실제 개인정보/구매 내역을 사용하지 않았다.
- 브라우저에서 화이트 2마/16,000원, 네이비 1마/7,500원, 주문일을 확인했다. 패션스타트 63-929의 상품 링크/소재/폭 보강을 확인했으며 구매 값은 유지됐다.
- 같은 이미지 재전송 후 2건 유지, 수정 후 새로고침 유지, 항목 하나 제외 후 1건 유지 및 재전송으로 복원되지 않음을 확인했다.
- 1280px 데스크톱/390px 모바일 화면과 편집 창을 확인했다. 가로 넘침 및 JavaScript 실행 오류 없음.
- 검증은 합성 주문 샘플과 사이트 한 상품에 한정된다. 실제 다양한 주문 화면의 추출 정확도, 카카오 실로그인, 운영 배포는 검증하지 않았다.
- Gemini가 숫자/배열 상한이 포함된 스키마를 거부하는 문제를 실제 호출에서 확인했다. 전송 스키마의 복잡한 제약을 제거하고 로컬 금액/항목 수 검증은 유지했다.
