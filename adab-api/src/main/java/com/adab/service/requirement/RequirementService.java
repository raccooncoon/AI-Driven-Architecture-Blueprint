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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
            List<RequirementResponse> requirements;
            String filename = file.getOriginalFilename();

            if (filename != null && (filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
                requirements = parseExcel(file);
            } else {
                requirements = objectMapper.readValue(
                        file.getInputStream(),
                        new TypeReference<List<RequirementResponse>>() {
                        });
            }

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
        return requirementRepository.findAllByOrderBySequenceNoAsc().stream()
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
                .sequenceNo(dto.getSequenceNo())
                .rfpId(dto.getRfpId())
                .name(dto.getName())
                .definition(dto.getDefinition())
                .requestContent(dto.getRequestContent())
                .deadline(dto.getDeadline())
                .implementationOpinion(dto.getImplementationOpinion())
                .pobaOpinion(dto.getPobaOpinion())
                .techInnovationOpinion(dto.getTechInnovationOpinion())
                .constraints(dto.getConstraints())
                .solution(dto.getSolution())
                .category(dto.getCategory())
                .source(dto.getSource())
                .priority(dto.getPriority())
                .acceptance(dto.getAcceptance())
                .acceptanceReason(dto.getAcceptanceReason())
                .changeType(dto.getChangeType())
                .changeDate(dto.getChangeDate())
                .changeReason(dto.getChangeReason())
                .manager(dto.getManager())
                .build();
    }

    private RequirementResponse toResponse(Requirement entity) {
        return RequirementResponse.builder()
                .requirementId(entity.getRequirementId())
                .sequenceNo(entity.getSequenceNo())
                .rfpId(entity.getRfpId())
                .name(entity.getName())
                .definition(entity.getDefinition())
                .requestContent(entity.getRequestContent())
                .deadline(entity.getDeadline())
                .implementationOpinion(entity.getImplementationOpinion())
                .pobaOpinion(entity.getPobaOpinion())
                .techInnovationOpinion(entity.getTechInnovationOpinion())
                .constraints(entity.getConstraints())
                .solution(entity.getSolution())
                .category(entity.getCategory())
                .source(entity.getSource())
                .priority(entity.getPriority())
                .acceptance(entity.getAcceptance())
                .acceptanceReason(entity.getAcceptanceReason())
                .changeType(entity.getChangeType())
                .changeDate(entity.getChangeDate())
                .changeReason(entity.getChangeReason())
                .manager(entity.getManager())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public byte[] generateSampleExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Requirements Sample");

            // Create Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Headers matches parseExcel logic
            String[] headers = {
                    "No", "구분", "과업 ID", "요구사항 ID", "요구사항명", "요구사항 내용",
                    "요구사항 출처", "우선순위", "수용여부", "수용불가/제외 사유",
                    "변경구분", "변경일자", "변경근거", "담당자", "제약사항", "해결방안",
                    "구축 의견", "PO/BA 의견", "기술혁신 의견", "완료기한"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000); // Set default width
            }
            // Adjust specific column widths
            sheet.setColumnWidth(5, 8000); // Content
            sheet.setColumnWidth(4, 6000); // Name

            // Add a Sample Data Row
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(1);
            dataRow.createCell(1).setCellValue("기능");
            dataRow.createCell(3).setCellValue("REQ-001");
            dataRow.createCell(4).setCellValue("샘플 요구사항");
            dataRow.createCell(5).setCellValue("사용자가 편리하게 엑셀을 업로드할 수 있어야 한다.");
            dataRow.createCell(6).setCellValue("RFP");
            dataRow.createCell(7).setCellValue("상");
            dataRow.createCell(8).setCellValue("수용");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private List<RequirementResponse> parseExcel(MultipartFile file) throws IOException {
        List<RequirementResponse> list = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip Header
            if (!rows.hasNext())
                return list;
            Row headerRow = rows.next();

            // Map headers to indices
            int rfpIdIdx = -1, reqIdIdx = -1, nameIdx = -1, contentIdx = -1;
            int constraintsIdx = -1, solutionIdx = -1, categoryIdx = -1, sourceIdx = -1;
            int priorityIdx = -1, acceptanceIdx = -1, acceptanceReasonIdx = -1;
            int changeTypeIdx = -1, changeDateIdx = -1, changeReasonIdx = -1, managerIdx = -1;
            int implOpinionIdx = -1, pobaOpinionIdx = -1, techOpinionIdx = -1;

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                int idx = cell.getColumnIndex();
                if (header.contains("과업") && header.contains("ID"))
                    rfpIdIdx = idx;
                else if (header.contains("요구사항") && header.contains("ID"))
                    reqIdIdx = idx;
                else if (header.equals("요구사항명"))
                    nameIdx = idx;
                else if (header.equals("요구사항 내용"))
                    contentIdx = idx;
                else if (header.equals("제약사항"))
                    constraintsIdx = idx;
                else if (header.equals("해결방안"))
                    solutionIdx = idx;
                else if (header.equals("구분"))
                    categoryIdx = idx;
                else if (header.contains("출처"))
                    sourceIdx = idx;
                else if (header.equals("우선순위"))
                    priorityIdx = idx;
                else if (header.equals("수용여부"))
                    acceptanceIdx = idx;
                else if (header.contains("미수용") || header.contains("부분수용"))
                    acceptanceReasonIdx = idx;
                else if (header.equals("변경구분"))
                    changeTypeIdx = idx;
                else if (header.equals("변경일자"))
                    changeDateIdx = idx;
                else if (header.equals("변경근거"))
                    changeReasonIdx = idx;
                else if (header.equals("담당자"))
                    managerIdx = idx;
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                if (isRowEmpty(row))
                    continue;

                RequirementResponse.RequirementResponseBuilder builder = RequirementResponse.builder();

                if (rfpIdIdx != -1)
                    builder.rfpId(getCellValue(row.getCell(rfpIdIdx)));

                // Column 0 is usually sequenceNo if requested
                if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC) {
                    builder.sequenceNo((int) row.getCell(0).getNumericCellValue());
                } else {
                    // Try parsing as string if numeric fails or check if header mapping exists
                    // But user said "Add a sequence column at the very front"
                    String seqVal = getCellValue(row.getCell(0));
                    if (seqVal.matches("\\d+")) {
                        builder.sequenceNo(Integer.parseInt(seqVal));
                    }
                }
                if (reqIdIdx != -1)
                    builder.requirementId(getCellValue(row.getCell(reqIdIdx)));
                if (nameIdx != -1)
                    builder.name(getCellValue(row.getCell(nameIdx)));
                if (contentIdx != -1)
                    builder.requestContent(getCellValue(row.getCell(contentIdx)));
                if (constraintsIdx != -1)
                    builder.constraints(getCellValue(row.getCell(constraintsIdx)));
                if (solutionIdx != -1)
                    builder.solution(getCellValue(row.getCell(solutionIdx)));
                if (categoryIdx != -1)
                    builder.category(getCellValue(row.getCell(categoryIdx)));
                if (sourceIdx != -1)
                    builder.source(getCellValue(row.getCell(sourceIdx)));
                if (priorityIdx != -1)
                    builder.priority(getCellValue(row.getCell(priorityIdx)));
                if (acceptanceIdx != -1)
                    builder.acceptance(getCellValue(row.getCell(acceptanceIdx)));
                if (acceptanceReasonIdx != -1)
                    builder.acceptanceReason(getCellValue(row.getCell(acceptanceReasonIdx)));
                if (changeTypeIdx != -1)
                    builder.changeType(getCellValue(row.getCell(changeTypeIdx)));
                if (changeDateIdx != -1)
                    builder.changeDate(getCellValue(row.getCell(changeDateIdx)));
                if (changeReasonIdx != -1)
                    builder.changeReason(getCellValue(row.getCell(changeReasonIdx)));
                if (managerIdx != -1)
                    builder.manager(getCellValue(row.getCell(managerIdx)));

                // Keep ID mandatory
                RequirementResponse dto = builder.build();
                if (dto.getRequirementId() != null && !dto.getRequirementId().isEmpty()) {
                    list.add(dto);
                }
            }
        }
        return list;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null)
            return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf((long) cell.getNumericCellValue()); // Integer only for now
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }
}
