package com.adab.service.config;

import com.adab.domain.config.ModelConfig;
import com.adab.domain.config.ModelConfigRepository;
import com.adab.dto.ModelConfigRequest;
import com.adab.dto.ModelConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelConfigService {

    private final ModelConfigRepository modelConfigRepository;

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

        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            throw new RuntimeException("API key not configured");
        }

        // Claude API 테스트 (간단한 메시지 전송)
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("x-api-key", config.getApiKey());
            headers.set("anthropic-version", "2023-06-01");

            String requestBody = "{"
                    + "\"model\":\"" + config.getModelName() + "\","
                    + "\"max_tokens\":10,"
                    + "\"messages\":[{\"role\":\"user\",\"content\":\"Hi\"}]"
                    + "}";

            String url = config.getBaseUrl() != null && !config.getBaseUrl().isEmpty()
                    ? (config.getBaseUrl().endsWith("/messages") ? config.getBaseUrl() : config.getBaseUrl() + "/v1/messages")
                    : "https://api.anthropic.com/v1/messages";

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return "Connection successful! Model: " + config.getModelName();
            } else {
                throw new RuntimeException("API returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Test connection failed", e);
            throw new RuntimeException("Connection test failed: " + e.getMessage());
        }
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
