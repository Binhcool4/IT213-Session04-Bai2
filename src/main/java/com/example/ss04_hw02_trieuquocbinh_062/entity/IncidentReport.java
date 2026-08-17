package com.example.ss04_hw02_trieuquocbinh_062.entity;

import com.example.ss04_hw02_trieuquocbinh_062.model.enums.IncidentType;
import com.example.ss04_hw02_trieuquocbinh_062.model.enums.ReportStatus;
import com.example.ss04_hw02_trieuquocbinh_062.model.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "incident_reports", indexes = {
        @Index(name = "idx_driver_phone", columnList = "driver_phone"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_phone", nullable = false, length = 20)
    private String driverPhone;

    @Column(name = "vehicle_plate", nullable = false, length = 20)
    private String vehiclePlate;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", nullable = false, length = 50)
    private IncidentType incidentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false, length = 20)
    private SeverityLevel severityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReportStatus status;

    @Column(name = "summary", length = 1000)
    private String summary;

    @Column(name = "raw_ai_payload", columnDefinition = "TEXT")
    private String rawAiPayload;

    @Column(name = "estimated_delay_minutes", nullable = false)
    private int estimatedDelayMinutes;

    @Column(name = "needs_assistance", nullable = false)
    private boolean needsAssistance;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "resolution_notes", length = 500)
    private String resolutionNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static IncidentReport createFromValidatedExtraction(
            String driverPhone,
            String vehiclePlate,
            String location,
            IncidentType incidentType,
            SeverityLevel severityLevel,
            String summary,
            String rawAiPayload,
            int estimatedDelayMinutes,
            boolean needsAssistance,
            Double confidenceScore
    ) {
        IncidentReport report = new IncidentReport();

        report.driverPhone = Objects.requireNonNull(driverPhone, "driverPhone không được null");
        report.vehiclePlate = Objects.requireNonNull(vehiclePlate, "vehiclePlate không được null");
        report.location = (location != null && !location.isBlank()) ? location : "Không xác định";
        report.incidentType = (incidentType != null) ? incidentType : IncidentType.OTHER;
        report.severityLevel = (severityLevel != null) ? severityLevel : SeverityLevel.LOW;
        report.status = ReportStatus.PENDING_VERIFICATION;
        report.summary = summary;
        report.rawAiPayload = rawAiPayload;
        report.estimatedDelayMinutes = Math.max(0, estimatedDelayMinutes);
        report.needsAssistance = needsAssistance;
        report.confidenceScore = confidenceScore != null ? confidenceScore : 0.5;

        return report;
    }

    public void verify(String operatorNotes) {
        if (this.status == ReportStatus.RESOLVED || this.status == ReportStatus.REJECTED) {
            throw new IllegalStateException("Không thể xác minh báo cáo đã hoàn tất hoặc bị từ chối!");
        }
        this.status = ReportStatus.VERIFIED;
        if (operatorNotes != null && !operatorNotes.isBlank()) {
            this.resolutionNotes = "[VERIFIED] " + operatorNotes;
        }
    }

    public void markInProgress() {
        if (this.status != ReportStatus.VERIFIED && this.status != ReportStatus.PENDING_VERIFICATION) {
            throw new IllegalStateException("Chỉ báo cáo đã xác minh hoặc đang chờ mới được chuyển sang xử lý!");
        }
        this.status = ReportStatus.IN_PROGRESS;
    }

    public void resolve(String resolutionNotes) {
        this.status = ReportStatus.RESOLVED;
        this.resolutionNotes = resolutionNotes;
    }

    public void reject(String reason) {
        this.status = ReportStatus.REJECTED;
        this.resolutionNotes = "[REJECTED] Lý do: " + reason;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReportStatus.PENDING_VERIFICATION;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IncidentReport that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IncidentReport{" +
                "id=" + id +
                ", driverPhone='" + driverPhone + '\'' +
                ", vehiclePlate='" + vehiclePlate + '\'' +
                ", location='" + location + '\'' +
                ", incidentType=" + incidentType +
                ", severityLevel=" + severityLevel +
                ", status=" + status +
                ", delay=" + estimatedDelayMinutes + "m" +
                ", needsAssistance=" + needsAssistance +
                ", createdAt=" + createdAt +
                '}';
    }
}

