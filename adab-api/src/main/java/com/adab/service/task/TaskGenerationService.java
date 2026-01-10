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

    @org.springframework.beans.factory.annotation.Value("${spring.ai.ollama.chat.options.model:unknown}")
    private String ollamaModel;

    @org.springframework.beans.factory.annotation.Value("${spring.ai.openai.chat.options.model:unknown}")
    private String openaiModel;

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
            var chatResponse = chatModel.call(new Prompt(prompt));
            String llmResponse = chatResponse.getResult().getOutput().getContent();

            // LLM 모델명 추출 (설정 파일에서 가져오기)
            String modelName = "Unknown LLM";
            try {
                String chatModelClass = chatModel.getClass().getSimpleName();
                if (chatModelClass.contains("OpenAi")) {
                    modelName = "OpenAI " + openaiModel;
                } else if (chatModelClass.contains("Ollama")) {
                    modelName = "Ollama " + ollamaModel;
                } else if (chatModelClass.contains("Anthropic")) {
                    modelName = "Anthropic Claude";
                } else {
                    modelName = chatModelClass.replace("ChatModel", "").replace("Client", "");
                }
            } catch (Exception e) {
                log.warn("Failed to extract model name, using default: {}", e.getMessage());
            }
            log.info("LLM Response from {}: {}", modelName, llmResponse);

            // 5. JSON 파싱
            List<Map<String, String>> taskDataList = parseJsonFromResponse(llmResponse);

            if (taskDataList == null || taskDataList.isEmpty()) {
                sendError(emitter, "LLM 응답에서 JSON을 찾을 수 없습니다");
                emitter.complete();
                return;
            }

            // 6. 기존 Task 소프트 삭제 (중복 방지)
            int deletedCount = taskRepository.softDeleteByParentRequirementId(request.getRequirementId(), java.time.LocalDateTime.now());
            if (deletedCount > 0) {
                log.info("Soft deleted {} existing tasks for requirement: {}", deletedCount, request.getRequirementId());
            }

            // 7. Task 생성 및 전송
            AtomicInteger taskCount = new AtomicInteger(0);
            final String finalModelName = modelName; // final 변수로 만들어 람다에서 사용 가능하도록
            for (int i = 0; i < taskDataList.size(); i++) {
                Map<String, String> taskData = taskDataList.get(i);

                // Task ID 생성
                String taskId = String.format("%s-TASK-%03d", request.getRequirementId(), i + 1);

                // Task 엔티티 생성 및 저장
                Task task = new Task();
                task.setId(taskId);  // taskId -> id로 변경
                task.setParentRequirementId(request.getRequirementId());
                task.setParentIndex(request.getIndex());
                task.setSummary(taskData.get("summary"));
                task.setMajorCategoryId(taskData.getOrDefault("majorCategoryId", "CAT-" + String.format("%03d", i + 1)));
                task.setMajorCategory(taskData.get("majorCategory"));
                task.setDetailFunctionId(taskData.getOrDefault("detailFunctionId", "FUNC-" + String.format("%03d", i + 1)));
                task.setDetailFunction(taskData.get("detailFunction"));
                task.setSubFunction(taskData.get("subFunction"));
                task.setGeneratedBy(finalModelName);  // LLM 모델명 설정

                Task savedTask = taskRepository.save(task);

                // TaskResponse 생성 및 전송
                TaskResponse taskResponse = TaskResponse.builder()
                        .id(taskId)
                        .parentRequirementId(request.getRequirementId())
                        .parentIndex(request.getIndex())
                        .summary(savedTask.getSummary())
                        .majorCategoryId(savedTask.getMajorCategoryId())
                        .majorCategory(savedTask.getMajorCategory())
                        .detailFunctionId(savedTask.getDetailFunctionId())
                        .detailFunction(savedTask.getDetailFunction())
                        .subFunction(savedTask.getSubFunction())
                        .generatedBy(savedTask.getGeneratedBy())  // LLM 모델명 추가
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
        return String.format("""
                다음 RFP 요구사항을 분석하여 구체적이고 실행 가능한 세부 과업(Task)으로 분해해주세요.

                요구사항 정보:
                - ID: %s
                - 명칭: %s
                - 정의: %s
                - 제안요청내용:
                %s

                **핵심 원칙:**
                1. 제안요청내용의 각 항목을 **기능 단위**로 분해하세요
                2. 하나의 시스템/서비스/컴포넌트 = 1개 과업 (예: LLM 모델 관리, 통합 채팅 서비스, 데이터 마트 설계)
                3. 각 과업의 subFunction은 **요구사항 정의서 작성에 필요한 핵심 정보**를 포함해야 합니다

                **과업 분해 기준 (참고):**
                - LLM 모델 관리, LLMOps 구축, 통합 채팅 서비스, 데이터 마트 설계
                - 관리자 포털, 문서 QA 및 요약, 보고서 자동 생성
                - T2SQL 파이프라인, 벡터 검색 DB 구축, AI 코딩 어시스턴트
                - 프로젝트 관리, 개발 표준, 유지보수, 교육지원

                **과업 분해 규칙:**
                - 제안요청내용에 "○"나 "-"로 시작하는 항목이 있다면:
                  1) 각 항목이 **독립적인 기능/시스템**이면 → 1개 과업으로 생성
                  2) 한 항목에 **"또는" 으로 구분된 다른 기능**이 있으면 → 분리
                     예: "글 또는 이미지 생성" → "글 생성 기능", "이미지 생성 기능"
                  3) 한 항목에 여러 세부 기능이 나열되어도 **같은 시스템/목적**이면 → 1개 과업 유지
                     예: "LLM 모델 제시 및 학습" → 1개 과업 (LLM 모델 관리)
                     예: "채팅 UI 개발 및 API 연동" → 1개 과업 (통합 채팅 서비스)
                - **기능 단위로 적절히 그룹핑**하되, 과도하게 세분화하지 마세요

                **각 과업은 다음 JSON 형식으로 응답:**

                [
                  {
                    "summary": "과업 내용을 한 문장으로 요약 (무엇을 구현/개발/구축하는지 명확히)",
                    "majorCategoryId": "CAT-XXX",
                    "majorCategory": "기능 대분류 (예: 모델 개발, 데이터 처리, 인프라 구축, API 개발, 품질 검증 등)",
                    "detailFunctionId": "FUNC-XXX",
                    "detailFunction": "구체적인 기능명 (개발자가 즉시 이해할 수 있는 수준)",
                    "subFunction": "요구사항 정의서 작성을 위한 핵심 정보를 3-4문장으로 간결하게 작성:\n1) 구현 목적 및 범위 (무엇을 왜 만드는가)\n2) 핵심 기술 요구사항 (제안요청내용에 명시된 기술/방법)\n3) 주요 제약사항 또는 성능 기준 (있다면)\n제안요청내용에 명시된 핵심 내용만 포함하고, 불필요한 상세 설명은 제외할 것"
                  }
                ]

                **subFunction 작성 예시:**
                "프라이빗 클라우드 환경에서 독립적으로 운영 가능한 LLM 모델을 제시해야 합니다. 외부 API 의존 없이 자체 인프라에서 안전하게 운영되어야 하며, 제안 시 모델 규모, 필요 하드웨어 스펙, 예상 성능을 명시해야 합니다. 보안 및 데이터 주권 측면에서 민감 데이터가 외부 유출되지 않도록 격리 환경 운영이 가능해야 합니다."

                **주의사항:**
                - 제안요청내용에 명시되지 않은 기능이나 기술을 임의로 추가하지 마세요
                - 핵심 요구사항과 제약사항 위주로 간결하게 작성하세요
                - 각 과업이 독립적으로 이해되고 구현될 수 있도록 작성하세요

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
