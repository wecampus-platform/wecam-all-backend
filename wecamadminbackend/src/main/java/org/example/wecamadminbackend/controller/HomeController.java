package org.example.wecamadminbackend.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // "/"로 들어오면 "/admin/login"으로 리다이렉트
        return "redirect:/admin/login";
    }
}
