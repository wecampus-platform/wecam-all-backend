package org.example.wecambackend.service.client.common.filesave;

import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

// 파일 저장을 담당하는 서비스 클래스입니다.
// 현재는 로컬 디스크에 저장하지만, 추후 S3로 전환하기 쉽도록 fileUrl과 filePath를 분리해 관리합니다.
@Service
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir; // 실제 서버에 저장되는 물리적 경로 (ex: ./uploads)

    @Value("${app.file.url-prefix}")
    private String uploadUrlPrefix; // 사용자에게 제공할 파일 접근 경로 prefix (ex: /uploads 또는 S3 도입 시 https://...)

    // MultipartFile을 받아 저장하고, 사용자 접근용 fileUrl을 반환합니다.
    public Map<String, String> save(MultipartFile file, UUID uuid,FilePath fileDir) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        try {
            String allPath = uploadDir + "/" + fileDir + "/";
            // 1. 저장 디렉토리 생성
            Path basePath = Paths.get(allPath).toAbsolutePath().normalize();
            Files.createDirectories(basePath);

            // 2. 저장 파일명
            String originalFilename = file.getOriginalFilename();
            String storedFileName = uuid + "_" + originalFilename;

            // 3. 저장 경로 설정
            Path savePath = basePath.resolve(storedFileName);
            file.transferTo(savePath.toFile());

            // 4. filePath (상대경로)와 fileUrl (접근 URL)
            String filePath = allPath + storedFileName; // DB에 저장할 상대 경로
            String fileUrl = uploadUrlPrefix + "/" +  fileDir + "/" + storedFileName;

            return Map.of(
                    "filePath", filePath,
                    "fileUrl", fileUrl
            );

        } catch (IOException e) {
            throw new BaseException(BaseResponseStatus.FILE_SAVE_FAILED);
        }
    }


}
