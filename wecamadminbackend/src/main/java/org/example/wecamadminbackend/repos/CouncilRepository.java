package org.example.wecamadminbackend.repos;

import org.example.model.Council;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouncilRepository extends JpaRepository<Council,Long> {
    boolean existsCouncilByOrganization_OrganizationId(Long organizationId);

}
