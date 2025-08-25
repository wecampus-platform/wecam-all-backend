package org.example.model.todo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.model.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "todo_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoFile extends BaseEntity {

    // 파일 고유 ID (UUID, 16바이트 바이너리 저장)
    @Id
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID todoFileId;

    // 원본 파일명
    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    // 저장된 파일명
    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    // 로컬에 저장된 파일 경로
    @Column(name = "file_path", nullable = false)
    private String filePath;

    // 파일 접근용 URL (추후에 필요 시 사용)
    @Column(name = "file_url")
    private String fileUrl;

    // 연관된 할일 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @Column(name = "is_final", nullable = false)
    private boolean isFinal = false;              // TINYINT(1) ↔ boolean 자동 매핑

    @Column(name = "final_set_by")
    private Long finalSetBy;              // 굳이 연관관계 아니어도 OK (필요하면 @ManyToOne(User)로 변경)

    @Column(name = "final_set_at")
    private LocalDateTime finalSetAt;

    @Builder
    public TodoFile(UUID todoFileId, Todo todo, String originalFileName,
                    String storedFileName, String filePath, String fileUrl) {
        this.todoFileId = todoFileId;
        this.todo = todo;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.fileUrl = fileUrl;
    }

}
