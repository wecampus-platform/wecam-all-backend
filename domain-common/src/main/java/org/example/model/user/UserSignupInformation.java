package org.example.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseTimeEntity;

//TODO: UserSignupInformation 을 두개로 분기처리 하는게 더 나을 수도 있어 보임.
@Entity
@Table(name = "user_signup_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupInformation extends BaseTimeEntity {
//회원가입 시 저장된 정보 (user_signup_information)
    @Id
    @Column(name = "user_pk_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_pk_id")
    private User user;

    //select 는 선택했을 경우
    @Column(name = "select_organization_id")
    private Long selectOrganizationId;

    @Column(name = "select_school_id")
    private Long selectSchoolId;

    //직접 입력
    @Column(name = "enroll_year", length = 4)
    private String enrollYear;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    //input은 대표자 가입 경우
    @Column(name = "input_college_name", length = 20)
    private String inputCollegeName;

    @Column(name = "input_school_name", length = 20)
    private String inputSchoolName;

    @Column(name = "input_department_name", length = 20)
    private String inputDepartmentName;


    //대표자 인지 아닌지 해당 값으로 INPUT 할지 SELECT 할지 정함.
    @Column(name = "is_make_workspace")
    @Builder.Default
    private Boolean isMakeWorkspace = false;

}
