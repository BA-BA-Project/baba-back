package com.baba.back.oauth.dto;

import org.springframework.http.HttpStatus;

public record SocialLoginResponse(HttpStatus httpStatus, TokenResponse tokenResponse) {
}
