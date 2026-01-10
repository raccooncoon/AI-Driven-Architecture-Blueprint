package com.adab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {
    private String summary;
    private String majorCategoryId;
    private String majorCategory;
    private String detailFunctionId;
    private String detailFunction;
    private String subFunction;
}
