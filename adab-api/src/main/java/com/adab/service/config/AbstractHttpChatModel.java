package com.adab.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

@Slf4j
public abstract class AbstractHttpChatModel implements ChatModel {

    protected final String apiKey;
    protected final String model;
    protected final String baseUrl;
    protected final Float temperature;
    protected final Integer maxTokens;
    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;

    protected AbstractHttpChatModel(String apiKey, String model, String baseUrl, Float temperature, Integer maxTokens) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public abstract ChatResponse call(Prompt prompt);

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        // Default implementation for streaming, can be overridden by subclasses
        return Flux.just(call(prompt));
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return null;
    }
}
