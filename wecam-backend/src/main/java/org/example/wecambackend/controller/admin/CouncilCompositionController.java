package org.example.wecambackend.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.model.council.CouncilDepartment;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CheckCouncilEntity;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.example.wecambackend.dto.responseDTO.UnassignedCouncilMemberResponse;
import org.example.wecambackend.service.admin.CouncilCompositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/council/{councilName}/composition")
@RequiredArgsConstructor
@Tag(name = "Council Composition Controller", description = "학생회 관리자 페이지 내 구성원 관리 부분")
public class CouncilCompositionController {
    private final CouncilCompositionService councilCompositionService;

    @IsCouncil
    @CheckCouncilEntity(idParam = "departmentId", entityClass = CouncilDepartment.class)
    // 해당 department의 councilId 와 현재 접속 councilId 가 같은지 확인
    @Operation(
            summary = "요청 온 부서의 멤버들 불러오기",
            description = "접속한 X-council-Id 에 속해있는 학생회 명단 친구들 중 , 요청으로 온 부서에 해당되는 멤버들 불러오기.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속 중인 학생회 ID (헤더)", in = ParameterIn.HEADER)
            }
    )
    @GetMapping("/members/department")
    public ResponseEntity<List<CouncilMemberResponse>> getInvitationCodeRequests(
            @PathVariable String councilName,
            @RequestParam("departmentId") Long departmentId) {
        Long councilId = CouncilContextHolder.getCouncilId();
        List<CouncilMemberResponse> list = councilCompositionService.getDepartmentCouncilMembers(councilId,departmentId);
        return ResponseEntity.ok(list);
    }

}
