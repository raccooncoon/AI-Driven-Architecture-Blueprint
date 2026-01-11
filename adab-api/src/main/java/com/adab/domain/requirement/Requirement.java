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
        if (updated.rfpId != null)
            this.rfpId = updated.rfpId;
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
    }
}
