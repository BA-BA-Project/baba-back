package com.baba.back.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenResponse {

    @JsonProperty("isSignedUp")
    private final Boolean signedUp;
    private final String message;
    private final String token;
}
