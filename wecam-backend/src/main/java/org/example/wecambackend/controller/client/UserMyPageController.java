package org.example.wecambackend.controller.client;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.responseDTO.MyPageResponse;
import org.example.wecambackend.service.client.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("client/user/mypage")
@Tag(name = "User MyPage Controller ",description = "유저 마이페이지 - 권한 ; 로그인한 유저(client/)")
public class UserMyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<MyPageResponse> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(myPageService.getMyPageInfo(userDetails));

    }

}
