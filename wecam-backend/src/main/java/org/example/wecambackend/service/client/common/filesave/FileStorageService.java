package org.example.wecambackend.service.client.common.filesave;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 파일 저장을 담당하는 서비스 클래스입니다.
// 현재는 로컬 디스크에 저장하지만, 추후 S3로 전환하기 쉽도록 fileUrl과 filePath를 분리해 관리합니다.
@Service
@Getter
@Slf4j
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
            Path basePath = Paths.get(allPath);
            Files.createDirectories(basePath);

            // 2. 저장 파일명
            String originalFilename = file.getOriginalFilename();
            String storedFileName = uuid + "_" + originalFilename;

            // 3. 저장 경로 설정
            Path savePath = basePath.resolve(storedFileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
            }
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

    // 원본 + 썸네일 저장
    public Map<String, String> saveWithThumbnail(
            MultipartFile file,
            UUID uuid,
            FilePath dir,
            FilePath thumbDir,
            int thumbWidth,
            int thumbHeight
    ) {
        if (file.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FILE_EMPTY);
        }

        try {
            // 사진 저장할 폴더 생성
            Path origBase = Paths.get(uploadDir, dir.name());
            Path thumbBase = Paths.get(uploadDir, thumbDir.name());
            Files.createDirectories(origBase);
            Files.createDirectories(thumbBase);

            String originalFilename = file.getOriginalFilename();
            String storedFileName = uuid + "_" + originalFilename;

            // 원본 사진 저장
            Path origPath = origBase.resolve(storedFileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, origPath, StandardCopyOption.REPLACE_EXISTING);
            }
            // 썸네일 생성
            Path thumbPath = thumbBase.resolve(storedFileName);
            Thumbnails.of(origPath.toFile())
                    .size(thumbWidth, thumbHeight)
                    .toFile(thumbPath.toFile());

            HashMap<String, String> result = new HashMap<>();
            // DB에 저장할 상대 경로
            result.put("filePath", dir.name() + "/" + storedFileName);
            result.put("thumbPath", thumbDir.name() + "/" + storedFileName);

            // 클라이언트에 반환할 URL
            result.put("fileUrl",  uploadUrlPrefix + "/" + dir.name() + "/" + storedFileName);
            result.put("thumbUrl", uploadUrlPrefix + "/" + thumbDir.name() + "/" + storedFileName);

            return result;

        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new BaseException(BaseResponseStatus.FILE_SAVE_FAILED);
        }
    }


}
