package org.example.model.council;

import jakarta.persistence.*;
import org.example.model.enums.CouncilPermissionType;

@Entity
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

