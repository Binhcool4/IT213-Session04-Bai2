package com.example.ss04_hw02_trieuquocbinh_062.runner;

import com.example.ss04_hw02_trieuquocbinh_062.dto.IncidentExtraction;
import com.example.ss04_hw02_trieuquocbinh_062.entity.IncidentReport;
import com.example.ss04_hw02_trieuquocbinh_062.service.IncidentReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IncidentDemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IncidentDemoRunner.class);

    private final IncidentReportService incidentReportService;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n==========================================================================================");
        System.out.println(">>> BẮT ĐẦU CHƯƠNG TRÌNH DEMO: THIẾT KẾ CẤU TRÚC DỮ LIỆU BÓC TÁCH PHÒNG THỦ (BÀI 2) <<<");
        System.out.println("==========================================================================================\n");

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("[KỊCH BẢN 1] Tin nhắn tài xế: 'Alo điều hành, xe tôi 29A-888.99 bị va chạm nhẹ ở ngã tư Nguyễn Trãi - Khuất Duy Tiến, sđt 0912-345-678, chậm tầm 30p'");
        System.out.println("------------------------------------------------------------------------------------------");

        String rawJson1 = """
                {
                    "driverPhone": "0912-345-678",
                    "vehiclePlate": " 29a-888.99 ",
                    "rawLocation": "Ngã tư Nguyễn Trãi - Khuất Duy Tiến, Thanh Xuân, Hà Nội",
                    "incidentType": "ACCIDENT",
                    "severity": "HIGH",
                    "summary": "Va chạm nhẹ với phương tiện khác tại ngã tư",
                    "estimatedDelayMinutes": 30,
                    "needsAssistance": true,
                    "confidenceScore": 0.95
                }
                """;

        IncidentExtraction extraction1 = objectMapper.readValue(rawJson1, IncidentExtraction.class);
        System.out.println("==> 1. DTO Record sau khi hứng từ AI: " + extraction1);

        IncidentReport report1 = incidentReportService.processAndSaveExtraction(extraction1, rawJson1);
        System.out.println("==> 2. JPA Entity sau khi qua Mapper & Lưu DB thành công: " + report1);
        System.out.println();

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("[KỊCH BẢN 2] Tin nhắn tài xế: 'Xe chết máy trên cầu Vĩnh Tuy' (AI trả về nhiều trường null và khoảng trắng)");
        System.out.println("------------------------------------------------------------------------------------------");

        String rawJson2 = """
                {
                    "driverPhone": "   0988 777 666   ",
                    "vehiclePlate": "  51f-123.45  ",
                    "rawLocation": "   Cầu Vĩnh Tuy, Hà Nội   ",
                    "incidentType": "HONG_XE_CHET_MAY",
                    "severity": null,
                    "summary": null,
                    "estimatedDelayMinutes": null,
                    "needsAssistance": null,
                    "confidenceScore": null
                }
                """;

        IncidentExtraction extraction2 = objectMapper.readValue(rawJson2, IncidentExtraction.class);
        System.out.println("==> 1. DTO Record (Tự động Sanitize & Điền Fallback an toàn qua Compact Constructor):");
        System.out.println("    - driverPhone: '" + extraction2.driverPhone() + "'");
        System.out.println("    - vehiclePlate: '" + extraction2.vehiclePlate() + "'");
        System.out.println("    - rawLocation: '" + extraction2.rawLocation() + "'");
        System.out.println("    - incidentType: '" + extraction2.incidentType() + "'");
        System.out.println("    - severity: '" + extraction2.severity() + "'");
        System.out.println("    - estimatedDelayMinutes: " + extraction2.estimatedDelayMinutes());
        System.out.println("    - needsAssistance: " + extraction2.needsAssistance());

        IncidentReport report2 = incidentReportService.processAndSaveExtraction(extraction2, rawJson2);
        System.out.println("==> 2. JPA Entity sau khi qua Mapper xử lý (IncidentType tự quy về BREAKDOWN, sđt làm sạch): " + report2);
        System.out.println();

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println(" [KỊCH BẢN 3] Tin nhắn tài xế: 'Trời mưa to ngập nửa bánh xe ở đại lộ Thăng Long, tắc đường nghiêm trọng'");
        System.out.println("------------------------------------------------------------------------------------------");

        String rawJson3 = """
                {
                    "driverPhone": "+84 933 111 222",
                    "vehiclePlate": "30E-999.88",
                    "rawLocation": "Đại lộ Thăng Long",
                    "incidentType": "MUA_NGAP_DUONG",
                    "severity": "KHAN_CAP",
                    "summary": "Mưa to gây ngập nước sâu, xe không thể di chuyển",
                    "estimatedDelayMinutes": 120,
                    "needsAssistance": true,
                    "confidenceScore": 0.88
                }
                """;

        IncidentExtraction extraction3 = objectMapper.readValue(rawJson3, IncidentExtraction.class);
        System.out.println("==> 1. DTO Record nhận dạng: " + extraction3);

        IncidentReport report3 = incidentReportService.processAndSaveExtraction(extraction3, rawJson3);
        System.out.println("==> 2. JPA Entity sau khi Map thành công (Enum WEATHER_ISSUE & CRITICAL): " + report3);
        System.out.println();

        System.out.println("==========================================================================================");
        System.out.println("DANH SÁCH BẢN GHI SỰ CỐ ĐÃ LƯU TRONG DATABASE JPA/H2:");
        System.out.println("==========================================================================================");
        List<IncidentReport> allReports = incidentReportService.getAllReports();
        for (IncidentReport rep : allReports) {
            System.out.printf(" ID: %d | Xe: %-12s | SĐT: %-14s | Loại: %-15s | Mức độ: %-10s | Trạng thái: %-22s | Địa điểm: %s%n",
                    rep.getId(),
                    rep.getVehiclePlate(),
                    rep.getDriverPhone(),
                    rep.getIncidentType(),
                    rep.getSeverityLevel(),
                    rep.getStatus(),
                    rep.getLocation());
        }
        System.out.println("==========================================================================================\n");
    }
}

