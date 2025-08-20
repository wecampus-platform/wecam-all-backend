package org.example.model.category;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;

/**
 * 범용 카테고리 할당 엔티티.
 * 할 일, 회의록, 일정 등 다양한 엔티티에 카테고리를 할당할 수 있도록 하는 중간 테이블.
 */
@Entity
@Table(
        name = "category_assignment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_assignment_unique",
                        columnNames = {"category_id", "entity_type", "entity_id"}
                )
        },
        indexes = {
                @Index(name = "idx_assignment_category", columnList = "category_id"),
                @Index(name = "idx_assignment_entity", columnList = "entity_type, entity_id")
        }
)@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 할당된 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 엔티티 타입 (TODO, MEETING, SCHEDULE 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    // 엔티티 ID (해당 엔티티의 PK)
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /**
     * 엔티티 타입을 정의하는 열거형
     */
    public enum EntityType {
        TODO("할 일"),
        MEETING("회의록"),
        SCHEDULE("일정");

        private final String description;

        EntityType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 카테고리 할당 생성 정적 팩토리 메서드
     */
    public static CategoryAssignment create(Category category, EntityType entityType, Long entityId) {
        return CategoryAssignment.builder()
                .category(category)
                .entityType(entityType)
                .entityId(entityId)
                .build();
    }
}
