package org.example.wecambackend.service.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Council;
import org.example.model.CouncilMember;
import org.example.model.University;
import org.example.model.enums.AcademicStatus;
import org.example.model.enums.CodeType;
import org.example.model.enums.MemberRole;
import org.example.model.enums.UserRole;
import org.example.model.invitation.InvitationCode;
import org.example.model.invitation.InvitationHistory;
import org.example.model.organization.Organization; // <- 실제 entity
import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.example.model.user.UserSignupInformation;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.requestDTO.InvitationCreateRequest;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.example.wecambackend.repos.*;
import org.example.wecambackend.repos.organization.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationCodeService {
    private final InvitationCodeRepository invitationCodeRepository;
    private final InvitationHistoryRepository invitationHistoryRepository;
    private final UserRepository userRepository;
    private final CouncilRepository councilRepository;
    private final UserSignupInformationRepository userSignupInformationRepository;
    private final OrganizationRepository organizationRepository;
    private final UserInformationRepository userInformationRepository;
    private final CouncilMemberRepository councilMemberRepository;

    public List<InvitationCodeResponse> findByCouncilId(Long councilId) {
        return invitationCodeRepository.findAllByCouncilId(councilId);
    }

    @Transactional
    public void createInvitationCode(CodeType codeType,InvitationCreateRequest requestDto, Long userId,Long councilId){
    // 1. 유저 조회
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

    // 2. 유저가 소속된 조직 또는 학생회 가져오기 (예: 학과 학생회 기준)
    Council council = councilRepository.findById(councilId)
            .orElseThrow(() -> new IllegalArgumentException("COUNCIL_NOT_FOUND"));

    // 3. 소속 조직
    Organization organization = council.getOrganization();

    String generatedCode = generateUniqueCode();  // 여기서 자동 생성


        // 4. 초대코드 엔티티 생성
    InvitationCode invitationCode = InvitationCode.builder()
            .code(generatedCode)
            .codeType(codeType)
            .isUsageLimit(requestDto.getIsUsageLimit())
            .usageLimit(requestDto.getIsUsageLimit() ? requestDto.getUsageLimit() : null)
            .isActive(true)
            .user(user)
            .council(council)
            .organization(organization)
            .build();

    // 5. 저장
    invitationCodeRepository.save(invitationCode);
    }

    private static final int CODE_LENGTH = 10;
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

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않습니다."));
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
                    .memberLevel(3)
                    .isActive(true)
                    .council(invitationCode.getCouncil())
                    .memberType("")
                    .build();
            councilMemberRepository.save(councilMember);

        } else if (codeType.equals(CodeType.student_member)&& status) {
            UserRole userRole = UserRole.STUDENT;
            createUserInformationByCode(userEnrollYear,userRole,user,userSignupInformation.getName(),organization);
        }
        else {
            throw new IllegalArgumentException("다시 입력하세요.");
        }
        int usageCount = invitationCode.getUsageCount();
        int codeLimit = invitationCode.getUsageLimit();
        if (invitationCode.getIsUsageLimit() && usageCount<codeLimit
        ) {
            invitationCode.setUsageCount(usageCount + 1);
            if (codeLimit==usageCount+1) {
                invitationCode.setIsActive(Boolean.FALSE);
            }
        }
        else {
            throw new IllegalArgumentException("유효하지 않은 초대코드입니다.");
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
                                    .name(name)
                                    .university(university)
                                    .academicStatus(AcademicStatus.ENROLLED)
                                    .build();
                            userInformationRepository.save(userInfo);
                        }
                );
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
                    .orElseThrow(() -> new IllegalArgumentException("해당 코드가 존재하지 않습니다."));
            return invitationCode;

        }
}
