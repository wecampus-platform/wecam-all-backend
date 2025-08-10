package org.example.wecambackend.repos.todo;

import org.example.model.todo.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo,Long> {


    @Query("SELECT t FROM Todo t " +
            "JOIN FETCH t.createUser cu " +
            "JOIN FETCH cu.userInformation ui " +
            "WHERE t.todoId = :todoId")
    Optional<Todo> findTodoWithCreator(@Param("todoId") Long todoId);

    List<Todo> findAllByCreateUser_UserPkIdAndManagers_User_UserPkIdAndCouncil_Id(Long creatorId, Long managerId,Long councilId);

    List<Todo> findAllByManagers_User_UserPkIdAndCouncil_Id(Long userId,Long councilId);

    List<Todo> findAllByCreateUser_UserPkIdAndCouncil_Id(Long userId,Long councilId);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.managers m LEFT JOIN FETCH m.user WHERE t.id = :todoId")
    Optional<Todo> findWithManagersAndUsersById(@Param("todoId") Long todoId);

    List<Todo> findByCouncil_IdAndManagers_User_UserPkIdAndDueAtBetween(
            Long councilId,
            Long userId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
