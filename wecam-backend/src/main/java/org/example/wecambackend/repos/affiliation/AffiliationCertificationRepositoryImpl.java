//package org.example.wecambackend.repos.affiliation;
//
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.example.wecambackend.dto.responseDTO.AffiliationVerificationResponse;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public class AffiliationCertificationRepositoryImpl implements AffiliationCertificationRepositoryCustom {
//
//    private final EntityManager em;
//
//    @Override
//    public List<AffiliationVerificationResponse> findAllRequestsByOrganization(Long councilOrganizationId) {
//        return em.createQuery(
//                """
//                SELECT new org.example.wecambackend.dto.responseDTO.AffiliationVerificationResponse(
//                    ac.id.userId,
//                    ac.id.authenticationType,
//                    ac.ocrUserName,
//                    ac.ocrSchoolName,
//                    ac.ocrOrganizationName,
//                    ac.ocrEnrollYear,
//                    u.email,
//                    ac.ocrResult,
//                    ac.status,
//                    ac.requestedAt,
//                    af.filePath)
//                FROM AffiliationCertification ac
//                JOIN User u ON ac.id.userId = u.id
//                LEFT JOIN AffiliationFile af
//                    ON af.pkUploadUserId = ac.id.userId AND af.authenticationType = ac.id.authenticationType
//                WHERE ac.organizationPkId = :orgId
//                ORDER BY ac.requestedAt DESC
//                """,
//                AffiliationVerificationResponse.class
//        ).setParameter("orgId", councilOrganizationId).getResultList();
//
//    }
//
//}
