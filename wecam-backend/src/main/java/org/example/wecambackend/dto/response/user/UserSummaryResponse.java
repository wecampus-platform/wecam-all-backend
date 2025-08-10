package org.example.wecambackend.dto.response.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.AcademicStatus;

@Getter @Setter
@Builder
public class UserSummaryResponse {
    private Long userPkId; // 조회 , 제명 , 상세보기 등등 해야됨.
    private String userName; // 학생 이름
    private String studentNumber; // 2자리만..(맨 앞자리 4개에서 그 4자리 중 뒤에 2개..)
    private String departmentName; //학과 이름
    private Integer grade; // 학년 , 1자리임.
    private AcademicStatus academicStatus; // 재학 , 휴학 여부
}
