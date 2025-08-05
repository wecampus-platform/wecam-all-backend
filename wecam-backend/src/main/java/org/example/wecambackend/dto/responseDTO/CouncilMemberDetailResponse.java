package org.example.wecambackend.dto.responseDTO;

import lombok.Builder;
import lombok.Getter;
import org.example.model.enums.MemberRole;

@Getter
@Builder
public class CouncilMemberDetailResponse {
    
    // 사용자 ID
    private Long userId;
    
    // 사용자 이름
    private String userName;
    
    // 프로필 사진
    private String profileImage;
    
    // 학생회 내 역할
    private MemberRole memberRole;
    
    // 부서명
    private String departmentName;
    
    // 부서 내 직책
    private String departmentRoleName;
} 