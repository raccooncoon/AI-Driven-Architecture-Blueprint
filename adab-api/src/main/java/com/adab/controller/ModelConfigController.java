package com.adab.controller;

import com.adab.dto.ModelConfigRequest;
import com.adab.dto.ModelConfigResponse;
import com.adab.service.config.DynamicChatModelService;
import com.adab.service.config.ModelConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/model-configs")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost" })
@Slf4j
public class ModelConfigController {

    private final ModelConfigService modelConfigService;
    private final DynamicChatModelService dynamicChatModelService;

    /**
     * 모든 모델 설정 조회
     */
    @GetMapping
    public ResponseEntity<List<ModelConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(modelConfigService.getAllConfigs());
    }

    /**
     * 특정 모델 설정 조회
     */
    @GetMapping("/{name}")
    public ResponseEntity<ModelConfigResponse> getConfig(@PathVariable String name) {
        return ResponseEntity.ok(modelConfigService.getConfigByName(name));
    }

    /**
     * 기본 모델 설정 조회
     */
    @GetMapping("/default")
    public ResponseEntity<ModelConfigResponse> getDefaultConfig() {
        return ResponseEntity.ok(modelConfigService.getDefaultConfig());
    }

    /**
     * 현재 사용 중인 모델 이름 조회
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, String>> getCurrentModel() {
        String modelName = dynamicChatModelService.getDefaultModelName();
        Map<String, String> response = new HashMap<>();
        response.put("modelName", modelName);
        return ResponseEntity.ok(response);
    }

    /**
     * 모델 설정 생성 또는 업데이트
     */
    @PutMapping("/{name}")
    public ResponseEntity<ModelConfigResponse> updateConfig(
            @PathVariable String name,
            @RequestBody ModelConfigRequest request) {
        return ResponseEntity.ok(modelConfigService.createOrUpdateConfig(name, request));
    }

    /**
     * 모델 설정 삭제
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String name) {
        modelConfigService.deleteConfig(name);
        return ResponseEntity.ok().build();
    }

    /**
     * 모델 연결 테스트
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        Map<String, Object> response = new HashMap<>();

        try {
            String result = modelConfigService.testConnection(name);
            response.put("success", true);
            response.put("modelName", name);
            response.put("testResponse", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Connection test failed for model: {}", name, e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
