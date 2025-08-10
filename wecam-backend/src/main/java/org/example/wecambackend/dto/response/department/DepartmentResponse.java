package org.example.wecambackend.dto.response.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.model.council.CouncilDepartment;
import org.example.model.council.CouncilDepartmentRole;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 부서 정보 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "부서 정보 응답 DTO")
public class DepartmentResponse {
    @Schema(description = "부서 ID", example = "1")
    private Long id;

    @Schema(description = "부서명", example = "기획부")
    private String name;

    @Schema(description = "부서 내 역할 목록")
    private List<DepartmentRoleResponse> roles;

    public static DepartmentResponse from(CouncilDepartment department, List<CouncilDepartmentRole> roles) {
        List<DepartmentRoleResponse> roleResponses = roles.stream()
                .map(DepartmentRoleResponse::from)
                .collect(Collectors.toList());
        
        return new DepartmentResponse(department.getId(), department.getName(), roleResponses);
    }
} 