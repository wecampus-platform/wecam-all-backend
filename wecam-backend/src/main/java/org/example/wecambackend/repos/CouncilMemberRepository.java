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

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long> {

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
            "u.name, cm.memberRole, u.userPkId,d.id, dr.id, d.name, dr.name) " +
            "FROM CouncilMember cm " +
            "JOIN cm.user u " +
            "LEFT JOIN cm.department d " +
            "LEFT JOIN cm.departmentRole dr " +
            "WHERE cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    List<CouncilMemberResponse> findByCouncilIdAndDepartmentOrUnassigned(@Param("councilId") Long councilId, @Param("departmentId") Long departmentId);




    @Query("SELECT new org.example.wecambackend.dto.responseDTO.CouncilMemberResponse(" +
            "u.name, cm.memberRole, u.userPkId, d.id, dr.id, d.name, dr.name) " +
            "FROM CouncilMember cm " +
            "JOIN cm.user u " +
            "LEFT JOIN cm.department d " +
            "LEFT JOIN cm.departmentRole dr " +
            "WHERE cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE " +
            "ORDER BY d.createdAt DESC")
    List<CouncilMemberResponse> findAllActiveMembersByCouncilId(@Param("councilId") Long councilId);

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
