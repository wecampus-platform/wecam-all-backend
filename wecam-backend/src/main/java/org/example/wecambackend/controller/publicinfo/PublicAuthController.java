package org.example.wecambackend.controller.publicinfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.auto.LoginRequest;
import org.example.wecambackend.dto.auto.LoginResponse;
import org.example.wecambackend.dto.requestDTO.RepresentativeRegisterRequest;
import org.example.wecambackend.dto.requestDTO.StudentRegisterRequest;
import org.example.wecambackend.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
@Tag(name = "Public Auth Controller", description = "인증 없이 접근 가능한 회원가입 및 로그인 API")

public class PublicAuthController {

    private final AuthService authService;

    @Operation(
            summary = "학생 회원가입",
            description = "학생 정보를 입력받아 회원가입을 처리합니다."
    )
    @PostMapping("/sign/student")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegisterRequest request) {
        authService.registerStudent(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(
            summary = "대표자 회원가입",
            description = "대표자 학생 정보를 입력받아 회원가입을 처리합니다."
    )
    @PostMapping("/sign/leader")
    public ResponseEntity<?> registerStudent(@RequestBody RepresentativeRegisterRequest request) {
        authService.registerLeader(request);
        return ResponseEntity.ok("대표자 회원가입이 완료되었습니다.");
    }



    @Operation(
            summary = "학생 로그인",
            description = "이메일과 비밀번호를 입력받아 로그인 후 토큰을 반환합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
