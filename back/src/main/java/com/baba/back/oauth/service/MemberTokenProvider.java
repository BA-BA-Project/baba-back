package com.baba.back.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MemberTokenProvider extends TokenProvider {

    public MemberTokenProvider(@Value("${security.jwt.token.member.secret-key}") String secretKey,
                               @Value("${security.jwt.token.member.expire-length}") Long validityInMilliseconds) {
        super(secretKey, validityInMilliseconds);
    }
}
