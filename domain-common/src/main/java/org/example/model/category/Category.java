package org.example.model.category;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.todo.Todo;
import org.example.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 학생회별 카테고리를 나타내는 엔티티.
 * 할 일, 회의록, 일정 등을 분류하는 데 사용됩니다.
 * 같은 학생회 내에서는 카테고리명이 중복될 수 없습니다.
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 학생회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;

    // 카테고리명
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    // 카테고리 생성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdUser;

    // 카테고리명 업데이트 메서드
    public void updateName(String name) {
        this.name = name;
    }
}
