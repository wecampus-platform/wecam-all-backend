package org.example.wecambackend.repos;

import org.example.model.council.Council;
import org.example.model.council.CouncilDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouncilDepartmentRepository extends JpaRepository<CouncilDepartment, Long> {
    List<CouncilDepartment> findByCouncilAndIsActiveTrue(Council council);
}
