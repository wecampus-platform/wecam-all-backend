package org.example.wecambackend.dto.responseDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.model.enums.UserRole;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//TODO: 학생회비 납부 이력을 생각하지못했다. 우선 현재 구현 단계 아니니 그냥 두지만 나중에 넣어야 됨.
//User Mypage에 들어갈 기본 정보들
public class UserInfoResponse {
    private Long userPkId;
    private Long organizationId;
    private Long phoneNumber;
    private Long email;
    private Boolean isAuthentication;
    private UserRole userRole;
}
