package com.adab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfigResponse {
    private Long id;
    private String name;
    private Boolean enabled;
    private Boolean isDefault;
    private String modelName;
    private String apiKey;  // Will be masked in response
    private String baseUrl;
    private String temperature;
    private String maxTokens;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
