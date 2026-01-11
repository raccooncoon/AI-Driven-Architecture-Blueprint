package com.adab.service.task;

import com.adab.domain.task.Task;
import com.adab.domain.task.TaskRepository;
import com.adab.dto.TaskGenerationRequest;
import com.adab.dto.TaskResponse;
import com.adab.service.common.SseNotificationService;
import com.adab.service.config.DynamicChatModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskGenerationService {

    private final TaskRepository taskRepository;
    private final DynamicChatModelService dynamicChatModelService;
    private final SseNotificationService sseNotificationService;
    private final TaskPromptService taskPromptService;
    private final TaskParserService taskParserService;

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
            sseNotificationService.sendStatus(emitter, "요구사항을 분석하고 있습니다...");

            // 2. 동적으로 ChatModel 생성
            ChatModel chatModel = dynamicChatModelService.getDefaultChatModel();
            String modelName = dynamicChatModelService.getDefaultModelName();

            // 3. 프롬프트 생성
            Prompt prompt = taskPromptService.buildPrompt(request);
            log.info("Generated prompt for requirement: {} using model: {}", request.getRequirementId(), modelName);

            // 4. LLM 호출 상태 전송
            sseNotificationService.sendStatus(emitter, "LLM이 응답을 생성하고 있습니다...");

            // 5. LLM 호출
            var chatResponse = chatModel.call(prompt);
            String llmResponse = chatResponse.getResult().getOutput().getContent();

            log.info("LLM Response from {}: {}", modelName, llmResponse);

            // 6. JSON 파싱
            List<Map<String, String>> taskDataList = taskParserService.parseJsonFromResponse(llmResponse);

            if (taskDataList == null || taskDataList.isEmpty()) {
                sseNotificationService.sendError(emitter, "LLM 응답에서 JSON을 찾을 수 없습니다");
                emitter.complete();
                return;
            }

            // 7. 기존 Task 소프트 삭제 (중복 방지)
            deletePreviousTasks(request.getRequirementId());

            // 8. Task 생성 및 전송
            AtomicInteger taskCount = new AtomicInteger(0);
            for (int i = 0; i < taskDataList.size(); i++) {
                Map<String, String> taskData = taskDataList.get(i);

                // Task ID 생성 (비즈니스 식별자)
                String taskId = String.format("%s-TASK-%03d", request.getRequirementId(), i + 1);

                // Task 엔티티 생성
                Task task = new Task();
                task.setTaskId(taskId);
                task.setParentRequirementId(request.getRequirementId());
                task.setParentIndex(request.getIndex());
                task.setSummary(taskData.get("summary"));

                String majorCategory = taskData.get("majorCategory");
                String majorCategoryId = taskData.get("majorCategoryId");

                // 명칭 기반 ID 자동 보정
                if (majorCategory != null) {
                    String cleanName = majorCategory.trim();
                    for (Map.Entry<String, String> entry : CATEGORY_MAP.entrySet()) {
                        if (cleanName.contains(entry.getKey())) {
                            majorCategoryId = entry.getValue();
                            majorCategory = entry.getKey();
                            break;
                        }
                    }
                }

                // ID 보정 (ETC 처리)
                if (majorCategoryId == null || majorCategoryId.contains(" ") || majorCategoryId.equals(majorCategory)) {
                    majorCategoryId = "ETC-" + String.format("%03d", i + 1);
                }

                task.setMajorCategoryId(majorCategoryId);
                task.setMajorCategory(majorCategory);
                task.setDetailFunctionId(
                        taskData.getOrDefault("detailFunctionId", "FUNC-" + String.format("%03d", i + 1)));
                task.setDetailFunction(taskData.get("detailFunction"));
                task.setSubFunction(taskData.get("subFunction"));
                task.setGeneratedBy(modelName);
                task.setCreatedAt(LocalDateTime.now());
                task.setUpdatedAt(LocalDateTime.now());

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
                        .generatedBy(savedTask.getGeneratedBy())
                        .createdAt(savedTask.getCreatedAt())
                        .updatedAt(savedTask.getUpdatedAt())
                        .build();

                sseNotificationService.sendTask(emitter, taskResponse);
                taskCount.incrementAndGet();

                // 상태 업데이트
                sseNotificationService.sendStatus(emitter,
                        String.format("과업 %d/%d 생성됨", taskCount.get(), taskDataList.size()));

                // 0.5초 대기 (SSE 시각적 효과)
                Thread.sleep(500);
            }

            // 9. 완료 메시지
            sseNotificationService.sendComplete(emitter, String.format("✓ %d개 과업 생성 완료", taskCount.get()));
            emitter.complete();

        } catch (Exception e) {
            log.error("Error generating tasks", e);
            sseNotificationService.sendError(emitter, "과업 생성 중 오류가 발생했습니다: " + e.getMessage());
            emitter.completeWithError(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletePreviousTasks(String requirementId) {
        int deletedCount = taskRepository.softDeleteByParentRequirementId(requirementId, LocalDateTime.now());
        if (deletedCount > 0) {
            log.info("Soft deleted {} existing tasks for requirement: {}", deletedCount, requirementId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Task saveTask(Task task) {
        return taskRepository.saveAndFlush(task);
    }
}
