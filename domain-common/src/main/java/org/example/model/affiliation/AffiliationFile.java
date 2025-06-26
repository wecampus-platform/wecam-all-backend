package org.example.model.affiliation;

import jakarta.persistence.*;
import lombok.*;
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
public class AffiliationFile {

    @Id
    @Column(name = "pk_upload_userid")
    private Long userId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type")
    private AuthenticationType authenticationType;

    @Column(name = "uuid", nullable = false, columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(name = "file_path", columnDefinition = "TEXT", nullable = false)
    private String filePath;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        this.expiresAt = LocalDateTime.now().plusDays(10);
        //추후 커스텀마이징 설정값으로 바꿀 예정.
    }

    public String getFilePath() {
        return this.filePath;
    }
}
