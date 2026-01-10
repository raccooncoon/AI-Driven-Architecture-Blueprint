package com.adab.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByParentRequirementIdOrderByIdAsc(String parentRequirementId);

    boolean existsByParentRequirementId(String parentRequirementId);

    long countByParentRequirementId(String parentRequirementId);

    @Modifying
    @Transactional
    void deleteByParentRequirementId(String parentRequirementId);
}
