package org.example.wecambackend.dto.response.councilMember;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSelectionResponse {
    
    @Schema(description = "학생회 멤버 ID", example = "123")
    private Long councilMemberId;
    
    @Schema(description = "구성원 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "부서명", example = "기획부")
    private String departmentName;
    
    @Schema(description = "프로필 썸네일 이미지 URL", example = "/uploads/PROFILE_THUMB/abc123.jpg")
    private String profileThumbnailUrl;
}
