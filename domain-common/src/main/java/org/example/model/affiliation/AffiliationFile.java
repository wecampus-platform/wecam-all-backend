package org.example.model.affiliation;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
import org.example.model.enums.AuthenticationType;
import org.example.model.enums.FileType;

import java.time.LocalDateTime;
import java.util.UUID;

@IdClass(AffiliationCertificationId.class)
@Entity
@Table(name = "affiliation_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 소속 인증 신청 시 첨부된 파일을 저장하는 엔티티
public class AffiliationFile extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "pk_upload_userid", referencedColumnName = "pk_upload_userid", insertable = false, updatable = false),
            @JoinColumn(name = "authentication_type", referencedColumnName = "authentication_type", insertable = false, updatable = false)
    })
    private AffiliationCertification affiliationCertification;
    
    // 파일 업로드한 유저의 ID (복합키)
    @Id
    @Column(name = "pk_upload_userid")
    private Long userId;

    // 인증 유형 (ex: 재학/졸업 등) - 복합키
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type")
    private AuthenticationType authenticationType;

    // UUID (파일 식별자) - DB상에서는 BINARY(16)으로 저장
    @Column(name = "uuid", nullable = false, columnDefinition = "BINARY(16)")
    private UUID uuid;

    // getter override (필요 시 사용)
    // 실제 저장 경로 기준 상대 경로 (예: "user/uuid_abc.jpg")
    @Getter
    @Column(name = "file_path", columnDefinition = "TEXT", nullable = false)
    private String filePath;

    //클라이언트에서 접근 가능한 URL 경로 (예: "/uploads/user/uuid_abc.jpg")
    @Column(name = "file_url", length = 512)
    private String fileUrl;

    // 원본 파일 이름 (사용자 업로드 기준 이름)
    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    // 파일 타입 (ex: IMAGE, PDF 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    // 파일 만료일 (예: 기본 10일 뒤로 설정됨)
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // 엔티티 저장 전 자동으로 날짜 필드 초기화
    @Override
    protected void prePersistChild() {
        // 기본 만료일 = 10일 후 (추후 설정값으로 바꿀 수 있음)
        this.expiresAt = LocalDateTime.now().plusDays(10);
    }

}
