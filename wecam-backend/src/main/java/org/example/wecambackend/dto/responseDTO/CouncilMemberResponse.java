package org.example.wecambackend.dto.responseDTO;

import lombok.*;
import org.example.model.enums.MemberRole;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouncilMemberResponse {
    private String userName;
    private MemberRole userCouncilRole;
    private Long userId;
    private String departmentRole;

}
