package org.example.wecambackend.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.example.wecambackend.dto.responseDTO.UniversitySimpleResponse;
import org.example.wecambackend.service.admin.CouncilMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.example.wecambackend.dto.requestDTO.DepartmentAssignmentRequest;
import org.example.wecambackend.dto.requestDTO.ExpulsionRequest;
import org.example.wecambackend.config.security.annotation.CheckCouncilAccess;
import org.example.wecambackend.config.security.annotation.IsPresidentTeam;
import org.example.wecambackend.config.security.annotation.CheckCouncilEntity;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.responseDTO.DepartmentResponse;
import org.example.model.council.CouncilMember;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/council/{councilName}/member")
@RequiredArgsConstructor
@IsCouncil
@Tag(name = "Council Member Controller", description = "학생회 관리자 페이지 안에서 본인 조직에 속한 멤버 관리 컨트롤러")
public class CouncilMemberController {


    private final CouncilMemberService councilMemberService;
    @PostMapping("/list")
    @IsCouncil
    @Operation(
            summary = "학생회 관리자 페이지 전체 멤버 조회 요청",
            description = "해당 학생회가 관리하는 조직에 속한 전체 멤버 조회",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)
            })
    public List<CouncilMemberResponse> getAllMembers(@PathVariable("councilName") String councilName,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long councilId = CouncilContextHolder.getCouncilId();
        return councilMemberService.getAllCouncilMembers(councilId);
    }

    @PutMapping("/{memberId}/department")
    @CheckCouncilAccess
    @IsPresidentTeam
    @CheckCouncilEntity(idParam = "memberId", entityClass = CouncilMember.class)
    @Operation(
            summary = "학생회 부원 부서 배치/이동",
            description = "학생회 부원을 특정 부서로 배치하거나 이동시킵니다. 회장과 부회장만 이 기능을 사용할 수 있으며(임시), 부서 배치 시 부원의 역할이 자동으로 DEPUTY로 변경됩니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "memberId", description = "배치할 부원의 ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<String> assignMemberToDepartment(
            @PathVariable Long memberId,
            @RequestBody @Valid DepartmentAssignmentRequest request,
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        councilMemberService.assignMemberToDepartment(memberId, request.getDepartmentId());
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, "부서 배치가 완료되었습니다.");
    }

    @GetMapping("/departments")
    @CheckCouncilAccess
    @Operation(
            summary = "학생회 부서 및 역할 목록 조회",
            description = "현재 학생회의 모든 부서와 각 부서의 역할 목록을 조회합니다. 부서 배치 시 선택할 수 있는 부서 목록을 제공합니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<List<DepartmentResponse>> getAllDepartments(
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<DepartmentResponse> departments = councilMemberService.getAllDepartments();
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, departments);
    }

    @DeleteMapping("/{memberId}")
    @CheckCouncilAccess
    @IsPresidentTeam
    @CheckCouncilEntity(idParam = "memberId", entityClass = CouncilMember.class)
    @Operation(
            summary = "학생회 구성원 제명",
            description = "학생회 구성원을 제명합니다. 회장과 부회장만 이 기능을 사용할 수 있으며, 회장은 제명할 수 없습니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "memberId", description = "제명할 구성원의 ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<String> expelMember(
            @PathVariable Long memberId,
            @RequestBody(required = false) ExpulsionRequest request,
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String reason = request != null ? request.getReason() : null;
        councilMemberService.expelMember(memberId, reason);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, "구성원이 성공적으로 제명되었습니다.");
    }
}
