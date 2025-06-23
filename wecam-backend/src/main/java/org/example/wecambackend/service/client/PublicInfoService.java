package org.example.wecambackend.service.client;

import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.responseDTO.OrganizationSimpleResponse;
import org.example.wecambackend.dto.responseDTO.UniversitySimpleResponse;
import org.example.wecambackend.repos.OrganizationRepository;
import org.example.wecambackend.repos.SchoolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicInfoService {

    private final SchoolRepository schoolRepository;
    private final OrganizationRepository organizationRepository;

    // Service
    public List<UniversitySimpleResponse> getAllApprovedSchools() {
        return schoolRepository.findAll().stream()
                .map(UniversitySimpleResponse::fromEntity)
                .toList();
    }

    public List<OrganizationSimpleResponse> getOrganizationsBySchoolAndLevel(Long schoolId, int level) {
        return organizationRepository.findByUniversity_SchoolIdAndLevel(schoolId, level).stream()
                .map(OrganizationSimpleResponse::fromEntity)
                .toList();
    }

    public List<OrganizationSimpleResponse> getChildOrganizations(Long parentId) {
        return organizationRepository.findByParent_OrganizationId(parentId).stream()
                .map(OrganizationSimpleResponse::fromEntity)
                .toList();
    }

}
