package org.example.wecambackend.dto.request.todo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class TodoCreateRequest {
    private LocalDateTime dueAt;
    private String title;
    private String content;
    private List<Long> managers;
}
