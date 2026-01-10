package com.adab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfigRequest {
    private String name;
    private Boolean enabled;
    private Boolean isDefault;
    private String modelName;
    private String apiKey;
    private String baseUrl;
    private String temperature;
    private String maxTokens;
}
