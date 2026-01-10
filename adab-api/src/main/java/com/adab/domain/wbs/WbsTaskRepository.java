package com.adab.domain.wbs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WbsTaskRepository extends JpaRepository<WbsTask, Long> {
    List<WbsTask> findByFeatureId(Long featureId);
}
