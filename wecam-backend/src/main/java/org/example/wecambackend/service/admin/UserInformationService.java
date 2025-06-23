package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.example.model.affiliation.AffiliationCertification;
import org.example.model.enums.AuthenticationType;
import org.example.wecambackend.repos.UserInformationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInformationService {


    //TODO : 조직 수정 까지는 구현 안함. 단순 신입생 인증 했을 때와 재학생 인증 시 다를 수 있는 정보가 있을 때 만들 예정
    private final UserInformationRepository userInformationRepository;

    public void createUserInformation(User user, AffiliationCertification cert, AuthenticationType type) {
        UserInformation info = userInformationRepository.findByUser(user)
                .orElse(null); // 있으면 수정하는 방향 , 없으면 생성
        if (info == null) {
            info = UserInformation.builder()
                    .user(user)
                    .name(cert.getUsername())
                    .university(cert.getUniversity())
                    .isAuthentication(Boolean.TRUE)
                    .isCouncilFee(Boolean.FALSE)
                    .studentGrade(cert.getOcrschoolGrade())
                    .build();
            userInformationRepository.save(info);
        }

    }
}
