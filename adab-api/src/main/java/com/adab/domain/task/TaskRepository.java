package com.adab.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    // 삭제되지 않은 과업만 조회
    List<Task> findByParentRequirementIdAndDeletedFalseOrderByCreatedAtAsc(String parentRequirementId);

    // 삭제되지 않은 과업 존재 여부
    boolean existsByParentRequirementIdAndDeletedFalse(String parentRequirementId);

    // 삭제되지 않은 과업 개수
    long countByParentRequirementIdAndDeletedFalse(String parentRequirementId);

    // 삭제되지 않은 과업 조회 (taskId로)
    Optional<Task> findByTaskIdAndDeletedFalse(String taskId);

    // 삭제되지 않은 과업 조회 (uuid로)
    Optional<Task> findByUuidAndDeletedFalse(String uuid);

    // 소프트 삭제 (요구사항별)
    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.deleted = true, t.deletedAt = :deletedAt WHERE t.parentRequirementId = :parentRequirementId AND t.deleted = false")
    int softDeleteByParentRequirementId(String parentRequirementId, LocalDateTime deletedAt);

    // 소프트 삭제 (개별 - taskId 기준)
    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.deleted = true, t.deletedAt = :deletedAt WHERE t.taskId = :taskId AND t.deleted = false")
    int softDeleteByTaskId(String taskId, LocalDateTime deletedAt);

    // 소프트 삭제 (개별 - uuid 기준)
    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.deleted = true, t.deletedAt = :deletedAt WHERE t.uuid = :uuid AND t.deleted = false")
    int softDeleteByUuid(String uuid, LocalDateTime deletedAt);

    // 실제 삭제 (기존 호환성 유지)
    @Modifying
    @Transactional
    void deleteByParentRequirementId(String parentRequirementId);
}
