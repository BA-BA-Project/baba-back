package com.baba.back.oauth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberJoinResponse {
    private final boolean isSignedUp;
    private final String message;
}
