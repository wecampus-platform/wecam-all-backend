package org.example.wecambackend.dto.projection;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class TodoFileInfo {
    private UUID todoFileId;
    private String originalFileName;
    private String fileUrl;

    public TodoFileInfo(UUID todoFileId, String originalFileName, String fileUrl) {
        this.todoFileId = todoFileId;
        this.originalFileName = originalFileName;
        this.fileUrl = fileUrl;
    }
}
