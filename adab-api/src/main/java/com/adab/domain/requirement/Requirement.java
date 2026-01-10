package com.adab.domain.requirement;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requirements")
@Getter
@Setter
@NoArgsConstructor
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawText;

    private String categoryId;

    private String summary;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String priority;

    // Defines the relationship to features if needed, but keeping it decoupled or
    // simple for now is also fine.
    // Based on requirement: 1:N (Features)
    // We can map it here or just foreign key in Feature. Let's start with just ID
    // here for simplicity unless bidirectional is needed.
}
