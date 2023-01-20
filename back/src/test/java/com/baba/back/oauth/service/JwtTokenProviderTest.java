package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private final String secretKey = "a".repeat(100);
    private final Long validityInMilliSeconds = 3600000L;
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(secretKey, validityInMilliSeconds);

    @Test
    void payLoad를_통해_토큰을_만든다() {
        // given
        final String payLoad = "kakao1231";

        // when
        final String token = jwtTokenProvider.createToken(payLoad);

        // then
        assertThat(token).isNotNull();
    }
}
