package org.example.wecambackend.service.client;

import org.example.model.enums.UserRole;
import org.example.model.user.UserSignupInformation;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.repos.UserSignupInformationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.responseDTO.MyPageResponse;
import org.example.model.organization.Organization;
import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.example.wecambackend.repos.UserInformationRepository;
import org.example.wecambackend.repos.UserPrivateRepository;
import org.example.wecambackend.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.example.wecambackend.util.PhoneEncryptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class MyPageService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final PhoneEncryptor phoneEncryptor;
    private final UserPrivateRepository userPrivateRepository;

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(UserDetailsImpl currentUser) {

        // 1. 유저 + organization 즉시 로딩
        User user = userRepository.findByIdWithOrganization(currentUser.getId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        // 3. 전화번호 복호화
        String phoneNumber = maskPhoneNumber(phoneEncryptor.decrypt(
                userPrivateRepository.findEncryptedPhoneNumberByUserId(currentUser.getId())
                        .orElseThrow(() -> new IllegalStateException("전화번호 정보 없음.")))
        );

        if (user.getRole().equals(UserRole.UNAUTH)) {

            UserSignupInformation signInfo = userSignupInformationRepository.findByUser_UserPkId(currentUser.getId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
            return MyPageResponse.builder()
                    .role(user.getRole())
                    .isAuthentication(user.isAuthentication())
                    .userEmail(user.getEmail())
                    .username(signInfo.getName()) // 이름은 User.username 필드로부터
                    .phoneNumber(phoneNumber)
                    .build();

        }


        else {

            // 2. 유저 정보
            UserInformation info = userInformationRepository.findByUser_UserPkId(currentUser.getId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
            // 4. 조직 계층 이름 리스트
            List<String> hierarchyList = getOrganizationNameHierarchy(user.getOrganization());

            return MyPageResponse.builder()
                    .organizationId(user.getOrganizationId())
                    .role(user.getRole())
                    .academicStatus(info.getAcademicStatus())
                    .isAuthentication(user.isAuthentication())
                    .isCouncilFee(info.getIsCouncilFee())
                    .nickName(info.getNickname())
                    .student_grade(info.getStudentGrade())
                    .userEmail(user.getEmail())
                    .phoneNumber(phoneNumber)
                    .studentId(info.getStudentId())
                    .universityId(info.getUniversity().getSchoolId())
                    .organizationHierarchyList(hierarchyList)
                    .username(info.getName())
                    .build();

    }
}


    // 조직 계층 이름 추출
    public List<String> getOrganizationNameHierarchy(Organization org) {
        List<String> names = new ArrayList<>();
        while (org != null) {
            names.add(org.getOrganizationName());
            org = org.getParent();
        }
        Collections.reverse(names);
        return names;
    }

    // 디자인 요구사항 : 마스킹 전화번호를 위함.
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) return phoneNumber;
        return phoneNumber.substring(0, 3) + "-****-" + phoneNumber.substring(phoneNumber.length() - 4);
    }

    private final UserSignupInformationRepository userSignupInformationRepository;
}
