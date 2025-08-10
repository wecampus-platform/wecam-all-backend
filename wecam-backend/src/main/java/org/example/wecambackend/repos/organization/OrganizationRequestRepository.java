package org.example.wecambackend.repos.organization;

import org.example.model.enums.RequestStatus;
import org.example.model.organization.OrganizationRequest;

import org.example.wecambackend.dto.request.organization.OrganizationRequestDTO;
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

    /**
     * 현재 학생회의 하위 조직에서 요청한 워크스페이스 생성 요청 목록을 조회
     * @param parentOrganizationId 상위 조직 ID
     * @return 하위 조직의 워크스페이스 생성 요청 목록
     */
    @Query("SELECT o FROM OrganizationRequest o " +
           "LEFT JOIN FETCH o.user u " +
           "LEFT JOIN FETCH u.userInformation ui " +
           "LEFT JOIN FETCH o.targetOrganization t " +
           "WHERE t.parent.organizationId IN (" +
           "  SELECT child.organizationId FROM Organization child " +
           "  WHERE child.parent.organizationId = :parentOrganizationId " +
           "  OR child.organizationId = :parentOrganizationId" +
           ") " +
           "ORDER BY o.createdAt DESC")
    List<OrganizationRequest> findSubOrganizationRequestsByParentId(@Param("parentOrganizationId") Long parentOrganizationId);
    @Query("SELECT new org.example.wecambackend.dto.request.organization.OrganizationRequestDTO(" +
            "o.requestId, u.email, o.requestStatus, o.createdAt, o.councilName, o.schoolName, t) " +
            "FROM OrganizationRequest o " +
            "LEFT JOIN o.user u " +
            "LEFT JOIN o.targetOrganization t " +
            "WHERE o.requestStatus = :requestStatus")
    List<OrganizationRequestDTO> findRequestDtosByStatus(@Param("status") RequestStatus requestStatus);


}
