package org.example.wecambackend.dto.projection;


import java.time.LocalDateTime;

public record FileItemDto(
        String  sourceType,     // TODO / MEETING / STANDALONE
        String  entityType,      // TODO / MEETING / FILE (카테고리 매칭용)
        Long    entityId,
        String  fileId,          // 통일된 문자열 (UUID/Long 모두 CHAR로 투영)
        String  sourceTitle,     // 할일/회의록 제목 (단독 업로드는 null)
        String  fileName,
        Long    councilId,
        Long    uploaderId,
        String  uploaderName,    // 조인으로 붙임
        String  categoryNames,   // "A,B,C" (여러 개면 ,로 합침)
        java.sql.Timestamp uploadedAt,
        boolean isFinal
) {}
