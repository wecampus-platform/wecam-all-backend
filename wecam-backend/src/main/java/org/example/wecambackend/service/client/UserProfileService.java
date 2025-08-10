package org.example.wecambackend.service.client;

import lombok.RequiredArgsConstructor;
import org.example.model.user.UserInformation;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.dto.responseDTO.ProfileImageResponse;
import org.example.wecambackend.repos.user.UserInformationRepository;
import org.example.wecambackend.service.client.common.filesave.FilePath;
import org.example.wecambackend.service.client.common.filesave.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

import static org.example.wecambackend.common.response.BaseResponseStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserProfileService { // 이미지 업로드, 수정, 삭제용
    private final FileStorageService fileStorageService;
    private final UserInformationRepository userInformationRepository;

    /**
     * 프로필 원본 + 썸네일 저장 후 URL 반환
     * DB에는 원본 경로만 저장
     *  - filePath : DB에 저장할 상대 경로 (예: "PROFILE/uuid_원본.jpg")
     *  - fileUrl  : 클라이언트에 반환할 URL (예: "/uploads/PROFILE/uuid_원본.jpg")
     */
    @Transactional
    public ProfileImageResponse uploadProfileImage(Long userId, MultipartFile file) {
        UUID uuid = UUID.randomUUID();

        Map<String, String> saved = fileStorageService.saveWithThumbnail(
                file,
                uuid,
                FilePath.PROFILE,
                FilePath.PROFILE_THUMB,
                200, 200
        );

        UserInformation info = userInformationRepository.findByUser_UserPkId(userId)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
        info.setProfileImagePath(saved.get("filePath"));

        return ProfileImageResponse.builder()
                .imageUrl(saved.get("fileUrl"))
                .thumbnailUrl(saved.get("thumbUrl"))
                .build();
    }
}
