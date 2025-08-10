package org.example.wecambackend.repos.todo;

import org.example.model.todo.Todo;
import org.example.model.todo.TodoManager;
import org.example.model.todo.TodoManagerId;
import org.example.wecambackend.dto.projection.ManagerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TodoManagerRepository extends JpaRepository<TodoManager, TodoManagerId> {
    @Query("""
    SELECT new org.example.wecambackend.dto.projection.ManagerInfo(
        u.userPkId, u.name
    ) FROM TodoManager tm JOIN tm.user u WHERE tm.todo.todoId = :todoId
    """)
    List<ManagerInfo> findManagersByTodoId(@Param("todoId") Long todoId);


    boolean existsByTodo_TodoIdAndUser_UserPkId(Long todoId, Long userId);

    void deleteByTodo(Todo todo);

    List<TodoManager> findByTodo_TodoId(Long todoId);

    void deleteByTodo_TodoIdAndUser_UserPkIdIn(Long todoId, Set<Long> toDelete);
}
