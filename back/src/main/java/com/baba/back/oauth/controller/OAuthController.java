package com.baba.back.oauth.controller;

import com.baba.back.oauth.dto.TokenResponse;
import com.baba.back.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService OAuthService;

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<TokenResponse> signInKakao(@RequestParam("code") String code) {
        return ResponseEntity.ok(OAuthService.signInKakao(code));
    }
}
