package org.example.wecambackend.service.client.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.organization.OrganizationRequest;
import org.example.model.organization.OrganizationRequestFile;
import org.example.wecambackend.repos.organization.OrganizationRequestFileRepository;
import org.example.wecambackend.service.client.common.filesave.FilePath;
import org.example.wecambackend.service.client.common.filesave.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationRequestFileService {

    private final OrganizationRequestFileRepository fileRepository;
    private final FileStorageService fileStorageService;
    // 프로젝트 루트 기준 절대경로
    private static final String BASE_DIR = System.getProperty("user.dir") + "/upload/organization_request/";

    // DB에 저장되는 상대경로 상수
    private static final String RELATIVE_PATH = "/uploads/organization_request/";

    public void storeRequestFile(MultipartFile file, OrganizationRequest request) {
        try {
            Files.createDirectories(Paths.get(BASE_DIR));

            UUID uuid = UUID.randomUUID();
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null || originalFileName.isBlank()) {
                throw new IllegalArgumentException("파일명이 비어 있습니다.");
            }

            Map<String, String> fileInfo = fileStorageService.save(file, uuid, FilePath.AFFILIATION);
            String filePath = fileInfo.get("filePath");
            String fileUrl = fileInfo.get("fileUrl");

            OrganizationRequestFile saved = OrganizationRequestFile.builder()
                    .uuid(uuid)
                    .originalFileName(originalFileName)
                    .savedFileName(uuid+"_"+originalFileName)
                    .filePath(filePath)
                    .fileUrl(fileUrl)
                    .organizationRequest(request)
                    .build();

            fileRepository.save(saved);

        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("조직 요청 파일 저장 실패", e);
        }
    }
}
