package org.example.wecamadminbackend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;


@Controller
public class AdminController {

    @GetMapping("/admin/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Principal principal) {
        String username = principal.getName();  // 현재 로그인한 유저명
        model.addAttribute("username", username);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(">>> Authorities: " + authentication.getAuthorities());
        System.out.println(">>> Authenticated: " + authentication.isAuthenticated());
        System.out.println(">>> Auth class: " + authentication.getClass());
        return "admin/dashboard";
    }
}

