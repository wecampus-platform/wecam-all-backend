package org.example.wecambackend.service.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.enums.OrganizationType;
import org.example.model.organization.OrganizationRequest;
import org.example.model.organization.OrganizationRequestFile;
import org.example.model.organization.Organization;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;
import org.example.model.user.User;
import org.example.model.user.UserSignupInformation;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.responseDTO.*;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.repos.council.CouncilMemberRepository;
import org.example.wecambackend.repos.council.CouncilRepository;
import org.example.wecambackend.repos.organization.OrganizationRequestFileRepository;
import org.example.wecambackend.repos.organization.OrganizationRequestRepository;
import org.example.wecambackend.repos.user.UserSignupInformationRepository;
import org.example.wecambackend.util.organization.OrganizationHierarchyUtil;
import org.example.wecambackend.util.user.UserInfoExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrganizationService {

    private final OrganizationRequestRepository organizationRequestRepository;
    private final OrganizationRequestFileRepository organizationRequestFileRepository;
    private final CouncilRepository councilRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final UserSignupInformationRepository userSignupInformationRepository;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    /** 워크스페이스 생성 요청 상세 조회 */
    @Transactional
    public SubOrganizationRequestDetailResponse getOrganizationRequestDetail(Long requestId, String councilName) {
        Council currentCouncil = getCurrentCouncilWithOrganization();

        OrganizationRequest request = organizationRequestRepository.findByIdWithUserAndOrganization(requestId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ORGANIZATION_NOT_FOUND));

        validateRequestAccess(currentCouncil.getOrganization(), request);

        User user = request.getUser();
        UserSignupInformation userInfo = userSignupInformationRepository.findByUser_UserPkId(user.getUserPkId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        List<OrganizationRequestFile> files = organizationRequestFileRepository.findByOrganizationRequest_RequestId(requestId);
        List<SubOrganizationRequestDetailResponse.OrganizationRequestFileResponse> fileResponses = files.stream()
                .map(file -> SubOrganizationRequestDetailResponse.OrganizationRequestFileResponse.builder()
                        .fileId(file.getFileId())
                        .originalFileName(file.getOriginalFileName())
                        .downloadUrl(getDownloadUrl(file))
                        .build())
                .collect(Collectors.toList());

        return SubOrganizationRequestDetailResponse.builder()
                .requestId(request.getRequestId())
                .requestStatus(request.getRequestStatus())
                .createdAt(request.getCreatedAt())
                .representativeName(UserInfoExtractor.getRepresentativeName(user))
                .representativeEmail(user.getEmail())
                .representativePhone(UserInfoExtractor.getRepresentativePhoneNumber(user))
                .representativeAffiliation(OrganizationHierarchyUtil.buildAffiliationString(request))
                .enrollYear(Integer.parseInt(userInfo.getEnrollYear()))
                .councilName(request.getCouncilName())
                .organizationType(request.getOrganizationType())
                .files(fileResponses)
                .build();
    }

    /** 파일 다운로드 URL 조회 */
    @Transactional
    public String getFileDownloadUrl(Long fileId) {
        OrganizationRequestFile file = organizationRequestFileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.FILE_NOT_FOUND));
        return getDownloadUrl(file);
    }

    /** 하위 학생회 목록 조회 */
    @Transactional
    public List<SubOrganizationResponse> getSubOrganizations() {
        Council currentCouncil = getCurrentCouncilWithOrganization();
        validateSubOrganizationAccess(currentCouncil.getOrganization());

        List<Council> subCouncils = councilRepository.findSubCouncilsByParentOrganization(
                currentCouncil.getOrganization().getOrganizationId(),
                currentCouncil.getOrganization().getLevel());

        return subCouncils.stream()
                .map(this::buildSubOrganizationResponse)
                .collect(Collectors.toList());
    }

    /** 하위 학생회 상세 조회 */
    @Transactional
    public SubOrganizationDetailResponse getSubOrganizationDetail(Long councilId) {
        Council currentCouncil = getCurrentCouncilWithOrganization();
        validateSubOrganizationAccess(currentCouncil.getOrganization());

        Council targetCouncil = councilRepository.findByIdWithOrganization(councilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));

        if (!isSubOrganization(currentCouncil.getOrganization(), targetCouncil.getOrganization())) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }

        String affiliation = OrganizationHierarchyUtil.buildAffiliationString(targetCouncil.getOrganization());

        List<CouncilMemberDetailResponse> members = councilMemberRepository
                .findAllActiveMembersWithDetailsByCouncilId(targetCouncil.getId())
                .stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());

        return SubOrganizationDetailResponse.builder()
                .councilId(targetCouncil.getId())
                .organizationType(targetCouncil.getOrganization().getOrganizationType().name())
                .affiliation(affiliation)
                .organizationName(targetCouncil.getCouncilName())
                .representativeName(UserInfoExtractor.getRepresentativeName(targetCouncil.getUser()))
                .representativeProfileImage(UserInfoExtractor.getRepresentativeProfileImage(targetCouncil.getUser()))
                .representativePhoneNumber(UserInfoExtractor.getRepresentativePhoneNumber(targetCouncil.getUser()))
                .workspaceCreatedAt(targetCouncil.getStartDate())
                .members(members)
                .build();
    }

    /** 하위 학생회의 워크스페이스 요청 목록 조회 */
    public List<SubOrganizationRequestListResponse> getSubOrganizationRequestList() {
        Council currentCouncil = getCurrentCouncilWithOrganization();
        Long currentOrganizationId = currentCouncil.getOrganization().getOrganizationId();

        List<OrganizationRequest> requests = organizationRequestRepository.findSubOrganizationRequestsByParentId(currentOrganizationId);
        return requests.stream()
                .map(this::buildSubOrganizationRequestListResponse)
                .collect(Collectors.toList());
    }

    /** 현재 학생회의 조직 정보 조회 (공통 처리) */
    private Council getCurrentCouncilWithOrganization() {
        Long currentCouncilId = CouncilContextHolder.getCouncilId();
        return councilRepository.findByIdWithOrganization(currentCouncilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));
    }

    /** 요청 접근 권한 검증 */
    private void validateRequestAccess(Organization currentOrganization, OrganizationRequest request) {
        if (request.getTargetOrganization() != null) {
            if (!isSubOrganization(currentOrganization, request.getTargetOrganization())) {
                throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
            }
        } else {
            int requestLevel = OrganizationHierarchyUtil.getLevelFromOrganizationType(request.getOrganizationType());
            if (requestLevel <= currentOrganization.getLevel()) {
                throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
            }
        }
    }

    /** targetOrg가 currentOrg의 하위 조직인지 확인 */
    private boolean isSubOrganization(Organization currentOrg, Organization targetOrg) {
        if (targetOrg.getLevel() <= currentOrg.getLevel()) {
            return false;
        }
        Organization parent = targetOrg.getParent();
        while (parent != null) {
            if (parent.getOrganizationId().equals(currentOrg.getOrganizationId())) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /** 하위 학생회 접근 권한 검증 */
    private void validateSubOrganizationAccess(Organization currentOrganization) {
        if (currentOrganization.getLevel() > 1) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }
    }

    /** 파일 다운로드 URL 생성 */
    private String getDownloadUrl(OrganizationRequestFile file) {
        if ("local".equals(activeProfile)) {
            return file.getFilePath();
        } else {
            return file.getFileUrl() != null ? file.getFileUrl() : file.getFilePath();
        }
    }

    /**
     * 요청에서 단과대 이름 추출
     */
    private String extractCollegeName(OrganizationRequest request) {
        if (request.getOrganizationType() == OrganizationType.COLLEGE) {
            return request.getCollegeName();
        } else if (request.getOrganizationType() == OrganizationType.DEPARTMENT) {
            return request.getCollegeName();
        }
        return null;
    }

    /**
     * 요청에서 학과 이름 추출
     */
    private String extractDepartmentName(OrganizationRequest request) {
        if (request.getOrganizationType() == OrganizationType.DEPARTMENT) {
            return request.getDepartmentName();
        }
        return null;
    }

    /**
     * Council 엔티티를 SubOrganizationResponse로 변환
     */
    private SubOrganizationResponse buildSubOrganizationResponse(Council council) {
        var organization = council.getOrganization();

        return SubOrganizationResponse.builder()
                .councilId(council.getId())
                .organizationType(organization.getOrganizationType().name())
                .collegeName(OrganizationHierarchyUtil.extractCollegeName(organization))
                .departmentName(OrganizationHierarchyUtil.extractDepartmentName(organization))
                .organizationName(council.getCouncilName())
                .representativeProfileImage(UserInfoExtractor.getRepresentativeProfileImage(council.getUser()))
                .representativeName(UserInfoExtractor.getRepresentativeName(council.getUser()))
                .workspaceCreatedAt(council.getStartDate())
                .build();
    }

    /** CouncilMember 엔티티를 CouncilMemberDetailResponse로 변환 */
    private CouncilMemberDetailResponse fromEntity(CouncilMember member) {
        return CouncilMemberDetailResponse.builder()
                .userId(member.getUser().getUserPkId())
                .userName(member.getUser().getName())
                .profileImage(UserInfoExtractor.getRepresentativeProfileImage(member.getUser()))
                .memberRole(member.getMemberRole())
                .departmentName(member.getDepartment() != null ? member.getDepartment().getName() : null)
                .departmentRoleName(member.getDepartmentRole() != null ? member.getDepartmentRole().getName() : null)
                .build();
    }

    /**
     * OrganizationRequest를 SubOrganizationRequestListResponse로 변환
     */
    private SubOrganizationRequestListResponse buildSubOrganizationRequestListResponse(OrganizationRequest request) {
        return SubOrganizationRequestListResponse.builder()
                .requestId(request.getRequestId())
                .organizationType(request.getOrganizationType())
                .collegeName(extractCollegeName(request))
                .departmentName(extractDepartmentName(request))
                .councilName(request.getCouncilName())
                .representativeProfileImage(UserInfoExtractor.getRepresentativeProfileImage(request.getUser()))
                .representativeName(UserInfoExtractor.getRepresentativeName(request.getUser()))
                .createdAt(request.getCreatedAt())
                .build();
    }
}
