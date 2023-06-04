package com.baba.back.oauth.service;

import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider extends TokenProvider {

    public AccessTokenProvider(@Value("${security.jwt.token.access.secret-key}") String secretKey,
                               @Value("${security.jwt.token.access.expire-length}") Long validityInMilliseconds,
                               Clock clock) {
        super(secretKey, validityInMilliseconds, clock);
    }
}
