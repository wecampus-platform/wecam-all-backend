package org.example.wecambackend.dto.requestDTO;

import lombok.*;
import org.example.model.enums.OrganizationType;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationRequestRequest {

    // 학교 선택한 경우 (학교는 select 기반 → schoolOrg 찾기용으로 사용 , 학교도 선택 안했을 수도 있음.)
    private Long selectSchoolId;

    // 단과대 (있으면 select)
    private Long selectCollegeOrganizationId;

    // 학과 (있으면 select, 없으면 input)
    private Long selectDepartmentOrganizationId;

    // 학교 직접 입력 (학교 select가 안된 경우)
    private String inputSchoolName;

    // 단과대 직접 입력 (단과부터 없는 경우 → input 으로 입력)
    private String inputCollegeName;

    // 학과 직접 입력 (학과부터 없는 경우 → input 으로 입력)
    private String inputDepartmentName;

    //학생회 이름 (ex.세론, 정의)
    private String councilName;

    //학생회 계층 구분 (총학생회 / 단과대 / 학과) 우선 3계층만.
    private OrganizationType organizationType;
}
