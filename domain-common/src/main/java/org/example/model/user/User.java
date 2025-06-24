package org.example.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.Organization;
import org.example.model.University;
import org.example.model.enums.UserRole;

import java.time.LocalDateTime;

/**
 * 사용자 정보를 저장하는 엔티티 클래스.
 * 인증, 가입일, 조직 소속, 권한 등을 관리합니다.
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** 사용자 PK (Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pk_id")
    private Long userPkId;

    /** 사용자 이메일 (고유 값) */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /** 계정 만료일 (가입일 기준 30일 후 자동 설정됨) */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /** 계정 생성일 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 마지막 업데이트 시각 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 인증 여부 (ex. 전화번호 인증 등) */
    @Column(name = "is_authentication", nullable = false)
    private boolean isAuthentication;

    /** 이메일 인증 여부 */
    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    /** 사용자 민감 정보 (비밀번호 등) - 1:1 매핑 */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserPrivate userPrivate;

    /** 사용자 일반 정보 (이름, 학번 등) - 1:1 매핑 */
    //기본적으로 nullable = true --> 소속 인증 후 연결
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserInformation userInformation;

    /** 소속된 조직 (학과, 단과대 등) - 다대일 매핑 */
    //기본적으로 nullable = true --> 소속 인증 후 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization")
    private Organization organization;

    @Column(name = "organization_id")
    private Long organizationId;

    /** 사용자 역할 (UserRole 참고) */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;


    /**사용자 활동여부 **/
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    /** 시스템 슈퍼유저 여부 (True인 경우 플랫폼 전체 관리 권한) */
    @Column(name = "is_superuser", nullable = false)
    private Boolean isSuperuser;


    //추가됨. ocr 인증 마치고 소속 인증 승인 받으면 들어가는 데이터
    @Column(name="enroll_year",length = 4)
    private String enrollYear;

    /** 최초 생성 시 자동 설정되는 값들 */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.expiresAt = now.plusDays(30); // 가입일 기준 30일 유효 //TODO: 추후 role 업데이트된 사람들은 ExpiresAT 없애야 함.
        this.isAuthentication = false;
        this.isEmailVerified = false;
        if (this.isSuperuser == null) this.isSuperuser = false;
        if (this.role ==null) this.role = UserRole.UNAUTH; // 처음 가입시 role이 null이면 UNAUTH로 설정
    }

    /** 업데이트 시 자동으로 수정 시각 갱신 */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private University university;

    public void setOrganization(Organization organization) {
        this.organization = organization;
        if (organization == null) {
            throw new IllegalArgumentException("organization은 null일 수 없습니다.");
        }
        this.organizationId =  organization.getOrganizationId();
    }
}
