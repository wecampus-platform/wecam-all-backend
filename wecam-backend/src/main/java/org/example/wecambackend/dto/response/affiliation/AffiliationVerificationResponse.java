package org.example.wecambackend.dto.response.affiliation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
//학생회에게 보여주는 인증서 내용
public class AffiliationVerificationResponse {
    private Long userId;                // affiliation_certification 기본키

    private String ocrUserName;                     // ocr_user_name
    private String ocrSchoolName;                   // ocr_school_name
    private String ocrOrganizationName;             // ocr_organization_name
    private String ocrEnrollYear;                   // ocr_enroll_year
    private String inputUserName;                   // 가입시 등록했던 유저 이름
    private String inputOrganizationName;           // 가입시 등록했던 조직 이름
    private String inputSchoolName;                 // 가입시 등록했던 학교 이름
    private String inputEnrollYear;                 // 가입시 입력했던 입학년도
    private LocalDateTime issuanceDate;             // 증명서 발급 일자
    private String studentEmail;                    // 사용자 이메일 (user join 필요)
    private String authenticationType;              // enum: 'NEW_STUDENT', 'CURRENT_STUDENT'
    private String ocrResult;                       // enum: 'SUCCESS', 'FAILURE', 'UNCLEAR'
    private String status;                          // enum: 'PENDING', 'APPROVED', ...
    private LocalDateTime requestedAt;              // 요청 시각
    private String uploadedFileUrl;                 // affiliation_file.file_path → 이미지 등 보여주기 위해

    public AffiliationVerificationResponse(
            Long userId,
            String authenticationType,
            String userName,
            String schoolName,
            String organizationName,
            String enrollYear,
            String inputSchoolName,
            String inputOrganizationName,
            String inputEnrollYear,
            String inputUserName,
            String ocrResult,
            String status,
            LocalDateTime requestedAt,
            String uploadedFileUrl,
            LocalDateTime issuanceDate
            ) {
        this.userId = userId;
        this.authenticationType = authenticationType;
        this.ocrUserName = userName;
        this.ocrSchoolName = schoolName;
        this.ocrOrganizationName = organizationName;
        this.ocrEnrollYear = enrollYear;
        this.ocrResult = ocrResult;
        this.status = status;
        this.requestedAt = requestedAt;
        this.uploadedFileUrl = uploadedFileUrl;
        this.inputEnrollYear = inputEnrollYear;
        this.inputOrganizationName = inputOrganizationName;
        this.inputUserName = inputUserName;
        this.inputSchoolName = inputSchoolName;
        this.issuanceDate = issuanceDate;
    }

}
