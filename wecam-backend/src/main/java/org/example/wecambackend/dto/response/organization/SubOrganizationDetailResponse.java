package org.example.wecambackend.dto.response.organization;

import lombok.Builder;
import lombok.Getter;
import org.example.wecambackend.dto.response.councilMember.CouncilMemberDetailResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SubOrganizationDetailResponse {

    // 하위 학생회 ID
    private Long councilId;

    // 조직 타입
    private String organizationType;

    // 학생회 소속 (단과대/학과명)
    private String affiliation;

    // 학생회 이름
    private String organizationName;

    // 대표자 이름
    private String representativeName;

    // 대표자 프로필 사진
    private String representativeProfileImage;

    // 대표자 전화번호
    private String representativePhoneNumber;

    // 워크스페이스 생성일시
    private LocalDateTime workspaceCreatedAt;

    // 학생회원 목록
    private List<CouncilMemberDetailResponse> members;
} 