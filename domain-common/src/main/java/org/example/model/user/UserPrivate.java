package org.example.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_private")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrivate {

    @Id
    @Column(name = "user_pk_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_pk_id")
    private User user;

    @Column(name = "phone_number",length = 150, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "password_update_at")
    private LocalDateTime passwordUpdateAt;

    public void updatePassword() {
        this.passwordUpdateAt = LocalDateTime.now();
    }
}
