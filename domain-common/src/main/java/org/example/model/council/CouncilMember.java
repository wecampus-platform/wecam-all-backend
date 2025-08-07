package org.example.model.council;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
import org.example.model.user.User;
import org.example.model.enums.MemberRole;
import org.example.model.enums.ExitType;

import java.time.LocalDateTime;

@Entity
@Table(name = "council_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 학생회 구성원
public class CouncilMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "council_member_pk_id")
    private Long id;

    // 소속된 학생회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk_id")
    private User user;

    // 소속된 부서
    @ManyToOne(fetch = FetchType.LAZY)
    private CouncilDepartment department;

    @ManyToOne(fetch = FetchType.LAZY)
    private CouncilDepartmentRole departmentRole;

    // 학생회 내 역할
    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private MemberRole memberRole;

    // 탈퇴/만료 관련 필드
    @Enumerated(EnumType.STRING)
    @Column(name = "exit_type", nullable = false)
    private ExitType exitType = ExitType.ACTIVE;

    // 제명 사유 (제명인 경우에만 사용)
    @Column(name = "expulsion_reason", length = 500)
    private String expulsionReason;

    // 탈퇴/만료 날짜
    @Column(name = "exit_date")
    private LocalDateTime exitDate;

}
