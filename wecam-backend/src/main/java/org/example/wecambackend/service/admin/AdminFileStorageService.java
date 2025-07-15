package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
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

/**
 * 관리자용 파일 업로드/삭제 서비스
 *
 * 사용 예시:
 * - 게시판 이미지 업로드
 * - 관리자 전용 콘텐츠 파일 저장
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminFileStorageService {

    /**
     * application.yml에 설정된 파일 저장 디렉토리
     * 예: ./uploads
     */
    @Value("${app.file.upload-dir}")
    private String uploadDir;

    /**
     * 단일 파일 저장
     *
     * @param file MultipartFile (업로드된 파일)
     * @param folder UploadFolder enum (저장할 디렉토리 종류)
     * @return AdminFileResponse (저장된 파일 정보: 원본 이름, 저장 이름, 경로 등)
     * @throws BaseException 파일 저장 실패 시 예외 발생
     *
     * 사용 위치 예:
     * - 관리자 페이지 이미지 업로드
     */
    public AdminFileResponse saveFile(MultipartFile file, UploadFolder folder) {
        if (file.isEmpty()) {
            throw new BaseException(BaseResponseStatus.INVALID_FILE_INPUT);
        }
        try {
            String originalName = file.getOriginalFilename();
            String extension = "";

            // 확장자 추출 (.jpg, .png 등)
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // UUID 기반 파일명 생성 (충돌 방지)
            String uuid = UUID.randomUUID().toString();
            String storedFileName = uuid + "_" + originalName;

            // 저장 경로 생성 (예: ./uploads/banner/)
            Path folderPath = Paths.get(uploadDir, folder.getFolderName()).toAbsolutePath().normalize();
            Files.createDirectories(folderPath); // 디렉토리 없으면 생성

            // 실제 파일 저장
            Path filePath = folderPath.resolve(storedFileName);
            file.transferTo(filePath.toFile());

            // 응답 정보 구성
            String fileUrl = uploadDir + "/" + folder.getFolderName() + "/" + storedFileName;
            String relativePath = folder.getFolderName() + "/" + storedFileName;

            return AdminFileResponse.builder()
                    .originalFileName(originalName)
                    .storedFileName(storedFileName)
                    .filePath(relativePath)
                    .url(fileUrl)
                    .build();

        } catch (IOException e) {
            // 저장 실패 → 서버 에러로 응답
            throw new BaseException(BaseResponseStatus.FILE_SAVE_FAILED);
        }
    }

    /**
     * 다중 파일 저장
     *
     * @param files List<MultipartFile> (업로드할 파일 목록)
     * @param folder UploadFolder enum (저장할 디렉토리 종류)
     * @return List<AdminFileResponse> (각 파일 저장 결과)
     *
     * 사용 위치 예:
     * - 관리자 페이지에서 여러 이미지 한 번에 업로드할 때
     */
    public List<AdminFileResponse> saveFiles(List<MultipartFile> files, UploadFolder folder) {
        if (files == null || files.isEmpty()) return Collections.emptyList();

        return files.stream()
                .map(file -> saveFile(file, folder)) // 각 파일 저장
                .collect(Collectors.toList());
    }

    /**
     * 저장된 파일 삭제
     *
     * @param filePath 상대 경로 (예: banner/uuid_filename.jpg)
     * @throws BaseException 파일이 없거나 삭제 실패 시 예외 발생
     *
     * 사용 위치 예:
     * - 이미지 교체 시 기존 이미지 삭제
     * - 불필요한 파일 정리
     */
    public void deleteFile(String filePath) {
        File file = new File(uploadDir + File.separator + filePath);

        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                // 파일 삭제 시도했지만 실패
                throw new BaseException(BaseResponseStatus.FILE_DELETE_FAILED);
            }
        } else {
            // 파일이 존재하지 않음
            throw new BaseException(BaseResponseStatus.FILE_NOT_FOUND);
        }
    }
}
