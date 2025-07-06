package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.validation.CouncilAccessValidator;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CheckCouncilAccess;
import org.example.wecambackend.dto.requestDTO.SelectCouncilRequest;
import org.example.wecambackend.service.admin.AdminAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/council")
@RequiredArgsConstructor
@Tag(name = "Council Access Controller" , description = "처음 학생회 페이지 접속 컨트롤러")
public class CouncilAccessController {

    private final AdminAuthService adminAuthService;

    @CheckCouncilAccess
    @Operation(
            summary = "학생회 관리자 접속 시 권한 부여 및 확인"
            )
    @GetMapping("/home")
    public ResponseEntity<?> getAdminHome(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader("X-Council-Id") Long councilId) {
        return ResponseEntity.ok("학생회 관리자 홈 진입 성공 (councilId = " + councilId + ")");
    }


    //학생회 관리자 Id 변경
    @CheckCouncilAccess
    @Operation(
            summary = "학생회 관리자 접속 시 권한 변경 및 확인"
    )
    @GetMapping("/change-council")
    public ResponseEntity<?> ChangeCouncilMain(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader("X-Council-Id") Long councilId) {
        //UserDetails 의 currentCouncilId 셋팅
        return ResponseEntity.ok("학생회 관리자 홈 전환 성공 (councilId = " + councilId + ")");
    }
}

