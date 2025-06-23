package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.projection.AffiliationFileProjection;
import org.example.wecambackend.dto.responseDTO.AffiliationVerificationResponse;
import org.example.model.Council;
import org.example.model.Organization;
import org.example.model.University;
import org.example.model.user.User;
import org.example.model.affiliation.AffiliationCertification;
import org.example.model.affiliation.AffiliationCertificationId;
import org.example.model.enums.AuthenticationType;
import org.example.wecambackend.repos.CouncilRepository;
import org.example.wecambackend.repos.OrganizationRepository;
import org.example.wecambackend.repos.SchoolRepository;
import org.example.wecambackend.repos.UserRepository;
import org.example.wecambackend.repos.affiliation.AffiliationCertificationRepository;
import org.example.wecambackend.repos.affiliation.AffiliationFileRepository;
import org.example.wecambackend.service.client.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final UserRepository userRepository;



    public List<AffiliationVerificationResponse> getRequestsForOrganization(Long organizationId) {
        List<AffiliationCertification> certifications =
                affiliationCertificationRepository.findByOrganizationOrganizationIdOrderByRequestedAtDesc(organizationId);

        return certifications.stream().map(ac -> {
            // 복합키 구성 정보
            Long userId = ac.getId().getUserId();
            AuthenticationType authenticationType = ac.getId().getAuthenticationType();

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
                    ac.getOcrResult().name(),
                    ac.getStatus().name(),
                    ac.getRequestedAt(),
                    filePath
            );
        }).toList();
    }

    @Transactional
    public List<AffiliationVerificationResponse> getRequestsByCouncilId(Long councilId) {
        Council council = councilRepository.findById(councilId)
                .orElseThrow(() -> new RuntimeException("해당 학생회를 찾을 수 없습니다."));

        Long organizationId = council.getOrganization().getOrganizationId();

        return getRequestsForOrganization(organizationId);
    }

    //복합 도메인 트랜잭션 처리->
    @Transactional
    public void approveAffiliationRequest(AffiliationCertificationId id, Long councilId,UserDetailsImpl currentUser) {
        // 인증 요청 조회
        AffiliationCertification cert = affiliationCertificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 인증 요청을 찾을 수 없습니다."));


        // 요청이 해당 councilId가 관리하는 범위에 있는지 검증 (선택) --- TODO: 할지 말지 모르겠음. 우선 제외
        User uploadUser = cert.getUser();
        AuthenticationType type = cert.getAuthenticationType();
        User reviewUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("리뷰어 유저 없음"));
        String enrollYear = cert.getOcrEnrollYear();

        Organization organization = organizationRepository.findByOrganizationId(cert.getOrganization().getOrganizationId())
                        .orElseThrow(()-> new IllegalArgumentException("해당 조직을 찾을 수 없습니다."));
        University university = schoolRepository.findBySchoolId(cert.getUniversity().getSchoolId())
                .orElseThrow(()-> new IllegalArgumentException("해당 학교를 찾을 수 없습니다."));

        System.out.println(organization.getOrganizationName());
        markApproved(cert,reviewUser);
        userInformationService.createUserInformation(uploadUser, cert, type);
        userService.updateUserRoleAndStatus(uploadUser, organization,university, type, enrollYear);
        log.info("[소속 인증 승인] {}가 {}의 인증 요청을 승인함",
                reviewUser.getEmail(),
                uploadUser.getEmail());
    }


    public void markApproved(AffiliationCertification cert, User reviwerUser) {
        if (!cert.isApprovable()) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        cert.approve(reviwerUser);
        affiliationCertificationRepository.save(cert); // dirty checking 보장 안되면 save
    }

    private final OrganizationRepository organizationRepository;
    private final SchoolRepository schoolRepository;
}
