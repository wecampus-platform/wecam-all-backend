package org.example.wecambackend.dto.responseDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.ProgressStatus;

import java.awt.*;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
public class TodoResponse {
    Long todoId;
    Long createUserId;
    String title;
    TextArea content;
    LocalDateTime dueAt; //할일 마감일
    ProgressStatus progressStatus;//할일 진행 상태
}
