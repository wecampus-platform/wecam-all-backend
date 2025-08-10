package org.example.wecambackend.dto.auto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "이메일", example = "college@wecampus.kr", required = true)
    private String email;

    @Schema(description = "비밀번호", example = "college!01", required = true)
    private String password;
}

