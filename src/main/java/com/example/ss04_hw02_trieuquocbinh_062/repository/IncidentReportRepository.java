package com.example.ss04_hw02_trieuquocbinh_062.repository;

import com.example.ss04_hw02_trieuquocbinh_062.entity.IncidentReport;
import com.example.ss04_hw02_trieuquocbinh_062.model.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {
    List<IncidentReport> findByStatus(ReportStatus status);
    List<IncidentReport> findByDriverPhone(String driverPhone);
}
