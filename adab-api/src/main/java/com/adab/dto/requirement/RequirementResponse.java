package com.adab.dto.requirement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequirementResponse {

    @JsonProperty("requirementId")
    private String requirementId;

    @JsonProperty("rfpId")
    private String rfpId;

    @JsonProperty("sequenceNo")
    private Integer sequenceNo;

    @JsonProperty("name")
    private String name;

    @JsonProperty("definition")
    private String definition;

    @JsonProperty("requestContent")
    private String requestContent;

    @JsonProperty("deadline")
    private String deadline;

    @JsonProperty("implementationOpinion")
    private String implementationOpinion;

    @JsonProperty("pobaOpinion")
    private String pobaOpinion;

    @JsonProperty("techInnovationOpinion")
    private String techInnovationOpinion;

    @JsonProperty("constraints")
    private String constraints;

    @JsonProperty("solution")
    private String solution;

    @JsonProperty("category")
    private String category;

    @JsonProperty("source")
    private String source;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("acceptance")
    private String acceptance;

    @JsonProperty("acceptanceReason")
    private String acceptanceReason;

    @JsonProperty("changeType")
    private String changeType;

    @JsonProperty("changeDate")
    private String changeDate;

    @JsonProperty("changeReason")
    private String changeReason;

    @JsonProperty("manager")
    private String manager;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
