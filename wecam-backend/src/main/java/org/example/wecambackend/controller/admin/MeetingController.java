package org.example.wecambackend.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.request.meeting.MeetingCreateRequest;
import org.example.wecambackend.dto.response.meeting.MeetingResponse;
import org.example.wecambackend.service.admin.meeting.MeetingService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/council/{councilName}/meeting")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MeetingController", description = "회의록 컨트롤러")
public class MeetingController {

    private final MeetingService meetingService;

    @IsCouncil
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "회의록 생성",
            description = "새로운 회의록을 생성합니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<MeetingResponse> createMeetingJson(
            @RequestBody MeetingCreateRequest requestDto,
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

            MeetingResponse response = meetingService.createMeeting(requestDto, userDetails.getId());
            return new BaseResponse<>(response);
    }

    @IsCouncil
    @PostMapping(value = "/{meetingId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "회의록 첨부파일 업로드",
            description = "기존 회의록에 첨부파일을 업로드합니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "meetingId", description = "회의록 ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<MeetingResponse> uploadMeetingFiles(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("councilName") String councilName,
            @RequestPart(value = "files", required = true) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

            MeetingResponse response = meetingService.addFilesToMeeting(meetingId, files, userDetails.getId());
            return new BaseResponse<>(response);
    }
}
