package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.category.Category;
import org.example.wecambackend.dto.response.category.CategoryListResponse;
import org.example.wecambackend.repos.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
