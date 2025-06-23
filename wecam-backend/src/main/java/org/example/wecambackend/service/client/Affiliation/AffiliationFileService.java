package org.example.wecambackend.service.client.Affiliation;

import lombok.RequiredArgsConstructor;
import org.example.model.affiliation.AffiliationCertification;
import org.example.model.affiliation.AffiliationFile;
import org.example.model.enums.FileType;
import org.example.wecambackend.repos.affiliation.AffiliationFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AffiliationFileService {

    private final AffiliationFileRepository affiliationFileRepository;


    public void saveToDB(AffiliationCertification cert,MultipartFile file, String path, UUID uuid) {

        String originalFileName = file.getOriginalFilename();
        AffiliationFile affiliationFile = AffiliationFile.builder()
                .affiliationCertification(cert)
                .id(cert.getId()) //복합키 설정
                .filePath(path)
                .fileName(originalFileName)
                .uuid(uuid)
                .fileType(FileType.IMAGE) // TODO: 우선은 IMAGE 로 고정
                .createdAt(LocalDateTime.now())
                .build();

        affiliationFileRepository.save(affiliationFile);    }
}
