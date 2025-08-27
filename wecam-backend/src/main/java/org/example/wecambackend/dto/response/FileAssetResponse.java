package org.example.wecambackend.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "파일 업로드/상세 응답")
public record FileAssetResponse(
        Long fileId,
        String uuid,
        String originalFileName,
        String storedFileName,
        String url,
        String title,
        String description,
        boolean isFinal,
        @Schema(description = "연결된 카테고리 ID 목록") List<Long> categoryIds,
        Instant createdAt
) {}
