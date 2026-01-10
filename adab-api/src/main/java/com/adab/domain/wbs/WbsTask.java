package com.adab.domain.wbs;

import com.adab.domain.feature.Feature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wbs_tasks")
@Getter
@Setter
@NoArgsConstructor
public class WbsTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @Column(nullable = false)
    private String taskName;

    private String ownerRole;

    private String duration; // e.g. "3d", "1w"
}
