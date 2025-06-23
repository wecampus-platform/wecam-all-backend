package org.example.model;

import jakarta.persistence.*;
import lombok.*;
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
}
