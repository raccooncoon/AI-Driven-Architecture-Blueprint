package com.adab.domain.requirement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "requirements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requirement {

    @Id
    @Column(name = "requirement_id", nullable = false)
    private String requirementId;

    @Column(name = "rfp_id")
    private String rfpId;

    @Column(name = "sequence_no")
    private Integer sequenceNo;

    @Column(name = "name")
    private String name;

    @Column(name = "definition")
    private String definition;

    @Column(name = "request_content", columnDefinition = "TEXT")
    private String requestContent;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "implementation_opinion", columnDefinition = "TEXT")
    private String implementationOpinion;

    @Column(name = "poba_opinion", columnDefinition = "TEXT")
    private String pobaOpinion;

    @Column(name = "tech_innovation_opinion", columnDefinition = "TEXT")
    private String techInnovationOpinion;

    // --- New Fields based on Table Image ---

    @Column(name = "constraints", columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "solution", columnDefinition = "TEXT")
    private String solution;

    @Column(name = "category")
    private String category;

    @Column(name = "source")
    private String source;

    @Column(name = "priority")
    private String priority;

    @Column(name = "acceptance")
    private String acceptance;

    @Column(name = "acceptance_reason", columnDefinition = "TEXT")
    private String acceptanceReason;

    @Column(name = "change_type")
    private String changeType;

    @Column(name = "change_date")
    private String changeDate;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "manager")
    private String manager;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void update(Requirement updated) {
        if (updated.rfpId != null)
            this.rfpId = updated.rfpId;
        if (updated.sequenceNo != null)
            this.sequenceNo = updated.sequenceNo;
        if (updated.name != null)
            this.name = updated.name;
        if (updated.definition != null)
            this.definition = updated.definition;
        if (updated.requestContent != null)
            this.requestContent = updated.requestContent;
        if (updated.deadline != null)
            this.deadline = updated.deadline;
        if (updated.implementationOpinion != null)
            this.implementationOpinion = updated.implementationOpinion;
        if (updated.pobaOpinion != null)
            this.pobaOpinion = updated.pobaOpinion;
        if (updated.techInnovationOpinion != null)
            this.techInnovationOpinion = updated.techInnovationOpinion;

        // Update new fields
        if (updated.constraints != null)
            this.constraints = updated.constraints;
        if (updated.solution != null)
            this.solution = updated.solution;
        if (updated.category != null)
            this.category = updated.category;
        if (updated.source != null)
            this.source = updated.source;
        if (updated.priority != null)
            this.priority = updated.priority;
        if (updated.acceptance != null)
            this.acceptance = updated.acceptance;
        if (updated.acceptanceReason != null)
            this.acceptanceReason = updated.acceptanceReason;
        if (updated.changeType != null)
            this.changeType = updated.changeType;
        if (updated.changeDate != null)
            this.changeDate = updated.changeDate;
        if (updated.changeReason != null)
            this.changeReason = updated.changeReason;
        if (updated.manager != null)
            this.manager = updated.manager;
    }
}
