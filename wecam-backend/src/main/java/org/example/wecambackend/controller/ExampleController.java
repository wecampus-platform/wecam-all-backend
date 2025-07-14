package org.example.wecambackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

@Tag(name = "응답 테스트 API", description = "BaseResponse 테스트용 컨트롤러")
@RestController
@RequestMapping("/example")
public class ExampleController {

    @Operation(summary = "성공 응답 (문자열)", description = "단순 문자열 성공 응답 테스트")
    @GetMapping("/success")
    public BaseResponse<String> success() {
        return BaseResponse.from("요청이 성공했습니다.");
    }

    @Operation(summary = "성공 응답 (DTO)", description = "DTO 객체를 성공 응답으로 반환")
    @GetMapping("/dto-success")
    public BaseResponse<ExDto> dtoSuccess() {
        ExDto dto = new ExDto("홍길동", "hong@example.com");
        return new BaseResponse<>(dto);
    }

    @Operation(summary = "에러 응답", description = "BaseResponseStatus 기반의 에러 응답 반환")
    @GetMapping("/error")
    public BaseResponse<BaseResponseStatus> error() {
        throw new BaseException(BaseResponseStatus.EMAIL_DUPLICATED);
    }

    @Operation(
            summary = "유효성 검사 테스트",
            description = "요청 DTO의 유효성 검사 실패 시 에러 응답 확인"
    )
    @PostMapping("/validate")
    public BaseResponse<String> validateError(@RequestBody @Valid ExDto dto) {
        return new BaseResponse<>("유효성 검사 통과!");
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExDto {

        @Schema(description = "이름", example = "홍길동", required = true)
        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        @Schema(description = "이메일", example = "hong@example.com", required = true)
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String email;
    }
}
