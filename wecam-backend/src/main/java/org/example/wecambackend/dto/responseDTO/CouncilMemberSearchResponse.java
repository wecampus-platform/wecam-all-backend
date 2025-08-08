package org.example.wecambackend.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "학생회 구성원 검색 결과 DTO")
public class CouncilMemberSearchResponse {
    
    @Schema(description = "사용자 ID")
    private Long userPkId;

    @Schema(description = "학생회 구성원 ID")
    private Long councilMemberPkId;
    
    @Schema(description = "사용자 이름")
    private String name;
    
    @Schema(description = "학번 (입학년도 뒷자리)")
    private String studentNumber;
    
    @Schema(description = "학부명")
    private String organizationName;
    
    @Schema(description = "부서명")
    private String departmentName;
    
    @Schema(description = "직책 (부장, 부원 등)")
    private String position;
    
    @Schema(description = "사용자 프로필 사진 경로")
    private String profileImagePath;
}
