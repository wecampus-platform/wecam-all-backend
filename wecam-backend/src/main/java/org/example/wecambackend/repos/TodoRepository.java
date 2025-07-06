package org.example.wecambackend.repos;

import org.example.model.todo.Todo;
import org.example.wecambackend.dto.responseDTO.TodoDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo,Long> {


    @Query("SELECT t FROM Todo t " +
            "JOIN FETCH t.createUser cu " +
            "JOIN FETCH cu.userInformation ui " +
            "WHERE t.todoId = :todoId")
    Optional<Todo> findTodoWithCreator(@Param("todoId") Long todoId);

}
