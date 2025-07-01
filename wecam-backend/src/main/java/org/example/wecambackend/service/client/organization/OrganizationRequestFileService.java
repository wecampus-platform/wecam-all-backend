package org.example.wecambackend.service.client.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.organization.OrganizationRequest;
import org.example.model.organization.OrganizationRequestFile;
import org.example.wecambackend.repos.organization.OrganizationRequestFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationRequestFileService {

    private final OrganizationRequestFileRepository fileRepository;

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

            String savedFileName = uuid + "_" + originalFileName;
            String absoluteFilePath = BASE_DIR + savedFileName;
            String relativeFilePath = RELATIVE_PATH + savedFileName;

            log.info("파일 절대 저장 경로: {}", absoluteFilePath);

            file.transferTo(new File(absoluteFilePath));

            OrganizationRequestFile saved = OrganizationRequestFile.builder()
                    .uuid(uuid)
                    .originalFileName(originalFileName)
                    .savedFileName(savedFileName)
                    .filePath(relativeFilePath)
                    .organizationRequest(request)
                    .build();

            fileRepository.save(saved);

        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("조직 요청 파일 저장 실패", e);
        }
    }
}