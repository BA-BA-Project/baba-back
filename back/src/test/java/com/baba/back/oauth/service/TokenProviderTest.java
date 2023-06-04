package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.ExpiredTokenAuthenticationException;
import com.baba.back.oauth.exception.InvalidTokenAuthenticationException;
import java.time.Clock;
import org.junit.jupiter.api.Test;

class TokenProviderTest {

    private static final String SECRET_KEY = "a".repeat(100);
    private static final Long VALIDITY_MILLISECONDS = 3600000L;
    private static final Clock CLOCK = Clock.systemDefaultZone();
    private final TokenProvider tokenProvider = new AccessTokenProvider(SECRET_KEY, VALIDITY_MILLISECONDS, CLOCK);

    @Test
    void payLoad를_통해_토큰을_만든다() {
        // given
        final String payLoad = "kakao1231";

        // when
        final String token = tokenProvider.createToken(payLoad);

        // then
        assertThat(token).isNotNull();
    }

    @Test
    void 만료된_토큰인지_확인한다() {
        // given
        final AccessTokenProvider tokenProvider = new AccessTokenProvider(SECRET_KEY, 0L, CLOCK);
        final String expiredToken = tokenProvider.createToken("expiredToken");

        // when & then
        assertThatThrownBy(() -> this.tokenProvider.validateToken(expiredToken))
                .isInstanceOf(ExpiredTokenAuthenticationException.class);
    }

    @Test
    void 유효하지_않은_토큰인지_확인한다() {
        // given
        final String invalidToken = "invalidToken";

        // when & then
        assertThatThrownBy(() -> tokenProvider.validateToken(invalidToken))
                .isInstanceOf(InvalidTokenAuthenticationException.class);
    }

    @Test
    void 토큰을_파싱한다() {
        // given
        final String actual = "payload";
        final String token = tokenProvider.createToken(actual);

        // when
        final String result = tokenProvider.parseToken(token);

        // then
        assertThat(actual).isEqualTo(result);
    }

    @Test
    void 토큰을_파싱_시_유효하지_않은_토큰이라면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";

        // when & then
        assertThatThrownBy(() -> tokenProvider.parseToken(invalidToken))
                .isInstanceOf(InvalidTokenAuthenticationException.class);
    }

    @Test
    void 토큰을_파싱_시_만료된_토큰이라면_예외를_던진다() {
        // given
        final AccessTokenProvider tokenProvider = new AccessTokenProvider(SECRET_KEY, 0L, CLOCK);
        final String expiredToken = tokenProvider.createToken("expiredToken");

        // when & then
        assertThatThrownBy(() -> tokenProvider.parseToken(expiredToken))
                .isInstanceOf(InvalidTokenAuthenticationException.class);
    }
}
