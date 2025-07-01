package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.organization.Organization;
import org.example.model.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "council")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Council {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "council_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "council_name", length = 50, nullable = false)
    private String councilName;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id",nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.startDate = now;
        this.endDate = now.plusDays(365); // 가입일 기준 30일 유효 //TODO: 추후 설정 가능하게 해야 함.
    }
}
