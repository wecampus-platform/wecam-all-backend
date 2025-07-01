package org.example.wecambackend.repos.organization;

import org.example.model.organization.OrganizationRequestFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface OrganizationRequestFileRepository extends JpaRepository<OrganizationRequestFile, Long> {
    List<OrganizationRequestFile> findByOrganizationRequest_RequestId(Long requestId);

    OrganizationRequestFile findByUuid(UUID uuid);
}
