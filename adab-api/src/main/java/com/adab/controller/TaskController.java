package com.adab.controller;

import com.adab.dto.TaskGenerationRequest;
import com.adab.service.task.TaskGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    private final ExecutorService executor = Executors.newCachedThreadPool();

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
}
