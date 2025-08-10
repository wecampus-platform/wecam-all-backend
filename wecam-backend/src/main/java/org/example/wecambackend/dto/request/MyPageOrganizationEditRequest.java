package org.example.wecambackend.dto.request;

import lombok.Getter;
import org.example.model.enums.AcademicStatus;

@Getter
public class MyPageOrganizationEditRequest {
    private String studentNumber;
    private Integer schoolGrade;
    private AcademicStatus academicStatus;
}
