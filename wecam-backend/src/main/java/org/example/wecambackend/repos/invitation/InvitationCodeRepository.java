package org.example.wecambackend.repos.invitation;


import org.example.model.enums.CodeType;
import org.example.model.invitation.InvitationCode;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface InvitationCodeRepository extends JpaRepository<InvitationCode,Long> {

    @Query("SELECT new org.example.wecambackend.dto.responseDTO.InvitationCodeResponse(ic.id,ic.code, u.name, ic.codeType, ic.createdAt, ic.isActive, ic.expirationDate) " +
            "FROM InvitationCode ic " +
            "JOIN ic.user u " +
            "WHERE ic.council.id = :councilId")
    List<InvitationCodeResponse> findAllByCouncilId(@Param("councilId") Long councilId);

    boolean existsByCode(String code);

    Optional<InvitationCode> findByCodeAndCodeTypeAndIsActive(String code, CodeType codeType,Boolean isActive);


    @Query(" select ic.organization.university.schoolId "+
    "FROM InvitationCode ic " +
    "WHERE ic.code = :code ")
    Long findSchoolIdByCode(@Param("code") String code);

}
