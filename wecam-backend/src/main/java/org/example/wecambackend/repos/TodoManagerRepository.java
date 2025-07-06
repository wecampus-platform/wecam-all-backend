package org.example.wecambackend.repos;

import org.example.model.todo.Todo;
import org.example.model.todo.TodoManager;
import org.example.model.todo.TodoManagerId;
import org.example.wecambackend.dto.projection.ManagerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoManagerRepository extends JpaRepository<TodoManager, TodoManagerId> {
    List<TodoManager> findByTodo_TodoId(Long todo);

    void deleteByTodoAndUserUserPkId(Todo todo, Long removeId);


    @Query("""
    SELECT new org.example.wecambackend.dto.projection.ManagerInfo(
        u.userPkId, ui.name
    ) FROM TodoManager tm JOIN tm.user u JOIN u.userInformation ui WHERE tm.todo.todoId = :todoId
    """)
    List<ManagerInfo> findManagersByTodoId(@Param("todoId") Long todoId);

}
