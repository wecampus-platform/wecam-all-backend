package org.example.wecambackend.controller.client;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.request.MyPageOrganizationEditRequest;
import org.example.wecambackend.dto.response.MyPageResponse;
import org.example.wecambackend.service.client.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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



    @PostMapping(value = "/userInfo/edit")
    @Operation(summary = "기본 정보 수정하기에서 이름 값 수정하기",
            description = "수정된 이름값 반환"
    )
    public BaseResponse<String> editUserName(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("userName") String userName){
        return new BaseResponse<>(myPageService.editUserName(userDetails.getId(),userName));
    }


    @Operation(summary = "기본 정보 수정하기에서 소속 정보의 학번 , 학년 ,재학여부 수정하기" ,
    description = "반환값 없음.")
    @PostMapping("/userOrganization/edit")
    public BaseResponse<?> editUserOrganizationInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestPart("request") MyPageOrganizationEditRequest request){
        myPageService.editUserOrganizationInfo(userDetails.getId(),request);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

}
