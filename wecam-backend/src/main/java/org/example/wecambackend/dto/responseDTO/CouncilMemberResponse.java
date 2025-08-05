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

    public CouncilMemberResponse(String userName, MemberRole userCouncilRole, Long userId, ExitType exitType, String expulsionReason) {
        this.userName = userName;
        this.userCouncilRole = userCouncilRole;
        this.userId = userId;
        this.exitType = exitType;
        this.expulsionReason = expulsionReason;
    }
}
