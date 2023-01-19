package com.baba.back.auth.controller;

import com.baba.back.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/oauth2/code/kakao")
    public void signInKakao(@RequestParam("code") String code) {
        authService.signInKakao(code);
    }

}
