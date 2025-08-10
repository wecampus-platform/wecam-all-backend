package org.example.wecambackend.dto.request.organization;


import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.RequestStatus;
import org.example.model.organization.Organization;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrganizationRequest {
    private Long requestId;
    private String email;
    //    private String phoneNumber;
    private RequestStatus requestStatus;
    private LocalDateTime createdAt;
    private String councilName;
    private String schoolName; // input 값이 들어가거나 아니거나
    private String schoolId_Sel; //선택된 schoolId 값이 들어가거나 없거나
    private String target_OrgName;

    public OrganizationRequest(Long requestId, String email, RequestStatus requestStatus,
                                  LocalDateTime createdAt, String councilName, String schoolName,
                                  Organization targetOrganization) {
        this.requestId = requestId;
        this.email = email;
        this.requestStatus = requestStatus;
        this.createdAt = createdAt;
        this.councilName = councilName;
        this.schoolName = schoolName;

        if (targetOrganization != null) {
            this.target_OrgName = targetOrganization.getOrganizationName();
        }
    }
}
