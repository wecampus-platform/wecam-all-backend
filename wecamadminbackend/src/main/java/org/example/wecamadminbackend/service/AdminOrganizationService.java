package org.example.wecamadminbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Council;
import org.example.model.organization.Organization;
import org.example.model.organization.OrganizationRequest;
import org.example.model.University;
import org.example.model.enums.OrganizationType;
import org.example.model.enums.RequestStatus;
import org.example.model.enums.UserRole;
import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.example.model.user.UserSignupInformation;
import org.example.model.user.UserStatus;
import org.example.wecamadminbackend.dto.PresidentSignupInfoDTO;
import org.example.wecamadminbackend.dto.request.OrganizationRequestDTO;
import org.example.wecamadminbackend.repos.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrganizationService {

    private final OrganizationRequestRepository organizationRequestRepository;
    private final PresidentSignupInformationRepository presidentSignupInformationRepository;
    private final OrganizationRepository organizationRepository;
    private final UniversityRepository universityRepository;
    private final CouncilRepository councilRepository;
    private final UserInformationRepository userInformationRepository;
    private final UserRepository userRepository;


    @Transactional
    public void approveWorkspaceRequest(Long requestId) {
        OrganizationRequest request = organizationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다."));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        User user = request.getUser();

        //탈퇴된 사용자 필터링
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalStateException("탈퇴한 사용자의 요청은 승인할 수 없습니다.");
        }

        // 정지 상태일 경우
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalStateException("정지된 사용자의 요청은 승인할 수 없습니다.");
        }

        // 승인 처리
        request.setStatus(RequestStatus.APPROVED);
        //가입 조직의 테이블 생성

        //가입할때의 user 정보 가져옴
        Long userId = user.getUserPkId();
        PresidentSignupInfoDTO presidentSignupInfoDTO = showUserSignUpInformation((userId));
        Organization organization = createOrganizationIfNeeded(presidentSignupInfoDTO);

        createUserInformation(user , presidentSignupInfoDTO , organization);
        //실제 워크스페이스(학생회 테이블) 생성
        createWorkspace(request.getCouncilName(),request);

        //학생회장 _ 신청서 작성자 회원가입 완료 시키기
        organizationRequestRepository.save(request);
    }

    public List<OrganizationRequestDTO> getPendingRequests() {
        List<OrganizationRequestDTO> organizationRequestDTOS =  organizationRequestRepository.findRequestDtosByStatus(RequestStatus.PENDING);
        return organizationRequestDTOS;
    }

    private void createWorkspace(String councilName,OrganizationRequest request) {
        OrganizationType type = request.getOrganizationType(); // enum 타입
// 1. 조직 이름 추출
        String orgName = switch (type) {
            case UNIVERSITY -> request.getSchoolName();
            case COLLEGE -> request.getCollegeName();
            case DEPARTMENT -> request.getDepartmentName();
            default -> throw new IllegalArgumentException("알 수 없는 조직 타입");
        };

// 2. schoolId 결정
        Long schoolId;
        if (request.getSchoolName() != null) {
            schoolId = universityRepository.findBySchoolName(request.getSchoolName())
                    .orElseThrow(() -> new RuntimeException("학교 없음")).getSchoolId();
        } else if (request.getTargetOrganization() != null) {
            schoolId = request.getTargetOrganization().getUniversity().getSchoolId();
            if (orgName == null || orgName.isBlank()) {
                orgName = request.getTargetOrganization().getOrganizationName();
            }
        } else {
            throw new IllegalStateException("학교 정보를 찾을 수 없습니다.");
        }

// 3. 조직 조회
        Organization org = organizationRepository
                .findByOrganizationNameAndOrganizationTypeAndUniversity_SchoolId(orgName, type, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("해당 조직이 존재하지 않습니다."));

// 4. 학생회 중복 체크
        if (councilRepository.existsCouncilByOrganization_OrganizationId(org.getOrganizationId())) {
            throw new IllegalStateException("이미 학생회가 존재합니다.");
        }

// 5. 학생회 생성
        Council council = Council.builder()
                .organization(org)
                .councilName(councilName)
                .user(request.getUser())
                .isActive(true)
                .build();
        councilRepository.save(council);


    }

    private PresidentSignupInfoDTO showUserSignUpInformation(Long userId) {
        UserSignupInformation userSignupInformation = presidentSignupInformationRepository.findByUser_UserPkId(userId)
                .orElseThrow(()->new IllegalArgumentException("해당 요청을 한 학생회장 회원가입이 존재하지 않습니다."));

        PresidentSignupInfoDTO presidentSignupInfoDTO = new PresidentSignupInfoDTO();
        presidentSignupInfoDTO.setUserId(userId);
        presidentSignupInfoDTO.setEnrollYear(userSignupInformation.getEnrollYear());
        presidentSignupInfoDTO.setUserName(userSignupInformation.getName());
        presidentSignupInfoDTO.setIsWorkspace(userSignupInformation.getIsMakeWorkspace());
        presidentSignupInfoDTO.setInputSchoolName(userSignupInformation.getInputSchoolName());
        presidentSignupInfoDTO.setInputCollegeName(userSignupInformation.getInputCollegeName());
        presidentSignupInfoDTO.setInputDepartmentName(userSignupInformation.getInputDepartmentName());
        presidentSignupInfoDTO.setSelectSchoolId(userSignupInformation.getSelectSchoolId());
        presidentSignupInfoDTO.setSelectOrganizationId(userSignupInformation.getSelectOrganizationId());


        return presidentSignupInfoDTO;
    }
    private Organization createOrganizationIfNeeded(PresidentSignupInfoDTO dto) {
        Organization university;
        Organization college = null;
        Organization department = null;
        University uni;

        //대학교
        if (dto.getSelectSchoolId() != null) {
            university = organizationRepository.findFirstByUniversity_SchoolIdAndLevel(dto.getSelectSchoolId(), 0)
                    .orElseThrow(() -> new IllegalArgumentException("선택한 대학교가 존재하지 않습니다."));
            uni = universityRepository.findById(dto.getSelectSchoolId())
                    .orElseThrow(()->new IllegalArgumentException("선택한 대학교가 존재하지 않습니다."));
        } else if (dto.getInputSchoolName() != null) {
            uni  = universityRepository.findBySchoolName(dto.getInputSchoolName())
                    .orElseGet(() -> universityRepository.save(
                            University.builder().schoolName(dto.getInputSchoolName()).build()
                    ));
            university = organizationRepository.findByOrganizationNameAndOrganizationType(dto.getInputSchoolName(), OrganizationType.UNIVERSITY)
                    .orElseGet(() ->
                            organizationRepository.save(
                            Organization.createUniversity(dto.getInputSchoolName(), uni)
                    ));
        } else {
            university = null;
            uni = null;
            throw new IllegalArgumentException("학교 정보는 필수입니다.");
        }

        //조직 선택 처리 (단과대학 or 학과)
        if (dto.getSelectOrganizationId() != null) {
            Organization selectedOrg = organizationRepository.findById(dto.getSelectOrganizationId())
                    .orElseThrow(() -> new IllegalArgumentException("선택한 조직이 존재하지 않습니다."));

            Organization finalCollege = college;
            switch (selectedOrg.getOrganizationType()) {
                case COLLEGE -> {
                    college = selectedOrg;
                    if (dto.getInputDepartmentName() != null) {
                        department = organizationRepository.findByOrganizationNameAndOrganizationTypeAndParent(
                                dto.getInputDepartmentName(), OrganizationType.DEPARTMENT, college
                        ).orElseGet(() -> organizationRepository.save(
                                Organization.createDepartment(dto.getInputDepartmentName(), uni, finalCollege)
                        ));
                    }
                }
                case DEPARTMENT -> department = selectedOrg;
                case UNIVERSITY -> throw new IllegalArgumentException("선택된 조직은 단과대학 또는 학과여야 합니다.");
            }

        } else {
            //단과대학 생성 (선택된 조직이 없는 경우))
            if (dto.getInputCollegeName() != null) {
                college = organizationRepository.findByOrganizationNameAndOrganizationTypeAndParent(
                        dto.getInputCollegeName(), OrganizationType.COLLEGE, university
                ).orElseGet(() -> organizationRepository.save(
                        Organization.createCollege(dto.getInputCollegeName(), uni, university)
                ));

                // 학과도 입력된 경우
                if (dto.getInputDepartmentName() != null) {
                    Organization finalCollege1 = college;
                    department = organizationRepository.findByOrganizationNameAndOrganizationTypeAndParent(
                            dto.getInputDepartmentName(), OrganizationType.DEPARTMENT, college
                    ).orElseGet(() -> organizationRepository.save(
                            Organization.createDepartment(dto.getInputDepartmentName(), uni, finalCollege1)
                    ));
                }
            } else if (dto.getInputDepartmentName() != null) {
                // 단과대 없이 학과만 입력된 경우 → 잘못된 입력
                throw new IllegalArgumentException("학과를 생성하려면 단과대학 정보가 필요합니다.");
            }
        }//하위조직반환
        return department;
    }

    private void createUserInformation(User user,PresidentSignupInfoDTO presidentSignupInfoDTO, Organization department) {
        University uni = department.getUniversity();
        user.setRole(UserRole.COUNCIL);
        user.setOrganization(department);
        user.setUniversity(uni);
        user.setEnrollYear(presidentSignupInfoDTO.getEnrollYear());
        user.setAuthentication(Boolean.TRUE);

        userRepository.save(user);

        UserInformation info = UserInformation.builder()
                .user(user)
                .university(uni)
                .name(presidentSignupInfoDTO.getUserName())
                .isAuthentication(Boolean.TRUE)
                .build();

        userInformationRepository.save(info);
    }
}
