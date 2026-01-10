# AI-Driven Architecture Blueprint (ADAB)

**AI 기반 아키텍처 설계 자동화 플랫폼**
RFP 요건을 자동 분석하고 프로젝트 관리 산출물(WBS, 업무분장)까지 생성하는 플랫폼입니다.

## 🚀 프로젝트 개요 (Project Overview)
- **목표**: 비정형 RFP 텍스트를 입력받아 Event-Driven/RESTful하게 LLM과 통신하고, 결과를 구조화된 DB로 관리합니다.
- **핵심 기술**: Java/Spring Boot 3, React, AWS, Cloud Native, LLM (Local/Cloud).

## 🛠 기술 스택 (Tech Stack)
- **Backend**: Spring Boot 3.x (Java 17+)
- **Frontend**: React (TypeScript), TanStack Query
- **Database**: PostgreSQL (Relational), Vector DB (확장 예정)
- **AI/LLM**: Spring AI / LangChain4j (OpenAI 또는 Ollama 연동)

## 📂 프로젝트 구조 (Project Structure)
```text
/adab-platform (Root)
├── adab-api        # Spring Boot Backend (LLM 연동 및 비즈니스 로직)
├── adab-view       # React Frontend (분석 대시보드 및 WBS 시각화)
├── adab-llm-worker # (선택사항) Python/Ollama 로컬 LLM 워커
└── docker-compose.yml
```

## 📅 로드맵 (Roadmap)
### 1단계: 실시간 분석 (현재)
- 요구사항 스트리밍 분석.
- 요구사항 분류 및 ID 생성.
- 중복 제거 및 표준화.

### 2단계: 대시보드 및 산출물 생성
- 기능별 트리 뷰(Tree View).
- 요구사항 정의서 자동 생성.
- WBS 및 R&R(업무분장) 생성.

## 💾 데이터베이스 스키마 (Database Schema)
PostgreSQL 활용:
- **Requirements**: `id`, `raw_text`, `category_id`, `priority`
- **Features**: `id`, `req_id`, `name`, `description`, `type`
- **WBS_Tasks**: `id`, `feature_id`, `task_name`, `owner_role`, `duration`
