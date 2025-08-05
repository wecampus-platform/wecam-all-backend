package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.service.admin.WorkSpaceManageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "Admin WorkSpace Manage Controller" , description = " 학생회 워크스페이스 (하위 학생회의) 조직 관리 ")
@RequestMapping("admin/council/{councilName}/workspace")
public class WorkSpaceManageController {


    private final WorkSpaceManageService workSpaceManageService;

    @IsCouncil
    @Operation(
            summary = "워크스페이스 승인 (자신의 하위 조직만 승인할 수 있음.)",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "councilId", description = "학생회 PK", in = ParameterIn.PATH),
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH)
            }
    )
    @PostMapping(value = "/{requestId}/Approve" )
    public BaseResponse<?> workSpaceApprove(
            @PathVariable("councilName") String councilName,
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // 로그 출력
        System.out.println("📝 [POST / Approve] 워크스페이스 승인 요청");
        System.out.println("    🔸 유저 ID: " + userDetails.getId());
        System.out.println("    🔸 학생회 이름: " + councilName);
        // 실제 로직
        workSpaceManageService.getAllWorkspaceRequestApprove(requestId,userDetails.getId());

        System.out.println("    ✅ 승인 완료");
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @IsCouncil
    @Operation(
            summary = "워크스페이스 거절 (자신의 하위 조직만 거절할 수 있음.)",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "councilId", description = "학생회 PK", in = ParameterIn.PATH),
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH)
            }
    )
    @PostMapping(value = "/{requestId}/reject" )
    public BaseResponse<?> workSpaceReject(
            @PathVariable("councilName") String councilName,
            @PathVariable Long requestId,
            @RequestParam("reason") String reason,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        System.out.println("📝 [POST / Reject] 워크스페이스 요청 거절");
        System.out.println("    🔸 유저 ID: " + userDetails.getId());
        System.out.println("    🔸 학생회 이름: " + councilName);
        // 실제 로직
        workSpaceManageService.getAllWorkspaceRequestReject(requestId,userDetails.getId(),reason);

        System.out.println("    ✅ 거절 완료");
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }
}
