package org.example.wecambackend.controller.admin.filelibrary;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.projection.FileItemDto;
import org.example.wecambackend.dto.projection.FileLibraryFilter;
import org.example.wecambackend.dto.request.FileUploadRequest;
import org.example.wecambackend.dto.response.FileAssetResponse;
import org.example.wecambackend.service.admin.filelibrary.FileLibraryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.multipart.MultipartFile;

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
            TODO/MEETING/FILE_ASSET 파일을 통합 조회합니다.
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
                            schema = @Schema(type = "string", allowableValues = {"TODO","MEETING","FILE_ASSET"}, example = "TODO")
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
            @RequestParam(required = false) String sourceType,   // TODO|MEETING|FILE_ASSET
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "false") boolean finalOnly,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long councilId = CouncilContextHolder.getCouncilId();
        var filter = new FileLibraryFilter(councilId, sourceType, categoryId, finalOnly, q);
        System.out.println(filter);
        return service.search(filter, PageRequest.of(page, size));
    }


    @IsCouncil
    @Operation(
            summary = "새 파일 업로드",
            description = """
            file_asset에 파일을 저장하고, 선택한 카테고리를 category_assignment로 매핑합니다.
            - entity_type은 'FILE_ASSET'로 고정됩니다.
            - 파일 외의 텍스트 필드는 multipart form-data의 다른 파트로 전달하세요.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description  = "업로드 성공",
            content      = @Content(
                    mediaType = "application/json",
                    schema    = @Schema(implementation = FileAssetResponse.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "잘못된 요청(파일 누락/형식 오류 등)")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음(다른 학생회 접근)")
    @PostMapping(
            value    = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BaseResponse<?> createFileAsset(
            @Parameter(
                    name = "X-Council-Id",
                    description = "현재 로그인한 학생회 ID",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "303"
            )
            @RequestHeader("X-Council-Id") Long councilId,
            @AuthenticationPrincipal UserDetailsImpl me,
            @Parameter(
                    description = "업로드할 실제 파일 (PDF/DOCX/이미지 등)",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("file") MultipartFile file,
            @Parameter(
                    description = """
                파일의 부가 정보.
                - categoryIds: 카테고리 ID 배열 (같은 키를 여러 번 보내거나 JSON 배열 모두 허용)
                - isFinal: 최종본 여부
                """,
                    required = false,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = FileUploadRequest.class),
                            examples = {
                                    @ExampleObject(name="기본 예시",
                                            value="""
                        --boundary
                        Content-Disposition: form-data; name="title"

                        학생회_회의록_정리본
                        --boundary
                        Content-Disposition: form-data; name="description"

                        8월 정례회의 정리본
                        --boundary
                        Content-Disposition: form-data; name="categoryIds"

                        1
                        --boundary
                        Content-Disposition: form-data; name="categoryIds"

                        2
                        --boundary
                        Content-Disposition: form-data; name="isFinal"

                        false
                        --boundary--
                        """)
                            }
                    )
            )
            @RequestPart(value="form",required = false) FileUploadRequest form
    ) {
        fileLibraryService.upload(councilId, me.getId(), file, form);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    private final FileLibraryService fileLibraryService;
}

