package org.example.wecambackend.controller.client;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsUnStudent;
import org.example.wecambackend.config.security.annotation.IsUnauth;
import org.example.model.enums.AuthenticationType;
import org.example.wecambackend.service.client.Affiliation.AffiliationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/client/user/affiliation")
@RequiredArgsConstructor
@Tag(name = "Client User Affiliation Controller", description = "유저가 재학생/신입생 인증 하는 것")
public class UserAffiliationController {

    private final AffiliationService affiliationService;


    /*TODO : 추후 업로드 파일 분기해야됨. 다만 어차피 S3 같은걸로 바꿀거라 나중에해도 될거같다는 생각이 들었음.
       학교별 ID , 조직별 ID 가져와서 폴더 생성해야될 듯*/
    //컨트롤러 딴에선 분리 다만 서비스는 합칠 것임.
    @IsUnauth
    @PostMapping("/freshman")
    @Operation(summary = "신입생 인증 요청", description = "사진을 업로드하고 OCR 추출 결과를 DB에 저장")
    public ResponseEntity<?> registerFreshmanAffiliation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("file") MultipartFile file
    ) {
        Long userId = userDetails.getId();
        affiliationService.saveStudentAffiliation(userId, file , AuthenticationType.NEW_STUDENT);
        return ResponseEntity.ok("신입생 인증 요청이 접수되었습니다.");
    }

    @IsUnStudent
    @PostMapping("/currentStudent")
    @Operation(summary = "재학생 인증 요청", description = "사진을 업로드하고 OCR 추출 결과를 DB에 저장")
    public ResponseEntity<?> registerCurrentStudentAffiliation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("file") MultipartFile file
    ) {
        Long userId = userDetails.getId();
        affiliationService.saveStudentAffiliation(userId, file , AuthenticationType.CURRENT_STUDENT);
        return ResponseEntity.ok("재학생 인증 요청이 접수되었습니다.");
    }


}
