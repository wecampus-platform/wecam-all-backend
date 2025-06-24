package org.example.wecamadminbackend.repos;

import org.example.model.user.UserSignupInformation;
import org.example.wecamadminbackend.dto.PresidentSignupInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PresidentSignupInformationRepository extends JpaRepository<UserSignupInformation,Long> {
    Optional<UserSignupInformation> findByUser_UserPkId(Long Id);
}
