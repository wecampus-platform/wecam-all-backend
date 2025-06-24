package org.example.wecamadminbackend.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresidentSignupInfoDTO {
    private Long userId;

    private String enrollYear;
    private String userName;
    private Boolean isWorkspace;
    //대표자일때 사용 가능해짐.
    private String inputSchoolName;
    private String inputCollegeName;
    private String inputDepartmentName;

    //대표자일 때도 선택할 경우가 있으니까
    private Long selectSchoolId;
    private Long selectOrganizationId;

}
