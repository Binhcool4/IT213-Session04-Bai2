package com.example.ss04_hw02_trieuquocbinh_062.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IncidentExtraction(
        @NotBlank(message = "Số điện thoại tài xế không được để trống")
        @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
        String driverPhone,

        @NotBlank(message = "Biển số xe không được để trống")
        @Size(max = 20, message = "Biển số xe tối đa 20 ký tự")
        String vehiclePlate,

        @NotBlank(message = "Địa điểm xảy ra sự cố không được để trống")
        @Size(max = 255, message = "Địa điểm tối đa 255 ký tự")
        String rawLocation,

        String incidentType,

        String severity,

        @Size(max = 1000, message = "Tóm tắt không quá 1000 ký tự")
        String summary,

        @Min(value = 0, message = "Thời gian chậm trễ không được âm")
        @Max(value = 1440, message = "Thời gian chậm trễ tối đa 24 giờ (1440 phút)")
        Integer estimatedDelayMinutes,

        Boolean needsAssistance,

        Double confidenceScore
) {
    public IncidentExtraction {
        driverPhone = (driverPhone != null) ? driverPhone.trim() : "UNKNOWN_PHONE";
        vehiclePlate = (vehiclePlate != null) ? vehiclePlate.trim().toUpperCase() : "UNKNOWN_PLATE";
        rawLocation = (rawLocation != null && !rawLocation.isBlank()) ? rawLocation.trim() : "Chưa xác định vị trí cụ thể";
        incidentType = (incidentType != null && !incidentType.isBlank()) ? incidentType.trim().toUpperCase() : "OTHER";
        severity = (severity != null && !severity.isBlank()) ? severity.trim().toUpperCase() : "LOW";
        summary = (summary != null && !summary.isBlank()) ? summary.trim() : "Không có mô tả chi tiết";

        estimatedDelayMinutes = (estimatedDelayMinutes != null && estimatedDelayMinutes >= 0) ? estimatedDelayMinutes : 0;
        needsAssistance = (needsAssistance != null) ? needsAssistance : Boolean.FALSE;
        confidenceScore = (confidenceScore != null && confidenceScore >= 0.0 && confidenceScore <= 1.0)
                ? confidenceScore : 0.5;
    }
}

