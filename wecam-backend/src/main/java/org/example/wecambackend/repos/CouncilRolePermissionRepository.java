package org.example.wecambackend.repos;

import org.example.model.council.CouncilRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilRolePermissionRepository extends JpaRepository<CouncilRolePermission, Long> {
}
