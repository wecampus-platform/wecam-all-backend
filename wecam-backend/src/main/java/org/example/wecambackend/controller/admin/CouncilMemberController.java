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


}
