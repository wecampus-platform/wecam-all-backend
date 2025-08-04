package org.example.wecambackend.repos;

import org.example.model.council.Council;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouncilRepository extends JpaRepository<Council,Long> {
     Boolean existsCouncilByOrganization_OrganizationId(Long orgId);
     
     /**
      * 학생회를 조직 정보와 함께 조회
      * @param councilId 학생회 ID
      * @return 조직 정보가 포함된 학생회 정보
      */
     @Query("SELECT c FROM Council c LEFT JOIN FETCH c.organization WHERE c.id = :councilId")
     Optional<Council> findByIdWithOrganization(@Param("councilId") Long councilId);
}
