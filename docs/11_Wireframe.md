# 11. Wireframe — 소로소로(SOROSORO)

> 소로소로 MVP의 화면 구조, 사용자 흐름, 주요 UI 구성 요소를 정의하는 문서

---

# 1. 문서 개요

## 1.1 문서 목적

이 문서는 소로소로(SOROSORO)의 MVP 화면 설계를 정의한다.

소로소로는 개인 재봉 기록 서비스로, 사용자가 재봉 작품, 날짜별 작업 일지, 원단, 사진을 기록하고 캘린더와 재봉 잔디로 작업 습관을 확인할 수 있도록 한다.

본 문서에서는 다음 내용을 정의한다.

- 전체 화면 목록
    
- 화면별 목적
    
- 주요 사용자 흐름
    
- 화면 간 이동 구조
    
- 핵심 UI 컴포넌트
    
- MVP 와이어프레임 범위
    
- 향후 Figma 설계 기준
    

---

## 1.2 문서 범위

본 문서는 MVP 화면을 기준으로 작성한다.

MVP에 포함되는 화면은 다음과 같다.

- 온보딩 / 로그인 화면
    
- 홈 화면
    
- Project 목록 화면
    
- Project 상세 화면
    
- Project 생성/수정 화면
    
- DailyLog 작성 화면
    
- DailyLog 상세 화면
    
- Fabric 목록 화면
    
- Fabric 상세 화면
    
- Fabric 등록/수정 화면
    
- Calendar 화면
    
- Contribution 화면
    
- Photo 업로드 UI
    
- 마이페이지 화면
    

MVP에서 제외되는 화면은 다음과 같다.

- 커뮤니티 화면
    
- 공개 프로젝트 화면
    
- 댓글 화면
    
- 좋아요 화면
    
- 팔로우 화면
    
- 알림 화면
    
- 관리자 화면
    
- AI 원단 자동 기록 화면
    
- 통계 대시보드 화면
    
- 원단 재고 관리 화면
    

---

# 2. 서비스 화면 구조

## 2.1 전체 화면 구조

```text
소로소로
├── Onboarding / Login
│
├── Home
│   ├── 오늘의 작업 요약
│   ├── 최근 Project
│   ├── 최근 DailyLog
│   └── 빠른 작성 버튼
│
├── Project
│   ├── Project 목록
│   ├── Project 상세
│   ├── Project 생성
│   ├── Project 수정
│   ├── Project 설정
│   ├── 참고자료 관리
│   └── Project-Fabric 연결
│
├── DailyLog
│   ├── DailyLog 작성
│   ├── DailyLog 상세
│   ├── DailyLog 수정
│   └── DailyLog 발행
│
├── Fabric
│   ├── Fabric 목록
│   ├── Fabric 상세
│   ├── Fabric 등록
│   ├── Fabric 수정
│   └── 사용 Project 목록
│
├── Calendar
│   ├── 월별 Calendar
│   └── 특정 날짜 작업 요약
│
├── Contribution
│   └── 연도별 재봉 잔디
│
└── My Page
    ├── 내 정보
    ├── 로그아웃
    └── 회원 탈퇴
```

---

## 2.2 하단 탭 구조

MVP에서는 모바일 웹 중심 UI를 기준으로 한다.

하단 탭은 다음 5개로 구성한다.

```text
Home
Project
Calendar
Fabric
My
```

|탭|역할|
|---|---|
|Home|오늘의 작업 요약과 빠른 작성 진입|
|Project|재봉 작품 관리|
|Calendar|날짜별 작업 기록 확인|
|Fabric|원단 기록 관리|
|My|사용자 정보와 설정|

Contribution은 Calendar 탭 안에서 진입하거나 Home에서 요약 카드로 진입한다.

---

# 3. 핵심 사용자 흐름

## 3.1 최초 로그인 흐름

```text
Onboarding
→ 카카오 로그인
→ Home
```

### 설명

사용자는 카카오 계정으로 로그인한 뒤 Home 화면으로 진입한다.

---

## 3.2 Project 생성 흐름

```text
Project 목록
→ Project 생성 버튼
→ Project 생성 화면
→ 저장
→ Project 상세
```

### 설명

사용자는 새 재봉 작품을 생성한다.  
Project는 title만으로도 생성 가능하다.

---

## 3.3 DailyLog 작성 흐름

```text
Project 상세
→ DailyLog 작성 버튼
→ DailyLog 작성 화면
→ 임시저장 또는 발행
→ Project 상세 / DailyLog 상세
```

### 설명

사용자는 Project에 날짜별 작업 일지를 작성한다.  
작성 중이면 DRAFT로 저장하고, 정식 기록이면 PUBLISHED로 발행한다.

---

## 3.4 Fabric 등록 흐름

```text
Fabric 목록
→ Fabric 등록 버튼
→ Fabric 등록 화면
→ 저장
→ Fabric 상세
```

### 설명

사용자는 원단 정보를 Project와 독립적으로 등록할 수 있다.

---

## 3.5 Project에 Fabric 연결 흐름

```text
Project 상세
→ 원단 연결 버튼
→ Fabric 선택
→ 연결 메모 입력
→ 저장
→ Project 상세
```

### 설명

사용자는 등록된 Fabric을 Project에 연결한다.  
같은 Fabric은 같은 Project에 중복 연결할 수 없다.

---

## 3.6 Photo 업로드 흐름

```text
Project / DailyLog / Fabric 화면
→ 사진 추가 버튼
→ 이미지 선택
→ 업로드 진행
→ 처리 중 표시
→ READY 상태 후 이미지 표시
```

### 설명

이미지는 서버에 직접 업로드하지 않고 S3 Presigned URL을 통해 업로드한다.  
업로드 후 Worker가 이미지를 리사이징하며, 처리 중에는 placeholder를 보여준다.

---

## 3.7 Calendar 확인 흐름

```text
Calendar 탭
→ 월별 Calendar 조회
→ 날짜 선택
→ 해당 날짜 DailyLog 목록 확인
→ DailyLog 상세 진입
```

### 설명

사용자는 날짜별 작업 기록과 총 작업 시간을 확인할 수 있다.

---

## 3.8 Contribution 확인 흐름

```text
Home 또는 Calendar
→ Contribution 카드 선택
→ 연도별 재봉 잔디 확인
```

### 설명

사용자는 연도별 작업 습관을 재봉 잔디 형태로 확인한다.

---

# 4. 화면 목록

## 4.1 화면 요약

|ID|화면명|설명|MVP|
|---|---|---|---|
|WF-001|Onboarding / Login|서비스 소개와 카카오 로그인|포함|
|WF-002|Home|오늘의 기록 요약과 빠른 진입|포함|
|WF-003|Project 목록|Project 목록 조회|포함|
|WF-004|Project 상세|Project 정보, 원단, 일지, 사진 조회|포함|
|WF-005|Project 생성/수정|Project 기본 정보 입력|포함|
|WF-006|Project 설정|패턴, 완성 크기, 제작 설정 입력|포함|
|WF-007|참고자료 관리|Project 참고 URL 관리|포함|
|WF-008|DailyLog 작성/수정|작업 일지 작성, 임시저장, 발행|포함|
|WF-009|DailyLog 상세|작업 내용, 시간, 사진 조회|포함|
|WF-010|Fabric 목록|원단 목록 조회와 검색|포함|
|WF-011|Fabric 상세|원단 정보와 사용 Project 조회|포함|
|WF-012|Fabric 등록/수정|원단 정보 입력|포함|
|WF-013|Project-Fabric 연결|Project에 Fabric 연결|포함|
|WF-014|Calendar|월별 작업 기록 조회|포함|
|WF-015|날짜별 작업 요약|특정 날짜의 DailyLog 목록|포함|
|WF-016|Contribution|연도별 재봉 잔디|포함|
|WF-017|Photo 업로드 상태|업로드/처리/실패 상태 표시|포함|
|WF-018|My Page|내 정보, 로그아웃, 탈퇴|포함|

---

# 5. 화면별 와이어프레임

---

# 5.1 WF-001. Onboarding / Login

## 목적

서비스의 목적을 간단히 설명하고, 카카오 로그인을 제공한다.

---

## 주요 UI

```text
[소로소로 로고]

하루하루 재봉 과정을 기록해요.

작품, 원단, 작업 시간을
한 곳에서 차곡차곡 남기는
개인 재봉일기 서비스

[카카오로 시작하기 버튼]
```

---

## 주요 액션

|액션|결과|
|---|---|
|카카오로 시작하기|카카오 OAuth 로그인 시작|
|로그인 성공|Home 이동|
|로그인 실패|에러 메시지 표시|

---

# 5.2 WF-002. Home

## 목적

사용자가 오늘의 작업 상태를 빠르게 확인하고, 주요 기능으로 진입할 수 있도록 한다.

---

## 주요 UI

```text
[상단]
안녕하세요, {nickname}님

[오늘의 작업 카드]
오늘 작업 시간: 120분
오늘 작성한 일지: 2개

[빠른 작성]
+ Project 만들기
+ DailyLog 작성
+ Fabric 등록

[최근 Project]
- 딸기 파우치
- 카드지갑

[최근 DailyLog]
- 7/2 파우치 재단
- 7/1 안감 재봉

[Contribution 요약]
이번 달 작업일: 8일

[하단 탭]
Home | Project | Calendar | Fabric | My
```

---

## 주요 액션

|액션|결과|
|---|---|
|Project 만들기|Project 생성 화면 이동|
|DailyLog 작성|Project 선택 후 DailyLog 작성 이동|
|Fabric 등록|Fabric 등록 화면 이동|
|최근 Project 선택|Project 상세 이동|
|최근 DailyLog 선택|DailyLog 상세 이동|
|Contribution 요약 선택|Contribution 화면 이동|

---

# 5.3 WF-003. Project 목록

## 목적

사용자의 재봉 Project 목록을 조회한다.

---

## 주요 UI

```text
[상단]
Project

[필터]
전체 | 진행 중 | 보류 | 완료 | 보관

[Project 카드 목록]
[썸네일]
딸기 파우치
진행 중
총 작업 시간 240분
최근 기록 2026-07-02

[썸네일]
카드지갑
완료
총 작업 시간 360분

[+ Project 버튼]

[하단 탭]
```

---

## 주요 액션

|액션|결과|
|---|---|
|Project 카드 선택|Project 상세 이동|
|+ Project|Project 생성 화면 이동|
|상태 필터 선택|목록 필터링|

---

# 5.4 WF-004. Project 상세

## 목적

하나의 Project에 대한 정보, 작업 일지, 연결 원단, 사진을 확인한다.

---

## 주요 UI

```text
[상단]
딸기 파우치
상태: 진행 중
[수정] [상태 변경]

[대표 사진]

[기본 정보]
설명
시작일
완료일
총 작업 시간

[제작 설정]
패턴 방식
완성 크기
재봉틀 설정
바늘 / 실

[연결 원단]
딸기 면 원단
+ 원단 연결

[DailyLog 목록]
2026-07-02 파우치 재단과 재봉 110분
2026-07-01 패턴 제도 60분

[사진]
사진 그리드

[+ DailyLog 작성]
```

---

## 주요 액션

|액션|결과|
|---|---|
|수정|Project 수정 화면 이동|
|상태 변경|상태 변경 모달|
|제작 설정 선택|Project 설정 화면 이동|
|원단 연결|Project-Fabric 연결 화면 이동|
|DailyLog 선택|DailyLog 상세 이동|
|+ DailyLog 작성|DailyLog 작성 화면 이동|
|사진 추가|Photo 업로드 시작|

---

## 상태별 UI

|Project 상태|UI 동작|
|---|---|
|IN_PROGRESS|수정, DailyLog 작성, 원단 연결 가능|
|ON_HOLD|수정, DailyLog 작성, 원단 연결 가능|
|COMPLETED|수정 가능|
|ARCHIVED|조회와 상태 변경만 가능|

---

# 5.5 WF-005. Project 생성/수정

## 목적

Project의 기본 정보를 입력하거나 수정한다.

---

## 주요 UI

```text
[상단]
Project 만들기

[입력]
작품명 *
설명
시작일
완료일
회고

[저장 버튼]
```

---

## 입력 필드

|필드|필수|설명|
|---|--:|---|
|title|Y|Project 제목|
|description|N|설명|
|startedAt|N|시작일|
|completedAt|N|완료일|
|retrospective|N|회고|

---

## 주요 액션

|액션|결과|
|---|---|
|저장|Project 생성 또는 수정|
|취소|이전 화면 이동|

---

# 5.6 WF-006. Project 설정

## 목적

Project의 제작 설정 정보를 기록한다.

---

## 주요 UI

```text
[상단]
제작 설정

[패턴]
패턴 방식
패턴명

[완성 크기]
가로 cm
세로 cm
크기 메모

[재봉 설정]
재봉틀 설정
바늘
실

[기타 메모]

[저장]
```

---

## 입력 필드

|필드|설명|
|---|---|
|patternMethod|직접 제도 / 기존 패턴 / 변형|
|patternName|패턴명|
|finishedWidthCm|완성 가로|
|finishedHeightCm|완성 세로|
|finishedSizeMemo|크기 메모|
|sewingMachineSettingMemo|재봉틀 설정|
|needleMemo|바늘 메모|
|threadMemo|실 메모|
|memo|기타 메모|

---

# 5.7 WF-007. 참고자료 관리

## 목적

Project 제작에 참고한 URL을 관리한다.

---

## 주요 UI

```text
[상단]
참고자료

[참고자료 목록]
제목
URL
메모

[+ 참고자료 추가]

[저장]
```

---

## 주요 액션

|액션|결과|
|---|---|
|참고자료 추가|입력 폼 추가|
|참고자료 수정|값 수정|
|참고자료 삭제|목록에서 제거|
|저장|서버 반영|

---

# 5.8 WF-008. DailyLog 작성/수정

## 목적

Project의 날짜별 작업 일지를 작성한다.

---

## 주요 UI

```text
[상단]
작업 일지 작성

[Project 정보]
딸기 파우치

[입력]
작업 날짜
제목
작업 종류 선택
메모

[작업 시간]
+ 시간 구간 추가

15:00 ~ 15:40
메모: 겉감 재단

20:00 ~ 21:10
메모: 지퍼 달기

총 작업 시간: 110분

[사진]
+ 사진 추가

[하단 버튼]
임시저장
발행하기
```

---

## 입력 필드

|필드|DRAFT|PUBLISHED|
|---|---|---|
|workedDate|선택|필수|
|title|선택|선택|
|workTypes|선택|선택|
|timeEntries|선택|1개 이상 필수|
|memo|선택|선택|
|photos|선택|선택|

---

## 주요 액션

|액션|결과|
|---|---|
|임시저장|DRAFT 저장|
|발행하기|PUBLISHED 저장|
|시간 구간 추가|TimeEntry 입력 추가|
|시간 구간 삭제|TimeEntry 제거|
|사진 추가|Photo 업로드 시작|

---

## 검증 메시지

|상황|메시지|
|---|---|
|PUBLISHED인데 작업 날짜 없음|작업 날짜를 입력해주세요.|
|PUBLISHED인데 작업 시간 없음|정식 기록에는 작업 시간이 필요합니다.|
|종료 시간이 시작 시간보다 빠름|종료 시각은 시작 시각보다 늦어야 합니다.|

---

# 5.9 WF-009. DailyLog 상세

## 목적

작성된 DailyLog의 내용을 확인한다.

---

## 주요 UI

```text
[상단]
파우치 재단과 재봉
상태: PUBLISHED
[수정] [삭제]

[날짜]
2026-07-02

[작업 종류]
재단, 재봉

[총 작업 시간]
110분

[시간 구간]
15:00 ~ 15:40 겉감 재단
20:00 ~ 21:10 지퍼 달기

[메모]
지퍼 부분이 어려웠다.

[사진]
사진 그리드
```

---

## 주요 액션

|액션|결과|
|---|---|
|수정|DailyLog 수정 화면 이동|
|삭제|삭제 확인 모달|
|사진 선택|이미지 확대 보기|

---

# 5.10 WF-010. Fabric 목록

## 목적

사용자가 등록한 원단 목록을 조회하고 검색한다.

---

## 주요 UI

```text
[상단]
Fabric

[검색]
원단명, 상품명, 품번 검색

[필터]
구매처
재구매 의사

[Fabric 카드]
[썸네일]
딸기 면 원단
원단마켓
면 100%
재구매 의사 있음

[+ Fabric 등록]

[하단 탭]
```

---

## 주요 액션

|액션|결과|
|---|---|
|Fabric 카드 선택|Fabric 상세 이동|
|검색어 입력|Fabric 검색|
|필터 선택|목록 필터링|
|+ Fabric 등록|Fabric 등록 화면 이동|

---

# 5.11 WF-011. Fabric 상세

## 목적

원단 상세 정보와 해당 원단이 사용된 Project를 확인한다.

---

## 주요 UI

```text
[상단]
딸기 면 원단
[수정] [삭제]

[사진]
원단 사진 1~2장

[기본 정보]
상품명
품번
구매처
구매일
구매 가격
색상
규격
원단폭
혼용률
평점
재구매 의사

[상품 URL]
바로가기

[메모]
얇고 부드러움. 심지 필요.

[사용 Project]
딸기 파우치
카드지갑
```

---

## 주요 액션

|액션|결과|
|---|---|
|수정|Fabric 수정 화면 이동|
|삭제|삭제 확인 모달|
|상품 URL 선택|외부 브라우저 이동|
|사용 Project 선택|Project 상세 이동|
|사진 추가|Photo 업로드 시작|

---

# 5.12 WF-012. Fabric 등록/수정

## 목적

원단 정보를 입력하거나 수정한다.

---

## 주요 UI

```text
[상단]
Fabric 등록

[사진]
+ 원단 사진 추가

[입력]
원단명 *
상품명
품번
상품 URL
구매처
구매일
구매 가격
색상
규격
원단폭
혼용률
평점
재구매 의사
메모

[저장]
```

---

## 입력 필드

|필드|필수|설명|
|---|--:|---|
|name|Y|사용자가 부르는 원단명|
|productName|N|쇼핑몰 상품명|
|productCode|N|품번|
|productUrl|N|상품 URL|
|storeName|N|구매처|
|purchasedAt|N|구매일|
|purchasePrice|N|구매 가격|
|color|N|색상|
|size|N|규격|
|width|N|원단폭|
|materialComposition|N|혼용률|
|rating|N|1~5|
|repurchaseIntention|N|YES / NO / UNKNOWN|
|memo|N|메모|

---

# 5.13 WF-013. Project-Fabric 연결

## 목적

Project에 사용할 Fabric을 선택하고 연결한다.

---

## 주요 UI

```text
[상단]
원단 연결

[검색]
원단명, 상품명, 품번 검색

[Fabric 선택 목록]
○ 딸기 면 원단
○ 무지 아이보리 원단

[연결 메모]
겉감으로 사용

[연결하기]
```

---

## 주요 액션

|액션|결과|
|---|---|
|Fabric 선택|선택 상태 변경|
|연결 메모 입력|ProjectFabric memo 입력|
|연결하기|Project와 Fabric 연결|
|새 Fabric 등록|Fabric 등록 화면 이동|

---

## 검증 메시지

|상황|메시지|
|---|---|
|이미 연결된 Fabric|이미 연결된 원단입니다.|
|ARCHIVED Project|보관된 프로젝트에는 원단을 연결할 수 없습니다.|

---

# 5.14 WF-014. Calendar

## 목적

월별 작업 기록을 날짜 단위로 확인한다.

---

## 주요 UI

```text
[상단]
Calendar

[월 선택]
< 2026년 7월 >

[Calendar Grid]
일 월 화 수 목 금 토

1  2  3  4
   ●  ●

[날짜별 표시]
Level 0: 표시 없음
Level 1: 연한 표시
Level 2: 중간 표시
Level 3: 진한 표시
Level 4: 가장 진한 표시

[선택 날짜 요약]
2026-07-02
총 작업 시간 180분
작업 일지 2개

[DailyLog 목록]
딸기 파우치 - 재단 60분
카드지갑 - 재봉 120분
```

---

## 주요 액션

|액션|결과|
|---|---|
|이전 달|이전 월 Calendar 조회|
|다음 달|다음 월 Calendar 조회|
|날짜 선택|날짜별 작업 요약 표시|
|DailyLog 선택|DailyLog 상세 이동|

---

# 5.15 WF-015. 날짜별 작업 요약

## 목적

특정 날짜의 작업 일지를 모아 보여준다.

---

## 주요 UI

```text
[상단]
2026-07-02

[요약]
총 작업 시간: 180분
작업 일지: 2개
Level 3

[DailyLog 목록]
딸기 파우치
파우치 재단
60분

카드지갑
재봉
120분

[+ DailyLog 작성]
```

---

## 주요 액션

|액션|결과|
|---|---|
|DailyLog 선택|DailyLog 상세 이동|
|+ DailyLog 작성|Project 선택 후 DailyLog 작성|

---

# 5.16 WF-016. Contribution

## 목적

연도별 작업 습관을 재봉 잔디 형태로 보여준다.

---

## 주요 UI

```text
[상단]
재봉 잔디

[연도 선택]
< 2026 >

[요약]
총 작업 시간: 3,200분
작업한 날: 42일
작업 일지: 58개

[Contribution Grid]
1월 2월 3월 ... 12월
□ □ ■ □ ■ ...

[Level 안내]
0분
1~30분
31~120분
121~240분
241분 이상
```

---

## 주요 액션

|액션|결과|
|---|---|
|이전 연도|이전 연도 Contribution 조회|
|다음 연도|다음 연도 Contribution 조회|
|날짜 선택|날짜별 작업 요약 이동|

---

# 5.17 WF-017. Photo 업로드 상태 UI

## 목적

이미지 업로드와 처리 상태를 사용자에게 보여준다.

---

## 상태별 UI

|PhotoStatus|UI|
|---|---|
|UPLOADING|업로드 중 표시|
|PROCESSING|이미지 처리 중 표시|
|READY|이미지 표시|
|FAILED|업로드 실패 / 다시 시도 표시|

---

## 주요 UI

```text
[사진 카드]

상태: UPLOADING
[progress indicator]

상태: PROCESSING
이미지 처리 중입니다.

상태: READY
[thumbnail image]

상태: FAILED
이미지 처리에 실패했습니다.
[삭제]
```

---

## 주요 액션

|액션|결과|
|---|---|
|사진 추가|파일 선택|
|사진 삭제|Photo 삭제|
|대표 설정|Project/DailyLog 대표 사진 지정|
|정렬 변경|sortOrder 수정|

---

# 5.18 WF-018. My Page

## 목적

사용자 정보를 확인하고 계정 관련 기능을 제공한다.

---

## 주요 UI

```text
[상단]
My

[프로필]
프로필 이미지
닉네임

[메뉴]
내 정보
로그아웃
회원 탈퇴

[앱 정보]
소로소로 v1.0
```

---

## 주요 액션

|액션|결과|
|---|---|
|로그아웃|RefreshToken 삭제 후 로그인 화면 이동|
|회원 탈퇴|확인 모달 후 탈퇴 처리|
|내 정보|사용자 정보 확인|

---

# 6. 공통 UI 컴포넌트

## 6.1 하단 탭

```text
Home | Project | Calendar | Fabric | My
```

---

## 6.2 Floating Action Button

주요 작성 화면으로 빠르게 이동한다.

화면별 FAB 동작은 다음과 같다.

|화면|FAB 동작|
|---|---|
|Project 목록|Project 생성|
|Project 상세|DailyLog 작성|
|Fabric 목록|Fabric 등록|
|Calendar|DailyLog 작성|
|날짜별 요약|DailyLog 작성|

---

## 6.3 상태 Badge

|상태|표시|
|---|---|
|IN_PROGRESS|진행 중|
|ON_HOLD|보류|
|COMPLETED|완료|
|ARCHIVED|보관|
|DRAFT|임시저장|
|PUBLISHED|발행됨|
|UPLOADING|업로드 중|
|PROCESSING|처리 중|
|READY|완료|
|FAILED|실패|

---

## 6.4 Empty State

데이터가 없는 화면에서는 다음 메시지를 사용한다.

|화면|메시지|
|---|---|
|Project 목록|아직 만든 작품이 없어요. 첫 작품을 기록해볼까요?|
|DailyLog 목록|아직 작업 일지가 없어요. 오늘의 과정을 남겨보세요.|
|Fabric 목록|아직 등록한 원단이 없어요. 구매한 원단을 기록해보세요.|
|Calendar|이 달에는 아직 작업 기록이 없어요.|
|Contribution|아직 재봉 잔디가 비어 있어요.|

---

## 6.5 Confirm Modal

삭제나 탈퇴처럼 되돌리기 어려운 동작에는 확인 모달을 사용한다.

예시는 다음과 같다.

```text
정말 삭제할까요?

삭제한 기록은 되돌릴 수 없습니다.

[취소] [삭제]
```

---

# 7. MVP 화면 우선순위

## 7.1 1순위 화면

반드시 먼저 설계한다.

- Onboarding / Login
    
- Home
    
- Project 목록
    
- Project 상세
    
- DailyLog 작성
    
- DailyLog 상세
    
- Fabric 목록
    
- Fabric 상세
    
- Calendar
    

---

## 7.2 2순위 화면

핵심 흐름 이후 설계한다.

- Project 생성/수정
    
- Project 설정
    
- 참고자료 관리
    
- Fabric 등록/수정
    
- Project-Fabric 연결
    
- Contribution
    
- My Page
    

---

## 7.3 3순위 화면

구현 중 보완한다.

- Photo 업로드 상태 UI
    
- 이미지 확대 보기
    
- 삭제 확인 모달
    
- Empty State
    
- Error State
    
- Loading State
    

---

# 8. Figma 작성 기준

## 8.1 Frame 기준

Figma에서는 모바일 화면 기준으로 먼저 작성한다.

권장 Frame은 다음과 같다.

```text
iPhone 14 / 390 x 844
```

---

## 8.2 화면 그룹

Figma 페이지는 다음처럼 나눈다.

```text
1. Flow
2. Components
3. Auth
4. Home
5. Project
6. DailyLog
7. Fabric
8. Calendar
9. My
```

---

## 8.3 컴포넌트화 대상

다음 UI는 컴포넌트로 만든다.

- Bottom Navigation
    
- Project Card
    
- DailyLog Card
    
- Fabric Card
    
- Photo Card
    
- Status Badge
    
- Primary Button
    
- Secondary Button
    
- Input Field
    
- Date Picker
    
- TimeEntry Input
    
- Empty State
    
- Confirm Modal
    

---

## 8.4 Figma 링크

Figma 설계가 완료되면 아래에 링크를 추가한다.

```text
Figma Link:
https://figma.com/file/...
```

---

# 9. 화면-API 매핑

## 9.1 Auth

|화면|API|
|---|---|
|Onboarding / Login|POST /api/v1/auth/kakao/login|
|My Page|GET /api/v1/users/me|
|My Page|POST /api/v1/auth/logout|
|My Page|DELETE /api/v1/users/me|

---

## 9.2 Project

|화면|API|
|---|---|
|Project 목록|GET /api/v1/projects|
|Project 상세|GET /api/v1/projects/{projectId}|
|Project 생성|POST /api/v1/projects|
|Project 수정|PATCH /api/v1/projects/{projectId}|
|Project 상태 변경|PATCH /api/v1/projects/{projectId}/status|
|Project 설정|GET /api/v1/projects/{projectId}/specification|
|Project 설정|PUT /api/v1/projects/{projectId}/specification|
|참고자료 관리|POST /api/v1/projects/{projectId}/references|
|참고자료 관리|PATCH /api/v1/projects/{projectId}/references/{referenceId}|
|참고자료 관리|DELETE /api/v1/projects/{projectId}/references/{referenceId}|

---

## 9.3 DailyLog

|화면|API|
|---|---|
|DailyLog 작성|POST /api/v1/projects/{projectId}/daily-logs|
|DailyLog 목록|GET /api/v1/projects/{projectId}/daily-logs|
|날짜별 DailyLog 목록|GET /api/v1/daily-logs|
|DailyLog 상세|GET /api/v1/daily-logs/{dailyLogId}|
|DailyLog 수정|PUT /api/v1/daily-logs/{dailyLogId}|
|DailyLog 발행|POST /api/v1/daily-logs/{dailyLogId}/publish|
|DailyLog 삭제|DELETE /api/v1/daily-logs/{dailyLogId}|

---

## 9.4 Fabric

|화면|API|
|---|---|
|Fabric 목록|GET /api/v1/fabrics|
|Fabric 상세|GET /api/v1/fabrics/{fabricId}|
|Fabric 등록|POST /api/v1/fabrics|
|Fabric 수정|PATCH /api/v1/fabrics/{fabricId}|
|Fabric 삭제|DELETE /api/v1/fabrics/{fabricId}|
|Project-Fabric 연결|POST /api/v1/projects/{projectId}/fabrics|
|Project-Fabric 연결 수정|PATCH /api/v1/projects/{projectId}/fabrics/{projectFabricId}|
|Project-Fabric 연결 삭제|DELETE /api/v1/projects/{projectId}/fabrics/{projectFabricId}|

---

## 9.5 Photo

|화면|API|
|---|---|
|Photo 업로드|POST /api/v1/photos/presigned-url|
|Photo 업로드 완료|POST /api/v1/photos/{photoId}/complete-upload|
|Photo 목록|GET /api/v1/photos|
|대표 사진 설정|PATCH /api/v1/photos/{photoId}/thumbnail|
|Photo 정렬|PATCH /api/v1/photos/sort-order|
|Photo 삭제|DELETE /api/v1/photos/{photoId}|

---

## 9.6 Calendar / Contribution

|화면|API|
|---|---|
|Calendar|GET /api/v1/calendar|
|날짜별 작업 요약|GET /api/v1/calendar/days/{date}|
|Contribution|GET /api/v1/contributions|

---

# 10. Wireframe 요약

소로소로 MVP의 화면 설계는 다음 원칙을 따른다.

- 모바일 웹 중심으로 설계한다.
    
- 하단 탭은 Home, Project, Calendar, Fabric, My로 구성한다.
    
- Project는 작품 단위의 중심 화면이다.
    
- DailyLog 작성은 Project 상세에서 자연스럽게 진입한다.
    
- Fabric은 Project와 독립된 탭으로 관리한다.
    
- Project와 Fabric은 연결 화면을 통해 관계를 만든다.
    
- Calendar는 날짜별 작업 기록을 보여준다.
    
- Contribution은 연도별 작업 습관을 보여준다.
    
- Photo는 모든 도메인에서 공통 업로드 UI를 사용한다.
    
- DRAFT와 PUBLISHED 상태 차이가 화면에서 명확히 드러나야 한다.
    
- ARCHIVED Project는 조회 가능하지만 수정성 액션은 제한되어야 한다.
    
- Empty State와 Loading State를 통해 기록 서비스의 빈 화면 경험을 보완한다.
    

이 문서를 기준으로 Figma 와이어프레임을 작성하고, 구현 과정에서 화면 구조가 변경되면 API 문서와 함께 업데이트한다.