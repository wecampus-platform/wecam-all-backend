package org.example.model.council;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.user.User;
import org.example.model.enums.MemberRole;

@Entity
@Table(name = "council_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouncilMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "council_member_pk_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private CouncilDepartment department;

    @ManyToOne(fetch = FetchType.LAZY)
    private CouncilDepartmentRole departmentRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private MemberRole memberRole;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive=true;
}
