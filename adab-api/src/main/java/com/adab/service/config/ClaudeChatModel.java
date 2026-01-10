package com.adab.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ClaudeChatModel implements ChatModel {

    private static final String DEFAULT_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final String apiKey;
    private final String model;
    private final String baseUrl;
    private final Float temperature;
    private final Integer maxTokens;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ClaudeChatModel(String apiKey, String model, String baseUrl, Float temperature, Integer maxTokens) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl != null ? baseUrl : DEFAULT_API_URL;
        this.temperature = temperature;
        this.maxTokens = maxTokens != null ? maxTokens : 4096;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", ANTHROPIC_VERSION);

            // Convert Spring AI messages to Claude format
            List<ClaudeMessage> claudeMessages = prompt.getInstructions().stream()
                    .map(msg -> {
                        String role = msg.getMessageType().getValue().toLowerCase();
                        // Claude only supports "user" and "assistant" roles
                        if (!role.equals("user") && !role.equals("assistant")) {
                            role = "user";
                        }
                        return new ClaudeMessage(role, msg.getContent());
                    })
                    .collect(Collectors.toList());

            ClaudeRequest request = new ClaudeRequest();
            request.setModel(model);
            request.setMessages(claudeMessages);
            request.setMaxTokens(maxTokens);

            if (temperature != null) {
                request.setTemperature(temperature);
            }

            String requestBody = objectMapper.writeValueAsString(request);
            log.debug("Claude API Request: {}", requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = baseUrl.endsWith("/messages") ? baseUrl : baseUrl + "/v1/messages";
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            log.debug("Claude API Response: {}", response.getBody());

            ClaudeResponse claudeResponse = objectMapper.readValue(response.getBody(), ClaudeResponse.class);

            // Extract text from content blocks
            String responseText = claudeResponse.getContent().stream()
                    .filter(content -> "text".equals(content.getType()))
                    .map(ClaudeContent::getText)
                    .collect(Collectors.joining("\n"));

            Generation generation = new Generation(responseText);

            return new ChatResponse(List.of(generation));

        } catch (Exception e) {
            log.error("Error calling Claude API", e);
            throw new RuntimeException("Claude API 호출 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        // Claude streaming not implemented yet, fallback to regular call
        return Flux.just(call(prompt));
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return null;
    }

    @Data
    static class ClaudeRequest {
        private String model;
        private List<ClaudeMessage> messages;

        @JsonProperty("max_tokens")
        private Integer maxTokens;

        private Float temperature;
    }

    @Data
    static class ClaudeMessage {
        private String role;
        private String content;

        public ClaudeMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Data
    static class ClaudeResponse {
        private String id;
        private String type;
        private String role;
        private List<ClaudeContent> content;
        private String model;

        @JsonProperty("stop_reason")
        private String stopReason;

        @JsonProperty("stop_sequence")
        private String stopSequence;

        private ClaudeUsage usage;
    }

    @Data
    static class ClaudeContent {
        private String type;
        private String text;
    }

    @Data
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    static class ClaudeUsage {
        @JsonProperty("input_tokens")
        private Integer inputTokens;

        @JsonProperty("output_tokens")
        private Integer outputTokens;
    }
}
