package org.example.wecambackend.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.responseDTO.ProfileImageResponse;
import org.example.wecambackend.service.client.UserProfileService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "User Profile",
        description = "사용자 프로필 이미지 업로드 및 관리 API"
)
@RestController
@RequestMapping("/client/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // 프로필 이미지 업로드
    @Operation(
            summary = "프로필 이미지 업로드",
            description = "원본 이미지와 썸네일을 생성한 후, 접근 가능한 URL을 반환합니다."
    )
    @PostMapping(
            value = "/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BaseResponse<ProfileImageResponse> uploadProfileImage( @AuthenticationPrincipal UserDetailsImpl currentUser, @RequestParam("file") MultipartFile file ) {

        ProfileImageResponse response = userProfileService.uploadProfileImage(currentUser.getId(), file);
        return new BaseResponse<>(response);

    }
}
