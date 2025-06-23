package org.example.wecambackend.service.client.Affiliation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    public String save(MultipartFile file, UUID uuid) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        try {
            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(basePath);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            //TODO : extension 저장할지 말지 디비에 (파일 TYPE 을 이걸로 할지 ..)

            // 파일명 = UUID + 확장자
            String storedFileName = uuid.toString()+'_'+originalFilename;
            Path savePath = basePath.resolve(storedFileName);

            file.transferTo(savePath.toFile());

            System.out.println("저장된 경로: " + savePath);
            return "/uploads/" + storedFileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

}