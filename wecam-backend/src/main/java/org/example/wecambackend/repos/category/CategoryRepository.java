package org.example.wecambackend.repos.category;

import org.example.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 특정 학생회에 속한 카테고리들을 조회
     */
    List<Category> findByCouncilIdOrderByNameAsc(Long councilId);

    /**
     * 특정 학생회에서 특정 이름의 카테고리를 조회
     */
    Optional<Category> findByCouncilIdAndName(Long councilId, String name);

    /**
     * 특정 학생회에서 특정 ID의 카테고리를 조회
     */
    Optional<Category> findByIdAndCouncilId(Long id, Long councilId);

    /**
     * 특정 학생회에서 카테고리명 중복 확인
     */
    boolean existsByCouncilIdAndName(Long councilId, String name);


}
