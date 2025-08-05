package org.example.wecambackend.util.organization;

import org.example.model.enums.OrganizationType;
import org.example.model.organization.Organization;
import org.example.model.organization.OrganizationRequest;

/**
 * 조직 계층 정보(단과대/학과명 등) 추출 및 소속 문자열 빌드 유틸리티
 */
public class OrganizationHierarchyUtil {

    /**
     * 주어진 조직에서 단과대 이름을 추출
     */
    public static String extractCollegeName(Organization organization) {
        if (organization.getOrganizationType() == OrganizationType.COLLEGE) {
            return organization.getOrganizationName();
        } else if (organization.getParent() != null && organization.getParent().getOrganizationType() == OrganizationType.COLLEGE) {
            return organization.getParent().getOrganizationName();
        }
        return null;
    }

    /**
     * 주어진 조직에서 학과 이름을 추출
     */
    public static String extractDepartmentName(Organization organization) {
        if (organization.getOrganizationType() == OrganizationType.DEPARTMENT) {
            return organization.getOrganizationName();
        } else if (organization.getOrganizationType() == OrganizationType.MAJOR) {
            Organization parent = organization.getParent();
            while (parent != null) {
                if (parent.getOrganizationType() == OrganizationType.DEPARTMENT) {
                    return parent.getOrganizationName();
                }
                parent = parent.getParent();
            }
        }
        return null;
    }

    /**
     * 조직 계층을 기반으로 소속 문자열을 생성 (학교, 단과대, 학과)
     */
    public static String buildAffiliationString(Organization organization) {
        StringBuilder affiliation = new StringBuilder();

        if (organization.getUniversity() != null) {
            affiliation.append(organization.getUniversity().getSchoolName());
        }

        String collegeName = extractCollegeName(organization);
        if (collegeName != null) {
            if (affiliation.length() > 0) affiliation.append(" ");
            affiliation.append(collegeName);
        }

        String departmentName = extractDepartmentName(organization);
        if (departmentName != null) {
            if (affiliation.length() > 0) affiliation.append(" ");
            affiliation.append(departmentName);
        }

        return affiliation.toString();
    }

    /**
     * 워크스페이스 생성 요청 기반으로 소속 문자열을 생성 (학교, 단과대, 학과)
     */
    public static String buildAffiliationString(OrganizationRequest request) {
        StringBuilder affiliation = new StringBuilder();

        if (request.getSchoolName() != null) {
            affiliation.append(request.getSchoolName());
        }
        if (request.getCollegeName() != null) {
            if (affiliation.length() > 0) affiliation.append(" ");
            affiliation.append(request.getCollegeName());
        }
        if (request.getDepartmentName() != null) {
            if (affiliation.length() > 0) affiliation.append(" ");
            affiliation.append(request.getDepartmentName());
        }

        return affiliation.toString();
    }

    /** OrganizationType(Enum)에 따른 계층 Level 반환 */
    public static int getLevelFromOrganizationType(OrganizationType type) {
        return switch (type) {
            case UNIVERSITY -> 0;
            case COLLEGE -> 1;
            case DEPARTMENT -> 2;
            case MAJOR -> 3;
        };
    }
}