package org.example.wecamadminbackend.repos;

import org.example.model.organization.Organization;
import org.example.model.enums.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    List<Organization> findByUniversity_SchoolIdAndLevel(Long schoolId, int level);
    List<Organization> findByParent_OrganizationId(Long parentId);
    Optional<Organization> findFirstByUniversity_SchoolIdAndLevel(Long schoolId, int level);
    Optional<Organization> findByOrganizationId(Long organizationId);
    Optional<Organization> findByOrganizationNameAndOrganizationTypeAndUniversity_SchoolId(String name,OrganizationType organizationType,Long schoolId);
    //해당 schoolId와 관련있는 Organization 추출
    Optional<Organization> findOrganizationByUniversity_SchoolIdAndParentIsNull(Long SchoolId);

    @Query("SELECT o.organizationName FROM Organization o WHERE o.organizationId = :id")
    String findOrganizationNameByOrganizationId(@Param("id") Long id);

    Optional<Organization> findByOrganizationNameAndOrganizationType(String inputSchoolName, OrganizationType organizationType);

    Optional<Organization> findByOrganizationNameAndOrganizationTypeAndParent(String inputDepartmentName, OrganizationType organizationType, Organization college);
}
