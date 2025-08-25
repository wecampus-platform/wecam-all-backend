package org.example.wecambackend.dto.response.category;
import java.time.LocalDateTime;
import java.util.List;

public record CategoryDetailResponse(
        Long categoryId,
        String categoryName,
        CreatedBy createdBy,
        Counts counts,
        List<Item> items,
        PageMeta page
) {
    public record CreatedBy(Long memberId, String name) {}
    public record Counts(
            long todoCompleted, long todoTotal,
            long fileFinal, long fileTotal,
            long meetingTotal
    ) {}
    public record Item(
            String entityType,   // "TODO" | "FILE" | "MEETING"
            Long entityId,
            String title,
            String status,       // 파일: "최종 문서", 할일: "진행 완료"/"진행 중" 등
            LocalDateTime createdAt
    ) {}
    public record PageMeta(int pageNumber, int pageSize, long totalElements) {}
}
