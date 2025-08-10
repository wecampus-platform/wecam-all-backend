package org.example.wecambackend.dto.request.councilMember;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Schema(description = "학생회 구성원 검색 요청 DTO")
public class CouncilMemberSearchRequest {
    
    @Schema(description = "검색할 이름", example = "김철수", required = true)
    @NotBlank(message = "검색할 이름을 입력해주세요.")
    private String name;
}
