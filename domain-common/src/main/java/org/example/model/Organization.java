package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseTimeEntity;
import org.example.model.enums.OrganizationType;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

//N+1 쿼리 없애야돼서 lazy 로 묶어놔서 parent 찾을 때 막힘.
//TODO: 추후 유지보수를 위해 변경해야될 수도 있음.
@BatchSize(size = 5)
@Entity
@Table(name = "organization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private University university;

    @Column(name = "organization_name", length = 50, nullable = false)
    private String organizationName;

    @Column(name = "level")
    private int level;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type")
    private OrganizationType organizationType;

    // 상위 조직 (자기 자신을 참조하는 FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @OneToMany(mappedBy = "parent") ///조회용입니다.........
    @Builder.Default
    private List<Organization> children = new ArrayList<>();

    public static Organization createUniversity(String name, University university) {
        return Organization.builder()
                .organizationName(name)
                .organizationType(OrganizationType.UNIVERSITY)
                .level(0)
                .university(university)
                .build();
    }

    public static Organization createCollege(String name, University university, Organization parent) {
        return Organization.builder()
                .organizationName(name)
                .organizationType(OrganizationType.COLLEGE)
                .level(1)
                .university(university)
                .parent(parent)
                .build();
    }

    public static Organization createDepartment(String name, University university, Organization parent) {
        return Organization.builder()
                .organizationName(name)
                .organizationType(OrganizationType.DEPARTMENT)
                .level(2)
                .university(university)
                .parent(parent)
                .build();
    }

}
