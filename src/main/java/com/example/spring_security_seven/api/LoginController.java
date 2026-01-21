package com.example.spring_security_seven.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /// 로그인 페이지 제공 메소드
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
