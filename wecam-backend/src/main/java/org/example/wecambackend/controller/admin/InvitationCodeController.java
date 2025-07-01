package org.example.wecambackend.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.model.enums.CodeType;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.requestDTO.InvitationCreateRequest;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.example.wecambackend.service.admin.InvitationCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/council/{councilName}/invitation")
@RequiredArgsConstructor
@IsCouncil
@Tag(name = "Invitation Code Controller", description = "학생회 관리자 페이지 안에서 초대 코드 관련 Controller")
public class InvitationCodeController {

    private final InvitationCodeService invitationCodeService;

    @IsCouncil
    @Operation(
            summary = "학생회 관리자 페이지 발급한 초대코드 조회 요청",
            description = "해당 학생회가 관리하는 조직에서 만든 초대 코드 전부 조회")
    @GetMapping("/list")
    public ResponseEntity<List<InvitationCodeResponse>> getInvitationCodeRequests(
            @PathVariable String councilName, // ← 화면용
            @RequestParam("councilId") Long councilId
    ) {
        List<InvitationCodeResponse> list = invitationCodeService.findByCouncilId(councilId);
        return ResponseEntity.ok(list);
    }


    @IsCouncil
    //TODO : 각 학생회별 관리자 페이지에서 커스텀마이징으로 설정해둔 , 초대코드 생성 권한이 있는 학생회사람만 사용 가능하게끔 해야됨.
    @Operation(
            summary = "학생회 관리자 페이지 초대코드 생성",
            description = "해당 학생회가 관리하는 조직 - 초대 코드 생성")
    @PostMapping("/create/{codeType}/student-invitation")
    public ResponseEntity<?> makeInvitationCodeStudent(
            @PathVariable String councilName, // ← 화면용
            @RequestParam("councilId") Long councilId,
            @RequestBody InvitationCreateRequest requestDto,
            @PathVariable CodeType codeType,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        invitationCodeService.createInvitationCode(codeType,requestDto,userDetails.getId(),councilId);
        return ResponseEntity.ok("초대 코드 생성이 완료되었습니다.");
    }





}
