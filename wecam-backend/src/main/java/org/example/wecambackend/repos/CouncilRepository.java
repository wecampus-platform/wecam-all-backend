package org.example.wecambackend.repos;

import org.example.model.Council;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouncilRepository extends JpaRepository<Council,Long> {
     Boolean existsCouncilByOrganization_OrganizationId(Long orgId);
}
