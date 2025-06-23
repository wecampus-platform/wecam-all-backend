package org.example.wecambackend.repos;

import org.example.model.user.User;
import org.example.model.user.UserPrivate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPrivateRepository  extends JpaRepository<UserPrivate, User> {

    //마이페이지 때 사용, 이때 비밀번호는 담기면 안되기 때문에 query 사용함.
    @Query("SELECT u.phoneNumber FROM UserPrivate u WHERE u.user.userPkId = :userId")
    Optional<String> findEncryptedPhoneNumberByUserId(@Param("userId") Long userId);

    boolean existsByPhoneNumber(String phoneNumber);
}
