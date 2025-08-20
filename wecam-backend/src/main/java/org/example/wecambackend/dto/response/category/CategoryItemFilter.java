package org.example.wecambackend.dto.response.category;

public enum CategoryItemFilter {
    ALL, TODO, FILE, MEETING;

    public static CategoryItemFilter from(String s) {
        if (s == null || s.isBlank()) return ALL;
        return CategoryItemFilter.valueOf(s.toUpperCase());
    }
}
