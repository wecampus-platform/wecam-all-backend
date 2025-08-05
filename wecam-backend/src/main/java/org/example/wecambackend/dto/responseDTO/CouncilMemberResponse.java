package org.example.wecambackend.dto.responseDTO;

import lombok.*;
import org.example.model.enums.MemberRole;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class CouncilMemberResponse {
    private String userName;
    private MemberRole userCouncilRole;
    private Long userId;

    //department 별 분기 조회 시 필요함.
    private Long departmentRoleId;
    private Long departmentId;
    private String departmentRole;
    private String departmentName;

    public CouncilMemberResponse(String name, MemberRole memberRole, Long userPkId,Long departmentId,Long departmentRoleId,
                                 String departmentName, String roleName) {
        this.userName = name;
        this.userCouncilRole = memberRole;
        this.userId = userPkId;
        this.departmentId = departmentId;
        this.departmentRoleId = departmentRoleId;
        this.departmentName = departmentName; // null 허용
        this.departmentRole = roleName;             // null 허용
    }

}
