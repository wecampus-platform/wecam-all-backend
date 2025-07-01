package org.example.wecamadminbackend.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.model.organization.Organization;
import org.example.model.enums.RequestStatus;

import java.time.LocalDateTime;


@Getter
@Setter
public class OrganizationRequestDTO {
    private Long requestId;
    private String email;
//    private String phoneNumber;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private String councilName;
    private String schoolName;
    private String schoolId_Sel;
    private String target_OrgName;

    public OrganizationRequestDTO(Long requestId, String email, RequestStatus status,
                                  LocalDateTime createdAt, String councilName, String schoolName,
                                  Organization targetOrganization) {
        this.requestId = requestId;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.councilName = councilName;
        this.schoolName = schoolName;

        if (targetOrganization != null) {
            this.target_OrgName = targetOrganization.getOrganizationName();
        }
    }
}

