package com.adab.service.requirement;

import com.adab.domain.requirement.Requirement;
import com.adab.domain.requirement.RequirementRepository;
import com.adab.dto.requirement.BatchUploadResponse;
import com.adab.dto.requirement.RequirementResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public BatchUploadResponse batchUpload(MultipartFile file) {
        try {
            List<RequirementResponse> requirements = objectMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<List<RequirementResponse>>() {}
            );

            List<Requirement> entities = requirements.stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList());

            // Upsert logic: check if exists, update or save
            entities.forEach(entity -> {
                Optional<Requirement> existing = requirementRepository.findById(entity.getRequirementId());
                if (existing.isPresent()) {
                    existing.get().update(entity);
                    requirementRepository.save(existing.get());
                } else {
                    requirementRepository.save(entity);
                }
            });

            return BatchUploadResponse.builder()
                    .success(true)
                    .message(entities.size() + "건의 요구사항이 성공적으로 저장되었습니다.")
                    .count(entities.size())
                    .build();

        } catch (IOException e) {
            log.error("Failed to parse JSON file", e);
            return BatchUploadResponse.builder()
                    .success(false)
                    .message("파일 파싱 중 오류가 발생했습니다: " + e.getMessage())
                    .count(0)
                    .build();
        }
    }

    public List<RequirementResponse> getAllRequirements() {
        return requirementRepository.findAllByOrderByRequirementIdAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<RequirementResponse> getRequirementById(String requirementId) {
        return requirementRepository.findById(requirementId)
                .map(this::toResponse);
    }

    @Transactional
    public Optional<RequirementResponse> updateRequirement(String requirementId, RequirementResponse dto) {
        return requirementRepository.findById(requirementId)
                .map(requirement -> {
                    requirement.update(toEntity(dto));
                    Requirement updated = requirementRepository.save(requirement);
                    return toResponse(updated);
                });
    }

    @Transactional
    public boolean deleteRequirement(String requirementId) {
        if (requirementRepository.existsById(requirementId)) {
            requirementRepository.deleteById(requirementId);
            return true;
        }
        return false;
    }

    private Requirement toEntity(RequirementResponse dto) {
        return Requirement.builder()
                .requirementId(dto.getRequirementId())
                .rfpId(dto.getRfpId())
                .name(dto.getName())
                .definition(dto.getDefinition())
                .requestContent(dto.getRequestContent())
                .deadline(dto.getDeadline())
                .implementationOpinion(dto.getImplementationOpinion())
                .pobaOpinion(dto.getPobaOpinion())
                .techInnovationOpinion(dto.getTechInnovationOpinion())
                .build();
    }

    private RequirementResponse toResponse(Requirement entity) {
        return RequirementResponse.builder()
                .requirementId(entity.getRequirementId())
                .rfpId(entity.getRfpId())
                .name(entity.getName())
                .definition(entity.getDefinition())
                .requestContent(entity.getRequestContent())
                .deadline(entity.getDeadline())
                .implementationOpinion(entity.getImplementationOpinion())
                .pobaOpinion(entity.getPobaOpinion())
                .techInnovationOpinion(entity.getTechInnovationOpinion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
