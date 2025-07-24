package org.example.wecambackend.service.client;

import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.responseDTO.ProfileImageResponse;
import org.example.wecambackend.repos.UserInformationRepository;
import org.example.wecambackend.service.client.common.filesave.FilePath;
import org.example.wecambackend.service.client.common.filesave.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService { // 이미지 업로드, 수정, 삭제용
    private final FileStorageService fileStorageService;
    private final UserInformationRepository userInformationRepository;

//    /**
//     * 프로필 원본 + 썸네일 저장 후 URL 반환
//     * DB에는 원본 경로만 저장
//     */
//    @Transactional
//    public ProfileImageResponse uploadProfileImage(Long userId, MultipartFile file) {
//        UUID uuid = UUID.randomUUID();
//        Map<String, String> original = fileStorageService.save(file, uuid, FilePath.PROFILE);
//
//        // 썸네일 생성 후 저장
//        String storedThumbNate = uuid + "_" + file.getOriginalFilename();
//
//    }
}
