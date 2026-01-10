package com.adab.service.task;

import com.adab.domain.task.Task;
import com.adab.domain.task.TaskRepository;
import com.adab.dto.TaskGenerationRequest;
import com.adab.dto.TaskResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskGenerationService {

    private final ChatModel chatModel;
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;

    private static final long SSE_TIMEOUT = 60000L; // 60초

    public void generateTasksWithStreaming(TaskGenerationRequest request, SseEmitter emitter) {
        try {
            // 1. 상태 전송: 분석 시작
            sendStatus(emitter, "요구사항을 분석하고 있습니다...");

            // 2. 프롬프트 생성
            String prompt = buildPrompt(request);
            log.info("Generated prompt for requirement: {}", request.getRequirementId());

            // 3. LLM 호출 상태 전송
            sendStatus(emitter, "LLM이 응답을 생성하고 있습니다...");

            // 4. LLM 호출
            String llmResponse = chatModel.call(new Prompt(prompt)).getResult().getOutput().getContent();
            log.info("LLM Response: {}", llmResponse);

            // 5. JSON 파싱
            List<Map<String, String>> taskDataList = parseJsonFromResponse(llmResponse);

            if (taskDataList == null || taskDataList.isEmpty()) {
                sendError(emitter, "LLM 응답에서 JSON을 찾을 수 없습니다");
                emitter.complete();
                return;
            }

            // 6. 기존 Task 삭제 (중복 방지)
            List<Task> existingTasks = taskRepository.findByParentRequirementId(request.getRequirementId());
            if (!existingTasks.isEmpty()) {
                taskRepository.deleteAll(existingTasks);
                log.info("Deleted {} existing tasks for requirement: {}", existingTasks.size(), request.getRequirementId());
            }

            // 7. Task 생성 및 전송
            AtomicInteger taskCount = new AtomicInteger(0);
            for (int i = 0; i < taskDataList.size(); i++) {
                Map<String, String> taskData = taskDataList.get(i);

                // Task ID 생성
                String taskId = String.format("%s-TASK-%03d", request.getRequirementId(), i + 1);

                // Task 엔티티 생성 및 저장
                Task task = new Task();
                task.setTaskId(taskId);
                task.setParentRequirementId(request.getRequirementId());
                task.setParentIndex(request.getIndex());
                task.setSummary(taskData.get("summary"));
                task.setMajorCategoryId(taskData.getOrDefault("majorCategoryId", "CAT-" + String.format("%03d", i + 1)));
                task.setMajorCategory(taskData.get("majorCategory"));
                task.setDetailFunctionId(taskData.getOrDefault("detailFunctionId", "FUNC-" + String.format("%03d", i + 1)));
                task.setDetailFunction(taskData.get("detailFunction"));
                task.setSubFunction(taskData.get("subFunction"));

                taskRepository.save(task);

                // TaskResponse 생성 및 전송
                TaskResponse taskResponse = TaskResponse.builder()
                        .id(taskId)
                        .parentRequirementId(request.getRequirementId())
                        .parentIndex(request.getIndex())
                        .summary(task.getSummary())
                        .majorCategoryId(task.getMajorCategoryId())
                        .majorCategory(task.getMajorCategory())
                        .detailFunctionId(task.getDetailFunctionId())
                        .detailFunction(task.getDetailFunction())
                        .subFunction(task.getSubFunction())
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
        return String.format("""
                다음 RFP 요구사항을 분석하여 세부 과업(Task)으로 분해해주세요.

                요구사항 정보:
                - ID: %s
                - 명칭: %s
                - 정의: %s
                - 제안요청내용:
                %s

                **중요: 제안요청내용에 나열된 모든 항목(한 줄 한 줄)을 빠짐없이 과업으로 생성해야 합니다.**

                제안요청내용의 각 줄(항목)을 분석하여, 각각을 하나의 독립적인 과업으로 만들어주세요.
                - 제안요청내용에 "○"나 "-"로 시작하는 항목이 있다면 각 항목마다 별도의 과업을 생성하세요.
                - 만약 제안요청내용이 한 줄만 있다면 그것을 여러 세부 과업으로 나누지 말고 하나의 과업으로 생성하세요.
                - 항목이 여러 개라면 반드시 모든 항목을 포함하여 과업을 생성하세요.

                각 과업은 다음 형식의 JSON 배열로 응답해주세요:

                [
                  {
                    "summary": "과업 내용 요약 (제안요청내용의 해당 항목을 구체적으로)",
                    "majorCategoryId": "CAT-XXX",
                    "majorCategory": "기능 대분류 (예: AI 모델 개발, 데이터 처리, 인프라 구축, 품질 검증 등)",
                    "detailFunctionId": "FUNC-XXX",
                    "detailFunction": "상세 기능명",
                    "subFunction": "세부 기능 설명"
                  }
                ]

                JSON 배열만 응답하고 다른 설명은 포함하지 마세요.
                """,
                request.getRequirementId(),
                request.getName(),
                request.getDefinition(),
                request.getRequestContent()
        );
    }

    private List<Map<String, String>> parseJsonFromResponse(String response) {
        try {
            // JSON 배열 추출 (정규식: \[[\s\S]*\])
            Pattern pattern = Pattern.compile("\\[([\\s\\S]*)\\]");
            Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                String jsonArray = "[" + matcher.group(1) + "]";
                return objectMapper.readValue(jsonArray, new TypeReference<List<Map<String, String>>>() {});
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to parse JSON from LLM response", e);
            return null;
        }
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
}
