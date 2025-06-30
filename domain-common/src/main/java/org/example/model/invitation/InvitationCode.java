package org.example.model.invitation;


import jakarta.persistence.*;
import lombok.*;
import org.example.model.Council;
import org.example.model.common.BaseTimeEntity;
import org.example.model.enums.MemberRole;
import org.example.model.user.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invitation_code")
public class InvitationCode extends BaseTimeEntity {

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

    @Column(name = "member_type", length = 20)
    private String memberType;

    @Column(name = "member_level")
    private Integer memberLevel;

    @Column(name = "member_parent_id")
    private Long memberParentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private MemberRole memberRole;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
