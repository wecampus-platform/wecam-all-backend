package org.example.wecambackend.dto.auth.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshJwtReq {

    @NotBlank(message = "refresh token을 입력해주세요.")
    private String refreshToken;
}