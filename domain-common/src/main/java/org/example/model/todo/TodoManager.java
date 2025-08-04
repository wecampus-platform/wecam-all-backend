package org.example.model.todo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.model.common.BaseEntity;
import org.example.model.user.User;

@Entity
@Getter
@Table(name = "todo_manager")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoManager extends BaseEntity {

    // 복합키: 할일 ID + 사용자 ID 조합
    @EmbeddedId
    private TodoManagerId id;

    // 할일 엔티티 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("todoId")
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    // 담당자 엔티티 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userPkId")
    @JoinColumn(name = "user_pk_id", nullable = false)
    private User user;


    public static TodoManager of(Todo todo, User user) {
        // 예외 검증도 여기서 할 수 있음
        if (todo == null || user == null) {
            throw new IllegalArgumentException("Todo와 User는 null일 수 없습니다.");
        }

        TodoManager tm = new TodoManager();
        tm.todo = todo;
        tm.user = user;
        tm.id = new TodoManagerId();
        return tm;
    }

}
