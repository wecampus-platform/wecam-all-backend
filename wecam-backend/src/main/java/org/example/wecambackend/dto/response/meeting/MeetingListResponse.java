package org.example.wecambackend.dto.response.meeting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회의록 목록 조회 응답 DTO")
public class MeetingListResponse {

    @Schema(description = "회의록 고유 번호", example = "1")
    private Long meetingId;

    @Schema(description = "회의록 제목", example = "2024년 1학기 정기회의")
    private String title;

    @Schema(description = "회의 일시", example = "2024-03-15T14:00:00")
    private LocalDateTime meetingDateTime;

    @Schema(description = "카테고리명 목록", example = "[\"MT\", \"OT\", \"새터\"]")
    private List<String> categoryNames;

    @Schema(description = "작성자명", example = "김학생")
    private String authorName;

    @Schema(description = "작성자 user ID", example = "1")
    private Long authorId;

    @Schema(description = "작성자 프로필 썸네일 URL", example = "/uploads/PROFILE_THUMB/profile.jpg")
    private String authorProfileThumbnailUrl;

    @Schema(description = "생성일시", example = "2024-03-15T10:00:00")
    private LocalDateTime createdAt;
}
