package com.adab.controller;

import com.adab.dto.requirement.BatchUploadResponse;
import com.adab.dto.requirement.RequirementResponse;
import com.adab.service.requirement.RequirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost" })
@Slf4j
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping("/batch")
    public ResponseEntity<BatchUploadResponse> batchUpload(@RequestParam("file") MultipartFile file) {
        BatchUploadResponse response = requirementService.batchUpload(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RequirementResponse>> getAllRequirements() {
        List<RequirementResponse> requirements = requirementService.getAllRequirements();
        return ResponseEntity.ok(requirements);
    }

    @GetMapping("/{requirementId}")
    public ResponseEntity<RequirementResponse> getRequirementById(@PathVariable String requirementId) {
        return requirementService.getRequirementById(requirementId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{requirementId}")
    public ResponseEntity<RequirementResponse> updateRequirement(
            @PathVariable String requirementId,
            @RequestBody RequirementResponse request) {
        return requirementService.updateRequirement(requirementId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{requirementId}")
    public ResponseEntity<Void> deleteRequirement(@PathVariable String requirementId) {
        boolean deleted = requirementService.deleteRequirement(requirementId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
