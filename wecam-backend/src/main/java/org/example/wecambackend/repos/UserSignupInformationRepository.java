package org.example.wecambackend.repos;

import org.example.model.user.User;
import org.example.model.user.UserSignupInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSignupInformationRepository extends JpaRepository<UserSignupInformation, User> {
    Optional<UserSignupInformation> findByUser_UserPkId(Long userid);

    @Query(value = """
    SELECT 
      CASE 
        WHEN usi.input_school_name IS NOT NULL AND usi.input_school_name != '' 
          THEN usi.input_school_name 
        ELSE s.school_name 
      END AS school_name 
    FROM user_signup_information usi 
    LEFT JOIN university s ON usi.select_school_id = s.id 
    WHERE usi.user_pk_id = :userPkId
    """, nativeQuery = true)
    String findSchoolNameByUserPkId(@Param("userPkId") Long userPkId);

}
