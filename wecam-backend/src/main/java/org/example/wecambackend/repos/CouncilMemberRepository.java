package org.example.wecambackend.repos;

import org.example.model.council.CouncilMember;
import org.example.model.user.User;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long> {

    Boolean existsByUserUserPkIdAndCouncil_Organization_organizationIdAndIsActiveTrue(Long userId, Long organizationId);
    @Query("select cm.council.id from CouncilMember cm where cm.user.userPkId = :userPkId and cm.isActive = true")
    List<Long> findCouncilIdByUserUserPkIdAndIsActiveTrue(@Param("userPkId") Long userPkId);

    boolean existsByUserUserPkIdAndCouncil_IdAndIsActiveTrue(Long id, Long councilId);

    @Query("SELECT cm.user FROM CouncilMember cm WHERE cm.user.userPkId = :userId AND cm.council.id = :councilId AND cm.isActive = true")
    Optional<User> findUserByUserUserPkIdAndCouncil_IdAndIsActiveTrue(Long userId, Long councilId);
    List<CouncilMember> findByUserUserPkIdAndIsActiveTrue( Long userPkId);


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
            "WHERE cm.council.id = :councilId AND cm.isActive = true AND ((:departmentId IS NULL AND cm.department IS NULL) OR (:departmentId IS NOT NULL AND cm.department.id = :departmentId))")
    List<CouncilMemberResponse> findByCouncilIdAndDepartmentOrUnassigned(@Param("councilId") Long councilId, @Param("departmentId") Long departmentId);




    @Query("SELECT new org.example.wecambackend.dto.responseDTO.CouncilMemberResponse(" +
            "u.name, cm.memberRole, u.userPkId,d.id, dr.id,d.name,dr.name) " +
            "FROM CouncilMember cm " +
            "JOIN cm.user u " +
            "LEFT JOIN cm.department d " +
            "LEFT JOIN cm.departmentRole dr " +
            "WHERE cm.council.id = :councilId AND cm.isActive = true")
    List<CouncilMemberResponse> findAllActiveMembersByCouncilId(@Param("councilId") Long councilId);


    //미배치 인원
    @Query("""
    SELECT cm.user.userPkId , cm.user.name
    FROM CouncilMember cm
    WHERE cm.council.id = :councilId AND cm.department is null
""")
    List<Map<Long,String>> findUnassignedMembersByCouncilId(Long councilId);
}
