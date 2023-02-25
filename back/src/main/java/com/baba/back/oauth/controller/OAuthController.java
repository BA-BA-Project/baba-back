package com.baba.back.oauth.controller;

import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TokenResponse;
import com.baba.back.oauth.service.OAuthService;
import com.baba.back.swagger.BadRequestResponse;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.OkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 관련 API")
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;

    @Operation(summary = "소셜 로그인 요청")
    @OkResponse
    @BadRequestResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponse> signInKakao(@RequestBody @Valid SocialTokenRequest tokenRequest) {
        final SocialLoginResponse socialLoginResponse = oauthService.signInKakao(tokenRequest);
        return ResponseEntity.status(socialLoginResponse.httpStatus()).body(socialLoginResponse.tokenResponse());
    }
}
