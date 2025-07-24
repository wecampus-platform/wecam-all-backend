package org.example.model.council;

import jakarta.persistence.*;
import org.example.model.enums.CouncilPermissionType;

@Entity
public class CouncilMemberPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private CouncilMember member;

    @Enumerated(EnumType.STRING)
    private CouncilPermissionType permission;
}
