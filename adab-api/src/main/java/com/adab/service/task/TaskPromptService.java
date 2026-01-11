package com.adab.service.task;

import com.adab.dto.TaskGenerationRequest;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskPromptService {

    public Prompt buildPrompt(TaskGenerationRequest request) {
        String systemInstruction = """
                # Role
                You are a professional IT System Architect. Your task is to decompose the given RFP requirement into detailed, actionable implementation tasks.

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
                """;

        String userPrompt = String.format("""
                # Input Data
                <requirement_info>
                - Requirement ID: %s
                - Requirement Name: %s
                - Definition: %s
                - Request Content:
                %s
                </requirement_info>

                위의 요구사항을 바탕으로 과업 리스트를 JSON 배열로 생성해 주세요.
                """,
                request.getRequirementId(),
                request.getName(),
                request.getDefinition(),
                request.getRequestContent());

        return new Prompt(List.of(
                new SystemMessage(systemInstruction),
                new UserMessage(userPrompt)));
    }
}
