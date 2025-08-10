package org.example.wecambackend.dto.response.admin;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminFileResponse {
    private String uuid;
    private String originalFileName;
    private String storedFileName;
    private String folder;
    private String filePath;
    private String url;
    private LocalDateTime uploadedAt;
}
