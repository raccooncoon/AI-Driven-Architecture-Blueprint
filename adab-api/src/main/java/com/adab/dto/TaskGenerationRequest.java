package com.adab.dto;

import lombok.Data;

@Data
public class TaskGenerationRequest {
    private String rfpId;
    private String requirementId;
    private String name;
    private String definition;
    private String requestContent;
    private String deadline;
    private String implementationOpinion;
    private String businessDevelopment;
    private String pobaOpinion;
    private String techInnovationOpinion;
    private Integer index;
}
