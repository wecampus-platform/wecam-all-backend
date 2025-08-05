package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.enums.UserRole;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.repos.UserInformationRepository;
import org.example.wecambackend.repos.UserRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
} 