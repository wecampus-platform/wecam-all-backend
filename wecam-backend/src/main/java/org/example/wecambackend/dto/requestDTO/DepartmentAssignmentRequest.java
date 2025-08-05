package org.example.wecambackend.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

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

    @Min(value = 0, message = "역할 레벨은 0 이상이어야 합니다.")
    @Schema(description = "부서 내 역할 레벨 (0: 부장, 1: 부원). 생략 시 부원(1)으로 설정됩니다.", example = "1")
    private Integer departmentLevel = 1;
}
