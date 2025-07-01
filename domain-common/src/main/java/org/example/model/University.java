package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.organization.Organization;
import org.example.model.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "university")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class University extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "school_name", length = 20, nullable = false)
    private String schoolName;

    @Column(name = "school_address", length = 100)
    private String schoolAddress;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL)
    private List<Organization> organizations = new ArrayList<>();
}
