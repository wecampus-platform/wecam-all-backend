package org.example.wecambackend.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.auth.request.RefreshJwtReq;
import org.example.wecambackend.dto.auth.response.JwtResponse;
import org.example.wecambackend.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/client/auth")
@RequiredArgsConstructor
@Tag(name = "ClientAuthController", description = "토큰이 필요한 인증 관련 API")
public class ClientAuthController {

    private final AuthService authService;

    @Operation(summary = "accessToken 을 갱신합니다.")
    @PostMapping("/token/refresh")
    public ResponseEntity<JwtResponse> refreshJwt(@Valid @RequestBody RefreshJwtReq request) {
        JwtResponse response = authService.refreshJwt(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃을 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody RefreshJwtReq request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }
}
