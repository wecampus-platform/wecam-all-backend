package org.example.model.council;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.model.common.BaseEntity;

/**
 * 학생회 내의 부서를 나타내는 엔티티.
 * 예: 기획국, 사무국, 홍보국 등
 * 각 부서는 특정 학생회(Council)에 종속되며,
 * 계층 구조(상위/하위 부서) 및 순서를 지정할 수 있다.
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class CouncilDepartment extends BaseEntity {

    // 부서 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 학생회 (필수)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id") // DB 컬럼명과 일치
    private Council council;

    // 부서명 (예: 기획국, 회계팀 등)
    @Column(nullable = false) @Getter @Setter
    @Builder.Default
    private String name = "새로운 부서";

    // 상위 부서 ID (null이면 최상위 부서)
    private Long parentId;

}
