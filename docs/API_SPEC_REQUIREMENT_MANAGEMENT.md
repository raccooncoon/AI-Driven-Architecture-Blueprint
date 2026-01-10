# 📝 ADAB API Specification - Requirement Management

## 1. 개요
프런트엔드에서 관리하던 RFP 샘플 데이터를 백엔드 데이터베이스(PostgreSQL)로 통합 관리하고, 이를 조회 및 업로드하기 위한 API 사양입니다.

## 2. 데이터 모델 (Requirement Entity)

| 필드명 | 타입 | 설명       | 비고 |
| :--- | :--- |:---------| :--- |
| `requirementId` | String | 요구사항 ID  | Primary Key (예: REQ-AI-BA-0001) |
| `rfpId` | String | RFP 번호   | (예: SFR-DHUB-008) |
| `name` | String | 요구사항 명칭  | |
| `definition` | String | 요구사항 정의  | |
| `requestContent` | Text | 제안요청내용   | 장문 텍스트 |
| `deadline` | String | 이행 기한    | |
| `implementationOpinion` | Text | 이행 부서 의견 | |
| `pobaOpinion` | Text | PO/BA 의견 | |
| `techInnovationOpinion` | Text | 기술혁신팀 의견 | |
| `createdAt` | DateTime | 생성 일시    | 자동 생성 |
| `updatedAt` | DateTime | 수정 일시    | 자동 업데이트 |

---

## 3. API 엔드포인트

### 3.1 RFP 일괄 업로드 (Batch Upload)
JSON 파일을 업로드하여 요구사항을 DB에 일괄 저장하거나 업데이트합니다.

- **URL**: `/api/requirements/batch`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Request Body**:
    - `file`: `rfp_sample.json` (File)
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "101건의 요구사항이 성공적으로 저장되었습니다.",
  "count": 101
}
```


### 3.2 요구사항 전체 목록 조회
DB에 저장된 모든 요구사항을 조회합니다.

- **URL**: `/api/requirements`
- **Method**: `GET`
- **Response** (200 OK):
```json
[
  {
    "requirementId": "REQ-AI-BA-0001",
    "rfpId": "SFR-DHUB-008",
    "name": "데이터허브 – 생성형 AI 모델",
    "definition": "생성형 AI 모델 개념 정의",
    "requestContent": "○ 프라이빗 클라우드 환경에서 활용 가능한 LLM 모델 제시",
    "deadline": "2026-01-31",
    "implementationOpinion": "ㅇ PO 제공 가능...",
    "pobaOpinion": "BA: PO/Lab 확인 필요",
    "techInnovationOpinion": "제공가능 모델...",
    "createdAt": "2026-01-10T15:00:00",
    "updatedAt": "2026-01-10T15:00:00"
  }
]
```


### 3.3 요구사항 상세 조회
특정 ID를 가진 요구사항의 상세 내용을 조회합니다.

- **URL**: `/api/requirements/{requirementId}`
- **Method**: `GET`
- **Response** (200 OK):
```json
{
  "requirementId": "REQ-AI-BA-0001",
  "rfpId": "SFR-DHUB-008",
  "name": "데이터허브 – 생성형 AI 모델",
  "definition": "생성형 AI 모델 개념 정의",
  "requestContent": "○ 프라이빗 클라우드 환경에서 활용 가능한 LLM 모델 제시",
  "deadline": "2026-01-31",
  "implementationOpinion": "ㅇ PO 제공 가능...",
  "pobaOpinion": "BA: PO/Lab 확인 필요",
  "techInnovationOpinion": "제공가능 모델...",
  "createdAt": "2026-01-10T15:00:00",
  "updatedAt": "2026-01-10T15:00:00"
}
```

- **Response** (404 Not Found):
```json
{
  "success": false,
  "message": "요구사항을 찾을 수 없습니다."
}
```


### 3.4 요구사항 생성
새로운 요구사항을 생성합니다.

- **URL**: `/api/requirements`
- **Method**: `POST`
- **Request Body**:
```json
{
  "requirementId": "REQ-AI-BA-0102",
  "rfpId": "SFR-DHUB-010",
  "name": "새로운 요구사항",
  "definition": "요구사항 정의",
  "requestContent": "요구사항 상세 내용",
  "deadline": "2026-02-28",
  "implementationOpinion": "이행 부서 의견",
  "pobaOpinion": "발주처 의견",
  "techInnovationOpinion": "기술혁신팀 의견"
}
```

- **Response** (201 Created):
```json
{
  "requirementId": "REQ-AI-BA-0102",
  "rfpId": "SFR-DHUB-010",
  "name": "새로운 요구사항",
  "definition": "요구사항 정의",
  "requestContent": "요구사항 상세 내용",
  "deadline": "2026-02-28",
  "implementationOpinion": "이행 부서 의견",
  "pobaOpinion": "발주처 의견",
  "techInnovationOpinion": "기술혁신팀 의견",
  "createdAt": "2026-01-10T16:00:00",
  "updatedAt": "2026-01-10T16:00:00"
}
```

- **Response** (400 Bad Request):
```json
{
  "success": false,
  "message": "요구사항 ID가 이미 존재합니다."
}
```


### 3.5 요구사항 수정
기존 요구사항을 수정합니다.

- **URL**: `/api/requirements/{requirementId}`
- **Method**: `PUT`
- **Request Body**:
```json
{
  "rfpId": "SFR-DHUB-010",
  "name": "수정된 요구사항",
  "definition": "수정된 정의",
  "requestContent": "수정된 상세 내용",
  "deadline": "2026-03-31",
  "implementationOpinion": "수정된 이행 의견",
  "pobaOpinion": "수정된 발주처 의견",
  "techInnovationOpinion": "수정된 기술혁신팀 의견"
}
```

- **Response** (200 OK):
```json
{
  "requirementId": "REQ-AI-BA-0001",
  "rfpId": "SFR-DHUB-010",
  "name": "수정된 요구사항",
  "definition": "수정된 정의",
  "requestContent": "수정된 상세 내용",
  "deadline": "2026-03-31",
  "implementationOpinion": "수정된 이행 의견",
  "pobaOpinion": "수정된 발주처 의견",
  "techInnovationOpinion": "수정된 기술혁신팀 의견",
  "createdAt": "2026-01-10T15:00:00",
  "updatedAt": "2026-01-10T16:30:00"
}
```

- **Response** (404 Not Found):
```json
{
  "success": false,
  "message": "요구사항을 찾을 수 없습니다."
}
```


### 3.6 요구사항 삭제
특정 요구사항을 삭제합니다.

- **URL**: `/api/requirements/{requirementId}`
- **Method**: `DELETE`
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "요구사항이 삭제되었습니다."
}
```

- **Response** (404 Not Found):
```json
{
  "success": false,
  "message": "요구사항을 찾을 수 없습니다."
}
```


---

## 4. 데이터베이스 초기화

### 4.1 테이블 생성
```sql
CREATE TABLE requirements (
    requirement_id VARCHAR(50) PRIMARY KEY,
    rfp_id VARCHAR(50),
    name VARCHAR(255),
    definition VARCHAR(500),
    request_content TEXT,
    deadline VARCHAR(50),
    implementation_opinion TEXT,
    poba_opinion TEXT,
    tech_innovation_opinion TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 4.2 샘플 데이터 삽입
`rfp_sample.json`의 101건 데이터를 INSERT하는 쿼리가 생성되었습니다.
- 파일 위치: `/tmp/requirements_insert.sql`
- 총 1433줄, 101개의 INSERT 문

**사용 방법**:
```bash
# PostgreSQL에 직접 실행
psql -U your_username -d adab_db -f /tmp/requirements_insert.sql

# 또는 API를 통한 일괄 업로드
curl -X POST http://localhost:8080/api/requirements/batch \
  -F "file=@adab-view/src/sample/rfp_sample.json"
```

---

## 5. 프런트엔드 변경 사항 (To-Do)
1. **API 호출**: `rfp_sample.json`을 직접 import 하던 로직을 제거하고, `useEffect` 또는 `TanStack Query`를 사용하여 `GET /api/requirements`를 호출하도록 수정합니다.
2. **관리자 메뉴**: 개발 환경에서 쉽게 데이터를 초기화할 수 있도록 파일을 선택해 `POST /api/requirements/batch`를 호출하는 업로드 버튼을 추가합니다.
3. **CRUD 기능**: 요구사항 생성, 수정, 삭제 기능을 UI에 추가합니다.
