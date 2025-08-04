package org.example.wecambackend.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.model.council.CouncilDepartmentRole;

/**
 * 부서 역할 정보 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "부서 역할 정보 응답 DTO")
public class DepartmentRoleResponse {
    @Schema(description = "역할 ID", example = "1")
    private Long id;
    
    @Schema(description = "역할명", example = "부장")
    private String name;
    
    @Schema(description = "역할 레벨 (숫자가 클수록 높은 권한)", example = "1")
    private Integer level;

    public static DepartmentRoleResponse from(CouncilDepartmentRole role) {
        return new DepartmentRoleResponse(role.getId(), role.getName(), role.getLevel());
    }
} 