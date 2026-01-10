package com.adab.service.task;

import com.adab.domain.task.Task;
import com.adab.domain.task.TaskRepository;
import com.adab.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * 모든 과업 조회 (삭제되지 않은 것만)
     */
    public TaskListResponse getAllTasks() {
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(task -> !task.getDeleted())
                .collect(Collectors.toList());

        List<TaskResponse> taskResponses = tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return TaskListResponse.builder()
                .success(true)
                .data(taskResponses)
                .count(taskResponses.size())
                .build();
    }

    /**
     * 요구사항 ID로 모든 과업 조회 (삭제되지 않은 것만)
     */
    public TaskListResponse getTasksByRequirementId(String requirementId) {
        List<Task> tasks = taskRepository.findByParentRequirementIdAndDeletedFalseOrderByIdAsc(requirementId);

        List<TaskResponse> taskResponses = tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return TaskListResponse.builder()
                .success(true)
                .data(taskResponses)
                .count(taskResponses.size())
                .build();
    }

    /**
     * Task ID로 특정 과업 조회 (삭제되지 않은 것만)
     */
    public ApiResponse<TaskResponse> getTaskById(String taskId) {
        return taskRepository.findByIdAndDeletedFalse(taskId)
                .map(task -> ApiResponse.<TaskResponse>builder()
                        .success(true)
                        .data(convertToResponse(task))
                        .build())
                .orElse(ApiResponse.<TaskResponse>builder()
                        .success(false)
                        .error("Task not found")
                        .taskId(taskId)
                        .build());
    }

    /**
     * 과업 수정 (삭제되지 않은 것만)
     */
    @Transactional
    public ApiResponse<TaskResponse> updateTask(String taskId, TaskUpdateRequest request) {
        return taskRepository.findByIdAndDeletedFalse(taskId)
                .map(task -> {
                    if (request.getSummary() != null) {
                        task.setSummary(request.getSummary());
                    }
                    if (request.getMajorCategoryId() != null) {
                        task.setMajorCategoryId(request.getMajorCategoryId());
                    }
                    if (request.getMajorCategory() != null) {
                        task.setMajorCategory(request.getMajorCategory());
                    }
                    if (request.getDetailFunctionId() != null) {
                        task.setDetailFunctionId(request.getDetailFunctionId());
                    }
                    if (request.getDetailFunction() != null) {
                        task.setDetailFunction(request.getDetailFunction());
                    }
                    if (request.getSubFunction() != null) {
                        task.setSubFunction(request.getSubFunction());
                    }

                    Task updatedTask = taskRepository.save(task);
                    
                    return ApiResponse.<TaskResponse>builder()
                            .success(true)
                            .data(convertToResponse(updatedTask))
                            .build();
                })
                .orElse(ApiResponse.<TaskResponse>builder()
                        .success(false)
                        .error("Task not found")
                        .taskId(taskId)
                        .build());
    }

    /**
     * 특정 과업 소프트 삭제
     */
    @Transactional
    public DeleteResponse deleteTask(String taskId) {
        int deletedCount = taskRepository.softDeleteById(taskId, LocalDateTime.now());

        if (deletedCount > 0) {
            return DeleteResponse.builder()
                    .success(true)
                    .message("Task soft deleted successfully")
                    .taskId(taskId)
                    .build();
        } else {
            return DeleteResponse.builder()
                    .success(false)
                    .message("Task not found or already deleted")
                    .taskId(taskId)
                    .build();
        }
    }

    /**
     * 요구사항 ID로 모든 과업 일괄 소프트 삭제
     */
    @Transactional
    public DeleteResponse deleteTasksByRequirementId(String requirementId) {
        int deletedCount = taskRepository.softDeleteByParentRequirementId(requirementId, LocalDateTime.now());

        return DeleteResponse.builder()
                .success(true)
                .message("All tasks soft deleted successfully")
                .requirementId(requirementId)
                .deletedCount(deletedCount)
                .build();
    }

    /**
     * 요구사항 ID로 과업 존재 여부 확인 (삭제되지 않은 것만)
     */
    public ExistsResponse checkTasksExist(String requirementId) {
        boolean exists = taskRepository.existsByParentRequirementIdAndDeletedFalse(requirementId);
        long count = taskRepository.countByParentRequirementIdAndDeletedFalse(requirementId);

        return ExistsResponse.builder()
                .success(true)
                .exists(exists)
                .count(count)
                .requirementId(requirementId)
                .build();
    }

    /**
     * Task 엔티티를 TaskResponse로 변환
     */
    private TaskResponse convertToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .parentRequirementId(task.getParentRequirementId())
                .parentIndex(task.getParentIndex())
                .summary(task.getSummary())
                .majorCategoryId(task.getMajorCategoryId())
                .majorCategory(task.getMajorCategory())
                .detailFunctionId(task.getDetailFunctionId())
                .detailFunction(task.getDetailFunction())
                .subFunction(task.getSubFunction())
                .generatedBy(task.getGeneratedBy())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
