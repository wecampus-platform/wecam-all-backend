package org.example.wecambackend.controller.admin;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@IsCouncil
@RequestMapping("/admin/council/{councilName}")
@Tag(name = "Admin Organization Controller", description = "학생회 관리자 용 조직 관리 기능을 처리")
public class AdminOrganizationController {

}
