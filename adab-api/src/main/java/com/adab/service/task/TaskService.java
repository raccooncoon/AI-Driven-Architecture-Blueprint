package com.adab.service.task;

import com.adab.domain.task.Task;
import com.adab.domain.task.TaskRepository;
import com.adab.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * 모든 과업 조회
     */
    public TaskListResponse getAllTasks() {
        List<Task> tasks = taskRepository.findAll();

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
     * 요구사항 ID로 모든 과업 조회
     */
    public TaskListResponse getTasksByRequirementId(String requirementId) {
        List<Task> tasks = taskRepository.findByParentRequirementIdOrderByIdAsc(requirementId);
        
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
     * Task ID로 특정 과업 조회
     */
    public ApiResponse<TaskResponse> getTaskById(String taskId) {
        return taskRepository.findById(taskId)
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
     * 과업 수정
     */
    @Transactional
    public ApiResponse<TaskResponse> updateTask(String taskId, TaskUpdateRequest request) {
        return taskRepository.findById(taskId)
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
     * 특정 과업 삭제
     */
    @Transactional
    public DeleteResponse deleteTask(String taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return DeleteResponse.builder()
                    .success(true)
                    .message("Task deleted successfully")
                    .taskId(taskId)
                    .build();
        } else {
            return DeleteResponse.builder()
                    .success(false)
                    .message("Task not found")
                    .taskId(taskId)
                    .build();
        }
    }

    /**
     * 요구사항 ID로 모든 과업 일괄 삭제
     */
    @Transactional
    public DeleteResponse deleteTasksByRequirementId(String requirementId) {
        long count = taskRepository.countByParentRequirementId(requirementId);
        taskRepository.deleteByParentRequirementId(requirementId);
        
        return DeleteResponse.builder()
                .success(true)
                .message("All tasks deleted successfully")
                .requirementId(requirementId)
                .deletedCount((int) count)
                .build();
    }

    /**
     * 요구사항 ID로 과업 존재 여부 확인
     */
    public ExistsResponse checkTasksExist(String requirementId) {
        boolean exists = taskRepository.existsByParentRequirementId(requirementId);
        long count = taskRepository.countByParentRequirementId(requirementId);
        
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
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
