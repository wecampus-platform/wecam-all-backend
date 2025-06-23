package org.example.wecambackend.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String accessToken;

    private String refreshToken;

}