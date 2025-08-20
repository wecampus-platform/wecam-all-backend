package org.example.wecambackend.dto.response.category;

public enum SortOption {
    LATEST, OLDEST;

    public static SortOption from(String s) {
        if (s == null || s.isBlank()) return LATEST;
        return SortOption.valueOf(s.toUpperCase());
    }

    public String toSql() {
        return this == OLDEST ? "ASC" : "DESC";
    }
}
