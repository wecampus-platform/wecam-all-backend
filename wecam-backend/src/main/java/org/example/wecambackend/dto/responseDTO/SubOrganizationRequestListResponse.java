package org.example.wecambackend.dto.responseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.OrganizationType;
import org.example.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SubOrganizationRequestListResponse {

    // 워크스페이스 생성 요청 ID
    private Long requestId;

    // 학생회 단위
    private OrganizationType organizationType;

    // 단과대명
    private String collegeName;
    
    // 학부명 (학부 학생회인 경우에만 값이 있음)
    private String departmentName;

    // 학생회 이름
    private String councilName;

    // 대표자 프로필 사진
    private String representativeProfileImage;

    // 대표자 명
    private String representativeName;

    // 생성일시
    private LocalDateTime createdAt;
} 