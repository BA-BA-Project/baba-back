package com.baba.back.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SignTokenProvider extends TokenProvider {
    public SignTokenProvider(@Value("${security.jwt.token.sign.secret-key}") String secretKey,
                                @Value("${security.jwt.token.sign.expire-length}") Long validityInMilliseconds) {
        super(secretKey, validityInMilliseconds);
    }
}