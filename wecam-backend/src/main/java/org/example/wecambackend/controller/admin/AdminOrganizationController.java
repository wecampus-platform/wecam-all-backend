package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.responseDTO.OrganizationRequestDetailResponse;
import org.example.wecambackend.dto.responseDTO.SubOrganizationResponse;
import org.example.wecambackend.dto.responseDTO.SubOrganizationDetailResponse;
import org.example.wecambackend.service.admin.AdminOrganizationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@IsCouncil
@RequestMapping("/admin/council/{councilName}/organization")
@Tag(name = "Admin Organization Controller", description = "학생회 관리자 용 조직 관리 기능을 처리")
@Slf4j
public class AdminOrganizationController {

    private final AdminOrganizationService organizationRequestAdminService;

    @GetMapping("/request/{requestId}/detail")
    @Operation(summary = "워크스페이스 생성 요청 상세 조회",
            description = "워크스페이스 생성 요청의 상세 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH),
                    @Parameter(name = "requestId", description = "워크스페이스 생성 요청 ID", in = ParameterIn.PATH)
            })
    public BaseResponse<OrganizationRequestDetailResponse> getOrganizationRequestDetail(
            @PathVariable String councilName,
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("워크스페이스 생성 요청 상세 조회: councilName={}, requestId={}, user={}", 
                councilName, requestId, userDetails.getUsername());
        
        OrganizationRequestDetailResponse response = organizationRequestAdminService.getOrganizationRequestDetail(requestId, councilName);
        return new BaseResponse<>(response);
    }

    @GetMapping("/request/{requestId}/files/{fileId}/download")
    @Operation(summary = "증빙자료 파일 다운로드 URL 조회",
            description = "워크스페이스 생성 요청의 증빙자료 파일 다운로드 URL을 조회합니다.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH),
                    @Parameter(name = "requestId", description = "워크스페이스 생성 요청 ID", in = ParameterIn.PATH),
                    @Parameter(name = "fileId", description = "파일 ID", in =ParameterIn.PATH)
            })
    public BaseResponse<String> getFileDownloadUrl(
            @PathVariable String councilName,
            @PathVariable Long requestId,
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("파일 다운로드 URL 조회: councilName={}, requestId={}, fileId={}, user={}", 
                councilName, requestId, fileId, userDetails.getUsername());
        
        String downloadUrl = organizationRequestAdminService.getFileDownloadUrl(fileId);
        return new BaseResponse<>(downloadUrl);
    }

    @GetMapping("/subs")
    @Operation(summary = "하위 학생회 목록 조회",
            description = "현재 학생회의 하위 학생회 목록을 조회합니다. (단과대/총학생회 전용)",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH)
            })
    public BaseResponse<List<SubOrganizationResponse>> getSubOrganizations(
            @PathVariable String councilName,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("하위 학생회 목록 조회: councilName={}, user={}", 
                councilName, userDetails.getUsername());
        
        List<SubOrganizationResponse> response = organizationRequestAdminService.getSubOrganizations();
        return new BaseResponse<>(response);
    }

    @GetMapping("/subs/{councilId}")
    @Operation(summary = "하위 학생회 상세 조회",
            description = "특정 하위 학생회의 상세 정보를 조회합니다. (단과대/총학생회 전용)",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH),
                    @Parameter(name = "councilId", description = "조회할 하위 학생회 ID", in = ParameterIn.PATH)
            })
    public BaseResponse<SubOrganizationDetailResponse> getSubOrganizationDetail(
            @PathVariable String councilName,
            @PathVariable Long councilId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("하위 학생회 상세 조회: councilName={}, councilId={}, user={}", 
                councilName, councilId, userDetails.getUsername());
        
        SubOrganizationDetailResponse response = organizationRequestAdminService.getSubOrganizationDetail(councilId);
        return new BaseResponse<>(response);
    }
}
