package org.example.wecambackend.dto.responseDTO;

import lombok.*;
import org.example.model.enums.AuthenticationType;
import org.example.model.enums.OcrResult;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class AffiliationCertificationSummaryResponse {
    private Long userId;                // affiliation_certification 기본키

    private String inputUserName;
    private String inputOrganizationName;
    private String inputEnrollYear;
    private AuthenticationType authenticationType;
    private OcrResult ocrResult;
    private String status;
    private LocalDateTime requestedAt;

}
