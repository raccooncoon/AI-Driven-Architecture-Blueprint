package com.adab.service.analysis;

import com.adab.dto.RequirementAnalysisResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class RequirementAnalysisService {

    private final ChatClient chatClient;

    public RequirementAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public RequirementAnalysisResponse analyze(String rawText) {
        String systemPrompt = """
                # Role
                You are a professional software architect assistant.

                # Task
                Analyze the provided RFP requirement text and extract key information.

                # Instructions
                1. Classify the requirement into an appropriate category.
                2. Provide a detailed technical explanation.
                3. Provide a concise summary.

                # Output Format
                Return ONLY a valid JSON object. Do not include any preamble or postamble.

                [JSON Structure]
                {
                  "category": "Category name",
                  "detail": "Detailed explanation",
                  "summary": "Short summary"
                }
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(rawText)
                .call()
                .entity(RequirementAnalysisResponse.class);
    }
}
