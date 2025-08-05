package org.example.wecamadminbackend.repos;

import org.example.model.organization.OrganizationRequest;
import org.example.model.enums.RequestStatus;
import org.example.wecamadminbackend.dto.request.OrganizationRequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationRequestRepository extends JpaRepository<OrganizationRequest,Long> {

    @Query("SELECT new org.example.wecamadminbackend.dto.request.OrganizationRequestDTO(" +
            "o.requestId, u.email, o.requestStatus, o.createdAt, o.councilName, o.schoolName, t) " +
            "FROM OrganizationRequest o " +
            "LEFT JOIN o.user u " +
            "LEFT JOIN o.targetOrganization t " +
            "WHERE o.requestStatus = :requestStatus")
    List<OrganizationRequestDTO> findRequestDtosByStatus(@Param("requestStatus") RequestStatus requestStatus);

// 예: PENDING 상태만 조회
    //**    PENDING,
    //    APPROVED,
    //    REJECTED


}
