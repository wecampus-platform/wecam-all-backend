package org.example.wecambackend.repos;

import org.example.model.common.BaseEntity;
import org.example.model.council.CouncilMember;
import org.example.model.user.User;
import org.example.wecambackend.dto.responseDTO.CouncilCompositionResponse;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.model.council.CouncilDepartment;
import org.example.model.enums.MemberRole;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long>, CouncilMemberCustomRepository {

//    Boolean existsByUserUserPkIdAndCouncil_Organization_organizationIdAndStatus(Long userId, Long organizationId);
    @Query("select cm.council.id from CouncilMember cm where cm.user.userPkId = :userPkId and cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    List<Long> findCouncilIdByUserUserPkIdAndStatusActive(@Param("userPkId") Long userPkId);

//    boolean existsByUserUserPkIdAndCouncil_IdAndStatus(Long id, Long councilId);

    @Query("SELECT cm.user FROM CouncilMember cm WHERE cm.user.userPkId = :userId AND cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    Optional<User> findUserByUserUserPkIdAndCouncil_IdAndStatusActive(Long userId, Long councilId);


    List<CouncilMember> findByUserUserPkIdAndStatus(Long userPkId, BaseEntity.Status ACTIVE);


    /**
     * 특정 학생회(councilId)에 속한 활성화된(CouncilMember.isActive = true) 구성원 중에서
     * 다음 조건 중 하나를 만족하는 사람을 조회한다:
     *   1. 부서가 아예 배정되지 않은 사람 (cm.department IS NULL)
     *   2. 부서가 특정 부서 ID(departmentId)인 사람
     *
     * 사용 예:
     *   - 미배치 인원만 조회하고 싶을 때: departmentId = null
     *   - 특정 부서 인원만 조회하고 싶을 때: departmentId = 5L
     *
     * 주의: 미배치 + 특정 부서 둘 다 동시에 포함하는 건 지원하지 않음.
     */
    //미배치 인원도 뜨게 해야 돼서 LEFTJOIN 으로 묶었습니다.
    @Query("SELECT new org.example.wecambackend.dto.responseDTO.CouncilMemberResponse(" +
            "u.name, cm.memberRole, d.id,dr.id, u.userPkId, cm.exitType, cm.expulsionReason,d.name,dr.name) " +
            "FROM CouncilMember cm " +
            "JOIN cm.user u " +
            "LEFT JOIN cm.department d " +
            "LEFT JOIN cm.departmentRole dr " +
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

    /**
     * 특정 부서의 특정 level 역할을 가진 활성 멤버를 조회
     * 
     * @param department 부서
     * @param level 역할 레벨
     * @return 해당 부서와 level을 가진 활성 멤버
     */
    @Query("SELECT cm FROM CouncilMember cm " +
            "JOIN cm.departmentRole cdr " +
            "WHERE cm.department = :department " +
            "AND cdr.level = :level " +
            "AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE " +
            "AND cm.exitType = org.example.model.enums.ExitType.ACTIVE")
    Optional<CouncilMember> findByDepartmentAndRoleLevel(@Param("department") CouncilDepartment department, @Param("level") Integer level);

    /**
     * 특정 학생회의 특정 역할을 가진 활성 멤버를 조회
     * 
     * @param councilId 학생회 ID
     * @param memberRole 멤버 역할
     * @return 해당 학생회와 역할을 가진 활성 멤버
     */
    @Query("SELECT cm FROM CouncilMember cm " +
            "WHERE cm.council.id = :councilId " +
            "AND cm.memberRole = :memberRole " +
            "AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE " +
            "AND cm.exitType = org.example.model.enums.ExitType.ACTIVE")
    Optional<CouncilMember> findByCouncilAndMemberRole(@Param("councilId") Long councilId, @Param("memberRole") MemberRole memberRole);

    //미배치 인원
    @Query("""
    SELECT cm.user.userPkId , cm.user.name
    FROM CouncilMember cm
    WHERE cm.council.id = :councilId AND cm.department is null
""")
    List<Map<Long,String>> findUnassignedMembersByCouncilId(Long councilId);



    @Query( "SELECT new org.example.wecambackend.dto.responseDTO.CouncilCompositionResponse(" +
            "u.name, cm.memberRole, u.userPkId, u.enrollYear, d.name, dr.name)" +
            "FROM CouncilMember  cm " +
            "JOIN cm.user u " +
            "LEFT JOIN cm.department d " +
            "LEFT JOIN cm.departmentRole dr " +
            "WHERE cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE AND cm.department.id = :departmentId " +
            "ORDER BY cm.createdAt DESC" )
    List<CouncilCompositionResponse> findByCouncilIdAndDepartmentId(
            @Param("councilId") Long councilId,
            @Param("departmentId") Long departmentId
    );

}
