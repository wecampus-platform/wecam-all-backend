package org.example.wecambackend.repos;

import org.example.model.common.BaseEntity;
import org.example.model.council.CouncilMember;
import org.example.model.user.User;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember,Long> {

//    Boolean existsByUserUserPkIdAndCouncil_Organization_organizationIdAndStatus(Long userId, Long organizationId);
    @Query("select cm.council.id from CouncilMember cm where cm.user.userPkId = :userPkId and cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    List<Long> findCouncilIdByUserUserPkIdAndStatusActive(@Param("userPkId") Long userPkId);

//    boolean existsByUserUserPkIdAndCouncil_IdAndStatus(Long id, Long councilId);

    @Query("SELECT cm.user FROM CouncilMember cm WHERE cm.user.userPkId = :userId AND cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    Optional<User> findUserByUserUserPkIdAndCouncil_IdAndStatusActive(Long userId, Long councilId);


    List<CouncilMember> findByUserUserPkIdAndStatus(Long userPkId, BaseEntity.Status ACTIVE);


    @Query("SELECT new org.example.wecambackend.dto.responseDTO.CouncilMemberResponse(" +
            "u.name, cm.memberRole, u.userPkId) " +
            "FROM CouncilMember cm " +
            "JOIN cm.user u " +
            "WHERE cm.council.id = :councilId AND cm.status = org.example.model.common.BaseEntity.Status.ACTIVE")
    List<CouncilMemberResponse> findAllActiveMembersByCouncilId(@Param("councilId") Long councilId);

}
