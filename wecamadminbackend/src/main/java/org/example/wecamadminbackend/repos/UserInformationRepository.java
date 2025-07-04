package org.example.wecamadminbackend.repos;

import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInformationRepository extends JpaRepository<UserInformation,Long> {
    Optional<UserInformation> findByUser(User user);
}
