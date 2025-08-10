package org.example.wecambackend.dto.response.council;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@Schema(description = "학생회 관리자용 유저 정보 조회 DTO")
public class CouncilVisibleUserDTO implements Serializable {

    @Schema(description = "유저 고유 ID", example = "12")
    private Long userId;

    @Schema(description = "유저 이름", example = "김철수")
    private String name;

    @Schema(description = "유저 이메일", example = "kimcs@example.com")
    private String email;

    @Schema(description = "유저 전화번호 (권한에 따라 마스킹될 수 있음)", example = "010-1234-5678")
    private String phoneNumber;

    // 생성자, getter, setter 등 생략 가능
}
