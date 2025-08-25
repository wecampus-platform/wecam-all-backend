package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.category.Category;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.response.category.CategoryListResponse;
import org.example.wecambackend.repos.category.CategoryRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
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
}
