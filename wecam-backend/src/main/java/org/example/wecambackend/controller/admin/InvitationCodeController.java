//package org.example.wecambackend.controller.admin;
//
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.example.wecambackend.config.security.annotation.IsCouncil;
//import org.example.wecambackend.dto.responseDTO.AffiliationVerificationResponse;
//import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
//import org.example.wecambackend.service.admin.InvitationCodeService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin/council/{councilName}/invitation")
//@RequiredArgsConstructor
//@IsCouncil
//@Tag(name = "Invitation Code Controller", description = "학생회 관리자 페이지 안에서 초대 코드 관련 Controller")
//public class InvitationCodeController {
//
//    private final InvitationCodeService invitationCodeService;
//
//    @Operation(
//            summary = "학생회 관리자 페이지 발급한 초대코드 조회 요청",
//            description = "해당 학생회가 관리하는 조직에서 만든 초대 코드 전부 조회")
//    @GetMapping("/list")
//    public ResponseEntity<List<InvitationCodeResponse>> getInvitationCodeRequests(
//            @PathVariable String councilName, // ← 화면용
//            @RequestParam("councilId") Long councilId
//    ) {
//        List<InvitationCodeResponse> list = invitationCodeService.findByCouncilId(councilId);
//        return ResponseEntity.ok(list);
//    }
//
//}
