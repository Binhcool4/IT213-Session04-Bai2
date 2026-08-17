package com.example.ss04_hw02_trieuquocbinh_062.service;

import com.example.ss04_hw02_trieuquocbinh_062.dto.IncidentExtraction;
import com.example.ss04_hw02_trieuquocbinh_062.entity.IncidentReport;
import com.example.ss04_hw02_trieuquocbinh_062.mapper.IncidentMapper;
import com.example.ss04_hw02_trieuquocbinh_062.repository.IncidentReportRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IncidentReportService {

    private static final Logger log = LoggerFactory.getLogger(IncidentReportService.class);

    private final IncidentReportRepository incidentReportRepository;
    private final IncidentMapper incidentMapper;
    private final Validator validator;

    @Transactional
    public IncidentReport processAndSaveExtraction(IncidentExtraction extraction, String rawAiPayload) {
        log.info("--- BẮT ĐẦU XỬ LÝ DỮ LIỆU BÓC TÁCH TỪ AI ---");
        log.info("Dữ liệu thô Record DTO nhận được: {}", extraction);

        Set<ConstraintViolation<IncidentExtraction>> violations = validator.validate(extraction);
        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder("Dữ liệu bóc tách không hợp lệ: ");
            for (ConstraintViolation<IncidentExtraction> violation : violations) {
                errorMessages.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            log.warn("Cảnh báo Bean Validation: {}", errorMessages);
        }

        IncidentReport entity = incidentMapper.toEntity(extraction, rawAiPayload);
        log.info("Entity được khởi tạo an toàn qua Factory Method & Mapper: {}", entity);

        IncidentReport saved = incidentReportRepository.save(entity);
        log.info("Đã lưu thành công IncidentReport ID: {} vào Database!", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<IncidentReport> getAllReports() {
        return incidentReportRepository.findAll();
    }
}

