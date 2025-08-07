package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.enums.UserRole;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.projection.OrganizationNameLevelDto;
import org.example.wecambackend.dto.responseDTO.UserSummaryResponse;
import org.example.wecambackend.repos.CouncilRepository;
import org.example.wecambackend.repos.UserInformationRepository;
import org.example.wecambackend.repos.UserRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final EntityFinderService entityFinderService;

    /**
     * 일반 학생을 제명합니다.
     * User의 role을 UNAUTH로 변경하고, UserInformation의 isAuthentication을 false로 변경합니다.
     * 
     * @param userId 제명할 학생의 ID
     * @param reason 제명 사유 (필수)
     */
    @Transactional
    public void expelStudent(Long userId, String reason) {
        // 1. 대상 학생 조회
        User user = entityFinderService.getUserByIdOrThrow(userId);

        // 2. 이미 UNAUTH 상태인지 확인
        if (user.getRole() == UserRole.UNAUTH) {
            throw new BaseException(BaseResponseStatus.ALREADY_EXPELLED_MEMBER);
        }
//
//        // 3. 학생회 구성원인지 확인 (학생회 구성원은 별도 API 사용)
//        if (user.getRole() == UserRole.COUNCIL) {
//            throw new BaseException(BaseResponseStatus.NO_PERMISSION_TO_MANAGE);
//        }

        // 4. 제명 정보 저장
        user.setExpulsionReason(reason);
        user.setExpulsionDate(LocalDateTime.now());

        // 5. 사용자 역할을 UNAUTH로 변경
        user.setRole(UserRole.UNAUTH);
        userRepository.save(user);

        // 6. 소속 인증 상태를 미인증으로 변경
        userInformationRepository.findByUser_UserPkId(user.getUserPkId())
                .ifPresent(userInfo -> {
                    userInfo.setIsAuthentication(false);
                    userInformationRepository.save(userInfo);
                });
    }


    /*학생 조회 서비스 로직 -> 학생 정보 조회이니까 Repos 에서 DTO 매핑 바로 할거임.
    학생 조회 시 학년 정보는 UserInfo 테이블에서 , 입학년도는 User 테이블의 EnrolledYear 값 뒤 2개
    나중에 Ehcache 캐시에 넣을 것 감안하고, 필터 조회가 많은걸 감안해서
    해당 (OrganizationId) 에 속한 StudentList 를 먼저 조회한 뒤,
    조회한 값에서 태그별 조회를 하는 게 좋을 거 같음.
    TODO : 추후 캐시레벨 저장하면 됨.
    organizationId에 속한 모든 학생을 한 번에 조회 → 이후 서비스 or 캐시 레벨에서 stdNumber, grade, tag 등으로 필터링
     */
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> showStudentListByDepartmentId
    (Long councilId, List<String> stdNumber , List<Integer> grade){
        OrganizationNameLevelDto organizationNameLevelDto =
                councilRepository.findOrganizationNameByCouncilId(councilId)
                        .orElseThrow(()->new BaseException(BaseResponseStatus.ACCESS_DENIED));
        if (!organizationNameLevelDto.level().equals(2)) {
            throw  new BaseException(BaseResponseStatus.ACCESS_DENIED);
            // 학부 만 하게끔 (TODO: 전공은 우선 어떻게 할 지 생각해봐야 될 듯)
        }

        // 2. 학생 리스트 조회 (전체)
        List<UserSummaryResponse> allStudents = userRepository.findByUserSummaryByOrgIdAndTagsAndIsActive(councilId);

        // 3. 필터링 (입학년도 뒷자리, 학년)
        return allStudents.stream()
                .filter(student -> {
                    boolean matchStd = (stdNumber == null || stdNumber.isEmpty()) ||
                            stdNumber.contains(student.getStudentNumber());

                    boolean matchGrade = (grade == null || grade.isEmpty()) ||
                            grade.contains(student.getGrade());

                    return matchStd && matchGrade;
                })
                .collect(Collectors.toList());
    }


    private final CouncilRepository councilRepository;
}
