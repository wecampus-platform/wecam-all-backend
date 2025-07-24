package org.example.wecambackend.repos;

import org.example.model.council.CouncilMemberPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilMemberPermissionRepository extends JpaRepository<CouncilMemberPermission, Long> {
}
