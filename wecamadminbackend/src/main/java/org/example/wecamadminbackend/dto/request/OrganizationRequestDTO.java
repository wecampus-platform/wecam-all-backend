package org.example.wecamadminbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.model.Organization;
import org.example.model.enums.RequestStatus;
import org.example.wecamadminbackend.util.PhoneEncryptor;

import java.time.LocalDateTime;

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

    public OrganizationRequestDTO(Long requestId, String email, RequestStatus status, LocalDateTime createdAt, String councilName, String schoolName, String org,String target_OrgName) {
        this.requestId = requestId;
        this.email = email;
        this.status = status;
        this.createdAt =createdAt;
        this.councilName = councilName;
        this.schoolName = schoolName;
        this.schoolId_Sel = org;
        this.target_OrgName = target_OrgName;
    }
}

