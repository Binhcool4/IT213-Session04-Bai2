package com.example.ss04_hw02_trieuquocbinh_062.model.enums;

public enum IncidentType {
    ACCIDENT,
    BREAKDOWN,
    TRAFFIC_JAM,
    WEATHER_ISSUE,
    CUSTOMER_DISPUTE,
    OTHER;

    public static IncidentType fromStringOrDefault(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OTHER;
        }
        try {
            return IncidentType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String normalized = value.trim().toUpperCase();
            if (normalized.contains("ACCIDENT") || normalized.contains("TAI_NAN") || normalized.contains("VA_CHAM")) {
                return ACCIDENT;
            } else if (normalized.contains("BREAKDOWN") || normalized.contains("HONG_XE") || normalized.contains("CHET_MAY")) {
                return BREAKDOWN;
            } else if (normalized.contains("TRAFFIC") || normalized.contains("KET_XE") || normalized.contains("TAC_DUONG")) {
                return TRAFFIC_JAM;
            } else if (normalized.contains("WEATHER") || normalized.contains("MUA") || normalized.contains("NGAP")) {
                return WEATHER_ISSUE;
            } else if (normalized.contains("DISPUTE") || normalized.contains("TRANH_CHAP")) {
                return CUSTOMER_DISPUTE;
            }
            return OTHER;
        }
    }
}

