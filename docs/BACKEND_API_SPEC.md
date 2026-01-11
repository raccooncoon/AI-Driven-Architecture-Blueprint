# Backend API Specification (백엔드 API 명세서)

This document provides a comprehensive overview of the backend API endpoints for the AI-Driven Architecture Blueprint (ADAB) project.
이 문서는 AI-Driven Architecture Blueprint (ADAB) 프로젝트의 백엔드 API 엔드포인트에 대한 포괄적인 개요를 제공합니다.

## Base URL (기본 URL)

The API is accessible at: `http://localhost:8080` (Default Spring Boot port).
API는 다음 주소에서 접근 가능합니다: `http://localhost:8080` (기본 스프링 부트 포트).

---

## 1. Model Configuration API (모델 설정 API)

Manages LLM model settings and configurations.
LLM 모델 설정 및 구성을 관리합니다.

**Base Path:** `/api/model-configs`

### Endpoints (엔드포인트)

#### Get All Configurations (모든 설정 조회)

- **URL:** `GET /`
- **Description:** Retrieves all registered model configurations. (등록된 모든 모델 설정을 조회합니다.)
- **Response:** `200 OK` (List of ModelConfigResponse)

#### Get Configuration by Name (이름으로 설정 조회)

- **URL:** `GET /{name}`
- **Description:** Retrieves a specific model configuration by its name. (특정 모델 설정을 이름으로 조회합니다.)
- **Response:** `200 OK` (ModelConfigResponse)

#### Get Default Configuration (기본 설정 조회)

- **URL:** `GET /default`
- **Description:** Retrieves the current default model configuration. (현재 기본으로 설정된 모델 구성을 조회합니다.)
- **Response:** `200 OK` (ModelConfigResponse)

#### Get Current Model Name (현재 모델 이름 조회)

- **URL:** `GET /current`
- **Description:** Returns the name of the model currently being used as default. (현재 기본으로 사용 중인 모델의 이름을 반환합니다.)
- **Response:** `200 OK` (`{"modelName": "model-name"}`)

#### Update or Create Configuration (설정 업데이트 또는 생성)

- **URL:** `PUT /{name}`
- **Description:** Creates a new model configuration or updates an existing one. (새 모델 설정을 생성하거나 기존 설정을 업데이트합니다.)
- **Body:** `ModelConfigRequest`
- **Response:** `200 OK` (ModelConfigResponse)

#### Delete Configuration (설정 삭제)

- **URL:** `DELETE /{name}`
- **Description:** Deletes a model configuration. (모델 설정을 삭제합니다.)
- **Response:** `200 OK`

#### Test Connection (연결 테스트)

- **URL:** `POST /test`
- **Description:** Tests the connection to a specific model. (특정 모델에 대한 연결을 테스트합니다.)
- **Body:** `{"name": "model-name"}`
- **Response:** `200 OK` (`{"success": true, "modelName": "...", "testResponse": "..."}`)

---

## 2. Requirement Management API (요구사항 관리 API)

Handles project requirements and batch uploads.
프로젝트 요구사항 및 일괄 업로드를 처리합니다.

**Base Path:** `/api/requirements`

### Endpoints (엔드포인트)

#### Batch Upload Requirements (요구사항 일괄 업로드)

- **URL:** `POST /batch`
- **Description:** Uploads a file (CSV/Excel) containing multiple requirements. (여러 요구사항이 포함된 파일(CSV/Excel)을 업로드합니다.)
- **Form Data:** `file` (MultipartFile)
- **Response:** `200 OK` (BatchUploadResponse)

#### Get All Requirements (모든 요구사항 조회)

- **URL:** `GET /`
- **Description:** Retrieves a list of all project requirements. (모든 프로젝트 요구사항 목록을 조회합니다.)
- **Response:** `200 OK` (List of RequirementResponse)

#### Get Requirement Detail (요구사항 상세 조회)

- **URL:** `GET /{requirementId}`
- **Description:** Retrieves details for a specific requirement. (특정 요구사항의 상세 정보를 조회합니다.)
- **Response:** `200 OK` (RequirementResponse) | `404 Not Found`

#### Update Requirement (요구사항 업데이트)

- **URL:** `PUT /{requirementId}`
- **Description:** Updates the details of an existing requirement. (기존 요구사항의 상세 정보를 업데이트합니다.)
- **Body:** `RequirementResponse`
- **Response:** `200 OK` (RequirementResponse)

#### Delete Requirement (요구사항 삭제)

- **URL:** `DELETE /{requirementId}`
- **Description:** Deletes a specific requirement. (특정 요구사항을 삭제합니다.)
- **Response:** `200 OK` | `404 Not Found`

---

## 3. Task Management API (과업 관리 API)

Handles task generation (AI) and CRUD operations for generated tasks.
과업 생성(AI) 및 생성된 과업에 대한 CRUD 작업을 처리합니다.

**Base Path:** `/api/tasks`

### Endpoints (엔드포인트)

#### Generate Tasks (AI) (과업 생성 (AI))

- **URL:** `POST /generate`
- **Description:** Generates architecture tasks from a requirement using AI. (AI를 사용하여 요구사항으로부터 아키텍처 과업을 생성합니다.)
- **Content-Type:** `application/json`
- **Accept:** `text/event-stream` (Server-Sent Events)
- **Body:** `TaskGenerationRequest`
- **Note:** This is a streaming endpoint. (이 엔드포인트는 스트리밍 방식으로 동작합니다.)

#### Get All Tasks (모든 과업 조회)

- **URL:** `GET /all`
- **Description:** Retrieves all tasks across all requirements. (모든 요구사항에 걸친 모든 과업을 조회합니다.)
- **Response:** `200 OK` (TaskListResponse)

#### Get Tasks by Requirement ID (요구사항 ID로 과업 조회)

- **URL:** `GET /`
- **Description:** Retrieves tasks filtered by requirement ID. (요구사항 ID로 필터링된 과업 목록을 조회합니다.)
- **Query Parameter:** `requirementId` (String)
- **Response:** `200 OK` (TaskListResponse)

#### Get Task Detail (과업 상세 조회)

- **URL:** `GET /{taskId}`
- **Description:** Retrieves details for a specific task. (특정 과업의 상세 정보를 조회합니다.)
- **Response:** `200 OK` (ApiResponse<TaskResponse>)

#### Update Task Details (과업 상세 업데이트)

- **URL:** `PUT /{taskId}`
- **Description:** Updates an existing task. (기존 과업을 업데이트합니다.)
- **Body:** `TaskUpdateRequest`
- **Response:** `200 OK` (ApiResponse<TaskResponse>)

#### Delete Individual Task (개별 과업 삭제)

- **URL:** `DELETE /{taskId}`
- **Description:** Deletes a specific task. (특정 과업을 삭제합니다.)
- **Response:** `200 OK` (DeleteResponse)

#### Delete Tasks by Requirement ID (요구사항 ID별 과업 삭제)

- **URL:** `DELETE /requirement/{requirementId}`
- **Description:** Deletes all tasks associated with a specific requirement. (특정 요구사항과 관련된 모든 과업을 삭제합니다.)
- **Response:** `200 OK` (DeleteResponse)

#### Check Tasks Existence for Requirement (요구사항 과업 존재 여부 확인)

- **URL:** `GET /requirement/{requirementId}/exists`
- **Description:** Checks if tasks have been generated for a given requirement. (특정 요구사항에 대해 과업이 생성되었는지 확인합니다.)
- **Response:** `200 OK` (ExistsResponse)

---

## Appendix: Data Transfer Objects (DTOs) (부록: 데이터 전송 객체)

### RequirementResponse (요구사항 응답)

```json
{
  "requirementId": "string",
  "rfpId": "string",
  "name": "string",
  "definition": "string",
  "requestContent": "string",
  "deadline": "string",
  "implementationOpinion": "string",
  "pobaOpinion": "string",
  "techInnovationOpinion": "string",
  "createdAt": "ISO-8601 Timestamp",
  "updatedAt": "ISO-8601 Timestamp"
}
```

### TaskResponse (과업 응답)

```json
{
  "id": "string",
  "parentRequirementId": "string",
  "parentIndex": 1,
  "summary": "string",
  "majorCategoryId": "string",
  "majorCategory": "string",
  "detailFunctionId": "string",
  "detailFunction": "string",
  "subFunction": "string",
  "generatedBy": "string",
  "createdAt": "ISO-8601 Timestamp",
  "updatedAt": "ISO-8601 Timestamp"
}
```

### TaskGenerationRequest (과업 생성 요청)

```json
{
  "rfpId": "string",
  "requirementId": "string",
  "name": "string",
  "definition": "string",
  "requestContent": "string",
  "deadline": "string",
  "implementationOpinion": "string",
  "pobaOpinion": "string",
  "techInnovationOpinion": "string",
  "index": 1
}
```

### ModelConfigRequest (모델 설정 요청)

```json
{
  "name": "string",
  "modelName": "string",
  "apiKey": "string",
  "baseUrl": "string",
  "maxTokens": 4096,
  "temperature": 0.7,
  "enabled": true,
  "isDefault": false
}
```
