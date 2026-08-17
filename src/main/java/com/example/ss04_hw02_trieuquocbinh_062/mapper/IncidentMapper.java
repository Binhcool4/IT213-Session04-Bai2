package com.example.ss04_hw02_trieuquocbinh_062.mapper;

import com.example.ss04_hw02_trieuquocbinh_062.dto.IncidentExtraction;
import com.example.ss04_hw02_trieuquocbinh_062.entity.IncidentReport;
import com.example.ss04_hw02_trieuquocbinh_062.model.enums.IncidentType;
import com.example.ss04_hw02_trieuquocbinh_062.model.enums.SeverityLevel;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentReport toEntity(IncidentExtraction extraction, String rawAiJson) {
        if (extraction == null) {
            throw new IllegalArgumentException("Dữ liệu bóc tách IncidentExtraction không được để null!");
        }

        String sanitizedPhone = extraction.driverPhone()
                .replaceAll("[^0-9+]", "")
                .trim();
        if (sanitizedPhone.isBlank()) {
            sanitizedPhone = "0000000000";
        }

        String sanitizedPlate = extraction.vehiclePlate()
                .replaceAll("\\s+", "")
                .toUpperCase();

        IncidentType incidentType = IncidentType.fromStringOrDefault(extraction.incidentType());
        SeverityLevel severityLevel = SeverityLevel.fromStringOrDefault(extraction.severity(), SeverityLevel.LOW);

        if (incidentType == IncidentType.ACCIDENT && extraction.needsAssistance() && severityLevel == SeverityLevel.LOW) {
            severityLevel = SeverityLevel.HIGH;
        }

        return IncidentReport.createFromValidatedExtraction(
                sanitizedPhone,
                sanitizedPlate,
                extraction.rawLocation(),
                incidentType,
                severityLevel,
                extraction.summary(),
                rawAiJson,
                extraction.estimatedDelayMinutes(),
                extraction.needsAssistance(),
                extraction.confidenceScore()
        );
    }
}

