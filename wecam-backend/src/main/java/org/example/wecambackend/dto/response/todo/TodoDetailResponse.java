package org.example.wecambackend.dto.response.todo;


import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.ProgressStatus;
import org.example.wecambackend.dto.projection.ManagerInfo;
import org.example.wecambackend.dto.projection.TodoFileInfo;
import org.example.wecambackend.dto.response.category.CategoryListResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TodoDetailResponse {
    private Long todoId;
    private String title;
    private String content;
    private LocalDateTime dueAt;
    private String createUserName;
    private List<ManagerInfo> managers;
    private List<CategoryListResponse> categoryListResponses;
    private Long createUserId;
    private ProgressStatus progressStatus;
    private List<TodoFileInfo> files;



    public TodoDetailResponse(Long todoId, String title, String content,
                              LocalDateTime dueAt, ProgressStatus progressStatus,
                              List<ManagerInfo> managers,Long createUserId, String createUserName,
                              List<TodoFileInfo> files,List<CategoryListResponse> categoryListResponses) {
        this.todoId = todoId;
        this.title = title;
        this.content = content;
        this.dueAt = dueAt;
        this.progressStatus = progressStatus;
        this.managers = managers;
        this.createUserName = createUserName;
        this.createUserId = createUserId;
        this.files = files;
        this.categoryListResponses = categoryListResponses;
    }

}
