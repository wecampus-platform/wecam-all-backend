package org.example.wecambackend.repos.council;

import org.example.model.council.Council;
import org.example.wecambackend.dto.projection.OrganizationNameLevelDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CouncilRepository extends JpaRepository<Council,Long> {
     Boolean existsCouncilByOrganization_OrganizationId(Long orgId);

     /**
      * 학생회를 조직 정보와 함께 조회
      *
      * @param councilId 학생회 ID
      * @return 조직 정보가 포함된 학생회 정보
      */
     @Query("SELECT c FROM Council c LEFT JOIN FETCH c.organization WHERE c.id = :councilId")
     Optional<Council> findByIdWithOrganization(@Param("councilId") Long councilId);

     /**
      * 하위 학생회 목록 조회 (단과대/총학생회 전용)
      * <p>
      * 계층 구조에 따라 모든 하위 학생회를 조회합니다:
      * - UNIVERSITY(level 0): COLLEGE, DEPARTMENT 학생회 모두 조회
      * - COLLEGE(level 1): DEPARTMENT 학생회 조회
      * <p>
      * 쿼리 조건:
      * 1. o.level > :parentLevel: 상위 조직보다 낮은 레벨의 모든 조직
      * 2. 직접 하위: o.parent.organizationId = :parentOrganizationId
      * 3. 간접 하위: EXISTS 절을 통해 2단계 하위 조직까지 포함
      *
      * @param parentOrganizationId 상위 조직 ID (현재 접속한 학생회의 조직 ID)
      * @param parentLevel          상위 조직 레벨 (현재 접속한 학생회의 조직 레벨)
      * @return 하위 학생회 목록 (조직 레벨 순으로 정렬)
      */
     @Query("SELECT c FROM Council c " +
             "LEFT JOIN FETCH c.organization o " +
             "LEFT JOIN FETCH c.user u " +
             "LEFT JOIN FETCH u.userInformation ui " +
             "WHERE o.level > :parentLevel " +
             "AND (o.parent.organizationId = :parentOrganizationId " +
             "OR EXISTS (SELECT 1 FROM Organization parent WHERE parent.organizationId = :parentOrganizationId " +
             "AND o.parent.parent = parent)) " +
             "ORDER BY o.level, o.organizationName")
     List<Council> findSubCouncilsByParentOrganization(@Param("parentOrganizationId") Long parentOrganizationId,
                                                       @Param("parentLevel") int parentLevel);


     /*해당 학생회의 조직 이름 출력*/
     @Query("SELECT new org.example.wecambackend.dto.projection.OrganizationNameLevelDto(o.organizationName, o.level) " +
             "FROM Council c JOIN c.organization o WHERE c.id = :councilId")
     Optional<OrganizationNameLevelDto> findOrganizationNameByCouncilId(@Param("councilId") Long councilId);

}
