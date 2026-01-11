package com.adab.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
public class SseNotificationService {

    public void sendStatus(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("status")
                    .data(java.util.Map.of("message", message != null ? message : "")));
        } catch (IOException e) {
            log.error("Failed to send status message", e);
        }
    }

    public void sendTask(SseEmitter emitter, Object task) {
        try {
            emitter.send(SseEmitter.event()
                    .name("task")
                    .data(task != null ? task : new Object()));
        } catch (IOException e) {
            log.error("Failed to send task data", e);
        }
    }

    public void sendComplete(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("complete")
                    .data(java.util.Map.of("message", message != null ? message : "")));
        } catch (IOException e) {
            log.error("Failed to send completion message", e);
        }
    }

    public void sendError(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(java.util.Map.of("message", message != null ? message : "")));
        } catch (IOException e) {
            log.error("Failed to send error message", e);
        }
    }
}
