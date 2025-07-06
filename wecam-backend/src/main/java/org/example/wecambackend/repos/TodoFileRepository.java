package org.example.wecambackend.repos;

import org.example.model.todo.Todo;
import org.example.model.todo.TodoFile;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoFileRepository extends JpaRepository<TodoFile,Long> {
    Optional<TodoFile> findByTodoFileId(UUID fileId);

    List<TodoFile> findByTodo(Todo todo);
}
