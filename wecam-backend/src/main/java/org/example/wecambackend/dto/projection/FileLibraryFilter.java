package org.example.wecambackend.dto.projection;

public record FileLibraryFilter(
        Long   councilId,       // 필수(화면이 학생회별 리스트)
        String sourceType,      // null or "TODO"|"MEETING"|"FILE_ASSET"
        Long   categoryId,      // null or 카테고리ID
        Boolean finalOnly,      // true면 최종만
        String query            // null or 검색어(파일명/업로더명 like)
) {}
