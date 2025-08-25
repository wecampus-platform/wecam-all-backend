package org.example.wecambackend.dto.response.category;

public record CategorySummary(
        Long id,
        String name,
        int noticeCount,
        int fileCount,
        int meetingCount
) {}
