package org.example.wecambackend.dto.request.meeting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회의록 목록 조회 요청 DTO")
public class MeetingListRequest {

    @Schema(description = "카테고리 ID로 필터링 (선택사항)", example = "1")
    private Long categoryId;

    @Schema(description = "참석자 ID로 필터링 (선택사항)", example = "123")
    private Long attendeeId;

    @Schema(description = "정렬 기준", example = "LATEST", allowableValues = {"LATEST", "OLDEST"})
    @Builder.Default
    private SortOrder sortOrder = SortOrder.LATEST;

    @Schema(description = "정렬 기준 (LATEST: 최신순, OLDEST: 오래된순)")
    public enum SortOrder {
        @Schema(description = "최신순 (회의 일시 내림차순)")
        LATEST,
        
        @Schema(description = "오래된순 (회의 일시 오름차순)")
        OLDEST
    }
}
