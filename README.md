# AI-Driven Architecture Blueprint (ADAB)

**AI 기반 아키텍처 설계 자동화 플랫폼 (AI-Assisted ISP/RFP Analyzer)**
RFP(제안요청서) 요건을 자동 분석하고, WBS(업무분류체계), 업무분장 등 프로젝트 관리 산출물을 자동으로 생성하는 지능형 플랫폼입니다.

## 🚀 주요 기능 (Key Features)

### 1. 요구사항 자동 분석
- **AI 기반 분석**: 비정형 텍스트로 된 요구사항을 입력받아 구조화된 데이터로 변환합니다.
- **Excel 업로드 지원**: 기존 RFP 요구사항 정의서를 Excel 파일(.xlsx)로 일괄 등록할 수 있습니다.
- **자동 분류**: 기능/비기능, 솔루션, 제약사항 등 속성을 자동 식별합니다.

### 2. 과업 및 산출물 생성
- **WBS 생성**: 요구사항을 기반으로 구체적인 과업(Task)을 도출합니다.
- **순차 처리 지원**: 대량의 요구사항도 서버 부하를 고려해 순차적으로 분석합니다.
- **실시간 대시보드**: SSE(Server-Sent Events) 기술로 분석 진행상황을 실시간으로 시각화합니다.

### 3. 유연한 LLM 연동
- **Spring AI**: OpenAI, Azure OpenAI, Ollama 등 다양한 LLM Provider를 표준화된 인터페이스로 지원합니다.
- **하이브리드 모드**: 보안이 중요한 데이터는 로컬 LLM(Ollama)으로, 성능이 필요한 작업은 클라우드 LLM으로 처리 가능합니다.

---

## 🛠 기술 스택 (Tech Stack)

| 구분 | 기술 |
| :--- | :--- |
| **Frontend** | React, TypeScript, Vite, TanStack Query, Axios, SSE |
| **Backend** | Spring Boot 3.3, Java 17, Spring AI, JPA |
| **Database** | PostgreSQL (Relational) |
| **AI/LLM** | Ollama (Gemma, Llama, Falcon 등 로컬 LLM), OpenAI |
| **Infrastructure** | Docker, Docker Compose, Proxmox LXC (Production) |

---

## 📂 프로젝트 구조 (Project Structure)
```text
/adab-platform
├── adab-api        # Spring Boot Backend (비즈니스 로직, LLM 연동, Excel 파싱)
├── adab-view       # React Frontend (대시보드, 엑셀 업로드, 실시간 시각화)
├── docker-compose.yml      # 로컬 개발용 Docker 구성
├── docker-compose.prod.yml # 운영 배포용 Docker 구성 (Redis 포함)
└── deployment_guide.md     # 상세 배포 가이드
```

---

## � 배포 가이드 (Deployment)

Proxmox LXC 컨테이너 환경에서의 배포 방법은 별도 가이드 문서를 참고하세요.
👉 **[배포 가이드 보러가기 (deployment_guide.md)](deployment_guide.md)**

**간단 요약 (Production Run):**
```bash
# 운영 환경 실행 (DB, Ollama 외부 연결 전제)
docker compose -f docker-compose.prod.yml up -d --build
```

---

## 💾 데이터베이스 스키마 (Database Schema)

### Requirements (요구사항)
RFP의 원천 데이터를 저장합니다.
- **주요 컬럼**: `rfpId`, `requirementId`, `name`, `requestContent`(상세내용), `constraints`(제약사항), `solution`(해결방안), `priority`, `acceptance` 등

### Tasks (과업)
요구사항을 분석하여 도출된 실행 단위입니다.
- **주요 컬럼**: `uuid`, `taskId`, `summary`, `majorCategory`, `detailFunction`, `subFunction`

### ModelConfigs (모델 설정)
다양한 LLM 설정을 동적으로 관리합니다.
- **주요 컬럼**: `modelName`, `baseUrl`, `apiKey`, `temperature`, `isDefault`
