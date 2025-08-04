package org.example.model.council;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
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
public class Council extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "council_id")
    private Long id;

    // 소속 조직 (워크스페이스)
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // 학생회 이름
    @Column(name = "council_name", length = 50, nullable = false)
    private String councilName;

    // 시작 일자
    @Column(name = "start_date")
    private LocalDateTime startDate;

    // 종료 일자
    @Column(name = "end_date")
    private LocalDateTime endDate;

    // 학생회 생성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id",nullable = false)
    private User user;

    @Override
    protected void prePersistChild() {
        LocalDateTime now = LocalDateTime.now();
        this.startDate = now;
        this.endDate = now.plusDays(365); // 가입일 기준 365일 유효 //TODO: 추후 설정 가능하게 해야 함.
    }
}
