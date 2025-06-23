package org.example.wecamadminbackend.controller;


import lombok.RequiredArgsConstructor;
import org.example.wecamadminbackend.service.AdminOrganizationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
