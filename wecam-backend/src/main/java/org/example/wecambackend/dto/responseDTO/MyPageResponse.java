package org.example.wecambackend.dto.responseDTO;


import lombok.*;
import org.example.model.enums.AcademicStatus;
import org.example.model.enums.UserRole;

import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponse {
    private String username; //이름
    private String phoneNumber; //전화번호 - 연락처
    private String userEmail; //아이디 이메일
    private Long universityId; //학교 아이디
    private Long organizationId; // 학교 단과대학 학과 이름
    private AcademicStatus academicStatus; //학적 상태
    private UserRole role;
    private int student_grade;
    private Boolean isAuthentication; //소속인증을 완료했는지 여부
    private Boolean isCouncilFee; // 학생회비 인증 완료했는지 여부
    private String nickName; //닉네임
    private String studentId; //학번

    private List<String> organizationHierarchyList; // 전체 조직 이름 뜨게 하기


}
