package org.example.model.council;

import jakarta.persistence.*;
import lombok.*;

import org.example.model.common.BaseEntity;


//ENUM 으로 안할거임 -> 추후 국장 / 부장 자기가 원하는 name으로 설정할 수 있게 해야될듯.
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouncilDepartmentRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CouncilDepartment department;

    @Column(nullable = false)
    private String name; // 예: Default 값 "회장","부회장","부장","부원"

    private Integer level; // 역할 순위 , 0 -> 국장, 1-> 부원
}
