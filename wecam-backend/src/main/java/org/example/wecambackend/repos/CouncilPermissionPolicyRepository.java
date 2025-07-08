package org.example.wecambackend.repos;

import org.example.model.council.Council;
import org.example.model.council.CouncilDepartmentRole;
import org.example.model.council.CouncilPermissionPolicy;
import org.example.model.enums.CouncilPermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilPermissionPolicyRepository extends JpaRepository<CouncilPermissionPolicy, Long> {
    boolean existsByCouncilAndDepartmentRoleAndPermission(Council council, CouncilDepartmentRole role, CouncilPermissionType permission);
}
