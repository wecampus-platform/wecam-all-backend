package org.example.wecambackend.dto.response.meeting;

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
@Schema(description = "회의록 응답 DTO")
public class MeetingResponse {

    @Schema(description = "회의록 고유 번호", example = "1")
    private Long id;

    @Schema(description = "회의록 제목", example = "2024년 1학기 정기회의")
    private String title;

    @Schema(description = "회의 일시", example = "2024-03-15T14:00:00")
    private LocalDateTime meetingDateTime;

    @Schema(description = "회의 장소", example = "학생회관 2층 회의실")
    private String location;

    @Schema(description = "회의 내용", example = "# 회의 안건\n1. 학생회 예산 현황\n2. 다음 달 행사 계획\n3. 기타 논의사항")
    private String content;

    @Schema(description = "회의록 생성자 ID", example = "123")
    private Long createdById;

    @Schema(description = "회의록 생성자 이름", example = "김학생")
    private String createdByName;

    @Schema(description = "카테고리 ID 목록", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @Schema(description = "카테고리명 목록", example = "[\"MT\", \"OT\", \"새터\"]")
    private List<String> categoryNames;

    @Schema(description = "참석자 목록")
    private List<MeetingAttendeeResponse> attendees;

    @Schema(description = "첨부파일 목록")
    private List<MeetingFileResponse> files;

    @Schema(description = "생성일시", example = "2024-03-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-03-15T10:00:00")
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "회의 참석자 응답 DTO")
    public static class MeetingAttendeeResponse {
        @Schema(description = "참석자 고유 번호", example = "1")
        private Long id;
        
        @Schema(description = "학생회 멤버 이름", example = "김학생")
        private String memberName;
        
        @Schema(description = "참석 상태", example = "PRESENT")
        private MeetingAttendanceStatus attendanceStatus;
        
        @Schema(description = "회의 내 역할", example = "ATTENDEE")
        private MeetingRole role;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "회의 첨부파일 응답 DTO")
    public static class MeetingFileResponse {
        @Schema(description = "파일 고유 번호", example = "1")
        private Long id;
        
        @Schema(description = "원본 파일명", example = "회의자료.pdf")
        private String fileName;
        
        @Schema(description = "파일 접근용 URL", example = "/uploads/meetings/1/abc123_회의자료.pdf")
        private String fileUrl;
        
        @Schema(description = "파일 크기 (bytes)", example = "1024000")
        private Long fileSize;
        
        @Schema(description = "파일 타입", example = "application/pdf")
        private String fileType;
    }
}
