package com.example.ss04_hw02_trieuquocbinh_062.model.enums;

public enum SeverityLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static SeverityLevel fromStringOrDefault(String value, SeverityLevel defaultLevel) {
        if (value == null || value.trim().isEmpty()) {
            return defaultLevel != null ? defaultLevel : LOW;
        }
        try {
            return SeverityLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String normalized = value.trim().toUpperCase();
            if (normalized.contains("CRITICAL") || normalized.contains("NGHIEM_TRONG") || normalized.contains("KHAN_CAP")) {
                return CRITICAL;
            } else if (normalized.contains("HIGH") || normalized.contains("CAO")) {
                return HIGH;
            } else if (normalized.contains("MEDIUM") || normalized.contains("TRUNG_BINH")) {
                return MEDIUM;
            }
            return defaultLevel != null ? defaultLevel : LOW;
        }
    }
}

