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

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    private final DynamicChatModelService dynamicChatModelService;

    private static final long SSE_TIMEOUT = 60000L; // 60초

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
                task.setTaskId(taskId);  // 비즈니스 ID 설정
                task.setParentRequirementId(request.getRequirementId());
                task.setParentIndex(request.getIndex());
                task.setSummary(taskData.get("summary"));
                task.setMajorCategoryId(taskData.getOrDefault("majorCategoryId", "CAT-" + String.format("%03d", i + 1)));
                task.setMajorCategory(taskData.get("majorCategory"));
                task.setDetailFunctionId(taskData.getOrDefault("detailFunctionId", "FUNC-" + String.format("%03d", i + 1)));
                task.setDetailFunction(taskData.get("detailFunction"));
                task.setSubFunction(taskData.get("subFunction"));
                task.setGeneratedBy(finalModelName);  // LLM 모델명 설정
                task.setCreatedAt(java.time.LocalDateTime.now());  // 생성 시간 수동 설정
                task.setUpdatedAt(java.time.LocalDateTime.now());  // 업데이트 시간 수동 설정

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

                **대분류 체계 (majorCategory 선택 시 참고):**
                - LLM 모델 관리: 모델 선정, 배포, 파인튜닝, 모니터링
                - LLMOps 구축: MLOps 파이프라인, 모델 버전 관리, A/B 테스트
                - 통합 채팅 서비스: 대화형 AI 인터페이스, 멀티턴 대화, 컨텍스트 관리
                - 데이터 마트 설계: 데이터 웨어하우스, ETL, 데이터 모델링
                - 관리자 포털: 백오피스, 대시보드, 사용자 관리
                - 문서 QA 및 요약: 문서 이해, 질의응답, 자동 요약
                - 보고서 자동 생성: 템플릿 기반 생성, 데이터 시각화
                - T2SQL 파이프라인: 자연어-SQL 변환, 쿼리 검증
                - 벡터 검색 DB 구축: 임베딩, 벡터 인덱싱, 유사도 검색
                - AI 코딩 어시스턴트: 코드 생성, 리뷰, 리팩토링
                - 프로젝트 관리: 일정, 이슈, 리스크 관리
                - 개발 표준: 코딩 컨벤션, 아키텍처 가이드, 보안 정책
                - 유지보수: 장애 대응, 성능 튜닝, 버그 수정
                - 교육지원: 사용자 가이드, 교육 자료, 온보딩

                **과업 분해 전략:**

                1. **시스템/서비스 레벨 분해:**
                   - 하나의 기능 요구사항을 구현하기 위해 필요한 **여러 시스템 컴포넌트**를 식별
                   - 예: "LLM 기반 챗봇" → "LLM 모델 관리", "통합 채팅 서비스", "관리자 포털"

                2. **개발 단계별 분해:**
                   - 기획/설계 → 개발 → 테스트 → 배포 단계로 세분화
                   - 예: "데이터 분석 시스템" → "데이터 마트 설계", "ETL 파이프라인 개발", "대시보드 구현"

                3. **기술 스택별 분해:**
                   - Frontend, Backend, Database, Infrastructure 등으로 구분
                   - 예: "검색 시스템" → "검색 UI 개발", "검색 API 구축", "벡터 검색 DB 구축"

                4. **기능 계층별 분해:**
                   - 핵심 기능, 부가 기능, 운영 기능으로 구분
                   - 예: "AI 서비스" → "AI 모델 개발", "모니터링 대시보드", "로깅 및 추적"

                **분해 규칙:**
                - 제안요청내용의 **1개 항목 = 보통 2~5개 과업**으로 분해
                - "~및 ~", "~와 ~", "~또는 ~" 표현이 있으면 별도 과업으로 분리 검토
                - 독립적으로 개발 가능한 단위로 분해 (각각 다른 개발자가 맡을 수 있는 수준)
                - 너무 세밀하게 쪼개지 않되, 하나의 과업이 여러 대분류에 걸치지 않도록 함

                **각 과업은 다음 JSON 형식으로 응답:**

                [
                  {
                    "summary": "과업 내용을 한 문장으로 요약 (무엇을 구현/개발/구축하는지 명확히)",
                    "majorCategoryId": "CAT-XXX",
                    "majorCategory": "위 대분류 체계 중 1개 선택 (정확히 일치하는 명칭 사용)",
                    "detailFunctionId": "FUNC-XXX",
                    "detailFunction": "구체적인 기능명 (개발자가 즉시 이해할 수 있는 수준)",
                    "subFunction": "요구사항 정의서 작성을 위한 핵심 정보를 3-4문장으로 간결하게:\n1) 구현 목적 및 범위 (이 과업이 전체 요구사항의 어느 부분을 담당하는지)\n2) 핵심 기술 요구사항 (사용할 기술/방법)\n3) 주요 산출물 또는 성공 기준\n제안요청내용에 명시된 내용 기반으로 작성하되, 이 과업의 역할을 명확히 할 것"
                  }
                ]

                **분해 예시:**

                제안요청내용: "LLM 기반 대화형 AI 챗봇을 개발하고, 관리자가 대화 이력을 모니터링할 수 있는 포털을 구축"

                → 과업 분해:
                1. {
                     "majorCategory": "LLM 모델 관리",
                     "detailFunction": "대화형 LLM 모델 선정 및 배포",
                     "subFunction": "챗봇 서비스에 적합한 LLM 모델을 선정하고 프라이빗 클라우드에 배포합니다..."
                   }
                2. {
                     "majorCategory": "통합 채팅 서비스",
                     "detailFunction": "멀티턴 대화 인터페이스 개발",
                     "subFunction": "사용자와 LLM 간의 실시간 대화를 처리하는 채팅 UI 및 백엔드 API를 구현합니다..."
                   }
                3. {
                     "majorCategory": "관리자 포털",
                     "detailFunction": "대화 이력 모니터링 대시보드",
                     "subFunction": "관리자가 사용자 대화 이력, 토큰 사용량, 응답 품질을 실시간으로 확인할 수 있는 대시보드를 개발합니다..."
                   }

                **주의사항:**
                - majorCategory는 위 대분류 체계의 **정확한 명칭**을 사용하세요
                - 제안요청내용에 명시되지 않은 기능을 임의로 추가하지 마세요
                - 각 과업은 독립적으로 착수 가능하도록 작성하세요
                - 최소 2개 이상의 과업으로 분해하되, 의미 있는 단위로만 분해하세요

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
