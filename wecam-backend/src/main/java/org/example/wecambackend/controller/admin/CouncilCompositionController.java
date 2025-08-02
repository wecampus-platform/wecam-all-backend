package org.example.wecambackend.controller.admin;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@IsCouncil
@RestController
@RequestMapping("/admin/council/{councilName}/composition")
@RequiredArgsConstructor
@Tag(name = "Council Composition Controller", description = "학생회 관리자 페이지 내 구성원 관리 부분")
public class CouncilCompositionController {


}
