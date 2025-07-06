package org.example.wecambackend.repos;


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

    @Query("SELECT new org.example.wecambackend.dto.responseDTO.InvitationCodeResponse(ic.code, ui.name, ic.usageCount, ic.codeType, ic.createdAt, ic.isActive, ic.isUsageLimit, ic.usageLimit) " +
            "FROM InvitationCode ic " +
            "JOIN ic.user u " +
            "JOIN u.userInformation ui "+
            "WHERE ic.council.id = :councilId")
    List<InvitationCodeResponse> findAllByCouncilId(@Param("councilId") Long councilId);

    boolean existsByCode(String code);

    Optional<InvitationCode> findByCodeAndCodeTypeAndIsActive(String code, CodeType codeType,Boolean isActive);


    @Query(" select ic.organization.university.schoolId "+
    "FROM InvitationCode ic " +
    "WHERE ic.code = :code ")
    Long findSchoolIdByCode(@Param("code") String code);

}
