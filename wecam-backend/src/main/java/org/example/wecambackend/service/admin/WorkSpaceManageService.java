package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.University;
import org.example.model.council.Council;
import org.example.model.council.CouncilDepartment;
import org.example.model.council.CouncilDepartmentRole;
import org.example.model.council.CouncilMember;
import org.example.model.enums.MemberRole;
import org.example.model.enums.OrganizationType;
import org.example.model.enums.RequestStatus;
import org.example.model.enums.UserRole;
import org.example.model.organization.Organization;
import org.example.model.organization.OrganizationRequest;
import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.example.model.user.UserSignupInformation;
import org.example.model.user.UserStatus;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.annotation.CurrentUser;
import org.example.wecambackend.dto.projection.PresidentSignupInfoDTO;
import org.example.wecambackend.repos.*;
import org.example.wecambackend.repos.organization.OrganizationRepository;
import org.example.wecambackend.repos.organization.OrganizationRequestRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.example.wecambackend.service.util.UserTagGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import org.example.model.council.Council;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkSpaceManageService {
// 워크스페이스 뷰 - 워크스페이스에 있는 targetOrg 의 parentId 가 Council 의 OrganizationId 와 동일하다면
// 해당 학생회에게 워크스페이스 승인 인가를 줌.


    private final OrganizationRequestRepository organizationRequestRepository;
    private final UserInformationRepository userInformationRepository;
//    //워크스페이스 승인 요청
    //requestId 확인
    @Transactional
    public void getAllWorkspaceRequestApprove(Long requestId,Long userId ) {


        OrganizationRequest request = organizationRequestRepository.findById(requestId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));
        User currentUser = entityFinderService.getUserByIdOrThrow(userId);
        Organization targetOrg = request.getTargetOrganization();
        if (targetOrg == null) {
            throw new BaseException(BaseResponseStatus.ACCESS_DENIED); // ← 적절한 에러 코드
        }

        Organization requestParentOrg = targetOrg.getParent();
        if (!requestParentOrg.equals(currentUser.getOrganization())) {
            throw new BaseException(BaseResponseStatus.ACCESS_DENIED);
        }

        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new BaseException(BaseResponseStatus.ACCESS_DENIED_REQUEST);
        }
        User user = request.getUser();

        //탈퇴된 사용자 필터링
        if (user.getUserStatus() == UserStatus.WITHDRAWN) {
            throw new BaseException(BaseResponseStatus.INVALID_USER);
        }

        // 정지 상태일 경우
        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            throw new BaseException(BaseResponseStatus.INVALID_USER);
        }

        // 승인 처리
        request.setRequestStatus(RequestStatus.APPROVED);
        //가입 조직의 테이블 생성

        //가입할때의 user 정보 가져옴
        Long requestUserId = user.getUserPkId();
        PresidentSignupInfoDTO presidentSignupInfoDTO = showUserSignUpInformation((requestUserId));
        Organization organization = createOrganizationIfNeeded(presidentSignupInfoDTO);

        createUserInformation(user , presidentSignupInfoDTO , organization);
        //실제 워크스페이스(학생회 테이블) 생성
        createWorkspace(request.getCouncilName(),request);

        //학생회장 _ 신청서 작성자 회원가입 완료 시키기
        organizationRequestRepository.save(request);
    }



    private void createWorkspace(String councilName,OrganizationRequest request) {
        OrganizationType type = request.getOrganizationType(); // enum 타입
// 1. 조직 이름 추출
        String orgName = switch (type) {
            case UNIVERSITY -> request.getSchoolName();
            case COLLEGE -> request.getCollegeName();
            case DEPARTMENT -> request.getDepartmentName();
            default -> throw new BaseException(BaseResponseStatus.INVALID_ORG_TYPE);
        };

// 2. schoolId 결정
        Long schoolId;
        if (request.getSchoolName() != null) {
            schoolId = schoolRepository.findBySchoolName(request.getSchoolName())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.SCHOOL_NOT_FOUND)).getSchoolId();
        } else if (request.getTargetOrganization() != null) {
            schoolId = request.getTargetOrganization().getUniversity().getSchoolId();
            if (orgName == null || orgName.isBlank()) {
                orgName = request.getTargetOrganization().getOrganizationName();
            }
        } else {
            throw new BaseException(BaseResponseStatus.SCHOOL_NOT_FOUND);
        }

// 3. 조직 조회
        Organization org = organizationRepository
                .findByOrganizationNameAndOrganizationTypeAndUniversity_SchoolId(orgName, type, schoolId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ORGANIZATION_NOT_FOUND));

// 4. 학생회 중복 체크
        if (councilRepository.existsCouncilByOrganization_OrganizationId(org.getOrganizationId())) {
            throw new BaseException(BaseResponseStatus.ALREADY_EXIST_COUNCIL);
        }

        User user = request.getUser();
// 5. 학생회 생성
        Council council = Council.builder()
                .organization(org)
                .councilName(councilName)
                .user(user)
                .build();
        councilRepository.save(council);

        // 기본 Department 생성시키기(학생회 부서 - 회장단) , 회장 배치
        CouncilDepartment councilDepartment = CouncilDepartment.builder()
                .council(council)
                .parentId(null)
                .name("회장단")
                .build();

        CouncilDepartmentRole councilDepartmentRole = CouncilDepartmentRole.builder()
                .department(councilDepartment)
                .level(0)
                .name("회장").build();

        // 6. 학생회 멤버 추가
        CouncilMember councilMember = CouncilMember.builder()
                .council(council)
                .memberRole(MemberRole.PRESIDENT)
                .user(user)
                .department(councilDepartment)
                .departmentRole(councilDepartmentRole)
                .build();
        councilMemberRepository.save(councilMember);


    }


    private PresidentSignupInfoDTO showUserSignUpInformation(Long userId) {
        UserSignupInformation userSignupInformation = userSignupInformationRepository.findByUser_UserPkId(userId)
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
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.SCHOOL_NOT_FOUND));
            uni = schoolRepository.findById(dto.getSelectSchoolId())
                    .orElseThrow(()->new BaseException(BaseResponseStatus.SCHOOL_NOT_FOUND));
        } else if (dto.getInputSchoolName() != null) {
            uni  = schoolRepository.findBySchoolName(dto.getInputSchoolName())
                    .orElseGet(() -> schoolRepository.save(
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
            throw  new BaseException(BaseResponseStatus.INVALID_FIELD_VALUE);
        }

        //조직 선택 처리 (단과대학 or 학과)
        if (dto.getSelectOrganizationId() != null) {
            Organization selectedOrg = organizationRepository.findById(dto.getSelectOrganizationId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.ORGANIZATION_NOT_FOUND));

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
                case UNIVERSITY -> throw new BaseException(BaseResponseStatus.INVALID_ORG_TYPE);
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
        String name = presidentSignupInfoDTO.getUserName();
        String userTag = userTagGenerator.generateUserTag(uni.getSchoolId(),name);
        user.setRole(UserRole.COUNCIL);
        user.setUserTag(userTag);
        user.setName(name);
        user.setOrganization(department);
        user.setUniversity(uni);
        user.setEnrollYear(presidentSignupInfoDTO.getEnrollYear());
        user.setAuthentication(Boolean.TRUE);

        userRepository.save(user);
        Optional<UserInformation> optionalInfo = userInformationRepository.findByUser(user);

        UserInformation info;

        if (optionalInfo.isPresent()) {
            // 이미 존재하면 업데이트
            info = optionalInfo.get();
            info.setUniversity(uni);
            info.setIsAuthentication(true);
        } else {
            //없으면 새로 생성
            info = UserInformation.builder()
                    .user(user)
                    .university(uni)
                    .isAuthentication(true)
                    .build();
        }

        userInformationRepository.save(info);

    }

    private final UserSignupInformationRepository userSignupInformationRepository;
    private final OrganizationRepository organizationRepository;
    private final SchoolRepository schoolRepository;
    private final UserTagGenerator userTagGenerator;
    private final UserRepository userRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilRepository councilRepository;
    private final EntityFinderService entityFinderService;

    public void getAllWorkspaceRequestReject(Long requestId, Long userId,String reason) {
        OrganizationRequest request = organizationRequestRepository.findById(requestId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));
        User currentUser = entityFinderService.getUserByIdOrThrow(userId);
        Organization targetOrg = request.getTargetOrganization();
        if (targetOrg == null) {
            throw new BaseException(BaseResponseStatus.ACCESS_DENIED); // ← 적절한 에러 코드
        }

        Organization requestParentOrg = targetOrg.getParent();
        if (!requestParentOrg.equals(currentUser.getOrganization())) {
            throw new BaseException(BaseResponseStatus.ACCESS_DENIED);
        }

        //거절
        request.setRequestStatus(RequestStatus.REJECTED);
        request.setReason(reason);

        organizationRequestRepository.save(request);

    }
}
