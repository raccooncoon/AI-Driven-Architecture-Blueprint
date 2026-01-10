# 과업 관리 백엔드 API 사양서 (Spring Boot)

## 개요
RFP 요구사항을 LLM으로 분석하여 세부 과업(Task)을 생성, 조회, 수정, 삭제하는 백엔드 API 사양서입니다.

## 기술 스택
- **언어/프레임워크**: Java 17+ / Spring Boot 3.x
- **데이터베이스**: JPA (H2/MySQL/PostgreSQL)
- **LLM**: Ollama (gemma3:12b 모델)
- **응답 방식**: Server-Sent Events (SSE) 스트리밍

## 데이터 모델

### Task Entity
```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    private String id;                    // REQ-AI-BA-0001-TASK-001

    @Column(nullable = false)
    private String parentRequirementId;   // REQ-AI-BA-0001

    @Column(nullable = false)
    private Integer parentIndex;          // 0

    @Column(length = 1000)
    private String summary;               // 과업 내용 요약

    private String majorCategoryId;       // CAT-001
    private String majorCategory;         // AI 모델 개발
    private String detailFunctionId;      // FUNC-001
    private String detailFunction;        // LLM 모델 구축

    @Column(length = 2000)
    private String subFunction;           // 세부 기능 설명

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

## API 엔드포인트

### 1. POST /api/tasks/generate
**요구사항을 분석하여 과업을 생성합니다 (SSE 스트리밍)**

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

#### Response (SSE Stream)
```
event: status
data: {"message": "요구사항을 분석하고 있습니다..."}

event: task
data: {"id":"REQ-AI-BA-0001-TASK-001","parentRequirementId":"REQ-AI-BA-0001",...}

event: complete
data: {"message": "✓ 3개 과업 생성 완료", "count": 3}

event: error
data: {"message": "에러 메시지"}
```

---

### 2. GET /api/tasks?requirementId={requirementId}
**특정 요구사항의 모든 과업을 조회합니다**

#### Query Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| requirementId | string | O | 요구사항 ID (예: REQ-AI-BA-0001) |

#### Response
```json
{
  "success": true,
  "data": [
    {
      "id": "REQ-AI-BA-0001-TASK-001",
      "parentRequirementId": "REQ-AI-BA-0001",
      "parentIndex": 0,
      "summary": "생성형 AI 모델의 기본 아키텍처 설계 및 구현",
      "majorCategoryId": "CAT-001",
      "majorCategory": "AI 모델 개발",
      "detailFunctionId": "FUNC-001",
      "detailFunction": "LLM 모델 구축",
      "subFunction": "사전학습 모델 선정 및 파인튜닝 환경 구성",
      "createdAt": "2026-01-10T13:00:00",
      "updatedAt": "2026-01-10T13:00:00"
    }
  ],
  "count": 3
}
```

---

### 3. GET /api/tasks/{taskId}
**특정 과업의 상세 정보를 조회합니다**

#### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| taskId | string | O | 과업 ID (예: REQ-AI-BA-0001-TASK-001) |

#### Response
```json
{
  "success": true,
  "data": {
    "id": "REQ-AI-BA-0001-TASK-001",
    "parentRequirementId": "REQ-AI-BA-0001",
    "parentIndex": 0,
    "summary": "생성형 AI 모델의 기본 아키텍처 설계 및 구현",
    "majorCategoryId": "CAT-001",
    "majorCategory": "AI 모델 개발",
    "detailFunctionId": "FUNC-001",
    "detailFunction": "LLM 모델 구축",
    "subFunction": "사전학습 모델 선정 및 파인튜닝 환경 구성",
    "createdAt": "2026-01-10T13:00:00",
    "updatedAt": "2026-01-10T13:00:00"
  }
}
```

#### Error Response (404)
```json
{
  "success": false,
  "error": "Task not found",
  "taskId": "REQ-AI-BA-0001-TASK-001"
}
```

---

### 4. PUT /api/tasks/{taskId}
**과업 정보를 수정합니다**

#### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| taskId | string | O | 과업 ID (예: REQ-AI-BA-0001-TASK-001) |

#### Request Body
```json
{
  "summary": "생성형 AI 모델의 기본 아키텍처 설계 및 구현 (수정됨)",
  "majorCategoryId": "CAT-001",
  "majorCategory": "AI 모델 개발",
  "detailFunctionId": "FUNC-001",
  "detailFunction": "LLM 모델 구축",
  "subFunction": "사전학습 모델 선정 및 파인튜닝 환경 구성 (업데이트)"
}
```

#### Response
```json
{
  "success": true,
  "data": {
    "id": "REQ-AI-BA-0001-TASK-001",
    "parentRequirementId": "REQ-AI-BA-0001",
    "parentIndex": 0,
    "summary": "생성형 AI 모델의 기본 아키텍처 설계 및 구현 (수정됨)",
    "majorCategoryId": "CAT-001",
    "majorCategory": "AI 모델 개발",
    "detailFunctionId": "FUNC-001",
    "detailFunction": "LLM 모델 구축",
    "subFunction": "사전학습 모델 선정 및 파인튜닝 환경 구성 (업데이트)",
    "createdAt": "2026-01-10T13:00:00",
    "updatedAt": "2026-01-10T14:00:00"
  }
}
```

---

### 5. DELETE /api/tasks/{taskId}
**특정 과업을 삭제합니다**

#### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| taskId | string | O | 과업 ID (예: REQ-AI-BA-0001-TASK-001) |

#### Response
```json
{
  "success": true,
  "message": "Task deleted successfully",
  "taskId": "REQ-AI-BA-0001-TASK-001"
}
```

---

### 6. DELETE /api/tasks/requirement/{requirementId}
**특정 요구사항의 모든 과업을 일괄 삭제합니다**

#### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| requirementId | string | O | 요구사항 ID (예: REQ-AI-BA-0001) |

#### Response
```json
{
  "success": true,
  "message": "All tasks deleted successfully",
  "requirementId": "REQ-AI-BA-0001",
  "deletedCount": 3
}
```

---

### 7. GET /api/tasks/requirement/{requirementId}/exists
**특정 요구사항에 대한 과업이 존재하는지 확인합니다**

#### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| requirementId | string | O | 요구사항 ID (예: REQ-AI-BA-0001) |

#### Response
```json
{
  "success": true,
  "exists": true,
  "count": 3,
  "requirementId": "REQ-AI-BA-0001"
}
```

---

## Spring Boot 구현 예시

### Controller
```java
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 1. 과업 생성 (SSE)
    @PostMapping(value = "/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateTasks(@RequestBody RequirementRequest request) {
        return taskService.generateTasksWithSSE(request);
    }

    // 2. 과업 목록 조회
    @GetMapping
    public ResponseEntity<TaskListResponse> getTasks(
        @RequestParam String requirementId
    ) {
        return ResponseEntity.ok(taskService.getTasksByRequirementId(requirementId));
    }

    // 3. 과업 상세 조회
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    // 4. 과업 수정
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
        @PathVariable String taskId,
        @RequestBody TaskUpdateRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    // 5. 과업 삭제
    @DeleteMapping("/{taskId}")
    public ResponseEntity<DeleteResponse> deleteTask(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }

    // 6. 요구사항별 과업 일괄 삭제
    @DeleteMapping("/requirement/{requirementId}")
    public ResponseEntity<DeleteResponse> deleteTasksByRequirement(
        @PathVariable String requirementId
    ) {
        return ResponseEntity.ok(taskService.deleteTasksByRequirementId(requirementId));
    }

    // 7. 과업 존재 여부 확인
    @GetMapping("/requirement/{requirementId}/exists")
    public ResponseEntity<ExistsResponse> checkTasksExist(
        @PathVariable String requirementId
    ) {
        return ResponseEntity.ok(taskService.checkTasksExist(requirementId));
    }
}
```

### Service (SSE 생성 로직)
```java
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private OllamaClient ollamaClient;

    public SseEmitter generateTasksWithSSE(RequirementRequest request) {
        SseEmitter emitter = new SseEmitter(60000L); // 60초 타임아웃

        CompletableFuture.runAsync(() -> {
            try {
                // 상태 전송
                emitter.send(SseEmitter.event()
                    .name("status")
                    .data("{\"message\": \"요구사항을 분석하고 있습니다...\"}"));

                // LLM 호출
                String prompt = buildPrompt(request);
                String response = ollamaClient.generate(prompt);

                emitter.send(SseEmitter.event()
                    .name("status")
                    .data("{\"message\": \"LLM이 응답을 생성하고 있습니다...\"}"));

                // JSON 파싱
                List<Task> tasks = parseTasksFromResponse(response, request);

                // DB 저장 및 전송
                for (int i = 0; i < tasks.size(); i++) {
                    Task savedTask = taskRepository.save(tasks.get(i));

                    emitter.send(SseEmitter.event()
                        .name("task")
                        .data(savedTask));

                    Thread.sleep(500); // 0.5초 간격

                    emitter.send(SseEmitter.event()
                        .name("status")
                        .data(String.format("{\"message\": \"과업 %d/%d 생성됨\"}",
                            i + 1, tasks.size())));
                }

                // 완료
                emitter.send(SseEmitter.event()
                    .name("complete")
                    .data(String.format("{\"message\": \"✓ %d개 과업 생성 완료\", \"count\": %d}",
                        tasks.size(), tasks.size())));

                emitter.complete();

            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"message\": \"" + e.getMessage() + "\"}"));
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            }
        });

        return emitter;
    }
}
```

### Repository
```java
@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByParentRequirementIdOrderByIdAsc(String parentRequirementId);

    boolean existsByParentRequirementId(String parentRequirementId);

    long countByParentRequirementId(String parentRequirementId);

    @Modifying
    @Transactional
    void deleteByParentRequirementId(String parentRequirementId);
}
```

## CORS 설정

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

## 에러 응답 형식

모든 에러는 다음 형식으로 반환:

```json
{
  "success": false,
  "error": "에러 메시지",
  "timestamp": "2026-01-10T13:00:00",
  "path": "/api/tasks/REQ-AI-BA-0001-TASK-001"
}
```

## 성능 요구사항
- GET 요청 응답 시간: 100ms 이내
- SSE 과업 생성 시작: 1초 이내
- SSE 과업 생성 완료: 30초 이내
- 동시 요청 처리: 최소 50개

## 추가 고려사항
1. Task ID 생성 시 동시성 문제 처리 (DB 시퀀스 또는 Lock)
2. SSE 타임아웃 처리 (클라이언트 재연결)
3. LLM 응답 실패 시 재시도 로직 (최대 2회)
4. 로깅: 요청/응답, 에러, 생성된 과업 수
5. Rate Limiting (IP당 분당 20회)
