package org.example.wecambackend.repos;

import org.example.model.todo.Todo;
import org.example.model.todo.TodoManager;
import org.example.model.todo.TodoManagerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoManagerRepository extends JpaRepository<TodoManager, TodoManagerId> {
}
