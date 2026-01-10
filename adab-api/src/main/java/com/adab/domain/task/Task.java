package com.adab.domain.task;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    private String id;  // REQ-AI-BA-0001-TASK-001

    @Column(nullable = false)
    private String parentRequirementId;  // REQ-AI-BA-0001

    @Column(nullable = false)
    private Integer parentIndex;  // 0

    @Column(length = 1000)
    private String summary;

    private String majorCategoryId;  // CAT-001
    private String majorCategory;    // AI 모델 개발
    private String detailFunctionId; // FUNC-001
    private String detailFunction;   // LLM 모델 구축

    @Column(length = 2000)
    private String subFunction;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
