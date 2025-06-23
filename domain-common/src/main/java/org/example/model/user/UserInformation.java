package org.example.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.University;
import org.example.model.common.BaseTimeEntity;
import org.example.model.enums.AcademicStatus;

@Entity
@Table(name = "user_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInformation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "information_pk_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk_id", nullable = false)
    private User user;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "school_email", unique = true)
    private String schoolEmail;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private University university;

    @Column(name = "student_grade")
    private int studentGrade; //학년

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_status")
    private AcademicStatus academicStatus;

    @Column(name = "nickname", length = 20, unique = true)
    private String nickname;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "is_authentication", nullable = false)
    private Boolean isAuthentication;

    @Column(name = "is_council_fee", nullable = false)
    private Boolean isCouncilFee;

}
