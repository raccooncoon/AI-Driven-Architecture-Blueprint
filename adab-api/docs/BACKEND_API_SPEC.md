# 과업 생성 백엔드 API 사양서

## 개요
RFP 요구사항을 LLM으로 분석하여 세부 과업(Task)을 생성하는 백엔드 API 사양서입니다.

## 기술 스택 권장사항
- **언어/프레임워크**: Python/FastAPI 또는 Node.js/Express
- **LLM**: Ollama (gemma3:12b 모델)
- **응답 방식**: Server-Sent Events (SSE) 스트리밍

## API 엔드포인트

### POST /api/tasks/generate

RFP 요구사항을 분석하여 과업을 생성합니다.

#### Request Headers
```
Content-Type: application/json
Accept: text/event-stream
```

#### Request Body
```json
{
  "rfpId": "SFR-DHUB-008",
  "requirementId": "REQ-AI-BA-0001",
  "name": "데이터허브 – 생성형 AI 모델",
  "definition": "생성형 AI 모델 개념 정의",
  "requestContent": "○ 프라이빗 클라우드 환경에서 활용 가능한 LLM 모델 제시",
  "deadline": "2026-01-31",
  "implementationOpinion": "ㅇ PO 제공 가능\n- 믿음 2.0",
  "businessDevelopment": "",
  "pobaOpinion": "BA: PO/Lab 확인 필요",
  "techInnovationOpinion": "제공가능 모델 (이행/PO정의 시)\n-믿음 K pro, 라마 K",
  "index": 0
}
```

#### Request Body 필드 설명
| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| rfpId | string | O | RFP ID (예: SFR-DHUB-008) |
| requirementId | string | O | 요구사항 ID (예: REQ-AI-BA-0001) |
| name | string | O | 요구사항 명칭 |
| definition | string | O | 요구사항 정의 |
| requestContent | string | O | 제안요청 내용 (주요 분석 대상) |
| deadline | string | X | 기능 제공 기한 (YYYY-MM-DD) |
| implementationOpinion | string | X | 이행 의견 |
| businessDevelopment | string | X | 사업개발 의견 |
| pobaOpinion | string | X | PO/BA 의견 |
| techInnovationOpinion | string | X | 기술혁신 의견 |
| index | number | O | 요구사항의 인덱스 (프론트엔드에서 사용) |

#### Response (SSE Stream)

응답은 Server-Sent Events 형식으로 스트리밍됩니다.

**이벤트 타입:**

1. **status** - 진행 상태 업데이트
```
event: status
data: {"message": "요구사항을 분석하고 있습니다..."}

event: status
data: {"message": "LLM이 응답을 생성하고 있습니다..."}

event: status
data: {"message": "과업 1/3 생성됨"}
```

2. **task** - 개별 과업 생성 완료
```
event: task
data: {"id":"REQ-AI-BA-0001-TASK-001","parentRequirementId":"REQ-AI-BA-0001","parentIndex":0,"summary":"생성형 AI 모델의 기본 아키텍처 설계 및 구현","majorCategoryId":"CAT-001","majorCategory":"AI 모델 개발","detailFunctionId":"FUNC-001","detailFunction":"LLM 모델 구축","subFunction":"사전학습 모델 선정 및 파인튜닝 환경 구성"}
```

3. **complete** - 전체 생성 완료
```
event: complete
data: {"message": "✓ 3개 과업 생성 완료"}
```

4. **error** - 에러 발생
```
event: error
data: {"message": "LLM 응답에서 JSON을 찾을 수 없습니다"}
```

#### Task 객체 스키마
```typescript
{
  "id": string,                    // 과업 ID (형식: {requirementId}-TASK-{seq})
  "parentRequirementId": string,   // 부모 요구사항 ID
  "parentIndex": number,           // 부모 요구사항 인덱스
  "summary": string,               // 과업 내용 요약
  "majorCategoryId": string,       // 기능 대분류 ID (예: CAT-001)
  "majorCategory": string,         // 기능 대분류명 (예: AI 모델 개발)
  "detailFunctionId": string,      // 상세 기능 ID (예: FUNC-001)
  "detailFunction": string,        // 상세 기능명 (예: LLM 모델 구축)
  "subFunction": string           // 세부 기능 설명
}
```

## LLM 프롬프트 템플릿

```
다음 RFP 요구사항을 분석하여 세부 과업(Task)으로 분해해주세요.

요구사항 정보:
- ID: {requirementId}
- 명칭: {name}
- 정의: {definition}
- 제안요청내용: {requestContent}

요구사항을 분석하여 3-5개의 구체적인 과업으로 나누어주세요. 각 과업은 다음 형식의 JSON 배열로 응답해주세요:

[
  {
    "summary": "과업 내용 요약 (구체적으로)",
    "majorCategoryId": "CAT-XXX",
    "majorCategory": "기능 대분류 (예: AI 모델 개발, 데이터 처리, 품질 검증 등)",
    "detailFunctionId": "FUNC-XXX",
    "detailFunction": "상세 기능명",
    "subFunction": "세부 기능 설명"
  }
]

JSON 배열만 응답하고 다른 설명은 포함하지 마세요.
```

## Ollama API 연동

### Ollama API 호출 예시
```bash
curl -X POST http://min-sff:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gemma3:12b",
    "prompt": "...",
    "stream": true,
    "options": {
      "temperature": 0.7
    }
  }'
```

### Ollama 응답 형식 (스트리밍)
```json
{"model":"gemma3:12b","created_at":"2026-01-10T13:00:00Z","response":"[","done":false}
{"model":"gemma3:12b","created_at":"2026-01-10T13:00:00Z","response":"\n","done":false}
{"model":"gemma3:12b","created_at":"2026-01-10T13:00:00Z","response":" {","done":false}
...
{"model":"gemma3:12b","created_at":"2026-01-10T13:00:01Z","response":"","done":true}
```

## 구현 로직

1. **요청 수신**: POST /api/tasks/generate로 요구사항 데이터 수신
2. **SSE 연결 설정**: 클라이언트와 SSE 연결 수립
3. **상태 전송**: `event: status, data: {"message": "요구사항을 분석하고 있습니다..."}`
4. **LLM 호출**: Ollama API에 프롬프트 전송 (스트리밍 모드)
5. **응답 수집**: Ollama로부터 스트리밍 응답 수집
6. **상태 업데이트**: `event: status, data: {"message": "LLM이 응답을 생성하고 있습니다..."}`
7. **JSON 파싱**: 수집된 응답에서 JSON 배열 추출 (정규식: `\[[\s\S]*\]`)
8. **Task ID 생성**: 각 과업에 `{requirementId}-TASK-{seq}` 형식의 ID 부여
9. **순차 전송**: 각 과업을 0.5초 간격으로 SSE로 전송
   ```
   event: task
   data: {...}
   ```
10. **완료 알림**: `event: complete, data: {"message": "✓ N개 과업 생성 완료"}`
11. **연결 종료**: SSE 연결 종료

## 에러 처리

### 에러 케이스
1. Ollama 서버 연결 실패
2. LLM 응답에서 JSON 파싱 실패
3. 타임아웃 (30초 초과)
4. 요청 데이터 유효성 검증 실패

### 에러 응답 형식
```
event: error
data: {"message": "에러 메시지"}
```

## 예상 응답 예시

### task_sample.json 참고
생성될 과업은 다음과 같은 형식입니다:

```json
[
  {
    "id": "REQ-AI-BA-0001-TASK-001",
    "parentRequirementId": "REQ-AI-BA-0001",
    "parentIndex": 0,
    "summary": "생성형 AI 모델의 기본 아키텍처 설계 및 구현",
    "majorCategoryId": "CAT-001",
    "majorCategory": "AI 모델 개발",
    "detailFunctionId": "FUNC-001",
    "detailFunction": "LLM 모델 구축",
    "subFunction": "사전학습 모델 선정 및 파인튜닝 환경 구성"
  },
  {
    "id": "REQ-AI-BA-0001-TASK-002",
    "parentRequirementId": "REQ-AI-BA-0001",
    "parentIndex": 0,
    "summary": "프롬프트 엔지니어링 시스템 개발",
    "majorCategoryId": "CAT-001",
    "majorCategory": "AI 모델 개발",
    "detailFunctionId": "FUNC-002",
    "detailFunction": "프롬프트 최적화",
    "subFunction": "프롬프트 템플릿 관리 및 버전 관리 시스템 구축"
  }
]
```

## CORS 설정

프론트엔드가 `http://localhost:5173`에서 실행되므로 CORS 허용 필요:

```javascript
// Express 예시
app.use(cors({
  origin: 'http://localhost:5173',
  credentials: true
}));
```

```python
# FastAPI 예시
from fastapi.middleware.cors import CORSMiddleware

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

## 성능 요구사항
- 응답 시작 시간: 1초 이내
- 과업 생성 완료: 30초 이내
- 동시 요청 처리: 최소 10개

## 추가 고려사항
1. LLM 응답이 JSON 형식이 아닐 경우 재시도 로직 (최대 2회)
2. 과업 ID 중복 방지 (시퀀스 번호 관리)
3. 요청 로깅 (요청 시간, 소요 시간, 생성된 과업 수)
4. Rate Limiting (사용자당 분당 10회)
