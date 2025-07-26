package org.example.wecambackend.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.ProgressStatus;
import org.example.wecambackend.dto.Enum.TodoTypeDTO;
import org.example.wecambackend.dto.projection.ManagerInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TodoSimpleResponse {
    private Long todoId;
    private String title;
    private String content;
    private LocalDateTime dueAt;
    private String createUserName;
    private List<ManagerInfo> managers;
    private Long createUserId;
    private TodoTypeDTO todoTypeDTO;
    private ProgressStatus progressStatus;

    public TodoSimpleResponse(Long todoId, String title, String content,
                              LocalDateTime dueAt, List<ManagerInfo> managers,Long createUserId, String createUserName,
                              TodoTypeDTO todoTypeDTO, ProgressStatus progressStatus
                              ) {
        this.todoId = todoId;
        this.title = title;
        this.content = content;
        this.dueAt = dueAt;
        this.managers = managers;
        this.createUserName = createUserName;
        this.createUserId = createUserId;
        this.todoTypeDTO = todoTypeDTO;
        this.progressStatus = progressStatus;
    }

    // equals & hashCode를 todoId 기준으로 오버라이딩 필요
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoSimpleResponse)) return false;
        TodoSimpleResponse that = (TodoSimpleResponse) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }
}
