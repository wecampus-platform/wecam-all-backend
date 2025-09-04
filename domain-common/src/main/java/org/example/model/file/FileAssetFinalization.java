package org.example.model.file;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.category.CategoryAssignment;
import org.example.model.common.BaseEntity;
import org.example.model.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "file_asset_finalization")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileAssetFinalization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "finalization_id")
    private Long id;

    // 엔티티 타입 (TODO, MEETING, SCHEDULE 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private CategoryAssignment.EntityType entityType;

    // 엔티티 ID (해당 엔티티의 PK)
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    // ✅ 요청 상태
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "final_status", nullable = false, length = 20)
    private FinalStatus finalStatus = FinalStatus.PENDING;

    @ManyToOne @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @ManyToOne @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}

