package org.example.wecambackend.dto.responseDTO;

import lombok.*;
import org.example.model.enums.MemberRole;
import org.example.model.enums.ExitType;

@Getter
@Setter
@NoArgsConstructor
public class CouncilMemberResponse {
    private String userName;
    private MemberRole userCouncilRole;
    private Long userId;
    private ExitType exitType;
    private String expulsionReason;
//    private String departmentRole;

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

    public CouncilMemberResponse(String userName, MemberRole userCouncilRole, Long userId, ExitType exitType, String expulsionReason) {
        this.userName = userName;
        this.userCouncilRole = userCouncilRole;
        this.userId = userId;
        this.exitType = exitType;
        this.expulsionReason = expulsionReason;
    }
}
