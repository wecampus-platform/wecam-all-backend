package org.example.wecambackend.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.model.enums.CodeType;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.example.wecambackend.service.admin.InvitationCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;





@IsCouncil
@RestController
@RequestMapping("/admin/council/{councilName}/invitation")
@RequiredArgsConstructor
@Tag(name = "Invitation Code Controller", description = "학생회 관리자 페이지 내 초대코드 발급 및 관리 API")
public class InvitationCodeController {

    private final InvitationCodeService invitationCodeService;

    @IsCouncil
    @Operation(
            summary = "발급한 초대코드 전체 조회",
            description = "학생회가 지금까지 발급한 모든 초대코드를 조회합니다. 초대코드 유형, 만료일, 사용 여부 등을 포함합니다.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속 중인 학생회 ID (헤더)", in = ParameterIn.HEADER),
                    @Parameter(name = "councilId", description = "해당 학생회 조직 ID", required = true)
            }
    )
    @GetMapping("/list")
    public ResponseEntity<List<InvitationCodeResponse>> getInvitationCodeRequests(
            @PathVariable String councilName,
            @RequestParam("councilId") Long councilId
    ) {
        List<InvitationCodeResponse> list = invitationCodeService.findByCouncilId(councilId);
        return ResponseEntity.ok(list);
    }

    @IsCouncil
    @Operation(
            summary = "초대코드 생성",
            description = """
                    학생회가 관리하는 조직의 초대코드를 생성합니다. 
                    codeType에 따라 일반 학생용(`student_member`) 또는 학생회 구성원용(`council_member`) 중 선택합니다.

                    ※ 추후에는 해당 학생회에서 초대코드 생성 권한이 부여된 관리자만 사용 가능하게 제한될 예정입니다.
                    """,
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속 중인 학생회 ID (헤더)", in = ParameterIn.HEADER),
                    @Parameter(name = "codeType", description = "`student_member` 또는 `council_member` 중 선택", required = true),
                    @Parameter(name = "councilId", description = "초대코드를 생성할 학생회 조직 ID", required = true)
            }
    )
    @PostMapping("/create/{codeType}")
    public ResponseEntity<?> makeInvitationCode(
            @PathVariable String councilName,
            @RequestParam("councilId") Long councilId,
            @PathVariable CodeType codeType,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        invitationCodeService.createInvitationCode(codeType, userDetails.getId(), councilId);
        return ResponseEntity.ok("초대 코드 생성이 완료되었습니다.");
    }

    @IsCouncil
    @Operation(
            summary = "초대코드 만료일 변경",
            description = """
                    특정 초대코드의 만료일을 수정합니다. 
                    기존 초대코드가 아직 만료되지 않았다면, 새로운 `expiredAt` 값으로 덮어씁니다. 
                    현재 시각 기준 최소 5분 이후의 시각만 설정할 수 있습니다.
                    """,
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속 중인 학생회 ID (헤더)", in = ParameterIn.HEADER),
                    @Parameter(name = "invitationId", description = "만료일을 변경할 초대코드 ID", required = true),
                    @Parameter(name = "expiredAt", description = "변경할 만료일시 (ISO 형식: yyyy-MM-dd'T'HH:mm:ss)", required = true)
            }
    )
    @PutMapping("/{invitationId}/edit/expiredAt")
    public ResponseEntity<?> EditInvitationCodeExpiredAt(
            @PathVariable String councilName,
            @RequestParam("expiredAt") LocalDateTime expiredAt,
            @PathVariable Long invitationId
    ) {
        invitationCodeService.editExpiredAtInvitation(expiredAt, invitationId);
        return ResponseEntity.ok("만료 기간 설정이 적용되었습니다.");
    }
}
