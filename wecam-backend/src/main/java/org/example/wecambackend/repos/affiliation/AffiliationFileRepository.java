package org.example.wecambackend.repos.affiliation;

import org.example.wecambackend.dto.projection.AffiliationFileProjection;
import org.example.model.affiliation.AffiliationCertificationId;
import org.example.model.affiliation.AffiliationFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AffiliationFileRepository extends JpaRepository<AffiliationFile, AffiliationCertificationId> {

//    Optional<AffiliationFile> findByAffiliationCertification_Id(AffiliationCertificationId affiliationCertificationId);
//    Optional<AffiliationFile> findById_UserIdAndId_AuthenticationType(Long userId,int auth);

    @Query(value = "SELECT * FROM affiliation_file WHERE pk_upload_userid = :userId AND authentication_type = :authOrdinal", nativeQuery = true)
    Optional<AffiliationFile> findByUserIdAndAuthOrdinal(
            @Param("userId") Long userId,
            @Param("authOrdinal") int authOrdinal
    );

    @Query(value = "SELECT file_path AS filePath, file_name AS fileName FROM affiliation_file WHERE pk_upload_userid = :userId AND authentication_type = :authOrdinal", nativeQuery = true)
    Optional<AffiliationFileProjection> findFilePathAndNameByUserIdAndAuthOrdinal(
            @Param("userId") Long userId,
            @Param("authOrdinal") int authOrdinal
    );


}
