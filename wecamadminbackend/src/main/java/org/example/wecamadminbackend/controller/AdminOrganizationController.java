package org.example.wecamadminbackend.controller;


import lombok.RequiredArgsConstructor;
import org.example.wecamadminbackend.service.AdminOrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RequiredArgsConstructor
@Controller
@RequestMapping("admin/organization")
public class AdminOrganizationController {

    private final AdminOrganizationService adminOrganizationService;

    @GetMapping("/list")
    public String organizationRequestPage(Model model,@AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("requestList",adminOrganizationService.getPendingRequests());
        model.addAttribute("user", userDetails);
        System.out.println(userDetails.getAuthorities());
        return "admin/organization/list";
    }

    @PostMapping("/{Id}/approve")
    public ResponseEntity<?> organizationApprove(@PathVariable("Id") Long id)
    {
        //신청서 Id에대한 승인 과정
        adminOrganizationService.approveWorkspaceRequest(id);
        return ResponseEntity.ok("워크스페이스 생성 요청 승인 완료.");
    }
}
