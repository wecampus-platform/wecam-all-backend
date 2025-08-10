package org.example.wecambackend.util;

import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class FileValidationUtil {
    // 허용된 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpg", "jpeg", "png");
    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    // 최대 파일 개수
    private static final int MAX_FILE_COUNT = 3;

    /**
     * 파일 개수 검증
     */
    public static void validateFileCount(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return; // 파일이 없어도 OK
        }
        if (files.size() > MAX_FILE_COUNT) {
            throw new BaseException(BaseResponseStatus.INVALID_INPUT, 
                String.format("첨부파일은 최대 %d개까지 업로드 가능합니다. (현재: %d개)", MAX_FILE_COUNT, files.size()));
        }
    }

    /**
     * 개별 파일 검증
     */
    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return; // 빈 파일은 OK
        }
        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BaseException(BaseResponseStatus.INVALID_INPUT,
                String.format("파일 크기는 최대 %dMB까지 가능합니다. (현재: %.2fMB)",
                    MAX_FILE_SIZE / (1024 * 1024),
                    file.getSize() / (1024.0 * 1024.0)));
        }
        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BaseException(BaseResponseStatus.INVALID_INPUT, "파일명이 없습니다.");
        }
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BaseException(BaseResponseStatus.INVALID_INPUT,
                String.format("허용되지 않는 파일 형식입니다. 허용된 형식: %s", String.join(", ", ALLOWED_EXTENSIONS)));
        }
    }

    /**
     * 파일 확장자 추출
     */
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 모든 파일 검증
     */
    public static void validateAllFiles(List<MultipartFile> files) {
        validateFileCount(files);
        if (files != null) {
            for (MultipartFile file : files) {
                validateFile(file);
            }
        }
    }
}
