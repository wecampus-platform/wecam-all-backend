package org.example.wecambackend.dto.requestDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class StudentRegisterRequest {
    private String email;
    private String password;
    private String phoneNumber;

    private String name;
    private String enrollYear;

    //
    private Long selectSchoolId;
    private Long selectOrganizationId;
}