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
            Map.entry("통합 채팅 서비스", "CAT-001"),
            Map.entry("데이터 마트 설계", "CAT-002"),
            Map.entry("관리자 포털", "CAT-003"),
            Map.entry("LLM 모델 관리", "CAT-004"),
            Map.entry("LLMOps 구축", "CAT-005"),
            Map.entry("문서 QA 및 요약", "CAT-006"),
            Map.entry("보고서 자동 생성", "CAT-007"),
            Map.entry("T2SQL 파이프라인", "CAT-008"),
            Map.entry("벡터 검색 DB 구축", "CAT-009"),
            Map.entry("AI 코딩 어시스턴트", "CAT-010"),
            Map.entry("프로젝트 관리", "CAT-011"),
            Map.entry("개발 표준", "CAT-012"),
            Map.entry("유지보수", "CAT-013"),
            Map.entry("교육지원", "CAT-014"));

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
                if (majorCategoryId == null || majorCategoryId.contains(" ") || majorCategoryId.length() > 10
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
                        다음 RFP 요구사항을 분석하여 구체적이고 실행 가능한 세부 과업(Task)으로 분해해주세요.

                        [요구사항 정보]
                        - ID: %s
                        - 명칭: %s
                        - 정의: %s
                        - 제안요청내용:
                        %s

                        **1. 대분류 체계:**
                        가장 적절한 대분류 명칭을 하나만 선택하세요.
                        리스트: 통합 채팅 서비스, 데이터 마트 설계, 관리자 포털, LLM 모델 관리, LLMOps 구축, 문서 QA 및 요약, 보고서 자동 생성, T2SQL 파이프라인, 벡터 검색 DB 구축, AI 코딩 어시스턴트, 프로젝트 관리, 개발 표준, 유지보수, 교육지원

                        **2. 과업 분해 규칙:**
                        - 제안요청내용의 1개 항목을 기능을 기준으로 2~5개 과업으로 분해
                        - 독립적으로 개발 가능한 단위로 분해
                        - 의미 있는 단위로 분해

                        **3. 응답 형식 (JSON 배열):**
                        [
                          {
                            "summary": "과업 요약",
                            "majorCategory": "선택한 대분류 명칭",
                            "detailFunctionId": "FUNC-001",
                            "detailFunction": "상세 기능명",
                            "subFunction": "세부 기능 설명 (3-4문장)"
                          }
                        ]
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
