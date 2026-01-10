package com.adab.domain.requirement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "requirements")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requirement {

    @Id
    @Column(name = "requirement_id", nullable = false)
    private String requirementId;

    @Column(name = "rfp_id")
    private String rfpId;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void update(Requirement updated) {
        this.rfpId = updated.rfpId;
        this.name = updated.name;
        this.definition = updated.definition;
        this.requestContent = updated.requestContent;
        this.deadline = updated.deadline;
        this.implementationOpinion = updated.implementationOpinion;
        this.pobaOpinion = updated.pobaOpinion;
        this.techInnovationOpinion = updated.techInnovationOpinion;
    }
}
