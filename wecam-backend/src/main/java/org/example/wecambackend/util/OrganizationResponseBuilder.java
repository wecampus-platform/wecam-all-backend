package org.example.wecambackend.util;

import org.example.model.council.Council;
import org.example.model.enums.OrganizationType;
import org.example.model.organization.Organization;
import org.example.model.organization.OrganizationRequest;
import org.example.model.user.User;
import org.example.wecambackend.dto.responseDTO.SubOrganizationRequestListResponse;
import org.example.wecambackend.dto.responseDTO.SubOrganizationResponse;

/**
 * 조직 관련 응답 객체 생성을 위한 공통 유틸리티 클래스
 * buildSubOrganizationResponse와 동일한 로직으로 단과대/학부명 분리 및 대표자 정보 추출
 */
public class OrganizationResponseBuilder {

    /**
     * OrganizationRequest를 SubOrganizationRequestListResponse로 변환
     * @param request 워크스페이스 생성 요청
     * @return 변환된 응답 객체
     */
    public static SubOrganizationRequestListResponse buildSubOrganizationRequestListResponse(OrganizationRequest request) {
        // 단과대명과 학부명 추출
        String collegeName = extractCollegeName(request);
        String departmentName = extractDepartmentName(request);
        
        // 대표자 정보 추출
        String representativeName = extractRepresentativeName(request.getUser());
        String representativeProfileImage = extractRepresentativeProfileImage(request.getUser());
        
        return SubOrganizationRequestListResponse.builder()
                .requestId(request.getRequestId())
                .organizationType(request.getOrganizationType())
                .collegeName(collegeName)
                .departmentName(departmentName)
                .councilName(request.getCouncilName())
                .representativeProfileImage(representativeProfileImage)
                .representativeName(representativeName)
                .createdAt(request.getCreatedAt())
                .build();
    }

    /**
     * Council을 SubOrganizationResponse로 변환 (기존 buildSubOrganizationResponse와 동일)
     * @param council 학생회 정보
     * @return 변환된 응답 객체
     */
    public static SubOrganizationResponse buildSubOrganizationResponse(Council council) {
        Organization organization = council.getOrganization();
        
        // 학생회 단위 결정
        String organizationType = organization.getOrganizationType().name();
        
        // 단과대 이름과 학과 이름 추출
        String collegeName = null;
        String departmentName = null;
        
        if (organization.getOrganizationType() == OrganizationType.COLLEGE) {
            // 단과대인 경우
            collegeName = organization.getOrganizationName();
        } else if (organization.getOrganizationType() == OrganizationType.DEPARTMENT) {
            // 학과인 경우 상위 조직이 단과대
            collegeName = organization.getParent() != null ? organization.getParent().getOrganizationName() : null;
            departmentName = organization.getOrganizationName();
        }
        
        return SubOrganizationResponse.builder()
                .councilId(council.getId())
                .organizationType(organizationType)
                .collegeName(collegeName)
                .departmentName(departmentName)
                .organizationName(council.getCouncilName())
                .representativeProfileImage(extractRepresentativeProfileImage(council.getUser()))
                .representativeName(extractRepresentativeName(council.getUser()))
                .workspaceCreatedAt(council.getStartDate())
                .build();
    }

    /**
     * 단과대명 추출 (buildSubOrganizationResponse와 동일한 로직)
     */
    private static String extractCollegeName(OrganizationRequest request) {
        if (request.getOrganizationType() == OrganizationType.COLLEGE) {
            // 단과대인 경우
            return request.getCollegeName();
        } else if (request.getOrganizationType() == OrganizationType.DEPARTMENT) {
            // 학부인 경우 단과대명도 함께 설정
            return request.getCollegeName();
        }
        return null;
    }

    /**
     * 학부명 추출 (buildSubOrganizationResponse와 동일한 로직)
     */
    private static String extractDepartmentName(OrganizationRequest request) {
        if (request.getOrganizationType() == OrganizationType.DEPARTMENT) {
            // 학부인 경우에만 학부명 설정
            return request.getDepartmentName();
        }
        return null;
    }

    /**
     * 대표자 이름 추출
     */
    private static String extractRepresentativeName(User user) {
        return user != null ? user.getName() : null;
    }

    /**
     * 대표자 프로필 이미지 추출
     */
    private static String extractRepresentativeProfileImage(User user) {
        if (user != null && user.getUserInformation() != null) {
            return user.getUserInformation().getProfileImagePath();
        }
        return null;
    }
} 