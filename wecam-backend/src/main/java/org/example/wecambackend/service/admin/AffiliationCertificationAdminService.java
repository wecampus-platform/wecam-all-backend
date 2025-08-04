package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.enums.AuthenticationStatus;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.projection.AffiliationFileProjection;
import org.example.wecambackend.dto.requestDTO.AffiliationApprovalRequest;
import org.example.wecambackend.dto.responseDTO.AffiliationCertificationSummaryResponse;
import org.example.wecambackend.dto.responseDTO.AffiliationVerificationResponse;
import org.example.model.council.Council;
import org.example.model.organization.Organization;
import org.example.model.University;
import org.example.model.user.User;
import org.example.model.affiliation.AffiliationCertification;
import org.example.model.affiliation.AffiliationCertificationId;
import org.example.model.enums.AuthenticationType;
import org.example.wecambackend.repos.CouncilRepository;
import org.example.wecambackend.repos.organization.OrganizationRepository;
import org.example.wecambackend.repos.SchoolRepository;
import org.example.wecambackend.repos.affiliation.AffiliationCertificationRepository;
import org.example.wecambackend.repos.affiliation.AffiliationFileRepository;
import org.example.wecambackend.service.admin.common.AdminFileStorageService;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.example.wecambackend.service.client.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AffiliationCertificationAdminService {
    private final AffiliationCertificationRepository affiliationCertificationRepository;
    private final AffiliationFileRepository affiliationFileRepository;
    private final CouncilRepository councilRepository;
    private final UserService userService;
    private final UserInformationService userInformationService;
    private final EntityFinderService entityFinderService;

    // 전체 조회만
    public List<AffiliationCertificationSummaryResponse> getRequestsForOrganizationList(Long organizationId) {
        List<AffiliationCertification> certifications =
                affiliationCertificationRepository.findByOrganizationOrganizationIdAndStatusOrderByRequestedAtDesc(organizationId,AuthenticationStatus.PENDING);

        return certifications.stream().map(ac -> {
            // 복합키 구성 정보
            Long userId = ac.getId().getUserId();
            AuthenticationType authenticationType = ac.getId().getAuthenticationType();

            return new AffiliationCertificationSummaryResponse(
                    userId,
                    ac.getUsername(),
                    ac.getSelOrganizationName(),
                    ac.getSelEnrollYear(),
                    authenticationType,
                    ac.getOcrResult(),
                    ac.getAuthenticationStatus().name(),
                    ac.getRequestedAt()
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<AffiliationCertificationSummaryResponse> getRequestsByCouncilIdList(Long councilId) {
        Council council = entityFinderService.getCouncilByIdOrThrow(councilId);
        Long organizationId = council.getOrganization().getOrganizationId();

        return getRequestsForOrganizationList(organizationId);
    }


    @Transactional(readOnly = true)
    public AffiliationVerificationResponse getRequestsByAffiliationIdDetail(Long userId, AuthenticationType authenticationType,Long councilId) {

        AffiliationCertification ac =
                affiliationCertificationRepository.findByUser_UserPkIdAndAuthenticationType(userId,authenticationType)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));

        // councilId가 관리하는 organizationId 가져오기
        Council council = entityFinderService.getCouncilByIdOrThrow(councilId);

        Long targetOrgId = ac.getOrganization().getOrganizationId();
        Long councilOrgId = council.getOrganization().getOrganizationId();

        if (!Objects.equals(targetOrgId, councilOrgId)) {
            throw new BaseException(BaseResponseStatus.NO_PERMISSION_TO_MANAGE);
        }
        Optional<AffiliationFileProjection> optionalFile = affiliationFileRepository.findFilePathAndNameByUserIdAndAuthOrdinal(userId, authenticationType.ordinal());
        System.out.println("조회된 파일: " + optionalFile);
        String filePath = optionalFile.map(file -> file.getFilePath()).orElse(null);

        return new AffiliationVerificationResponse(
                userId,
                authenticationType.name(),
                ac.getOcrUserName(),
                ac.getOcrSchoolName(),
                ac.getOcrOrganizationName(),
                ac.getOcrEnrollYear(),
                ac.getSelSchoolName(),
                ac.getSelOrganizationName(),
                ac.getSelEnrollYear(),
                ac.getUsername(),
                ac.getOcrResult().name(),
                ac.getAuthenticationStatus().name(),
                ac.getRequestedAt(),
                filePath,
                ac.getIssuanceDate()
        );


    }


    //TODO: 요청에 대한 승인 , 거절 진행시 user 의 활동여부 확인 로직 공통으로 만들기

    //복합 도메인 트랜잭션 처리->
    @Transactional
    public void approveAffiliationRequest(AffiliationCertificationId id, Long councilId,UserDetailsImpl currentUser) {
        // 인증 요청 조회
        AffiliationCertification cert = affiliationCertificationRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));
        if (cert.getAuthenticationStatus() == AuthenticationStatus.APPROVED ||cert.getAuthenticationStatus() == AuthenticationStatus.REJECTED ||cert.getAuthenticationStatus() == AuthenticationStatus.EXPIRED ) {
            throw new BaseException(BaseResponseStatus.ACCESS_DENIED_REQUEST);
        }
        // 요청이 해당 councilId가 관리하는 범위에 있는지 검증 (선택) --- TODO: 할지 말지 모르겠음. 우선 제외
        User uploadUser = cert.getUser();
        AuthenticationType type = cert.getAuthenticationType();

        User reviewUser = entityFinderService.getUserByIdOrThrow(currentUser.getId());
        String enrollYear = cert.getOcrEnrollYear();

        Organization organization = organizationRepository.findByOrganizationId(cert.getOrganization().getOrganizationId())
                        .orElseThrow(()-> new BaseException(BaseResponseStatus.ORGANIZATION_NOT_FOUND));
        University university = schoolRepository.findBySchoolId(cert.getUniversity().getSchoolId())
                .orElseThrow(()-> new BaseException(BaseResponseStatus.SCHOOL_NOT_FOUND));

        System.out.println(organization.getOrganizationName());
        markApproved(cert,reviewUser);
        userInformationService.createUserInformation(uploadUser, cert, type);
        String userName = cert.getUsername();
        userService.updateUserRoleAndStatus(uploadUser, organization,university, type, enrollYear,userName);
        log.info("[소속 인증 승인] {}가 {}의 인증 요청을 승인함",
                reviewUser.getEmail(),
                uploadUser.getEmail());
    }


    public void markApproved(AffiliationCertification cert, User reviwerUser) {
        if (!cert.isApprovable()) {
            throw new BaseException(BaseResponseStatus.ALREADY_PROCESSED);
        }
        cert.approve(reviwerUser);
        affiliationCertificationRepository.save(cert); // dirty checking 보장 안되면 save
    }

    private final OrganizationRepository organizationRepository;
    private final SchoolRepository schoolRepository;


    //TODO : 삭제하면 log 확인을 못함....
    //삭제
    @Transactional
    public void deleteAffiliationRequest(AffiliationCertificationId id, Long councilId, UserDetailsImpl currentUser) {
        AffiliationCertification cert = affiliationCertificationRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));

        // 권한 확인: 해당 학생회가 이 조직을 관리하는지
        Long targetOrgId = cert.getOrganization().getOrganizationId();
        Long councilOrgId = councilRepository.findById(councilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_PERMISSION_TO_MANAGE))
                .getOrganization().getOrganizationId();

        if (!Objects.equals(targetOrgId, councilOrgId)) {
            throw new BaseException(BaseResponseStatus.NO_PERMISSION_TO_MANAGE);
        }

        // 1. 파일 경로 수집
        // 파일 경로 조회
        Optional<AffiliationFileProjection> file = Optional.ofNullable(affiliationFileRepository.findFilePathAndNameByUserIdAndAuthOrdinal(id.getUserId(), id.getAuthenticationType().ordinal())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND)));
        String filePath = file.get().getFilePath();
        // 2. 실제 저장소에서 파일 삭제 (예: S3 또는 로컬)
        adminFileStorageService.deleteFile(filePath);
            affiliationCertificationRepository.delete(cert);
    }

    private final AdminFileStorageService adminFileStorageService;

    public List<AffiliationApprovalRequest> approveAffiliationRequests(List<AffiliationApprovalRequest> requests, Long councilId, UserDetailsImpl currentUser) {
        List<AffiliationApprovalRequest> failedList = new ArrayList<>();
        for (AffiliationApprovalRequest req : requests) {
            try {
                AffiliationCertificationId id = new AffiliationCertificationId(req.getUserId(), req.getAuthType());
                approveAffiliationRequest(id, councilId, currentUser);
            } catch (BaseException e) {
                log.warn("[소속 인증 승인 실패] userId={}, authType={}, 이유={}", req.getUserId(), req.getAuthType(), e.getMessage());
                failedList.add(req); // 실패한 요청 저장
            }
        }
        return failedList;
    }


    // 거절 로직 , 이떄 reason 에 선택한 거절 사유가 들어간다.
    public void rejectAffiliationRequest(AffiliationCertificationId id, Long councilId, UserDetailsImpl currentUser,String reason) {
        AffiliationCertification cert = affiliationCertificationRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));
        User user = entityFinderService.getUserByIdOrThrow(currentUser.getId());
        cert.setAuthenticationStatus(AuthenticationStatus.REJECTED);
        cert.setReason(reason);
        cert.setReviewedAt(LocalDateTime.now());
        cert.setReviewUser(user);
        affiliationCertificationRepository.save(cert);

        log.warn("[소속 인증 거절] userId={}, 이유={}", currentUser.getId(),reason);
    }
}
