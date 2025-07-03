package org.example.model.todo;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseTimeEntity;
import org.example.model.enums.ProgressStatus;
import org.example.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "todo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    // 할일 제목
    @Column(nullable = false, length = 20)
    private String title;

    // 할일 내용 (길이 무제한, null 가능)
    @Column(columnDefinition = "TEXT")
    private String content;

    // 마감 시간
    @Column(nullable = false)
    private LocalDateTime dueAt;

    // 진행 상태 (진행 전, 진행 중, 진행 완료)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus progressStatus;

    // 할일 생성자 (할당자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_user_id", nullable = false)
    private User createUser;

    // 할일에 첨부된 파일 목록
    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoFile> files;

    // 할일 담당자 목록
    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoManager> managers;
}
