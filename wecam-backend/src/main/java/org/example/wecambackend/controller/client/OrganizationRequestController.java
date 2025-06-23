package org.example.wecambackend.controller.client;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.requestDTO.OrganizationRequestRequest;
import org.example.wecambackend.service.client.OrganizationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/organization-request")
@RequiredArgsConstructor
@Tag(name = "OrganizationRequestController", description = "조직 요청 컨트롤러")
public class OrganizationRequestController {

    private final OrganizationRequestService organizationRequestService;

    @PostMapping("/create")
    @Operation(summary = "조직 요청서 만들기", description = "조직 요청 제출")
    public ResponseEntity<?> createOrganizationRequest(@RequestBody OrganizationRequestRequest requestDto,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        organizationRequestService.createOrganizationRequest(requestDto, userDetails.getId());
        return ResponseEntity.ok("조직 생성 요청이 접수되었습니다."); // TODO: 공통 Response로 변환 예정.
    }

}
