package org.example.wecambackend.dto.response.category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "카테고리 상세 내 아이템(파일) 요약")
public record CategoryItemResponse(
        @Schema(description="엔티티 PK", example="123") Long entityId,
        @Schema(description="엔티티 타입", example="FILE_ASSET") String entityType,
        @Schema(description="표시 제목(=원본 파일명)", example="회의록.pdf") String title,
        @Schema(description="접근 URL", example="https://cdn.../c/303/.../uuid.pdf") String url,
        @Schema(description="최종본 여부") boolean isFinal,
        @Schema(description="생성 시각") Instant createdAt
) {}
