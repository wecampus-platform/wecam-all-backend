package org.example.wecambackend.repos.council;

import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.council.CouncilDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouncilDepartmentRepository extends JpaRepository<CouncilDepartment, Long> {
    List<CouncilDepartment> findByCouncilAndStatus(Council council, BaseEntity.Status status);
    List<CouncilDepartment> findByCouncilId(Long councilId);
}
