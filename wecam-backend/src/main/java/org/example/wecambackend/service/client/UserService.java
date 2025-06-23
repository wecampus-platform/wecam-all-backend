package org.example.wecambackend.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Organization;
import org.example.model.University;
import org.example.model.user.User;
import org.example.model.enums.AuthenticationType;
import org.example.model.enums.UserRole;
import org.example.wecambackend.repos.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    // TODO:  user.setOrganization(organization); 값 확인 해봐야 함. ORganizationId가 안들어가짐.
    private final UserRepository userRepository;
    public void updateUserRoleAndStatus(User user, Organization organization, University university, AuthenticationType authenticationType,String enrollYear) {
        UserRole beforeRole = user.getRole();
        if (authenticationType == AuthenticationType.NEW_STUDENT) {
            user.setRole(UserRole.GUEST_STUDENT); // enum 기반
        }
        else {
            user.setRole(UserRole.STUDENT);
        }
        user.setEnrollYear(enrollYear);
        if (!beforeRole.equals(user.getRole())) {
            user.setExpiresAt(null); // TODO : 우선 ROLE 이 바뀌면 ExpiresAt은 비활성화 시켰음.
        }
        user.setAuthentication(true);
        user.setUniversity(university);
        user.setOrganization(organization);

        userRepository.save(user);
        log.info("[유저 상태 변경] {}: {} → {}, 학교: {}, 조직: {}",
                user.getEmail(),
                beforeRole.name(),
                user.getRole().name(),
                university.getSchoolName(),
                organization.getOrganizationName());
    }
}
