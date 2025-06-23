package org.example.wecambackend.controller.publicinfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.responseDTO.OrganizationSimpleResponse;
import org.example.wecambackend.dto.responseDTO.UniversitySimpleResponse;
import org.example.wecambackend.service.client.PublicInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Tag(name = "Public Info Controller", description = "인증 없이 접근 가능한 공공 정보 API")
public class PublicInfoController {

    private final PublicInfoService publicInfoService;

    @Operation(
            summary = "모든 승인된 학교 조회",
            description = "승인된 모든 학교 리스트를 반환합니다."
    )
    @GetMapping("/schools")
    public List<UniversitySimpleResponse> getAllSchools() {
        return publicInfoService.getAllApprovedSchools();
    }

    @Operation(
            summary = "특정 학교의 상위 조직 조회",
            description = "학교 ID와 조직 level을 기준으로 상위 조직(예: 학생회, 단과대 등)을 조회합니다."
    )
    @GetMapping("/schools/{schoolId}/organizations")
    public List<OrganizationSimpleResponse> getTopLevelOrganizations(
            @PathVariable Long schoolId,
            @RequestParam int level){
        return publicInfoService.getOrganizationsBySchoolAndLevel(schoolId,level);
    }

    @Operation(
            summary = "하위 조직 조회",
            description = "상위 조직 ID를 기반으로 하위 조직들을 조회합니다."
    )
    @GetMapping("/organizations/{parentId}/children")
    public List<OrganizationSimpleResponse> getChildOrganizations(@PathVariable Long parentId) {
        return publicInfoService.getChildOrganizations(parentId);
    }
}
