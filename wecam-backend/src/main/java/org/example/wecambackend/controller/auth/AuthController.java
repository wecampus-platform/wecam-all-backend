package org.example.wecambackend.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.dto.auth.response.EmailDuplicateCheckResponse;
import org.example.wecambackend.dto.auth.response.EmailPhoneDuplicateCheckResponse;
import org.example.wecambackend.dto.auth.response.PhoneDuplicateCheckResponse;
import org.example.wecambackend.service.auth.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "토큰 필요 없는 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/check/email")
    public BaseResponse<EmailDuplicateCheckResponse> validateEmail(@RequestParam String email) {
        EmailDuplicateCheckResponse result = authService.validateDuplicatedEmail(email);
        return new BaseResponse<>(result);
    }

    @Operation(summary = "전화번호 중복 확인")
    @GetMapping("/check/phone")
    public BaseResponse<PhoneDuplicateCheckResponse> validatePhone(@RequestParam String phone) {
        PhoneDuplicateCheckResponse result = authService.validateDuplicatedPhoneNumber(phone);
        return new BaseResponse<>(result);
    }

    @Operation(summary = "이메일 + 전화번호 중복 확인")
    @GetMapping("/check/both")
    public BaseResponse<EmailPhoneDuplicateCheckResponse> validateBoth(
            @RequestParam String email,
            @RequestParam String phone
    ) {
        EmailPhoneDuplicateCheckResponse result = authService.validateDuplicatedBoth(email, phone);
        return new BaseResponse<>(result);
    }
}
