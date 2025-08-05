package org.example.wecambackend.service.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.organization.OrganizationRequest;
import org.example.model.organization.OrganizationRequestFile;
import org.example.model.organization.Organization;
import org.example.model.council.Council;
import org.example.model.user.User;
import org.example.model.user.UserSignupInformation;
import org.example.model.enums.OrganizationType;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.responseDTO.OrganizationRequestDetailResponse;
import org.example.wecambackend.dto.responseDTO.SubOrganizationResponse;
import org.example.wecambackend.dto.responseDTO.SubOrganizationDetailResponse;
import org.example.wecambackend.dto.responseDTO.CouncilMemberDetailResponse;
import org.example.wecambackend.repos.organization.OrganizationRequestFileRepository;
import org.example.wecambackend.repos.organization.OrganizationRequestRepository;
import org.example.wecambackend.repos.organization.OrganizationRepository;
import org.example.wecambackend.repos.CouncilRepository;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.example.wecambackend.repos.UserRepository;
import org.example.wecambackend.repos.UserSignupInformationRepository;
import org.example.wecambackend.common.context.CouncilContextHolder;
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

    /**
     * 워크스페이스 생성 요청 상세 조회
     */
    @Transactional
    public OrganizationRequestDetailResponse getOrganizationRequestDetail(Long requestId, String councilName) {
        // 현재 접속한 학생회의 조직 정보 가져오기
        Long currentCouncilId = CouncilContextHolder.getCouncilId();
        Council currentCouncil = councilRepository.findByIdWithOrganization(currentCouncilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));
        
        // 요청 조회
        OrganizationRequest request = organizationRequestRepository.findByIdWithUserAndOrganization(requestId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ORGANIZATION_NOT_FOUND));

        // 권한 검증: 현재 학생회보다 낮은 레벨의 조직에서 온 요청인지 확인
        validateRequestAccess(currentCouncil.getOrganization(), request);

        User user = request.getUser();
        UserSignupInformation userInfo = userSignupInformationRepository.findByUser_UserPkId(user.getUserPkId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        // 증빙자료 조회
        List<OrganizationRequestFile> files = organizationRequestFileRepository.findByOrganizationRequest_RequestId(requestId);
        List<OrganizationRequestDetailResponse.OrganizationRequestFileResponse> fileResponses = files.stream()
                .map(file -> OrganizationRequestDetailResponse.OrganizationRequestFileResponse.builder()
                        .fileId(file.getFileId())
                        .originalFileName(file.getOriginalFileName())
                        .downloadUrl(getDownloadUrl(file))
                        .build())
                .collect(Collectors.toList());

        return OrganizationRequestDetailResponse.builder()
                .requestId(request.getRequestId())
                .requestStatus(request.getRequestStatus())
                .createdAt(request.getCreatedAt())
                .representativeName(user.getName())
                .representativeEmail(user.getEmail())
                .representativePhone(user.getUserPrivate().getPhoneNumber())
                .representativeAffiliation(buildAffiliationString(request))
                .enrollYear(Integer.parseInt(userInfo.getEnrollYear()))
                .councilName(request.getCouncilName())
                .organizationType(request.getOrganizationType())
                .files(fileResponses)
                .build();
    }

    /**
     * 파일 다운로드 URL 조회
     */
    @Transactional
    public String getFileDownloadUrl(Long fileId) {
        OrganizationRequestFile file = organizationRequestFileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.FILE_NOT_FOUND));
        
        return getDownloadUrl(file);
    }

    /**
     * 하위 학생회 목록 조회 (단과대/총학생회 전용)
     */
    @Transactional
    public List<SubOrganizationResponse> getSubOrganizations() {
        // 현재 접속한 학생회의 조직 정보 가져오기
        Long currentCouncilId = CouncilContextHolder.getCouncilId();
        Council currentCouncil = councilRepository.findByIdWithOrganization(currentCouncilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));

        // 권한 검증: 단과대/총학생회만 접근 가능
        validateSubOrganizationAccess(currentCouncil.getOrganization());

        // 하위 학생회 목록 조회
        List<Council> subCouncils = councilRepository.findSubCouncilsByParentOrganization(
                currentCouncil.getOrganization().getOrganizationId(),
                currentCouncil.getOrganization().getLevel());

        return subCouncils.stream()
                .map(this::buildSubOrganizationResponse)
                .collect(Collectors.toList());
    }

    /**
     * 하위 학생회 상세 조회 (단과대/총학생회 전용)
     */
    @Transactional
    public SubOrganizationDetailResponse getSubOrganizationDetail(Long councilId) {
        // 현재 접속한 학생회의 조직 정보 가져오기
        Long currentCouncilId = CouncilContextHolder.getCouncilId();
        Council currentCouncil = councilRepository.findByIdWithOrganization(currentCouncilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));

        // 권한 검증: 단과대/총학생회만 접근 가능
        validateSubOrganizationAccess(currentCouncil.getOrganization());

        // 대상 학생회 조회
        Council targetCouncil = councilRepository.findByIdWithOrganization(councilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));

        // 하위 학생회인지 검증
        if (!isSubOrganization(currentCouncil.getOrganization(), targetCouncil.getOrganization())) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }

        return buildSubOrganizationDetailResponse(targetCouncil);
    }

    /**
     * 요청 접근 권한 검증
     */
    private void validateRequestAccess(Organization currentOrganization, OrganizationRequest request) {
        // 요청이 targetOrganization을 가지고 있는 경우
        if (request.getTargetOrganization() != null) {
            Organization targetOrg = request.getTargetOrganization();
            
            // 현재 조직이 요청 조직의 상위 조직인지 확인
            if (!isSubOrganization(currentOrganization, targetOrg)) {
                throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
            }
        } else {
            // 직접 입력한 학교/단과대/학과 정보로 요청한 경우
            // OrganizationType에 따른 레벨 매핑
            int requestLevel = getLevelFromOrganizationType(request.getOrganizationType());
            if (requestLevel <= currentOrganization.getLevel()) {
                throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
            }
        }
    }

    /**
     * targetOrg가 currentOrg의 하위 조직인지 확인
     */
    private boolean isSubOrganization(Organization currentOrg, Organization targetOrg) {
        // targetOrg의 레벨이 currentOrg보다 높아야 함 (숫자가 클수록 하위)
        if (targetOrg.getLevel() <= currentOrg.getLevel()) {
            return false;
        }
        
        // targetOrg의 상위 조직 체인을 따라가면서 currentOrg와 일치하는지 확인
        Organization parent = targetOrg.getParent();
        while (parent != null) {
            if (parent.getOrganizationId().equals(currentOrg.getOrganizationId())) {
                return true;
            }
            parent = parent.getParent();
        }
        
        return false;
    }

    /**
     * OrganizationType에 따른 레벨 반환
     */
    private int getLevelFromOrganizationType(OrganizationType type) {
        return switch (type) {
            case UNIVERSITY -> 0;
            case COLLEGE -> 1;
            case DEPARTMENT -> 2;
            case MAJOR -> 3;
        };
    }

    /**
     * 다운로드 URL 생성
     */
    private String getDownloadUrl(OrganizationRequestFile file) {
        // 로컬 환경에서는 file_path 반환, 프로덕션에서는 file_url 반환
        if ("local".equals(activeProfile)) {
            return file.getFilePath();
        } else {
            return file.getFileUrl() != null ? file.getFileUrl() : file.getFilePath();
        }
    }

    /**
     * 하위 학생회 접근 권한 검증
     */
    private void validateSubOrganizationAccess(Organization currentOrganization) {
        // 단과대(level 1) 또는 총학생회(level 0)만 접근 가능 (MAJOR도 있긴 한데 일단은..)
        if (currentOrganization.getLevel() > 1) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }
    }
    
    /**
     * 하위 학생회 응답 객체 생성
     */
    private SubOrganizationResponse buildSubOrganizationResponse(Council council) {
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
                .representativeProfileImage(council.getUser().getUserInformation() != null ? 
                        council.getUser().getUserInformation().getProfileImagePath() : null)
                .representativeName(council.getUser().getName())
                .workspaceCreatedAt(council.getStartDate())
                .build();
    }

    /**
     * 소속 정보 문자열 생성 (OrganizationRequest용)
     * 워크스페이스 생성 요청 시 사용자가 입력한 학교/단과대/학과 정보를 기반으로 소속 문자열을 만듦
     */
    private String buildAffiliationString(OrganizationRequest request) {
        StringBuilder affiliation = new StringBuilder();
        
        if (request.getSchoolName() != null) {
            affiliation.append(request.getSchoolName());
        }
        
        if (request.getCollegeName() != null) {
            if (affiliation.length() > 0) {
                affiliation.append(" ");
            }
            affiliation.append(request.getCollegeName());
        }
        
        if (request.getDepartmentName() != null) {
            if (affiliation.length() > 0) {
                affiliation.append(" ");
            }
            affiliation.append(request.getDepartmentName());
        }
        
        return affiliation.toString();
    }

    /**
     * 소속 정보 문자열 생성 (Organization용)
     * DB에 저장된 실제 조직 엔티티의 계층 구조를 기반으로 소속 문자열을 만듦
     */
    private String buildAffiliationStringFromOrganization(Organization organization) {
        StringBuilder affiliation = new StringBuilder();

        // 학교 정보는 organization에서 직접 가져올 수 없으므로 상위 조직을 통해 추출
        if (organization.getUniversity() != null) {
            affiliation.append(organization.getUniversity().getSchoolName());
        }

        // 단과대 정보
        if (organization.getOrganizationType() == OrganizationType.COLLEGE) {
            if (affiliation.length() > 0) {
                affiliation.append(" ");
            }
            affiliation.append(organization.getOrganizationName());
        } else if (organization.getOrganizationType() == OrganizationType.DEPARTMENT) {
            // 학과인 경우 상위 조직(단과대) 정보 추가
            if (organization.getParent() != null) {
                affiliation.append(organization.getParent().getOrganizationName());
            }
            if (affiliation.length() > 0) {
                affiliation.append(" ");
            }
            affiliation.append(organization.getOrganizationName());
        }

        return affiliation.toString();
    }


    /**
     * 하위 학생회 상세 응답 객체 생성
     */
    private SubOrganizationDetailResponse buildSubOrganizationDetailResponse(Council council) {
        Organization organization = council.getOrganization();

        // 학생회원 목록 조회
        List<CouncilMemberDetailResponse> members = councilMemberRepository
                .findAllActiveMembersWithDetailsByCouncilId(council.getId())
                .stream()
                .map(this::buildCouncilMemberDetailResponseFromEntity)
                .collect(Collectors.toList());

        // 소속 정보 생성
        String affiliation = buildAffiliationStringFromOrganization(organization);

        return SubOrganizationDetailResponse.builder()
                .councilId(council.getId())
                .organizationType(organization.getOrganizationType().name())
                .affiliation(affiliation)
                .organizationName(council.getCouncilName())
                .representativeName(council.getUser().getName())
                .representativeProfileImage(council.getUser().getUserInformation() != null ?
                        council.getUser().getUserInformation().getProfileImagePath() : null)
                .representativePhoneNumber(council.getUser().getUserPrivate() != null ?
                        council.getUser().getUserPrivate().getPhoneNumber() : null)
                .workspaceCreatedAt(council.getStartDate())
                .members(members)
                .build();
    }

    /**
     * 학생회원 상세 응답 객체 생성 (CouncilMember 엔티티에서)
     */
    private CouncilMemberDetailResponse buildCouncilMemberDetailResponseFromEntity(org.example.model.council.CouncilMember member) {
        String departmentName = null;
        String departmentRoleName = null;
        
        if (member.getDepartment() != null) {
            departmentName = member.getDepartment().getName();
        }
        if (member.getDepartmentRole() != null) {
            departmentRoleName = member.getDepartmentRole().getName();
        }
        
        return CouncilMemberDetailResponse.builder()
                .userId(member.getUser().getUserPkId())
                .userName(member.getUser().getName())
                .profileImage(member.getUser().getUserInformation() != null ? 
                        member.getUser().getUserInformation().getProfileImagePath() : null)
                .memberRole(member.getMemberRole())
                .departmentName(departmentName)
                .departmentRoleName(departmentRoleName)
                .build();
    }
} 