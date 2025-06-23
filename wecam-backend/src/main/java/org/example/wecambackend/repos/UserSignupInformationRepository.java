package org.example.wecambackend.repos;

import org.example.model.user.User;
import org.example.model.user.UserSignupInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSignupInformationRepository extends JpaRepository<UserSignupInformation, User> {
    Optional<UserSignupInformation> findByUser_UserPkId(Long userid);
}
