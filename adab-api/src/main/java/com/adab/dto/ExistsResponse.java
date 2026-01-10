package com.adab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExistsResponse {
    private boolean success;
    private boolean exists;
    private long count;
    private String requirementId;
}
