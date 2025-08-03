package org.example.model.council;

import jakarta.persistence.*;
import org.example.model.common.BaseEntity;

@Entity
public class CouncilDepartmentRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CouncilDepartment department;

    @Column(nullable = false)
    private String name; // 예: 국장, 부원, 실무자

    private Integer level; // 역할 순위 , 0 -> 국장, 1-> 부원
}
