package org.example.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.University;
import org.example.model.common.BaseEntity;
import org.example.model.enums.AcademicStatus;

@Entity
@Table(name = "user_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInformation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "information_pk_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk_id", nullable = false)
    private User user;

    // 학번
    @Column(name = "student_id")
    private String studentId;

    // 학교 이메일
    @Column(name = "school_email", unique = true)
    private String schoolEmail;

    // 대학
    @ManyToOne
    @JoinColumn(name = "school_id")
    private University university;

    // 학년
    @Column(name = "student_grade")
    private int studentGrade;

    // 학적 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "academic_status")
    private AcademicStatus academicStatus;

    // 별명
    @Column(name = "nickname", length = 20, unique = true)
    private String nickname;


    // 소속 인증 여부
    @Column(name = "is_authentication", nullable = false)
    private Boolean isAuthentication;

    // 학생회비 납부 여부
    @Builder.Default
    @Column(name = "is_council_fee", nullable = false)
    private Boolean isCouncilFee = false;

    // 사용자 프로필 이미지 상대 경로 (지금은 일단 로컬 저장 경로)
    @Column(name = "profile_image_path", length = 255)
    private String profileImagePath;

}
