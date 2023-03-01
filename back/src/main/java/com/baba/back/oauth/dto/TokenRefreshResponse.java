package com.baba.back.oauth.dto;


public record TokenRefreshResponse(String accessToken, String refreshToken) {
}
