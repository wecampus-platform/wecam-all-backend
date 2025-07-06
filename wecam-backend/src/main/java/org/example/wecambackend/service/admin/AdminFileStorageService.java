package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.dto.responseDTO.AdminFileResponse;
import org.example.wecambackend.service.admin.Enum.UploadFolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminFileStorageService {



    @Value("${app.file.upload-dir}")
    private String uploadDir; // ex) ./uploads

    public AdminFileResponse saveFile(MultipartFile file, UploadFolder folder) {
        if (file.isEmpty()) throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");

        try {
            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String uuid = UUID.randomUUID().toString();
            String storedFileName = uuid + "_" + originalName;

            // enum에서 디렉토리명 추출
            Path folderPath = Paths.get(uploadDir, folder.getFolderName()).toAbsolutePath().normalize();
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(storedFileName);
            file.transferTo(filePath.toFile());

            String fileUrl = "/uploads/" + folder.getFolderName() + "/" + storedFileName;

            return AdminFileResponse.builder()
                    .originalFileName(originalName)
                    .storedFileName(storedFileName)
                    .filePath(filePath.toString())
                    .url(fileUrl)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

    public List<AdminFileResponse> saveFiles(List<MultipartFile> files, UploadFolder folder) {
        if (files == null || files.isEmpty()) return Collections.emptyList();

        return files.stream()
                .map(file -> saveFile(file, folder))
                .collect(Collectors.toList());
    }

    // 파일 삭제
    public void deleteFile(String filePath) {
        File file = new File(uploadDir + File.separator + filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new RuntimeException("파일 삭제 실패: " + filePath);
            }
        } else {
            throw new IllegalArgumentException("삭제할 파일이 존재하지 않음: " + filePath);
        }
    }

}
