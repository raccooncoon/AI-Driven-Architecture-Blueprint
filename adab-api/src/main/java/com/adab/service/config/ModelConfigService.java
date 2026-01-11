package com.adab.service.config;

import com.adab.domain.config.ModelConfig;
import com.adab.domain.config.ModelConfigRepository;
import com.adab.dto.ModelConfigRequest;
import com.adab.dto.ModelConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelConfigService {

    private final ModelConfigRepository modelConfigRepository;

    /**
     * 실행 시 Gemini 모델의 maxTokens 설정을 자동으로 패치 (기존 2048 -> 4096)
     */
    @PostConstruct
    @Transactional
    public void patchGeminiMaxTokens() {
        modelConfigRepository.findByName("gemini").ifPresent(config -> {
            if ("2048".equals(config.getMaxTokens()) || config.getMaxTokens() == null) {
                log.info("Patching existing Gemini maxTokens from {} to 4096", config.getMaxTokens());
                config.setMaxTokens("4096");
                modelConfigRepository.save(config);
            }
        });
    }

    /**
     * 모든 모델 설정 조회
     */
    public List<ModelConfigResponse> getAllConfigs() {
        return modelConfigRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 모델 설정 조회
     */
    public ModelConfigResponse getConfigByName(String name) {
        return modelConfigRepository.findByName(name)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Model config not found: " + name));
    }

    /**
     * 기본 모델 설정 조회
     */
    public ModelConfigResponse getDefaultConfig() {
        return modelConfigRepository.findByIsDefaultTrue()
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("No default model config found"));
    }

    /**
     * 모델 설정 생성 또는 업데이트
     */
    @Transactional
    public ModelConfigResponse createOrUpdateConfig(String name, ModelConfigRequest request) {
        ModelConfig config = modelConfigRepository.findByName(name)
                .orElse(ModelConfig.builder()
                        .name(name)
                        .enabled(false)
                        .isDefault(false)
                        .build());

        // 업데이트
        if (request.getEnabled() != null) {
            config.setEnabled(request.getEnabled());
        }
        if (request.getIsDefault() != null) {
            if (request.getIsDefault()) {
                // 기존 기본 설정 해제
                modelConfigRepository.findByIsDefaultTrue().ifPresent(existingDefault -> {
                    existingDefault.setIsDefault(false);
                    modelConfigRepository.save(existingDefault);
                });
                config.setIsDefault(true);
            } else {
                config.setIsDefault(false);
            }
        }
        if (request.getModelName() != null) {
            config.setModelName(request.getModelName());
        }
        if (request.getApiKey() != null && !request.getApiKey().startsWith("****")) {
            config.setApiKey(request.getApiKey());
        }
        if (request.getBaseUrl() != null) {
            config.setBaseUrl(request.getBaseUrl());
        }
        if (request.getTemperature() != null) {
            config.setTemperature(request.getTemperature());
        }
        if (request.getMaxTokens() != null) {
            config.setMaxTokens(request.getMaxTokens());
        }

        ModelConfig saved = modelConfigRepository.save(config);
        return convertToResponse(saved);
    }

    /**
     * 모델 설정 삭제
     */
    @Transactional
    public void deleteConfig(String name) {
        modelConfigRepository.findByName(name).ifPresent(modelConfigRepository::delete);
    }

    /**
     * 모델 연결 테스트
     */
    public String testConnection(String name) {
        ModelConfig config = modelConfigRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Model config not found: " + name));

        String provider = config.getName().toLowerCase();
        log.info("Starting connection test for provider: {}", provider);

        try {
            switch (provider) {
                case "ollama":
                    return testOllamaConnection(config);
                case "openai":
                    return testOpenAiConnection(config);
                case "claude":
                    return testClaudeConnection(config);
                case "gemini":
                    return testGeminiConnection(config);
                default:
                    throw new RuntimeException("Unsupported provider: " + provider);
            }
        } catch (Exception e) {
            log.error("Connection test failed for {}: {}", provider, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private org.springframework.web.client.RestTemplate createSecureRestTemplate() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new org.springframework.web.client.RestTemplate(factory);
    }

    private String sanitizeBaseUrl(String baseUrl, String defaultUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            return defaultUrl;
        }
        String sanitized = baseUrl.trim();
        if (sanitized.endsWith("/")) {
            sanitized = sanitized.substring(0, sanitized.length() - 1);
        }
        return sanitized;
    }

    private String testOllamaConnection(ModelConfig config) {
        org.springframework.web.client.RestTemplate restTemplate = createSecureRestTemplate();
        String baseUrl = sanitizeBaseUrl(config.getBaseUrl(), "http://localhost:11434");
        String url = baseUrl + "/api/tags";

        log.info("Testing Ollama at: {}", url);
        org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return "Ollama is reachable!";
        }
        throw new RuntimeException("Ollama returned status " + response.getStatusCode());
    }

    private String testOpenAiConnection(ModelConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty())
            throw new RuntimeException("API Key is missing");

        org.springframework.web.client.RestTemplate restTemplate = createSecureRestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + config.getApiKey());

        String baseUrl = sanitizeBaseUrl(config.getBaseUrl(), "https://api.openai.com");
        String url = baseUrl + "/v1/models";

        log.info("Testing OpenAI at: {}", url);
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(url,
                org.springframework.http.HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "OpenAI connection successful!";
        }
        throw new RuntimeException("OpenAI returned status " + response.getStatusCode());
    }

    private String testClaudeConnection(ModelConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty())
            throw new RuntimeException("API Key is missing");

        org.springframework.web.client.RestTemplate restTemplate = createSecureRestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("x-api-key", config.getApiKey());
        headers.set("anthropic-version", "2023-06-01");

        String baseUrl = sanitizeBaseUrl(config.getBaseUrl(), "https://api.anthropic.com");
        String url = baseUrl + "/v1/messages";

        String requestBody = String.format(
                "{\"model\":\"%s\",\"max_tokens\":10,\"messages\":[{\"role\":\"user\",\"content\":\"Hi\"}]}",
                config.getModelName() != null ? config.getModelName() : "claude-3-5-sonnet-20240620");

        log.info("Testing Claude at: {}", url);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody,
                headers);
        org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url, entity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "Claude connection successful!";
        }
        throw new RuntimeException("Claude returned status " + response.getStatusCode());
    }

    private String testGeminiConnection(ModelConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty())
            throw new RuntimeException("API Key is missing");

        org.springframework.web.client.RestTemplate restTemplate = createSecureRestTemplate();
        String baseUrl = sanitizeBaseUrl(config.getBaseUrl(), "https://generativelanguage.googleapis.com");
        String modelName = config.getModelName() != null ? config.getModelName().trim() : "gemini-1.5-flash";

        // Try v1 first as it's more stable for GA models
        String url = String.format("%s/v1/models/%s?key=%s", baseUrl, modelName, config.getApiKey());

        log.info("Testing Gemini at: {} (key masked)", url.split("\\?")[0]);
        try {
            org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return "Gemini connection successful! (Model: " + modelName + ")";
            }
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            throw new RuntimeException(
                    "Gemini model not found: " + modelName + ". Please check the model name (e.g., gemini-2.5-flash).");
        } catch (Exception e) {
            throw new RuntimeException("Gemini connection failed: " + e.getMessage());
        }
        throw new RuntimeException("Gemini returned an unexpected response");
    }

    /**
     * 엔티티를 응답 DTO로 변환 (API 키 마스킹)
     */
    private ModelConfigResponse convertToResponse(ModelConfig config) {
        String maskedApiKey = config.getApiKey() != null && !config.getApiKey().isEmpty()
                ? "****" + config.getApiKey().substring(Math.max(0, config.getApiKey().length() - 4))
                : null;

        return ModelConfigResponse.builder()
                .id(config.getId())
                .name(config.getName())
                .enabled(config.getEnabled())
                .isDefault(config.getIsDefault())
                .modelName(config.getModelName())
                .apiKey(maskedApiKey)
                .baseUrl(config.getBaseUrl())
                .temperature(config.getTemperature())
                .maxTokens(config.getMaxTokens())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
