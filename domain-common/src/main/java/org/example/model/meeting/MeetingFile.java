package org.example.model.meeting;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
import org.example.model.file.FinalStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingFile extends BaseEntity {

    // 파일 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    // 회의록 고유 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

//    // 원본 파일명
//    @Column(name = "file_name", length = 255, nullable = false)
//    private String fileName;

    // 원본 파일명
    @Column(name = "original_file_name", length = 255, nullable = false)
    private String originalFileName;

    // 파일 저장 경로
    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    // 파일 접근용 URL
    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;

    // 파일 크기 (bytes)
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    // 파일 타입 (MIME type)
    @Column(name = "file_type", length = 100, nullable = false)
    private String fileType;

    @Builder.Default
    @Column(name = "is_final", nullable = false)
    private boolean isFinal = false;              // TINYINT(1) ↔ boolean 자동 매핑

    @Column(name = "final_set_by")
    private Long finalSetBy;              // 굳이 연관관계 아니어도 OK (필요하면 @ManyToOne(User)로 변경)

    @Column(name = "final_set_at")
    private LocalDateTime finalSetAt;

    // ✅ 현재 승인 상태 캐시
    @Enumerated(EnumType.STRING)
    @Column(name = "final_status", nullable = false, length = 20)
    private FinalStatus finalStatus = FinalStatus.DRAFT;

}
