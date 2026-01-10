package com.adab.domain.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelConfigRepository extends JpaRepository<ModelConfig, Long> {
    Optional<ModelConfig> findByName(String name);
    Optional<ModelConfig> findByIsDefaultTrue();
}
