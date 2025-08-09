package org.example.wecambackend.repos;

import org.example.model.user.User;
import org.example.wecambackend.dto.responseDTO.UserSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.userPrivate WHERE u.email = :email")
    Optional<User> findByEmailWithPrivate(@Param("email") String email);
    boolean existsByEmail(String email);

    // 마이페이지 때 사용 - organization 재사용성 을 위함.
    @Query( "SELECT u FROM User u LEFT JOIN FETCH u.organization WHERE u.userPkId = :id")
    Optional<User> findByIdWithOrganization(@Param("id") Long id);

    boolean existsByNameAndUserTag(String name, String userTag);

    @Query("SELECT u.name FROM User u WHERE u.userPkId = :userId")
    String findNameByUserPkId(@Param("userId") Long userId);

    Optional<User> findUserByUserPkId(Long userId);


    @Query("""
    SELECT new org.example.wecambackend.dto.responseDTO.UserSummaryResponse(
        u.userPkId, u.name,SUBSTRING(u.enrollYear, 3, 2),u.organization.organizationName,ui.studentGrade,ui.academicStatus
    ) FROM User u left JOIN UserInformation ui WHERE u.status = org.example.model.common.BaseEntity.Status.ACTIVE
    """)
    List<UserSummaryResponse> findByUserSummaryByOrgIdAndTagsAndIsActive(Long orgId);

}
