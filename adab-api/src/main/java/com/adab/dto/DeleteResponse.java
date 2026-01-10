package com.adab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteResponse {
    private boolean success;
    private String message;
    private String taskId;
    private String requirementId;
    private Integer deletedCount;
}
