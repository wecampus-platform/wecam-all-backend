package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CheckCouncilAccess;
import org.example.wecambackend.config.security.annotation.IsPresidentTeam;
import org.example.wecambackend.dto.requestDTO.StudentExpulsionRequest;
import org.example.wecambackend.service.admin.StudentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/council/{councilName}/student")
@RequiredArgsConstructor
@Tag(name = "Student Controller", description = "일반 학생 관리 컨트롤러")
public class StudentController {

    private final StudentService studentExpulsionService;

    @DeleteMapping("/{userId}")
    @CheckCouncilAccess
    @IsPresidentTeam
    @Operation(
            summary = "일반 학생 제명",
            description = "일반 학생을 제명합니다. 회장과 부회장만 이 기능을 사용할 수 있습니다. 제명된 학생은 소속인증 미인증 상태가 됩니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "userId", description = "제명할 학생의 ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<String> expelStudent(
            @PathVariable Long userId,
            @RequestBody @Valid StudentExpulsionRequest request,
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        studentExpulsionService.expelStudent(userId, request.getReason());
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, "학생이 성공적으로 제명되었습니다.");
    }
}
