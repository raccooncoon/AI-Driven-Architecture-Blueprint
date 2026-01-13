package com.adab.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GeminiChatModel extends AbstractHttpChatModel {

    public GeminiChatModel(String apiKey, String model, String baseUrl, Float temperature, Integer maxTokens) {
        super(apiKey, model, baseUrl != null ? baseUrl : "https://generativelanguage.googleapis.com", temperature,
                maxTokens);
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Extract system instruction and regular messages
            String systemInstruction = prompt.getInstructions().stream()
                    .filter(msg -> msg.getMessageType().getValue().equalsIgnoreCase("system"))
                    .map(msg -> msg.getContent())
                    .collect(Collectors.joining("\n"));

            List<GeminiContent> contents = prompt.getInstructions().stream()
                    .filter(msg -> !msg.getMessageType().getValue().equalsIgnoreCase("system"))
                    .map(msg -> {
                        String role = msg.getMessageType().getValue().equalsIgnoreCase("assistant") ? "model" : "user";
                        return new GeminiContent(role, List.of(new GeminiPart(msg.getContent())));
                    })
                    .collect(Collectors.toList());

            // Merge system instruction into the first user message if present
            if (systemInstruction != null && !systemInstruction.isEmpty() && !contents.isEmpty()) {
                GeminiContent firstContent = contents.get(0);
                String originalText = firstContent.getParts().get(0).getText();
                String newText = "Instruction: " + systemInstruction + "\n\nUser Content: " + originalText;
                firstContent.setParts(List.of(new GeminiPart(newText)));
            }

            GeminiRequest request = new GeminiRequest();
            request.setContents(contents);

            GeminiGenerationConfig config = new GeminiGenerationConfig();
            if (temperature != null)
                config.setTemperature(temperature);
            if (maxTokens != null)
                config.setMaxOutputTokens(maxTokens);
            request.setGenerationConfig(config);

            String requestBody = objectMapper.writeValueAsString(request);
            log.info("Gemini Request to Model {}: {}", model, requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // Use v1 for maximum stability with GA models
            String url = String.format("%s/v1/models/%s:generateContent?key=%s",
                    baseUrl, model.trim(), apiKey.trim());

            // Retry loop for 503 Service Unavailable
            int maxRetries = 3;
            int attempt = 0;
            long backoff = 1000;

            while (true) {
                try {
                    ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                    log.info("Gemini Response status: {}", response.getStatusCode());

                    GeminiResponse geminiResponse = objectMapper.readValue(response.getBody(), GeminiResponse.class);

                    if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                        log.info("Gemini Candidate Finish Reason: {}",
                                geminiResponse.getCandidates().get(0).getFinishReason());
                    }

                    if (geminiResponse.getCandidates() == null || geminiResponse.getCandidates().isEmpty()) {
                        throw new RuntimeException("Gemini API returned no candidates. Check safety filters.");
                    }

                    GeminiCandidate candidate = geminiResponse.getCandidates().get(0);
                    if (candidate.getContent() == null || candidate.getContent().getParts() == null
                            || candidate.getContent().getParts().isEmpty()) {
                        throw new RuntimeException(
                                "Gemini candidate has no content. Finish reason: " + candidate.getFinishReason());
                    }

                    String responseText = candidate.getContent().getParts().get(0).getText();
                    Generation generation = new Generation(responseText);

                    return new ChatResponse(List.of(generation));

                } catch (org.springframework.web.client.HttpServerErrorException e) {
                    if (e.getStatusCode().value() == 503 && attempt < maxRetries) {
                        try {
                            log.warn("Gemini Service Unavailable (503). Retrying in {}ms (Attempt {}/{})", backoff,
                                    attempt + 1, maxRetries);
                            Thread.sleep(backoff);
                            backoff *= 2; // Exponential backoff
                            attempt++;
                            continue;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Interrupted during retry backoff", ie);
                        }
                    }
                    throw e; // Rethrow if not 503 or retries exhausted
                } catch (Exception e) {
                    throw e; // Rethrow other exceptions
                }
            }
        } catch (Exception e) {
            log.error("Gemini API failure", e);
            throw new RuntimeException("Gemini generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return Flux.just(call(prompt));
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GeminiRequest {
        private List<GeminiContent> contents;

        @com.fasterxml.jackson.annotation.JsonProperty("generationConfig")
        private GeminiGenerationConfig generationConfig;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GeminiContent {
        private String role;
        private List<GeminiPart> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GeminiPart {
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GeminiGenerationConfig {
        private Float temperature;
        private Integer maxOutputTokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    static class GeminiResponse {
        private List<GeminiCandidate> candidates;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    static class GeminiCandidate {
        private GeminiContentResponse content;
        private String finishReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    static class GeminiContentResponse {
        private List<GeminiPart> parts;
    }
}
