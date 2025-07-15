package org.example.model.affiliation;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.organization.Organization;
import org.example.model.University;
import org.example.model.user.User;
import org.example.model.enums.AuthenticationStatus;
import org.example.model.enums.AuthenticationType;
import org.example.model.enums.OcrResult;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "affiliation_certification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliationCertification {

    // 소속 인증 ID
    @EmbeddedId
    private AffiliationCertificationId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pk_upload_userid")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", insertable = false, updatable = false)
    private AuthenticationType authenticationType;

    @Column(name="ocr_school_grade",nullable = false)
    private int ocrschoolGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "ocr_result", nullable = false)
    private OcrResult ocrResult;

    @Column(name = "ocr_user_name", length = 20)
    private String ocrUserName;

    @Column(name = "ocr_enroll_year", length = 4)
    private String ocrEnrollYear;

    @Column(name = "ocr_school_name", length = 20)
    private String ocrSchoolName;

    @Column(name = "ocr_organization_name", length = 20)
    private String ocrOrganizationName;

    @Column(name = "sel_organization_name", length = 20)
    private String selOrganizationName;

    @Column(name = "sel_school_name", length = 20)
    private String selSchoolName;

    @Column(name = "sel_enroll_year", length = 20)
    private String selEnrollYear;

    @Column(name = "issuance_date", length = 20)
    private LocalDateTime issuanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuthenticationStatus status;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "user_name", length = 20)
    private String username;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // 소속 인증 검토자
    @ManyToOne
    @JoinColumn(name = "pk_reviewer_userid")
    private User reviewUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_pk_id")
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_pk_id")
    private Organization organization;


    //파일 삭제 동시
    @OneToMany(mappedBy = "affiliationCertification", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AffiliationFile> files;


    //소속 인증 승인 처리
    public void approve(User reviewer) {
        this.status = AuthenticationStatus.APPROVED;
        this.reviewUser = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }

    //소속인증 처리 확인
    public boolean isApprovable() {
        return this.status == AuthenticationStatus.PENDING;
    }


}
