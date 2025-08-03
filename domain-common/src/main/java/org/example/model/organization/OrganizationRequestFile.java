package org.example.model.organization;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "organization_request_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationRequestFile extends BaseEntity {

    // 조직 생성 요청 첨부파일 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    // 파일 고유 식별자
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID uuid;

    // 원본 파일명
    @Column(nullable = false)
    private String originalFileName;

    // DB 저장 파일명 (uuid_원본파일명)
    @Column(nullable = false)
    private String savedFileName;

    // 로컬 저장 경로 (나중에 S3 key로 변경)
    @Column(nullable = false)
    private String filePath;

    // S3 Presigned URL (추후 확장 고려)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private OrganizationRequest organizationRequest;
}
