package com.adab.service.task;

import com.adab.domain.task.Task;
import com.adab.domain.task.TaskRepository;
import com.adab.dto.TaskGenerationRequest;
import com.adab.dto.TaskResponse;
import com.adab.service.config.DynamicChatModelService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskGenerationService {

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    private final DynamicChatModelService dynamicChatModelService;

    private static final Map<String, String> CATEGORY_MAP = Map.ofEntries(
            Map.entry("통합 채팅 서비스", "INTEGRATED_CHAT_SERVICE"),
            Map.entry("데이터 마트 설계", "DATA_MART_DESIGN"),
            Map.entry("관리자 포털", "ADMIN_PORTAL"),
            Map.entry("LLM 모델 관리", "LLM_MODEL_MANAGEMENT"),
            Map.entry("LLMOps 구축", "LLMOPS_INFRASTRUCTURE"),
            Map.entry("문서 QA 및 요약", "DOC_QA_AND_SUMMARY"),
            Map.entry("보고서 자동 생성", "REPORT_GENERATION"),
            Map.entry("T2SQL 파이프라인", "T2SQL_PIPELINE"),
            Map.entry("벡터 검색 DB 구축", "VECTOR_SEARCH_DB"),
            Map.entry("AI 코딩 어시스턴트", "AI_CODING_ASSISTANT"),
            Map.entry("프로젝트 관리", "PROJECT_MANAGEMENT"),
            Map.entry("개발 표준", "DEVELOPMENT_STANDARDS"),
            Map.entry("유지보수", "MAINTENANCE"),
            Map.entry("교육지원", "EDUCATION_SUPPORT"));

    public void generateTasksWithStreaming(TaskGenerationRequest request, SseEmitter emitter) {
        try {
            // 1. 상태 전송: 분석 시작
            sendStatus(emitter, "요구사항을 분석하고 있습니다...");

            // 2. 동적으로 ChatModel 생성
            ChatModel chatModel = dynamicChatModelService.getDefaultChatModel();
            String modelName = dynamicChatModelService.getDefaultModelName();

            // 3. 프롬프트 생성
            String prompt = buildPrompt(request);
            log.info("Generated prompt for requirement: {} using model: {}", request.getRequirementId(), modelName);

            // 4. LLM 호출 상태 전송
            sendStatus(emitter, "LLM이 응답을 생성하고 있습니다...");

            // 5. LLM 호출
            var chatResponse = chatModel.call(new Prompt(prompt));
            String llmResponse = chatResponse.getResult().getOutput().getContent();

            log.info("LLM Response from {}: {}", modelName, llmResponse);
            final String finalModelName = modelName;

            // 5. JSON 파싱
            List<Map<String, String>> taskDataList = parseJsonFromResponse(llmResponse);

            if (taskDataList == null || taskDataList.isEmpty()) {
                sendError(emitter, "LLM 응답에서 JSON을 찾을 수 없습니다");
                emitter.complete();
                return;
            }

            // 6. 기존 Task 소프트 삭제 (중복 방지) - 별도 트랜잭션으로 실행
            deletePreviousTasks(request.getRequirementId());

            // 7. Task 생성 및 전송
            AtomicInteger taskCount = new AtomicInteger(0);
            for (int i = 0; i < taskDataList.size(); i++) {
                Map<String, String> taskData = taskDataList.get(i);

                // Task ID 생성 (비즈니스 식별자)
                String taskId = String.format("%s-TASK-%03d", request.getRequirementId(), i + 1);

                // Task 엔티티 생성 및 저장
                Task task = new Task();
                // uuid는 자동 생성됨 (@GeneratedValue)
                task.setTaskId(taskId); // 비즈니스 ID 설정
                task.setParentRequirementId(request.getRequirementId());
                task.setParentIndex(request.getIndex());
                task.setSummary(taskData.get("summary"));

                String majorCategory = taskData.get("majorCategory");
                String majorCategoryId = taskData.get("majorCategoryId");

                // 명칭 기반 ID 자동 보정 (LLM 실수 및 형식 불일치 방지)
                if (majorCategory != null) {
                    String cleanName = majorCategory.trim();
                    for (Map.Entry<String, String> entry : CATEGORY_MAP.entrySet()) {
                        if (cleanName.contains(entry.getKey())) {
                            majorCategoryId = entry.getValue();
                            majorCategory = entry.getKey(); // 명칭도 표준 명칭으로 교정
                            break;
                        }
                    }
                }

                // ID가 여전히 부적절하거나 명칭과 ID가 같은 경우 (ID 누락 시)
                if (majorCategoryId == null || majorCategoryId.contains(" ")
                        || majorCategoryId.equals(majorCategory)) {
                    majorCategoryId = "ETC-" + String.format("%03d", i + 1);
                }

                task.setMajorCategoryId(majorCategoryId);
                task.setMajorCategory(majorCategory);
                task.setDetailFunctionId(
                        taskData.getOrDefault("detailFunctionId", "FUNC-" + String.format("%03d", i + 1)));
                task.setDetailFunction(taskData.get("detailFunction"));
                task.setSubFunction(taskData.get("subFunction"));
                task.setGeneratedBy(finalModelName); // LLM 모델명 설정
                task.setCreatedAt(java.time.LocalDateTime.now()); // 생성 시간 수동 설정
                task.setUpdatedAt(java.time.LocalDateTime.now()); // 업데이트 시간 수동 설정

                Task savedTask = saveTask(task);

                // TaskResponse 생성 및 전송
                TaskResponse taskResponse = TaskResponse.builder()
                        .id(savedTask.getTaskId())
                        .parentRequirementId(request.getRequirementId())
                        .parentIndex(request.getIndex())
                        .summary(savedTask.getSummary())
                        .majorCategoryId(savedTask.getMajorCategoryId())
                        .majorCategory(savedTask.getMajorCategory())
                        .detailFunctionId(savedTask.getDetailFunctionId())
                        .detailFunction(savedTask.getDetailFunction())
                        .subFunction(savedTask.getSubFunction())
                        .generatedBy(savedTask.getGeneratedBy()) // LLM 모델명 추가
                        .createdAt(savedTask.getCreatedAt())
                        .updatedAt(savedTask.getUpdatedAt())
                        .build();

                sendTask(emitter, taskResponse);
                taskCount.incrementAndGet();

                // 상태 업데이트
                sendStatus(emitter, String.format("과업 %d/%d 생성됨", taskCount.get(), taskDataList.size()));

                // 0.5초 대기
                Thread.sleep(500);
            }

            // 8. 완료 메시지
            sendComplete(emitter, String.format("✓ %d개 과업 생성 완료", taskCount.get()));
            emitter.complete();

        } catch (Exception e) {
            log.error("Error generating tasks", e);
            sendError(emitter, "과업 생성 중 오류가 발생했습니다: " + e.getMessage());
            emitter.completeWithError(e);
        }
    }

    private String buildPrompt(TaskGenerationRequest request) {
        return String.format(
                """
                        # Role
                        You are a professional IT System Architect. Your task is to decompose the given RFP requirement into detailed, actionable implementation tasks.

                        # Input Data
                        <requirement_info>
                        - Requirement ID: %s
                        - Requirement Name: %s
                        - Definition: %s
                        - Request Content:
                        %s
                        </requirement_info>

                        # Task Decomposition Rules
                        1. Analyze the 'Request Content' and split it into 2-5 detailed tasks based on functionality.
                        2. Each task must be an independent unit of development.
                        3. Tasks should be meaningful and descriptive.

                        # Categorization Instructions
                        - Select the most appropriate 'majorCategory' from the list below.
                        - **NOTE**: You can propose a NEW category if none of the provided ones fit perfectly, but ensure it follows the same format: 'Korean Name' and a descriptive 'English_ID'.
                        - **CRITICAL**: Use the exact Korean name for `majorCategory` and the corresponding English ID for `majorCategoryId`.

                        [Categories List]
                        - 통합 채팅 서비스 (ID: INTEGRATED_CHAT_SERVICE)
                        - 데이터 마트 설계 (ID: DATA_MART_DESIGN)
                        - 관리자 포털 (ID: ADMIN_PORTAL)
                        - LLM 모델 관리 (ID: LLM_MODEL_MANAGEMENT)
                        - LLMOps 구축 (ID: LLMOPS_INFRASTRUCTURE)
                        - 문서 QA 및 요약 (ID: DOC_QA_AND_SUMMARY)
                        - 보고서 자동 생성 (ID: REPORT_GENERATION)
                        - T2SQL 파이프라인 (ID: T2SQL_PIPELINE)
                        - 벡터 검색 DB 구축 (ID: VECTOR_SEARCH_DB)
                        - AI 코딩 어시스턴트 (ID: AI_CODING_ASSISTANT)
                        - 프로젝트 관리 (ID: PROJECT_MANAGEMENT)
                        - 개발 표준 (ID: DEVELOPMENT_STANDARDS)
                        - 유지보수 (ID: MAINTENANCE)
                        - 교육지원 (ID: EDUCATION_SUPPORT)

                        # Output Format
                        Return ONLY a valid JSON array of objects. Do not include any preamble or postamble.

                        [JSON Structure]
                        [
                          {
                            "summary": "Short summary of the task (Korean)",
                            "majorCategory": "Selected Korean Category Name",
                            "majorCategoryId": "Corresponding English ID",
                            "detailFunctionId": "FUNC-XXX (e.g., FUNC-001)",
                            "detailFunction": "Specific function name (Korean)",
                            "subFunction": "Detailed description of the task (Korean, 3-4 sentences)"
                          }
                        ]

                        # Language Requirements
                        - `majorCategoryId` and `detailFunctionId` MUST be in English.
                        - All other descriptive fields (summary, majorCategory, detailFunction, subFunction) MUST be in Korean.
                        """,
                request.getRequirementId(),
                request.getName(),
                request.getDefinition(),
                request.getRequestContent());
    }

    private List<Map<String, String>> parseJsonFromResponse(String response) {
        try {
            // JSON 배열 추출 (정규식: \[[\s\S]*\])
            Pattern pattern = Pattern.compile("\\[([\\s\\S]*)\\]");
            Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                String jsonArray = "[" + matcher.group(1) + "]";

                // JSON 문자열 값 내부의 이스케이프되지 않은 줄바꿈 처리
                // "subFunction": "텍스트\n텍스트" -> "subFunction": "텍스트\\n텍스트"
                jsonArray = fixUnescapedNewlines(jsonArray);

                return objectMapper.readValue(jsonArray, new TypeReference<List<Map<String, String>>>() {
                });
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to parse JSON from LLM response", e);
            log.error("Response content: {}", response);
            return null;
        }
    }

    /**
     * JSON 문자열 값 내부의 이스케이프되지 않은 줄바꿈을 이스케이프 처리
     */
    private String fixUnescapedNewlines(String json) {
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                result.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                result.append(c);
                escaped = true;
                continue;
            }

            if (c == '"') {
                result.append(c);
                inString = !inString;
                continue;
            }

            // 문자열 내부의 줄바꿈 문자를 이스케이프
            if (inString && (c == '\n' || c == '\r')) {
                if (c == '\n') {
                    result.append("\\n");
                } else if (c == '\r') {
                    result.append("\\r");
                }
                continue;
            }

            result.append(c);
        }

        return result.toString();
    }

    private void sendStatus(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("status")
                    .data(Map.of("message", message)));
        } catch (IOException e) {
            log.error("Failed to send status", e);
        }
    }

    private void sendTask(SseEmitter emitter, TaskResponse task) {
        try {
            emitter.send(SseEmitter.event()
                    .name("task")
                    .data(task));
        } catch (IOException e) {
            log.error("Failed to send task", e);
        }
    }

    private void sendComplete(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("complete")
                    .data(Map.of("message", message)));
        } catch (IOException e) {
            log.error("Failed to send complete", e);
        }
    }

    private void sendError(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(Map.of("message", message)));
        } catch (IOException e) {
            log.error("Failed to send error", e);
        }
    }

    /**
     * 기존 Task 소프트 삭제 (별도 트랜잭션)
     */
    @Transactional
    public void deletePreviousTasks(String requirementId) {
        int deletedCount = taskRepository.softDeleteByParentRequirementId(requirementId, java.time.LocalDateTime.now());
        if (deletedCount > 0) {
            log.info("Soft deleted {} existing tasks for requirement: {}", deletedCount, requirementId);
        }
    }

    /**
     * Task 저장 (별도 트랜잭션)
     */
    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.saveAndFlush(task);
    }
}
