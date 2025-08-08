package org.example.wecambackend.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "일반 학생 검색 결과 DTO")
public class StudentSearchResponse {
    
    @Schema(description = "사용자 ID")
    private Long userPkId;
    
    @Schema(description = "사용자 이름")
    private String name;
    
    @Schema(description = "학번 (입학년도 4자리)")
    private String studentNumber;
    
    @Schema(description = "학부명")
    private String organizationName;
    
    @Schema(description = "학년")
    private Integer grade;
    
    @Schema(description = "재학/휴학 여부")
    private String academicStatus;
    
    @Schema(description = "사용자 프로필 사진 경로")
    private String profileImagePath;
}
