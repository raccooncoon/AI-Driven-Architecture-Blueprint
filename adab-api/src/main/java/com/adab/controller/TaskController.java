package com.adab.controller;

import com.adab.dto.*;
import com.adab.service.task.TaskGenerationService;
import com.adab.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class TaskController {

    private final TaskGenerationService taskGenerationService;
    private final TaskService taskService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    // 1. 과업 생성 (SSE)
    @PostMapping(value = "/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateTasks(@RequestBody TaskGenerationRequest request) {
        log.info("Received task generation request for requirement: {}", request.getRequirementId());

        SseEmitter emitter = new SseEmitter(60000L); // 60초 타임아웃

        emitter.onCompletion(() -> log.info("SSE completed for requirement: {}", request.getRequirementId()));
        emitter.onTimeout(() -> log.warn("SSE timeout for requirement: {}", request.getRequirementId()));
        emitter.onError((e) -> log.error("SSE error for requirement: " + request.getRequirementId(), e));

        // 비동기로 Task 생성 처리
        executor.execute(() -> taskGenerationService.generateTasksWithStreaming(request, emitter));

        return emitter;
    }

    // 2-1. 전체 과업 조회
    @GetMapping("/all")
    public ResponseEntity<TaskListResponse> getAllTasks() {
        log.info("Getting all tasks");
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // 2-2. 요구사항별 과업 목록 조회
    @GetMapping
    public ResponseEntity<TaskListResponse> getTasks(@RequestParam String requirementId) {
        log.info("Getting tasks for requirement: {}", requirementId);
        return ResponseEntity.ok(taskService.getTasksByRequirementId(requirementId));
    }

    // 3. 과업 상세 조회
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable String taskId) {
        log.info("Getting task: {}", taskId);
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    // 4. 과업 수정
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable String taskId,
            @RequestBody TaskUpdateRequest request) {
        log.info("Updating task: {}", taskId);
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    // 5. 과업 삭제
    @DeleteMapping("/{taskId}")
    public ResponseEntity<DeleteResponse> deleteTask(@PathVariable String taskId) {
        log.info("Deleting task: {}", taskId);
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }

    // 6. 요구사항별 과업 일괄 삭제
    @DeleteMapping("/requirement/{requirementId}")
    public ResponseEntity<DeleteResponse> deleteTasksByRequirement(@PathVariable String requirementId) {
        log.info("Deleting all tasks for requirement: {}", requirementId);
        return ResponseEntity.ok(taskService.deleteTasksByRequirementId(requirementId));
    }

    // 6-1. 시스템 전체 과업 일괄 삭제
    @DeleteMapping("/all")
    public ResponseEntity<DeleteResponse> deleteAllTasks() {
        log.info("Deleting all tasks in the system");
        return ResponseEntity.ok(taskService.deleteAllTasks());
    }

    // 7. 과업 존재 여부 확인
    @GetMapping("/requirement/{requirementId}/exists")
    public ResponseEntity<ExistsResponse> checkTasksExist(@PathVariable String requirementId) {
        log.info("Checking tasks existence for requirement: {}", requirementId);
        return ResponseEntity.ok(taskService.checkTasksExist(requirementId));
    }
}
