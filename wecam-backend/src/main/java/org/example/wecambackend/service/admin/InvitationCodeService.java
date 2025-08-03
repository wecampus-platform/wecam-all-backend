package org.example.wecambackend.service.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;
import org.example.model.University;
import org.example.model.enums.AcademicStatus;
import org.example.model.enums.CodeType;
import org.example.model.enums.MemberRole;
import org.example.model.enums.UserRole;
import org.example.model.invitation.InvitationCode;
import org.example.model.invitation.InvitationHistory;
import org.example.model.organization.Organization;
import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.example.model.user.UserSignupInformation;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.responseDTO.CreateTodoResponse;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.example.wecambackend.dto.responseDTO.InvitationUsedHistoryResponse;
import org.example.wecambackend.repos.*;
import org.example.wecambackend.repos.organization.OrganizationRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.example.wecambackend.service.admin.common.UserInfoAssembler;
import org.example.wecambackend.service.util.UserTagGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationCodeService {
    private final InvitationCodeRepository invitationCodeRepository;
    private final InvitationHistoryRepository invitationHistoryRepository;
    private final UserSignupInformationRepository userSignupInformationRepository;
    private final OrganizationRepository organizationRepository;
    private final UserInformationRepository userInformationRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final EntityFinderService entityFinderService;
    private final UserInfoAssembler userInfoAssembler;

    public List<InvitationCodeResponse> findByCouncilId(Long councilId) {
        return invitationCodeRepository.findAllByCouncilId(councilId);
    }

    @Transactional
    public CreateTodoResponse createInvitationCode(CodeType codeType, Long userId, Long councilId){
    // 1. 유저 조회
    User user = entityFinderService.getUserByIdOrThrow(userId);

    // 2. 유저가 소속된 조직 또는 학생회 가져오기 (예: 학과 학생회 기준)
    Council council = entityFinderService.getCouncilByIdOrThrow(councilId);

    // 3. 소속 조직
    Organization organization = council.getOrganization();

    String generatedCode = generateUniqueCode();  // 여기서 자동 생성

    // 타입별 만료일 계산
    LocalDateTime expirationDate = null;
    if (codeType == CodeType.student_member) {
        expirationDate = LocalDateTime.now().plusMinutes(10);
    } else if (codeType == CodeType.council_member) {
        expirationDate = LocalDateTime.now().plusDays(7);
    }

        // 4. 초대코드 엔티티 생성
    InvitationCode invitationCode = InvitationCode.builder()
            .code(generatedCode)
            .codeType(codeType)
            .isActive(true)
            .user(user)
            .council(council)
            .organization(organization)
            .expirationDate(expirationDate)
            .build();

    // 5. 저장
    invitationCodeRepository.save(invitationCode);

    CreateTodoResponse createTodoResponse = CreateTodoResponse.builder()
            .code(invitationCode.getCode())
            .expiredAt(invitationCode.getExpirationDate())
            .build();

    return createTodoResponse;
    }

    private static final int CODE_LENGTH = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode(CODE_LENGTH);
        } while (invitationCodeRepository.existsByCode(code));
        return code;
    }

    private String generateRandomCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // 클라이언트 코드 사용 전체 비즈니스 로직
    // 코드가 존재하는지
    // 코드의 타입과 선택한 타입이 일치하는지
    // 타입별 분기 -> 학생 타입 일 때 학생회 타입 일 때
    // 학생회 멤버 타입 -> 학교검사만 (대학교 검사) - organization 의 schoolId 와 가입자 signupInformation 의 Select_schoolId를 비교 ,
    // 학생의 information , user 테이블 정보 수정
    // 학생 학생회 멤버 추가
    // 학생 멤버 타입
    //TODO : 만료기간 넣을지 말지 (우선 뺀 거로만 해둠)
    @Transactional
    public void usedCode(String code, UserDetailsImpl userDetails, CodeType codeType){
        InvitationCode invitationCode = findByCode(code,codeType);

        User user = entityFinderService.getUserByIdOrThrow(userDetails.getId());
        UserSignupInformation userSignupInformation = userSignupInformationRepository.findByUser_UserPkId(userDetails.getId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));
        Organization organization = organizationRepository.findByOrganizationId(userSignupInformation.getSelectOrganizationId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 조직입니다."));
        String userEnrollYear = userSignupInformation.getEnrollYear();
        Boolean status = userSignupInformation.getSelectSchoolId().equals(invitationCodeRepository.findSchoolIdByCode(code));
        if (codeType.equals(CodeType.council_member) &&
                status)  {
            UserRole userRole = UserRole.COUNCIL;
            createUserInformationByCode(userEnrollYear,userRole,user,userSignupInformation.getName(),organization);
            CouncilMember councilMember = CouncilMember.builder()
                    .memberRole(MemberRole.STAFF) //일반부원으로 기본설정
                    .council(invitationCode.getCouncil())
                    .user(user)
                    .build();
            councilMemberRepository.save(councilMember);

        } else if (codeType.equals(CodeType.student_member)&& status) {
            UserRole userRole = UserRole.STUDENT;
            createUserInformationByCode(userEnrollYear,userRole,user,userSignupInformation.getName(),organization);
        }
        else {
            throw new IllegalArgumentException("다시 입력하세요.");
        }
        if (invitationCode.isExpired()) {
            invitationCode.setIsActive(false);
            invitationCodeRepository.save(invitationCode); // 상태 반영

            throw new BaseException(BaseResponseStatus.INVITATION_CODE_EXPIRED);
        }

        InvitationHistory invitationHistory = InvitationHistory.builder()
                .invitationPkId(invitationCode.getId())
                .user(user)
                .build();
        invitationHistoryRepository.save(invitationHistory);
    }

    public void createUserInformationByCode(String userEnrollYear,UserRole userRole,User user, String name,Organization organization) {
        University university = organization.getUniversity();
        userInformationRepository.findByUser(user)
                .ifPresentOrElse(
                        existingInfo -> {}, // 이미 존재하면 아무것도 안 함!
                        () -> {
                            UserInformation userInfo = UserInformation.builder()
                                    .isAuthentication(true)
                                    .user(user)
                                    .university(university)
                                    .academicStatus(AcademicStatus.ENROLLED)
                                    .build();
                            userInformationRepository.save(userInfo);
                        }
                );
        String userTag = userTagGenerator.generateUserTag(university.getSchoolId(),name);
        user.setName(name);
        user.setUserTag(userTag);
        user.setRole(userRole);
        user.setUniversity(university);
        user.setOrganization(organization);
        user.setExpiresAt(null);
        user.setEnrollYear(userEnrollYear);
        user.setAuthentication(true);

        log.info("[{}초대코드 사용] {}:{}, 학교: {}, 조직: {}",
                userRole,
                user.getEmail(),
                user.getRole().name(),
                university.getSchoolName(),
                organization.getOrganizationName());
    }

    // 코드가 존재하는지
    // 코드의 타입과 선택한 타입이 일치하는지
    public InvitationCode findByCode (String code, CodeType codeType){
        InvitationCode invitationCode = invitationCodeRepository.findByCodeAndCodeTypeAndIsActive(code, codeType,true)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.INVITATION_CODE_EXPIRED));
        return invitationCode;

    }


    // 코드의 ExpiredAt - 만료기간 업데이트 서비스 로직 , 전달받은 invitationPkId 에 해당되는 초대코드의 만료기간을 업데이트함.
    @Transactional
    public void editExpiredAtInvitation(LocalDateTime expiredAt, Long invitationId) {
        //전달받은 Id 가 실제 존재하는 Id 인지 확인 한 후 만료기간 업데이트
        //만료기간은 localdateTime.now() 보다 무조건 5분 뒤어야 함. 5분 이전으로 설정될 시 Exception
        //만료기간을 업데이트 하며 , 해당 초대코드가 비활성화인 상태라면 다시 활성화 상태로 변환시켜야 함.
        //초대코드 업데이트의 권한 여부는 controller 에서 어노테이션으로 실행

        // 5분 이후인지 확인
        if (expiredAt.isBefore(LocalDateTime.now().plusMinutes(5))) {
            throw new BaseException(BaseResponseStatus.INVALID_EXPIRATION_TIME);
        }

        // 해당 초대코드가 존재하지 않으면 예외
        InvitationCode code = invitationCodeRepository.findById(invitationId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.INVITATION_CODE_NOT_FOUND));

        code.setExpirationDate(expiredAt);

        // 비활성화 상태였으면 활성화로 바꿔줌
        if (!code.getIsActive()) {
            code.setIsActive(true);
        }

    }


    @Transactional
    //TODO:  list 는 제약성이 없을 때 페이징 처리가 필요함. 한꺼번에 많은 거 들고 오면 안됨.
    //히스토리 조회 _ 코드 번호 , 코드 타입, 생성자 , 생성시각 , 민료시각 은 invitationCode 테이블에서
    //사용 이력 정보는 _ 이름과 아이디(는 우선 이메일로..???) -> 이건 DTO 따른거로 매핑 , 사용 시각
    public List<InvitationUsedHistoryResponse> showHistoryInvitationCode(Long invitationId) {
        InvitationCode code = invitationCodeRepository.findById(invitationId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.INVITATION_CODE_NOT_FOUND));
        // InvitationCode의 사용 이력 조회
        List<InvitationHistory> usageList = invitationHistoryRepository.findByInvitationPkId(invitationId);

        // 각 이력 정보를 DTO로 매핑하여 리스트 반환
        return usageList.stream()
                .map(usage -> {
                    var user = usage.getUser();
                    return InvitationUsedHistoryResponse.builder()
                            .invitationPkId(code.getId())
                            .usedAtTime(usage.getUsedAt())
                            .userName(user.getName())
                            .userEmail(user.getEmail())
                            .userPkId(user.getUserPkId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private final UserTagGenerator userTagGenerator;
}
