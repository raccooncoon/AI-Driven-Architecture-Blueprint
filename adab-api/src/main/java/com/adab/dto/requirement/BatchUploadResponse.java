package com.adab.dto.requirement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchUploadResponse {

    private boolean success;
    private String message;
    private int count;
}
