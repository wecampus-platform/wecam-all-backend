package org.example.wecambackend.service.admin.common;

import lombok.AllArgsConstructor;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.dto.responseDTO.CouncilVisibleUserDTO;
import org.example.wecambackend.repos.UserInformationRepository;
import org.example.wecambackend.repos.UserPrivateRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

import static org.example.wecambackend.common.response.BaseResponseStatus.INVALID_USER;


//TODO: 추후 로직에 권한 설정하면 됨!!!
@AllArgsConstructor // 생성자 주입을 자동으로 생성해주는 Lombok 어노테이션
@Component // Spring이 이 클래스를 빈으로 등록할 수 있도록 해주는 어노테이션
public class UserInfoAssembler {

    // 유저의 개인정보(전화번호 등)를 조회하기 위한 리포지토리
    private final UserPrivateRepository userPrivateRepository;

    // 유저의 일반 정보(이름 등)를 조회하기 위한 리포지토리
    private final UserInformationRepository userInformationRepository;

    /**
     * CouncilVisibleUserDTO 객체를 생성해 반환하는 메서드.
     * - User 엔티티를 기반으로 추가 정보(이름, 전화번호)를 조회하여 DTO로 조립
     * @param user 조회 대상 User 엔티티
     * @return CouncilVisibleUserDTO (유저 id, 이름, 이메일, 전화번호 포함)
     */
    public CouncilVisibleUserDTO buildUserInfo(User user) {
        String phoneNumber = findUserPrivateByUserId(user.getUserPkId()); // 암호화된 전화번호 조회
        String name = findUserInformationByUserId(user.getUserPkId());   // 이름 조회

        // Builder 패턴으로 DTO 생성
        return CouncilVisibleUserDTO.builder()
                .userId(user.getUserPkId())
                .name(name)
                .email(user.getEmail())
                .phoneNumber(phoneNumber)
                .build();
    }

    /**
     * userId를 기반으로 암호화된 전화번호를 조회.
     * 해당 유저가 존재하지 않을 경우 예외 발생
     * @param userId 유저의 PK
     * @return 전화번호 (복호화되지 않은 암호화 상태일 수도 있음)
     */
    public String findUserPrivateByUserId(Long userId) {
        return userPrivateRepository.findEncryptedPhoneNumberByUserId(userId)
                .orElseThrow(() -> new BaseException(INVALID_USER));
    }

    /**
     * userId를 기반으로 이름을 조회.
     * - 정보가 존재하지 않을 경우 빈 문자열("") 반환 (예외 발생 X)
     * @param userId 유저의 PK
     * @return 이름 또는 빈 문자열
     */
    public String findUserInformationByUserId(Long userId) {
        return Optional.ofNullable(
                userInformationRepository.findNameByUserId(userId)
        ).orElse("");
    }
}
