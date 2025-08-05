package org.example.wecambackend.dto.responseDTO;

import lombok.Builder;
import org.example.model.enums.MemberRole;

@Builder
public class CouncilCompositionResponse {
    private String userName;
    private MemberRole userCouncilRole;
    private Long userId;

    private String enrollYear;
    private String DepartmentName;
    private String DepartmentRoleName;
}
