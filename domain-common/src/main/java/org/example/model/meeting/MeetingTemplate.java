package org.example.model.meeting;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;

@Entity
@Table(name = "meeting_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingTemplate extends BaseEntity {

    // 템플릿 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    // 템플릿명
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // 템플릿 설명
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 마크다운 템플릿 내용
    @Column(name = "content_template", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String contentTemplate;

    // 기본 템플릿 여부 (NULL: 비기본, TRUE: 기본)
    @Column(name = "is_default")
    private Boolean isDefault;

    // 소속 학생회 (NULL이면 전체 학생회 공통)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id")
    private Council council;

    // 템플릿 생성자 (NULL이면 시스템 차원 제공이라는 의미)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private CouncilMember createdBy;
}
