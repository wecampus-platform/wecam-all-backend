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


    public CouncilMemberResponse(String userName, MemberRole userCouncilRole,Long departmentId,Long departmentRoleId, Long userId, ExitType exitType, String expulsionReason,
                                 String departmentName, String departmentRole) {
        this.userName = userName;
        this.userCouncilRole = userCouncilRole;
        this.userId = userId;
        this.departmentId = departmentId;
        this.departmentRoleId = departmentRoleId;
        this.exitType = exitType;
        this.expulsionReason = expulsionReason;
        this.departmentName = departmentName;
        this.departmentRole = departmentRole;
    }
}
