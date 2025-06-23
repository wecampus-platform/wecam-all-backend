package org.example.wecambackend.service.client;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.requestDTO.OrganizationRequestRequest;
import org.example.model.Organization;
import org.example.model.OrganizationRequest;
import org.example.model.user.User;
import org.example.model.user.UserSignupInformation;
import org.example.model.enums.OrganizationType;
import org.example.model.enums.RequestStatus;
import org.example.wecambackend.repos.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationRequestService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationRequestRepository organizationRequestRepository;
    private final CouncilRepository councilRepository;
    private final UserSignupInformationRepository userSignupInformationRepository;


    //TODO : 추후 학생회장 가입 정보와 학생회 만들 때 설정할지 말지 (우선 우리단대 같은 경우 3,4학년이 공과대학 소속이지만 정의대 회장으로 활동이 가능한 점을 생각해서 대학교만 해둠. 부산대 , 서울대 이정도만)
    @Transactional
    public void createOrganizationRequest(OrganizationRequestRequest requestDto, Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        UserSignupInformation userOrgName = userSignupInformationRepository.findByUser_UserPkId(userId)
                .orElseThrow(()->new IllegalArgumentException("유효하지 않은 사용자입니다."));
        OrganizationType stats = requestDto.getOrganizationType();

        // OrganizationRequest 빌더 시작
        OrganizationRequest.OrganizationRequestBuilder builder = OrganizationRequest.builder()
                .user(user)
                .organizationType(requestDto.getOrganizationType())
                .councilName(requestDto.getCouncilName())
                .status(RequestStatus.PENDING);

        Organization parentOrg = null;
        String schoolName = null;

        // 학교 선택한 경우
        if (requestDto.getSelectSchoolId() != null) {
            Organization schoolOrg = organizationRepository
                    .findOrganizationByUniversity_SchoolIdAndParentIsNull(requestDto.getSelectSchoolId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 학교입니다."));
            parentOrg = schoolOrg;
            schoolName = schoolOrg.getOrganizationName();
            if(stats.equals(OrganizationType.UNIVERSITY)) {
                if (!(schoolName.equals(userOrgName.getInputSchoolName())
                        || schoolName.equals(organizationRepository.findOrganizationNameByOrganizationId(userOrgName.getSelectSchoolId())))) {
                    throw new IllegalArgumentException("알맞지 않은 조직 생성입니다.");
                }
            }

                // 단과 선택한 경우
            if (requestDto.getSelectCollegeOrganizationId() != null) {
                parentOrg = organizationRepository.findById(requestDto.getSelectCollegeOrganizationId())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 단과대학 조직입니다."));

            }

            // 학과 선택한 경우
            if (requestDto.getSelectDepartmentOrganizationId() != null) {
                parentOrg = organizationRepository.findById(requestDto.getSelectDepartmentOrganizationId())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 학과 조직입니다."));
            }

            // parentOrg가 schoolOrg 그대로면 → input 사용
            if (parentOrg.equals(schoolOrg) && requestDto.getOrganizationType() != OrganizationType.UNIVERSITY) {
                builder.schoolName(schoolName);
                builder.collegeName(requestDto.getInputCollegeName());
                builder.departmentName(requestDto.getInputDepartmentName());
            } else {
                // parentOrg가 더 깊으면 targetOrganization 설정
                Long orgId = parentOrg.getOrganizationId();
                if ( councilRepository.existsCouncilByOrganization_OrganizationId(orgId)) {
                    throw new IllegalArgumentException("이미 학생회가 존재하는 조직입니다.");
                }
                builder.targetOrganization(parentOrg);
            }

        } else if (requestDto.getInputSchoolName() != null) {
            // 학교 직접 입력한 경우 (모든 계층 직접 입력)
            builder.schoolName(requestDto.getInputSchoolName());
            builder.collegeName(requestDto.getInputCollegeName());
            builder.departmentName(requestDto.getInputDepartmentName());
        } else {
            throw new IllegalArgumentException("학교 정보가 누락되었습니다. 학교를 선택하거나 입력해야 합니다.");
        }

            // 최종 저장
        OrganizationRequest request = builder.build();
        organizationRequestRepository.save(request);
    }

}
