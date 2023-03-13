package com.baba.back.oauth.controller;

import com.baba.back.oauth.dto.SearchTermsResponse;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.dto.TokenRefreshResponse;
import com.baba.back.oauth.service.OAuthService;
import com.baba.back.swagger.BadRequestResponse;
import com.baba.back.swagger.CreatedResponse;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.OkResponse;
import com.baba.back.swagger.UnAuthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/auth/login")
    public ResponseEntity<SocialLoginResponse> signInKakao(@RequestBody @Valid SocialTokenRequest tokenRequest) {
        return ResponseEntity.ok().body(oauthService.signInKakao(tokenRequest));
    }

    @Operation(summary = "약관 조회 요청")
    @OkResponse
    @BadRequestResponse
    @IntervalServerErrorResponse
    @PostMapping("/auth/terms")
    public ResponseEntity<SearchTermsResponse> searchTerms(@RequestBody @Valid SocialTokenRequest tokenRequest) {
        return ResponseEntity.ok().body(oauthService.searchTerms(tokenRequest));
    }

    @Operation(summary = "토큰 재발급 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(@RequestBody @Valid TokenRefreshRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(oauthService.refresh(request));
    }
}
