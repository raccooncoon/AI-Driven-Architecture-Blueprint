package com.adab.domain.task;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String taskId;

    @Column(nullable = false)
    private String parentRequirementId;

    @Column(nullable = false)
    private Integer parentIndex;

    @Column(nullable = false, length = 1000)
    private String summary;

    @Column(nullable = false)
    private String majorCategoryId;

    @Column(nullable = false)
    private String majorCategory;

    @Column(nullable = false)
    private String detailFunctionId;

    @Column(nullable = false)
    private String detailFunction;

    @Column(nullable = false, length = 1000)
    private String subFunction;
}
