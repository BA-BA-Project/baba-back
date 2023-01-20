package com.baba.back.oauth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenResponse {
    private final boolean isSigned;
    private final String message;
    private final String token;
}
