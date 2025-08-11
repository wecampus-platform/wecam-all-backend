package org.example.wecambackend.repos.category;

import org.example.model.category.CategoryAssignment;
import org.example.model.common.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryAssignmentRepository extends JpaRepository<CategoryAssignment, Long> {

    /**
     * 특정 엔티티 타입과 ID에 대한 카테고리 할당 조회
     */
    Optional<CategoryAssignment> findByEntityTypeAndEntityId(CategoryAssignment.EntityType entityType, Long entityId);

    /**
     * 특정 엔티티 타입과 ID에 대한 ACTIVE 상태의 카테고리 할당 조회
     */
    Optional<CategoryAssignment> findByEntityTypeAndEntityIdAndStatus(CategoryAssignment.EntityType entityType, Long entityId, BaseEntity.Status status);

    /**
     * 특정 엔티티 타입과 ID에 대한 모든 ACTIVE 상태의 카테고리 할당 목록 조회
     */
    List<CategoryAssignment> findAllByEntityTypeAndEntityIdAndStatus(CategoryAssignment.EntityType entityType, Long entityId, BaseEntity.Status status);

    /**
     * 특정 엔티티 타입과 ID에 대한 카테고리 할당 목록 조회
     */
    List<CategoryAssignment> findByEntityTypeAndEntityIdIn(CategoryAssignment.EntityType entityType, List<Long> entityIds);

    /**
     * 특정 카테고리에 할당된 엔티티들 조회
     */
    List<CategoryAssignment> findByEntityTypeAndCategoryId(CategoryAssignment.EntityType entityType, Long categoryId);

    /**
     * 특정 엔티티 타입의 모든 할당 조회
     */
    List<CategoryAssignment> findByEntityType(CategoryAssignment.EntityType entityType);
}
