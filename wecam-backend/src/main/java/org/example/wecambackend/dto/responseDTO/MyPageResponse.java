package org.example.wecambackend.dto.responseDTO;


import lombok.*;
import org.example.model.enums.AcademicStatus;
import org.example.model.enums.UserRole;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponse {
    // 이름
    private String username;

    // 전화번호 - 연락처
    private String phoneNumber;

    // 아이디 이메일
    private String userEmail;

    // 학교 아이디
    private Long universityId;

    // 학교 단과대학 학과 이름
    private Long organizationId;

    // 학적 상태
    private AcademicStatus academicStatus;

    // 역할
    private UserRole role;

    // 학년
    private int student_grade;

    // 소속인증을 완료했는지 여부
    private Boolean isAuthentication;

    // 학생회비 인증 완료했는지 여부
    private Boolean isCouncilFee;

    // 닉네임
    private String nickName;

    // 학번
    private String studentId;

    // 전체 조직 이름 뜨게 하기
    private List<String> organizationHierarchyList;

    // 프로필 원본 url
    private String profileImageUrl;

    // 프로필 썸네일 url
    private String profileThumbnailUrl;


}
