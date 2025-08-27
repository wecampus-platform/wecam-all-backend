package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.response.PageResponse;
import org.example.wecambackend.dto.response.category.*;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.repos.category.CategoryQueryRepository;
import org.example.wecambackend.service.admin.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/council/{councilName}/category")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "카테고리 컨트롤러")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryQueryRepository categoryQueryRepository;

    // 특정 학생회에 속한 모든 카테고리를 조회
    @IsCouncil
    @GetMapping
    @Operation(
            summary = "특정 학생회의 카테고리 목록 조회",
            description = "특정 학생회가 생성한 카테고리 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<List<CategoryListResponse>> getCategoriesByCouncilId(
            @PathVariable String councilName) {
        List<CategoryListResponse> categories = categoryService.getCategories();
        return new BaseResponse<>(categories);
    }



    @IsCouncil
    @GetMapping("/dashboard")
    @Operation(
            summary = "특정 학생회의 카테고리 대쉬보드 조회",
            description = "특정 학생회가 생성한 카테고리 대시보드를 조회합니다.",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<PageResponse<CategorySummary>> list(
            @PathVariable String councilName,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(page-1,0), size);
        Long councilId = CouncilContextHolder.getCouncilId();
        Page<CategorySummary> result = categoryQueryRepository.summaries(councilId, query, pageable);

        return new BaseResponse<> (PageResponse.of(result));
    }

    @IsCouncil
    @PostMapping("/create")
    @Operation(
            summary = "특정 학생회의 카테고리 생성",
            description = "특정 학생회가 생성한 카테고리 생성. 생성할 카테고리 명만 받음!",
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<?> createCategory(
            @RequestParam String categoryName,
            @PathVariable String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getId();
        Long councilId = CouncilContextHolder.getCouncilId();
        categoryService.create(councilId, memberId, categoryName);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }




    @IsCouncil
    @GetMapping("/{categoryId}/items")
    @Operation(
            summary = "카테고리 상세 아이템 목록 (파일)",
            description = """
        카테고리 내부의 파일 목록을 페이지네이션으로 조회합니다.
        - entityType은 현재 FILE_ASSET만 지원 (상단 카운터 제외)
        - query로 파일명 부분검색 가능
        """,
            parameters = {
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "categoryId", description = "카테고리 ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER, required = true)
            }
    )
    public BaseResponse<PageResponse<CategoryDetailResponse.Item>> getCategoryFileItems(
            @PathVariable String councilName,
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "ALL") CategoryItemFilter filter,
            @RequestParam(defaultValue = "DESC") SortOption sort,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Long councilId = CouncilContextHolder.getCouncilId();


        Page<CategoryDetailResponse.Item> result =
                categoryService.listItems(councilId, categoryId, filter, sort, pageable);
        return new BaseResponse<>(PageResponse.of(result));
    }


}
