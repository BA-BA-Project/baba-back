package com.baba.back.oauth.service;

import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SignTokenProvider extends TokenProvider {
    public SignTokenProvider(@Value("${security.jwt.token.sign.secret-key}") String secretKey,
                             @Value("${security.jwt.token.sign.expire-length}") Long validityInMilliseconds,
                             Clock clock) {
        super(secretKey, validityInMilliseconds, clock);
    }
}