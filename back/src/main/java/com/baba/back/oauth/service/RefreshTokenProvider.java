package com.baba.back.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider extends TokenProvider {
    public RefreshTokenProvider(@Value("${security.jwt.token.refresh.secret-key}") String secretKey,
                                @Value("${security.jwt.token.refresh.expire-length}") Long validityInMilliseconds) {
        super(secretKey, validityInMilliseconds);
    }
}
