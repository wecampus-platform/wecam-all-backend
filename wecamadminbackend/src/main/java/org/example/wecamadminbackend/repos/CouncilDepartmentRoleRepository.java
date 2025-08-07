package org.example.wecamadminbackend.repos;


import org.example.model.council.CouncilDepartment;
import org.example.model.council.CouncilDepartmentRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouncilDepartmentRoleRepository extends JpaRepository<CouncilDepartmentRole, Long> {
    List<CouncilDepartmentRole> findByDepartment(CouncilDepartment department);
}
