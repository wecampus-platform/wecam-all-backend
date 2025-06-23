package org.example.wecambackend.repos;

import org.example.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.userPrivate WHERE u.email = :email")
    Optional<User> findByEmailWithPrivate(@Param("email") String email);
    boolean existsByEmail(String email);

    // 마이페이지 때 사용 - organization 재사용성 을 위함.
    @Query( "SELECT u FROM User u LEFT JOIN FETCH u.organization WHERE u.userPkId = :id")
    Optional<User> findByIdWithOrganization(@Param("id") Long id);
}
