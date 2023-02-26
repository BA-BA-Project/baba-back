package com.baba.back.oauth.dto;

public record LoginTokenResponse(String accessToken, String refreshToken) implements TokenResponse{

}
