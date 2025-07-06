package org.example.wecambackend.repos;

import org.example.model.todo.TodoFile;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoFileRepository extends JpaRepository<TodoFile,Long> {
}
