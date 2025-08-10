package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CurrentUser;
import org.example.wecambackend.config.security.annotation.HasAffiliationApprovalAuthority;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.request.affiliation.AffiliationApprovalRequest;
import org.example.wecambackend.dto.response.affiliation.AffiliationCertificationSummaryResponse;
import org.example.model.affiliation.AffiliationCertificationId;
import org.example.model.enums.AuthenticationType;
import org.example.wecambackend.dto.response.affiliation.AffiliationVerificationResponse;
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


    @IsCouncil
    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 정보 리스트 조회 요청",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청 전부 조회",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    @GetMapping("/requests/all")
    public ResponseEntity<List<AffiliationCertificationSummaryResponse>> getAffiliationRequestsAll() {
        Long councilId = CouncilContextHolder.getCouncilId();
        return ResponseEntity.ok(
                affiliationCertificationAdminService.getRequestsByCouncilIdList(councilId)
        );
    }


    @IsCouncil
    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 정보 상세 조회 요청",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청을 권한 확인 후 상세 조회 값 전달 \n  전달 받는 값의 설명 userId -> 재학생 또는 신입생 인증서를 작성한 사람의 UserId , authType ->Enum 타입으로,",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    @GetMapping("/requests/show")
    public ResponseEntity<AffiliationVerificationResponse> getAffiliationRequestsDetail(
            @PathVariable String councilName, // ← 화면용
            @RequestParam("userId") Long userId,
            @RequestParam("authType") AuthenticationType authType
    ) {
        Long councilId = CouncilContextHolder.getCouncilId();


        AffiliationVerificationResponse affiliationVerificationResponse =
                affiliationCertificationAdminService.getRequestsByAffiliationIdDetail(userId,authType,councilId);


        return ResponseEntity.ok(affiliationVerificationResponse);
    }



    @IsCouncil
    @HasAffiliationApprovalAuthority
    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 요청 승인",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청 승인을 진행함. ",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    @PostMapping("/approve")
    public BaseResponse<?> approveAffiliationRequest(
            @RequestParam("userId") Long userId,
            @RequestParam("authType") AuthenticationType authType,
            @CurrentUser UserDetailsImpl currentUser
    ) {
        AffiliationCertificationId id = new AffiliationCertificationId(userId, authType);
        Long councilId = CouncilContextHolder.getCouncilId();
        affiliationCertificationAdminService.approveAffiliationRequest(id, councilId, currentUser);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }


    @IsCouncil
    @HasAffiliationApprovalAuthority
    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 전체 선택 요청 승인",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청 전체 승인을 진행함. ",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    @PostMapping("/select/approve")
    public BaseResponse<?> approveSelectAffiliationRequest(
            @RequestBody List<AffiliationApprovalRequest> requests,
            @CurrentUser UserDetailsImpl currentUser
    ) {
        Long councilId = CouncilContextHolder.getCouncilId();
        List<AffiliationApprovalRequest> failedList =
                affiliationCertificationAdminService.approveAffiliationRequests(requests, councilId, currentUser);

        if (failedList.isEmpty()) {
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } else {
            return new BaseResponse<>(BaseResponseStatus.PARTIAL_SUCCESS,failedList);
        }
    }

    @IsCouncil
    @HasAffiliationApprovalAuthority
    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 요청 삭제",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청을 삭제함.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    @DeleteMapping("/delete")
    public BaseResponse<?> deleteAffiliationRequest(
            @RequestParam("userId") Long userId,
            @RequestParam("authType") AuthenticationType authType,
            @CurrentUser UserDetailsImpl currentUser
    ) {
        AffiliationCertificationId id = new AffiliationCertificationId(userId, authType);

        Long councilId = CouncilContextHolder.getCouncilId();
        affiliationCertificationAdminService.deleteAffiliationRequest(id, councilId, currentUser);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }


    @IsCouncil
    @HasAffiliationApprovalAuthority
    @Operation(
            summary = "학생회 관리자 페이지 소속 인증 요청 거절",
            description = "해당 학생회가 관리하는 조직으로 들어온 소속 인증 요청을 거절함.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    @PutMapping("/reject")
    public BaseResponse<?> rejectAffiliationRequest(
            @RequestParam("userId") Long userId,
            @RequestParam("authType") AuthenticationType authType,
            @CurrentUser UserDetailsImpl currentUser,
            @RequestParam("reason") String reason
    ) {
        AffiliationCertificationId id = new AffiliationCertificationId(userId, authType);

        Long councilId = CouncilContextHolder.getCouncilId();
        affiliationCertificationAdminService.rejectAffiliationRequest(id, councilId, currentUser,reason);

        //TODO : 나중에 알림 추가해야함.
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

}
