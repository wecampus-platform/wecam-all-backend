package org.example.wecambackend.repos.organization;

import org.example.model.organization.OrganizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRequestRepository extends JpaRepository<OrganizationRequest,Long> {
    
    /**
     * 워크스페이스 생성 요청을 사용자와 조직 정보와 함께 조회
     * @param requestId 워크스페이스 생성 요청 ID
     * @return 사용자와 조직 정보가 포함된 요청 정보
     */
    @Query("SELECT o FROM OrganizationRequest o " +
           "LEFT JOIN FETCH o.user u " +
           "LEFT JOIN FETCH o.targetOrganization t " +
           "WHERE o.requestId = :requestId")
    Optional<OrganizationRequest> findByIdWithUserAndOrganization(@Param("requestId") Long requestId);
}
