package org.example.wecamadminbackend.repos;

import org.example.model.council.CouncilMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long> {

}
