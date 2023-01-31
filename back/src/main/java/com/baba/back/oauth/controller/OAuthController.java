package com.baba.back.oauth.controller;

import com.baba.back.oauth.dto.TokenResponse;
import com.baba.back.oauth.service.OAuthService;
import com.baba.back.swagger.BadRequestResponse;
import com.baba.back.swagger.ForbiddenResponse;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.OkResponse;
import com.baba.back.swagger.UnAuthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 관련 API")
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService OAuthService;

    @Operation(summary = "카카오 로그인 요청")
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @ForbiddenResponse
    @OkResponse
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<TokenResponse> signInKakao(@RequestParam("code") String code) {
        return ResponseEntity.ok(OAuthService.signInKakao(code));
    }
}
