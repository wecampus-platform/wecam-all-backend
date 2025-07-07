package org.example.wecambackend.repos;

import org.example.model.CouncilMember;
import org.example.model.user.User;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long> {

    Boolean existsByUserUserPkIdAndCouncil_Organization_organizationIdAndIsActiveTrue(Long userId, Long organizationId);
    @Query("select cm.council.id from CouncilMember cm where cm.user.userPkId = :userPkId and cm.isActive = true")
    List<Long> findCouncilIdByUserUserPkIdAndIsActiveTrue(@Param("userPkId") Long userPkId);

    boolean existsByUserUserPkIdAndCouncil_IdAndIsActiveTrue(Long id, Long councilId);

    @Query("SELECT cm.user FROM CouncilMember cm WHERE cm.user.userPkId = :userId AND cm.council.id = :councilId AND cm.isActive = true")
    Optional<User> findUserByUserUserPkIdAndCouncil_IdAndIsActiveTrue(Long userId, Long councilId);
    List<CouncilMember> findByUserUserPkIdAndIsActiveTrue( Long userPkId);


    @Query("SELECT new org.example.wecambackend.dto.responseDTO.CouncilMemberResponse(" +
            "ui.name, cm.memberRole, u.userPkId, cm.memberType) " +
            "FROM CouncilMember cm " +
            "JOIN cm.user u " +
            "JOIN u.userInformation ui " +
            "WHERE cm.council.id = :councilId AND cm.isActive = true")
    List<CouncilMemberResponse> findAllActiveMembersByCouncilId(@Param("councilId") Long councilId);

}
