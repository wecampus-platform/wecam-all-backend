package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CurrentUser;
import org.example.wecambackend.config.security.annotation.HasAffiliationApprovalAuthority;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.responseDTO.AffiliationVerificationResponse;
import org.example.model.affiliation.AffiliationCertificationId;
import org.example.model.enums.AuthenticationType;
import org.example.wecambackend.service.admin.AffiliationCertificationAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/council/{councilName}/affiliation")
@RequiredArgsConstructor
@IsCouncil
@Tag(name = "Affiliation Certification Controller", description = "학생회 관리자 페이지 안에서 소속 인증 관련 정보")
public class AffiliationCertificationController {
    private final AffiliationCertificationAdminService affiliationCertificationAdminService;

    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 정보 리스트 조회 요청",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청 전부 조회")
    @GetMapping("/requests")
    public ResponseEntity<List<AffiliationVerificationResponse>> getAffiliationRequests(
            @PathVariable String councilName, // ← 화면용
            @RequestParam("councilId") Long councilId
    ) {
        List<AffiliationVerificationResponse> list =
                affiliationCertificationAdminService.getRequestsByCouncilId(councilId);


        return ResponseEntity.ok(list);
    }


    @HasAffiliationApprovalAuthority
    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 요청 승인",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청 승인을 진행함. ")
    @PostMapping("/approve")
    public ResponseEntity<?> approveAffiliationRequest(
            @RequestParam("userId") Long userId,
            @RequestParam("authType") AuthenticationType authType,
            @RequestParam("councilId") Long councilId,
            @CurrentUser UserDetailsImpl currentUser
    ) {
        AffiliationCertificationId id = new AffiliationCertificationId(userId, authType);

        affiliationCertificationAdminService.approveAffiliationRequest(id, councilId, currentUser);
        return ResponseEntity.ok("소속 인증 요청이 승인되었습니다.");
    }



}
