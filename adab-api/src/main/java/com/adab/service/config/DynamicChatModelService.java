package com.adab.service.config;

import com.adab.domain.config.ModelConfig;
import com.adab.domain.config.ModelConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@Slf4j
public class DynamicChatModelService {

    private final ModelConfigRepository modelConfigRepository;
    private final ChatModel defaultChatModel; // Spring AI 자동 설정된 기본 Ollama 모델

    @Value("${spring.ai.ollama.chat.options.model:gemma3:12b}")
    private String defaultModelName;

    public DynamicChatModelService(ModelConfigRepository modelConfigRepository,
            @Autowired(required = false) ChatModel defaultChatModel) {
        this.modelConfigRepository = modelConfigRepository;
        this.defaultChatModel = defaultChatModel;
    }

    /**
     * 기본 모델 설정을 기반으로 ChatModel 생성
     * 설정이 없으면 로컬 기본 LLM 사용
     */
    public ChatModel getDefaultChatModel() {
        Optional<ModelConfig> defaultConfig = modelConfigRepository.findByIsDefaultTrue();

        // 설정된 모델이 없으면 기본 로컬 LLM 사용
        if (defaultConfig.isEmpty()) {
            log.info("Using default local LLM (Ollama {})", defaultModelName);
            return defaultChatModel;
        }

        ModelConfig config = defaultConfig.get();

        if (!config.getEnabled()) {
            log.warn("Default model is disabled, falling back to local LLM");
            return defaultChatModel;
        }

        log.info("Using configured model: {} ({})", config.getName(), config.getModelName());
        return createChatModel(config);
    }

    /**
     * 기본 모델 이름 조회
     */
    public String getDefaultModelName() {
        return modelConfigRepository.findByIsDefaultTrue()
                .map(config -> {
                    String provider = config.getName();
                    String model = config.getModelName();
                    return formatModelName(provider, model);
                })
                .orElse("로컬 LLM (Ollama " + defaultModelName + ")");
    }

    /**
     * ModelConfig를 기반으로 ChatModel 생성
     */
    private ChatModel createChatModel(ModelConfig config) {
        log.info("Creating ChatModel for: {}", config.getName());

        switch (config.getName().toLowerCase()) {
            case "ollama":
                return createOllamaChatModel(config);
            case "openai":
                return createOpenAiChatModel(config);
            case "claude":
                return createClaudeChatModel(config);
            case "gemini":
                return createGeminiChatModel(config);
            default:
                throw new RuntimeException("지원하지 않는 모델 타입입니다: " + config.getName());
        }
    }

    private ChatModel createOllamaChatModel(ModelConfig config) {
        String baseUrl = config.getBaseUrl() != null ? config.getBaseUrl() : "http://localhost:11434";

        // 타임아웃 설정을 위한 RequestFactory 생성 (5분)
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(300000);
        requestFactory.setReadTimeout(300000);

        // RestClient.Builder에 RequestFactory 주입
        RestClient.Builder restClientBuilder = RestClient.builder()
                .requestFactory(requestFactory);

        OllamaApi ollamaApi = new OllamaApi(baseUrl, restClientBuilder);

        // 기본 옵션 설정
        OllamaOptions options = OllamaOptions.create()
                .withModel(config.getModelName())
                .withNumCtx(8192) // 컨텍스트 윈도우 확장
                .withNumPredict(4096); // 생성 제한 확장

        if (config.getTemperature() != null) {
            options.withTemperature(Float.parseFloat(config.getTemperature()));
        }

        log.info("Creating OllamaChatModel: baseUrl={}, model={}, num_ctx=8192",
                baseUrl, config.getModelName());

        return new OllamaChatModel(ollamaApi, options);
    }

    private ChatModel createOpenAiChatModel(ModelConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            throw new RuntimeException("OpenAI API 키가 설정되지 않았습니다.");
        }

        OpenAiApi openAiApi = new OpenAiApi(config.getApiKey());

        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder()
                .withModel(config.getModelName());

        if (config.getTemperature() != null) {
            optionsBuilder.withTemperature(Float.parseFloat(config.getTemperature()));
        }

        return new OpenAiChatModel(openAiApi, optionsBuilder.build());
    }

    private ChatModel createClaudeChatModel(ModelConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            throw new RuntimeException("Claude API 키가 설정되지 않았습니다.");
        }

        Float temperature = config.getTemperature() != null ? Float.parseFloat(config.getTemperature()) : 0.1f;
        Integer maxTokens = config.getMaxTokens() != null ? Integer.parseInt(config.getMaxTokens()) : 4096;

        return new ClaudeChatModel(
                config.getApiKey(),
                config.getModelName(),
                config.getBaseUrl(),
                temperature,
                maxTokens);
    }

    private ChatModel createGeminiChatModel(ModelConfig config) {
        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            throw new RuntimeException("Gemini API 키가 설정되지 않았습니다.");
        }

        Float temperature = config.getTemperature() != null ? Float.parseFloat(config.getTemperature()) : 0.1f;
        Integer maxTokens = config.getMaxTokens() != null ? Integer.parseInt(config.getMaxTokens()) : 4096;

        return new GeminiChatModel(
                config.getApiKey(),
                config.getModelName(),
                config.getBaseUrl(),
                temperature,
                maxTokens);
    }

    private String formatModelName(String provider, String model) {
        if (model == null || model.isEmpty()) {
            return provider;
        }

        switch (provider.toLowerCase()) {
            case "ollama":
                return "Ollama " + model;
            case "openai":
                return "OpenAI " + model;
            case "claude":
                return "Claude " + model;
            case "gemini":
                return "Gemini " + model;
            default:
                return provider + " " + model;
        }
    }
}
