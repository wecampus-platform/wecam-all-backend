package org.example.wecambackend.repos;

import org.example.model.common.BaseEntity;
import org.example.model.council.CouncilMember;
import org.example.model.user.User;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long> {

//    Boolean existsByUserUserPkIdAndCouncil_Organization_organizationIdAndStatus(Long userId, Long organizationId);
    @Query("select cm.council.id from CouncilMember cm where cm.user.userPkId = :userPkId and cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    List<Long> findCouncilIdByUserUserPkIdAndStatusActive(@Param("userPkId") Long userPkId);

//    boolean existsByUserUserPkIdAndCouncil_IdAndStatus(Long id, Long councilId);

    @Query("SELECT cm.user FROM CouncilMember cm WHERE cm.user.userPkId = :userId AND cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    Optional<User> findUserByUserUserPkIdAndCouncil_IdAndStatusActive(Long userId, Long councilId);


    List<CouncilMember> findByUserUserPkIdAndStatus(Long userPkId, BaseEntity.Status ACTIVE);


    @Query("SELECT new org.example.wecambackend.dto.responseDTO.CouncilMemberResponse(" +
            "u.name, cm.memberRole, u.userPkId, cm.exitType, cm.expulsionReason) " +
            "FROM CouncilMember cm " +
            "JOIN cm.user u " +
            "WHERE cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE " +
            "AND cm.exitType = org.example.model.enums.ExitType.ACTIVE")
    List<CouncilMemberResponse> findAllActiveMembersByCouncilId(@Param("councilId") Long councilId);

    /**
     * 특정 학생회의 활성 상태인 모든 학생회원을 상세 정보와 함께 조회
     * 
     * 조회되는 정보:
     * - 학생회원 기본 정보 (CouncilMember)
     * - 부서 정보 (CouncilDepartment)
     * - 부서 내 역할 정보 (CouncilDepartmentRole)
     * - 사용자 정보 (User)
     * - 사용자 프로필 정보 (UserInformation)
     * 
     * @param councilId 학생회 ID
     * @return 상세 정보가 포함된 학생회원 목록
     */
    @Query("SELECT cm FROM CouncilMember cm " +
            "LEFT JOIN FETCH cm.department " +
            "LEFT JOIN FETCH cm.departmentRole " +
            "LEFT JOIN FETCH cm.user u " +
            "LEFT JOIN FETCH u.userInformation " +
            "WHERE cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE " +
            "AND cm.exitType = org.example.model.enums.ExitType.ACTIVE")
    List<CouncilMember> findAllActiveMembersWithDetailsByCouncilId(@Param("councilId") Long councilId);

}
