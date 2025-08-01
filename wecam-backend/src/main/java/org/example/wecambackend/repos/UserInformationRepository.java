package org.example.wecambackend.repos;

import org.example.model.user.User;
import org.example.model.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInformationRepository extends JpaRepository<UserInformation,Long> {

    Optional<UserInformation> findByUser(User user);
    Optional<UserInformation> findByUser_UserPkId(Long userid);

}
