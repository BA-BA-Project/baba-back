package com.baba.back.oauth.dto;

public record MemberTokenResponse(String accessToken) implements TokenResponse {
}
