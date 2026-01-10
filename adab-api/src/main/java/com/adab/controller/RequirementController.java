package com.adab.controller;

import com.adab.dto.RequirementAnalysisResponse;
import com.adab.service.analysis.RequirementAnalysisService;
import com.adab.domain.requirement.Requirement;
import com.adab.domain.requirement.RequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // For development
public class RequirementController {

    private final RequirementAnalysisService analysisService;
    private final RequirementRepository requirementRepository;

    @PostMapping("/analyze")
    public Requirement analyzeAndSave(@RequestBody String rawText) {
        // 1. Analyze
        RequirementAnalysisResponse response = analysisService.analyze(rawText);

        // 2. Save
        Requirement req = new Requirement();
        req.setRawText(rawText);
        req.setCategoryId(response.getCategory());
        req.setSummary(response.getSummary());
        req.setDetail(response.getDetail());
        req.setPriority("Medium"); // Default

        return requirementRepository.save(req);
    }

    @GetMapping
    public Iterable<Requirement> getAllRequirements() {
        return requirementRepository.findAll();
    }
}
