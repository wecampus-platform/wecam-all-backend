package org.example.model.council;

import jakarta.persistence.*;
import org.example.model.enums.CouncilPermissionType;

@Entity
// 학생회 내 역할에 따른 권한 정책
public class CouncilPermissionPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id")
    private Council council;

    @ManyToOne(fetch = FetchType.LAZY)
    private CouncilDepartmentRole departmentRole;

    @Enumerated(EnumType.STRING)
    private CouncilPermissionType permission;
}

