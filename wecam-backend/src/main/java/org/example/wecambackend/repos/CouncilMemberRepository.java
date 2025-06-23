package org.example.wecambackend.repos;

import org.example.model.CouncilMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long> {

    Boolean existsByUserUserPkIdAndCouncil_Organization_organizationIdAndIsActiveTrue(Long userId, Long organizationId);

}
