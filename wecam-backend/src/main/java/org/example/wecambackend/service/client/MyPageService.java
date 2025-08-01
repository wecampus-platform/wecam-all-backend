package org.example.wecambackend.service.client;

import org.example.model.enums.UserRole;
import org.example.model.user.UserSignupInformation;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.requestDTO.MyPageOrganizationEditRequest;
import org.example.wecambackend.repos.UserSignupInformationRepository;
import org.springframework.beans.factory.annotation.Value;
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

import static org.example.wecambackend.common.response.BaseResponseStatus.USER_NOT_FOUND;


@Slf4j
@RequiredArgsConstructor
@Service
public class MyPageService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final PhoneEncryptor phoneEncryptor;
    private final UserPrivateRepository userPrivateRepository;

    @Value("${app.file.url-prefix}")
    private String urlPrefix;

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(UserDetailsImpl currentUser) {

        // 1. 유저 + organization 즉시 로딩
        User user = userRepository.findByIdWithOrganization(currentUser.getId())
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        // 3. 전화번호 복호화
        String phoneNumber = maskPhoneNumber(phoneEncryptor.decrypt(
                userPrivateRepository.findEncryptedPhoneNumberByUserId(currentUser.getId())
                        .orElseThrow(() -> new BaseException(BaseResponseStatus.PHONE_INFO_NOT_FOUND)))
        );

        if (user.getRole().equals(UserRole.UNAUTH)) {

            UserSignupInformation signInfo = userSignupInformationRepository.findByUser_UserPkId(currentUser.getId())
                    .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

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
                    .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
            // 4. 조직 계층 이름 리스트
            List<String> hierarchyList = getOrganizationNameHierarchy(user.getOrganization());

            String path = info.getProfileImagePath(); // e.g. "PROFILE/uuid_파일명.png"
            String imageUrl = null, thumbnailUrl = null;
            if (path != null) {
                imageUrl      = urlPrefix + "/" + path;
                thumbnailUrl  = urlPrefix + "/" + path.replaceFirst("PROFILE/", "PROFILE_THUMB/");
            }

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
                    .username(user.getName())
                    .profileImageUrl(imageUrl)
                    .profileThumbnailUrl(thumbnailUrl)
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



    //마이페이지 정보 편집 - 이름 수정
    @Transactional
    public String editUserName(Long userId,String userName){
        User user = userRepository.findUserByUserPkId(userId).orElseThrow(()->new BaseException(USER_NOT_FOUND));
        if (userName != null )
        {user.setName(userName);
        userRepository.save(user);}
        return userName;
    }

    //마이페이지 정보 편집 - 학년 , 재학여부, 학번 수정
    @Transactional
    public void editUserOrganizationInfo(Long userId, MyPageOrganizationEditRequest req){
        UserInformation userInformation = userInformationRepository.findByUser_UserPkId(userId).orElseThrow(()->
                new BaseException(USER_NOT_FOUND));
        boolean isModified = false;

        if (req.getAcademicStatus() != null) {
            userInformation.setAcademicStatus(req.getAcademicStatus());
            isModified = true;
        }
        if (req.getSchoolGrade() != null) {
            userInformation.setStudentGrade(req.getSchoolGrade());
            isModified = true;
        }
        if (req.getStudentNumber() != null) {
            userInformation.setStudentId(req.getStudentNumber());
            isModified = true;
        }

        if (isModified) {
            userInformationRepository.save(userInformation);
        }

    }

    private final UserSignupInformationRepository userSignupInformationRepository;
}
