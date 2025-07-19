package org.example.model.invitation;


import jakarta.persistence.*;
import lombok.*;
import org.example.model.council.Council;
import org.example.model.organization.Organization;
import org.example.model.common.BaseTimeEntity;
import org.example.model.enums.CodeType;

import org.example.model.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invitation_code")
public class InvitationCode extends BaseTimeEntity {

    // 초대코드 pk키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_pk_id")
    private Long id;

    // 초대코드를 발급한 학생회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;

    // 초대코드를 만든 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk_id")
    private User user;

    // 초대 코드
    @Column(name="code",nullable = false, length = 20, unique = true)
    private String code;

    // 초대코드를 발급한 조직
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // council_member, student_member
    @Enumerated(EnumType.STRING)
    @Column(name = "code_type", nullable = false)
    private CodeType codeType;

    // active 한 초대코드인지
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 초대코드 만료일
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    // 사용 시 유효성 검사 예시
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }


}

