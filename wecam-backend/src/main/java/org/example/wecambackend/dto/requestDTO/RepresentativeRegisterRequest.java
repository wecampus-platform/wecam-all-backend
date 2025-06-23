package org.example.wecambackend.dto.requestDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class RepresentativeRegisterRequest {

    private String email;

    //user_sign_up information 테이블에 들어감.
    private String name;
    private String enrollYear;


    //user private 테이블에 들어감.
    private String password;
    private String phoneNumber;

    //대표자일때 사용 가능해짐.
    private String inputSchoolName;
    private String inputCollegeName;
    private String inputDepartmentName;

    //대표자일 때도 선택할 경우가 있으니까
    private Long selectSchoolId;
    private Long selectOrganizationId;
    // 위에 두개는 input 일 경우 해당 값을 테이블에 매핑.
    // select 일때 해당 값을 테이블에 매핑.
    // 우선적으로 계층이 3개일 경우로 봄.
}
