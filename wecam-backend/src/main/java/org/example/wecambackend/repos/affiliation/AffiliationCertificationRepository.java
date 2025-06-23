package org.example.wecambackend.repos.affiliation;

import org.example.model.user.User;
//import org.example.model.affiliation.AffiliationCertification;
import org.example.model.affiliation.AffiliationCertification;
import org.example.model.affiliation.AffiliationCertificationId;
import org.example.model.enums.AuthenticationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AffiliationCertificationRepository extends
        JpaRepository<AffiliationCertification, AffiliationCertificationId>{


    //조직별 요청서 확인
    List<AffiliationCertification> findByOrganizationOrganizationIdOrderByRequestedAtDesc(Long organizationId);

    // 유저 당 신입생 인증 한번, 재학생 인증 한번
    boolean existsByUserAndAuthenticationType(User user, AuthenticationType authenticationType);

}
