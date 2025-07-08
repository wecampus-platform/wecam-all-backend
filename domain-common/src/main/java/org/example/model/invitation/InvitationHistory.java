package org.example.model.invitation;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invitation_history")
public class InvitationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_pk_id")
    private Long id;

    //사용 시간 조회
    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    // FK 연결하지 않고 그냥 ID만 저장 _ 사용된 초대코드 ID
    @Column(name = "invitation_pk_id", nullable = false)
    private Long invitationPkId;


    //사용한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        this.usedAt = LocalDateTime.now();
    }
}

