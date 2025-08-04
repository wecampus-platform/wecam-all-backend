package org.example.wecambackend.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

/**
 * 부서 배치 요청 DTO
 */
@Getter
@Setter
@Schema(description = "부서 배치 요청 DTO")
public class DepartmentAssignmentRequest {

    @NotNull(message = "부서 ID는 필수입니다.")
    @Schema(description = "배치할 부서의 ID", example = "1")
    private Long departmentId;

    // @NotNull(message = "부서 역할 ID는 필수입니다.")
    // private Long departmentRoleId;
}
