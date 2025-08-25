package org.example.wecambackend.controller.admin.filelibrary;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.projection.FileItemDto;
import org.example.wecambackend.dto.projection.FileLibraryFilter;
import org.example.wecambackend.service.admin.filelibrary.FileLibraryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/admin/council/{councilName}/fileAsset")
@RequiredArgsConstructor
@IsCouncil
@Tag(name = "File Asset Controller", description = "학생회 관리자 페이지 안에서 운영 문서함 부분")
public class FileAssetController {

    private final FileLibraryService service;

    @Operation(
            summary = "통합 파일 목록 조회",
            description = """
            TODO/MEETING/STANDALONE 파일을 통합 조회합니다.
            - 검색(q): 파일명/소스제목에 공백 AND 매칭
            - 최종문서(finalOnly=true) 필터 지원
            - 카테고리/소스 타입 필터 지원
            """,
            operationId = "listFiles",
            security = { @SecurityRequirement(name = "bearerAuth") },
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true),
                    @Parameter(
                            name = "sourceType",
                            description = "소스 타입 필터 (콤마 없이 단일 값).",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "string", allowableValues = {"TODO","MEETING","STANDALONE"}, example = "TODO")
                    ),
                    @Parameter(
                            name = "categoryId",
                            description = "카테고리 ID(단일).",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "integer", example = "12")
                    ),
                    @Parameter(
                            name = "finalOnly",
                            description = "최종문서만 조회할지 여부.",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "boolean", defaultValue = "false", example = "true")
                    ),
                    @Parameter(
                            name = "q",
                            description = "검색어(파일명/소스제목, 공백 AND).",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "string", example = "예산 보고서")
                    ),
                    @Parameter(
                            name = "page",
                            description = "0-based 페이지 번호.",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "0", example = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "페이지 크기(최대 100 권장).",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "20", example = "24")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = org.springframework.data.domain.Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public Page<FileItemDto> list(
            @RequestParam(required = false) String sourceType,   // TODO|MEETING|STANDALONE
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "false") boolean finalOnly,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long councilId = CouncilContextHolder.getCouncilId();
        var filter = new FileLibraryFilter(councilId, sourceType, categoryId, finalOnly, q);
        return service.search(filter, PageRequest.of(page, size));
    }
}

