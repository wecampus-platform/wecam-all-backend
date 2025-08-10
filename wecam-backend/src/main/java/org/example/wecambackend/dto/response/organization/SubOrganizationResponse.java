package org.example.wecambackend.dto.response.organization;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubOrganizationResponse {

    // 하위 학생회 ID
    private Long councilId;

    // 하위 학생회 단위
    private String organizationType;

    // 단과대 이름
    private String collegeName;

    // 학과 이름
    private String departmentName;

    // 조직명 (학생회 이름)
    private String organizationName;

    // 대표자 프로필 사진
    private String representativeProfileImage;

    // 대표자
    private String representativeName;

    // 워크스페이스 생성일
    private LocalDateTime workspaceCreatedAt;
} 