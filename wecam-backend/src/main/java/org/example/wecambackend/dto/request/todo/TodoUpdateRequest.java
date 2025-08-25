package org.example.wecambackend.dto.request.todo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TodoUpdateRequest {
    private String title;
    private String content;
    private LocalDateTime dueAt;
    private List<Long> categoryIds;
    private List<Long> managers; // 새 담당자 리스트
    private List<UUID> deleteFileIds; // 삭제할 파일 ID 목록
}
