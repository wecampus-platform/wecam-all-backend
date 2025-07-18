package org.example.wecambackend.controller.client;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.requestDTO.OrganizationRegisterRequest;
import org.example.wecambackend.service.client.organization.OrganizationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/client/organization-request")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OrganizationRequestController", description = "조직 요청 컨트롤러")
public class OrganizationRequestController {

    private final OrganizationRequestService organizationRequestService;

    @PostMapping("/create")
    @Operation(summary = "조직 요청서 만들기", description = "워크스페이스 생성 요청 제출")
    public ResponseEntity<?> createOrganizationRequest(
            @RequestPart("request") OrganizationRegisterRequest requestDto,
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.warn("조직 생성 요청 시작");
        organizationRequestService.createOrganizationRequest(requestDto, files, userDetails.getId());
        return ResponseEntity.ok("조직 생성 요청이 접수되었습니다."); // TODO: 공통 Response로 변환 예정.
    }

}
