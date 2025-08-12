package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.config.security.annotation.CheckCouncilEntity;
import org.example.wecambackend.dto.request.meeting.MeetingUpsertRequest;
import org.example.wecambackend.dto.response.meeting.MeetingResponse;
import org.example.wecambackend.service.admin.meeting.MeetingService;
import org.springframework.http.MediaType;
import org.example.model.meeting.Meeting;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.example.wecambackend.dto.request.meeting.MeetingListRequest;
import org.example.wecambackend.dto.response.meeting.MeetingListResponse;
import org.example.wecambackend.dto.response.meeting.MeetingTemplateListResponse;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;


@RestController
@RequestMapping("/admin/council/{councilName}/meeting")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MeetingController", description = "회의록 컨트롤러")
public class MeetingController {

    private final MeetingService meetingService;

    @IsCouncil  // 현재 로그인한 사용자가 X-Council-Id 헤더의 학생회에 소속되어 있는지 검증
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
            @RequestBody MeetingUpsertRequest request,
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

            MeetingResponse response = meetingService.createMeeting(request, userDetails.getId());
            return new BaseResponse<>(response);
    }

    @IsCouncil  // 현재 로그인한 사용자가 X-Council-Id 헤더의 학생회에 소속되어 있는지 검증
    @CheckCouncilEntity(idParam = "meetingId", entityClass = Meeting.class)  // 해당 회의록이 현재 접속한 학생회에 속하는지 검증
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

            MeetingResponse response = meetingService.addFilesToMeeting(meetingId, userDetails.getId(), files);
            return new BaseResponse<>(response);
    }

    @IsCouncil
    @CheckCouncilEntity(idParam = "meetingId", entityClass = Meeting.class)
    @PatchMapping(value = "/{meetingId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "회의록 수정",
            description = "회의록의 텍스트 정보 및 참석자/카테고리를 수정합니다. (첨부파일 제외)",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "meetingId", description = "회의록 ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<MeetingResponse> updateMeeting(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("councilName") String councilName,
            @RequestBody MeetingUpsertRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
            MeetingResponse response = meetingService.updateMeeting(meetingId, request, userDetails.getId());
            return new BaseResponse<>(response);
    }

    @IsCouncil
    @GetMapping
    @Operation(
            summary = "회의록 목록 조회",
            description = "필터링과 정렬을 지원하는 회의록 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true),
                    @Parameter(name = "categoryId", description = "필터 : 카테고리 ID", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "attendeeId", description = "필터 : 참석자 ID", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "sortOrder", description = "정렬 기준", in = ParameterIn.QUERY, required = false, 
                              schema = @Schema(allowableValues = {"LATEST", "OLDEST"}, 
                                             description = "LATEST: 최신순, OLDEST: 오래된순"))
            }
    )
    public BaseResponse<List<MeetingListResponse>> getMeetingList(
            @PathVariable("councilName") String councilName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "attendeeId", required = false) Long attendeeId,
            @RequestParam(value = "sortOrder", defaultValue = "LATEST") MeetingListRequest.SortOrder sortOrder) {

        MeetingListRequest request = MeetingListRequest.builder()
                .categoryId(categoryId)
                .attendeeId(attendeeId)
                .sortOrder(sortOrder)
                .build();

        List<MeetingListResponse> response = meetingService.getMeetingList(request);
        return new BaseResponse<>(response);
    }

    @IsCouncil
    @CheckCouncilEntity(idParam = "meetingId", entityClass = Meeting.class)
    @GetMapping(value = "/{meetingId}")
    @Operation(
            summary = "회의록 상세 조회",
            description = "특정 회의록 내용을 상세 조회합니다. (첨부파일 제외)",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "meetingId", description = "회의록 ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<MeetingResponse> getMeeting(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MeetingResponse response = meetingService.getMeeting(meetingId);
        return new BaseResponse<>(response);
    }

    @IsCouncil
    @GetMapping(value = "/templates")
    @Operation(
            summary = "회의록 템플릿 목록 조회",
            description = "특정 학생회의 회의록 템플릿 목록을 조회합니다. isDefault가 true면 기본 템플릿입니다. (true 아니면 null)",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<List<MeetingTemplateListResponse>> getTemplateList(@PathVariable("councilName") String councilName) {
        List<MeetingTemplateListResponse> response = meetingService.getTemplateList();
        return new BaseResponse<>(response);
    }
}
