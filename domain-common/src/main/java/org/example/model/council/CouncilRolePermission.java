package org.example.model.council;

import jakarta.persistence.*;
import org.example.model.common.BaseEntity;
import org.example.model.enums.CouncilPermissionType;

@Entity
@Table(name = "council_role_permission")
public class CouncilRolePermission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_role_id")
    private CouncilDepartmentRole role;

    @Enumerated(EnumType.STRING)
    private CouncilPermissionType permission;
}
