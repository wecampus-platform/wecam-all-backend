package org.example.wecambackend.dto.request.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentRegisterRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "입학년도는 필수입니다.")
    private String enrollYear;

    @NotNull(message = "학교 선택은 필수입니다.")
    private Long selectSchoolId;

    @NotNull(message = "학과 선택은 필수입니다.")
    private Long selectOrganizationId;
}
