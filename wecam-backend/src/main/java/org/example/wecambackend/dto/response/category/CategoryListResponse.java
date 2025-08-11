package org.example.wecambackend.dto.response.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 목록 조회 응답 DTO")
public class CategoryListResponse {

    @Schema(description = "카테고리 고유 번호", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리명", example = "MT")
    private String categoryName;
}
