package org.example.model.file;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.user.User;

import java.util.UUID;

@Entity
@Getter
@Table(name = "file_Asset")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileAsset extends BaseEntity {


    // 파일 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;


    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID uuid;

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

    @ManyToOne
    @Column(name = "council",nullable = false)
    private Council council;

    @ManyToOne
    @Column(name = "user",nullable = false)
    private User user;


}
