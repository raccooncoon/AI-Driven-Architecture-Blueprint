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
                You are a software architect assistant.
                Analyze the following RFP requirement text.
                Classify it into a category, provide a detailed explanation, and a short summary.
                Return the result in JSON format with keys: category, detail, summary.
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(rawText)
                .call()
                .entity(RequirementAnalysisResponse.class);
    }
}
