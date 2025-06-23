package org.example.wecambackend.dto.auto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String role;
    private Boolean auth;
//    private String name; -- 할지 말지 고민
}
