package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.config.security.annotation.IsPresidentTeam;
import org.example.wecambackend.dto.requestDTO.StudentExpulsionRequest;
import org.example.wecambackend.dto.responseDTO.UserSummaryResponse;
import org.example.wecambackend.service.admin.StudentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/admin/council/{councilName}/student")
@RequiredArgsConstructor
@Tag(name = "Student Controller", description = "일반 학생 관리 컨트롤러")
public class StudentController {

    private final StudentService studentExpulsionService;

    @DeleteMapping("/{userId}")
    @IsCouncil
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

    //학생회가 맡은 조직이 학과일 시, 해당 학과에 속한 일반 학생들 불러오기
    @IsCouncil
    @GetMapping("/students")
    @Operation(
            summary = "해당 학생회가 관리하는 학생 유저 list 불러오기",
            description = "접속한 X-council-Id 의 학생회가 관리하는 학생유저 list 반환, 검색 태그 설정할 시 해당 태그에 속한 학생들을 반환함.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속 중인 학생회 ID (헤더)", in = ParameterIn.HEADER)
            }
    )
    public BaseResponse<List<UserSummaryResponse>> ShowOrganizationStudents(
            @RequestParam(required = false)
            @Parameter(description = "입학년도 뒷자리 (예: 21, 22). 다중 선택 가능", example = "21")
            List<String> studentNumber,

            @RequestParam(required = false)
            @Parameter(description = "학년 (1~4). 다중 선택 가능", example = "1")
            List<Integer> grade,
            @PathVariable("councilName") String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long councilId = CouncilContextHolder.getCouncilId();
        List<UserSummaryResponse> response = studentExpulsionService.showStudentListByDepartmentId(
                councilId, studentNumber, grade
        );
        return new BaseResponse<>(response);
    }


}
