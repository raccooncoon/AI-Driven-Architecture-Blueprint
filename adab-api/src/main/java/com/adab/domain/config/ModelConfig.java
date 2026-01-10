package com.adab.domain.config;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "model_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ModelConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;  // "ollama", "claude", "gemini", "openai"

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Column(length = 100)
    private String modelName;  // e.g., "gemma3:12b", "claude-3-opus", "gemini-pro", "gpt-4"

    @Column(length = 500)
    private String apiKey;

    @Column(length = 500)
    private String baseUrl;  // For Ollama and custom endpoints

    @Column(length = 50)
    private String temperature;

    @Column(length = 50)
    private String maxTokens;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
