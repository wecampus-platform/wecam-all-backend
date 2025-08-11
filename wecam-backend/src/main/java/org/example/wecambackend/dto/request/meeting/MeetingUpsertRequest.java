package org.example.wecambackend.dto.request.meeting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.model.enums.MeetingAttendanceStatus;
import org.example.model.enums.MeetingRole;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회의록 생성/수정 요청 DTO")
public class MeetingUpsertRequest {

    @Schema(description = "회의록 제목", example = "2025년 1학기 정기회의")
    private String title;

    @Schema(description = "회의 일시", example = "2025-08-11T14:00:00")
    private LocalDateTime meetingDateTime;

    @Schema(description = "회의 장소", example = "학생회관 2층 회의실")
    private String location;

    @Schema(description = "회의 내용 (마크다운)", example = "# 회의 안건\n1. 학생회 예산 현황\n2. 다음 달 행사 계획\n3. 기타 논의사항")
    private String content;

    @Schema(description = "카테고리 ID 목록 (다중 선택 가능)", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @Schema(description = "참석자 목록")
    private List<MeetingAttendeeRequest> attendees;

    // 첨부파일은 MultipartFile로 별도 처리

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "회의 참석자 요청 DTO")
    public static class MeetingAttendeeRequest {
        @Schema(description = "학생회 멤버 ID", example = "123")
        private Long councilMemberId;
        
        @Schema(description = "참석 상태 (기본값: PRESENT)", example = "PRESENT")
        private MeetingAttendanceStatus attendanceStatus;
        
        @Schema(description = "회의 내 역할 (기본값: ATTENDEE)", example = "ATTENDEE")
        private MeetingRole role;
    }
}
