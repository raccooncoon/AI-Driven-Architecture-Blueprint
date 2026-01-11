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
public class TaskResponse {
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private String id;

    @com.fasterxml.jackson.annotation.JsonProperty("parentRequirementId")
    private String parentRequirementId;

    @com.fasterxml.jackson.annotation.JsonProperty("parentIndex")
    private Integer parentIndex;

    @com.fasterxml.jackson.annotation.JsonProperty("summary")
    private String summary;

    @com.fasterxml.jackson.annotation.JsonProperty("majorCategoryId")
    private String majorCategoryId;

    @com.fasterxml.jackson.annotation.JsonProperty("majorCategory")
    private String majorCategory;

    @com.fasterxml.jackson.annotation.JsonProperty("detailFunctionId")
    private String detailFunctionId;

    @com.fasterxml.jackson.annotation.JsonProperty("detailFunction")
    private String detailFunction;

    @com.fasterxml.jackson.annotation.JsonProperty("subFunction")
    private String subFunction;

    @com.fasterxml.jackson.annotation.JsonProperty("generatedBy")
    private String generatedBy;

    @com.fasterxml.jackson.annotation.JsonProperty("createdAt")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @com.fasterxml.jackson.annotation.JsonProperty("updatedAt")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
