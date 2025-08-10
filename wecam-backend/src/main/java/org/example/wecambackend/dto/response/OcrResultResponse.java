package org.example.wecambackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultResponse {
    private String userName;
    private String schoolName;
    private String orgName;
    private String enrollYear;
    private int schoolGrade;
    private LocalDateTime issuanceDate;
}
