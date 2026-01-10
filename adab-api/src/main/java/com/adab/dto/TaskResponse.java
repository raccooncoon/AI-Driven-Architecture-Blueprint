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
    private String id;
    private String parentRequirementId;
    private Integer parentIndex;
    private String summary;
    private String majorCategoryId;
    private String majorCategory;
    private String detailFunctionId;
    private String detailFunction;
    private String subFunction;
    private String generatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
