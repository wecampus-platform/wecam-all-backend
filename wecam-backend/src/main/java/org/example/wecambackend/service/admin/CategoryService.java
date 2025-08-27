package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.category.Category;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.response.category.*;
import org.example.wecambackend.repos.category.CategoryDetailNativeRepository;
import org.example.wecambackend.repos.category.CategoryRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 현재 접속한 학생회의 모든 카테고리를 조회
     */
    public List<CategoryListResponse> getCategories() {
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        List<Category> categories = categoryRepository.findByCouncilIdOrderByNameAsc(councilId);
        
        return categories.stream()
                .map(category -> CategoryListResponse.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .build())
                .toList();
    }


    @Transactional
    public void create(Long councilId, Long memberId, String name) {
        // 같은 이름이 ACTIVE로 이미 있으면 방어
        if (categoryRepository.existsByCouncilIdAndName(
                councilId, name)) {
            throw new BaseException(BaseResponseStatus.REQUEST_DUPLICATED);
        }
        Council council = entityFinderService.getCouncilByIdOrThrow(councilId);
        CouncilMember councilMember = entityFinderService.getCouncilMemberByIdOrThrow(memberId);
        categoryRepository.save(
                Category.builder()
                        .council(council)
                        .name(name)
                        .createdMember(councilMember)
                        .build()
        );
    }

    private final EntityFinderService entityFinderService;


    @Transactional(readOnly = true)
    public Page<CategoryDetailResponse.Item> listItems(
            Long councilId, Long categoryId,
            CategoryItemFilter filter, SortOption sort,
            Pageable pageable
    ) {
        // 1) 전체 개수
        long total = categoryDetailNativeRepository.countItems(councilId, categoryId, filter); // 권장: councilId 포함 버전

        // 2) 페이지 범위 밖이면 빈 페이지
        if (total == 0 || pageable.getOffset() >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        // 3) 실제 목록 조회
        int offset = (int) pageable.getOffset();
        int limit  = pageable.getPageSize();

        List<CategoryDetailResponse.Item> content =
                categoryDetailNativeRepository.findItems(councilId, categoryId, filter, sort, offset, limit); // 권장: councilId 포함 버전

        // 4) Page로 포장
        return new PageImpl<>(content, pageable, total);
    }

    private final CategoryDetailNativeRepository categoryDetailNativeRepository;
}
