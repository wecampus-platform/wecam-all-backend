package org.example.wecambackend.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.model.enums.CodeType;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsStudent;
import org.example.wecambackend.config.security.annotation.IsUnStudent;
import org.example.wecambackend.dto.auth.request.RefreshJwtReq;
import org.example.wecambackend.dto.auth.response.JwtResponse;
import org.example.wecambackend.service.admin.InvitationCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/invitation-code")
@RequiredArgsConstructor
@Tag(name = "Client Invitation Code Controller", description = "일반 유저 초대 코드 관련 API ")
public class ClientInvitationCodeController {

    //초대 코드 사용 - Council_member , Student_member 두개 통합해서 사용
    //프론트에서 어떤 타입인지 명시해서 줘야 함.
    private final InvitationCodeService invitationCodeService;

    @Operation(summary = "초대 코드 사용")
    @PostMapping("/use/{CodeType}")
    public ResponseEntity<?> useInvitationCode(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("Code") String code,
            @Parameter(description = "코드 타입", required = true)
            @PathVariable("CodeType") CodeType codeType) {
        invitationCodeService.usedCode(code,userDetails,codeType);
        return ResponseEntity.ok("코드 사용이 원활히 됐습니다.");
    }

}
