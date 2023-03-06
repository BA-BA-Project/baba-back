package com.baba.back.oauth.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RefreshTokenProviderTest {

    private static final String SECRET_KEY = "a".repeat(100);
    private static final Long VALIDITY_MILLISECONDS = 172800000L;
    private RefreshTokenProvider refreshTokenProvider;

    @Test
    void 만료기간이_하루_이하로_남으면_true를_반환한다() {
        // given
        final String memberId = "memberId";
        final LocalDateTime now = LocalDateTime.now();

        final Clock clock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        refreshTokenProvider = new RefreshTokenProvider(SECRET_KEY, VALIDITY_MILLISECONDS, clock);
        final String refreshToken = refreshTokenProvider.createToken(memberId);

        final Clock timeTravelClock = Clock.fixed(now.plusDays(1)
                .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        refreshTokenProvider = new RefreshTokenProvider(SECRET_KEY, VALIDITY_MILLISECONDS, timeTravelClock);

        // when & then
        Assertions.assertThat(refreshTokenProvider.isExpiringSoon(refreshToken)).isTrue();
    }

    @Test
    void 만료기간이_하루보다_많이_남으면_false를_반환한다() {
        // given
        final String memberId = "memberId";
        final LocalDateTime now = LocalDateTime.now();

        final Clock clock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        refreshTokenProvider = new RefreshTokenProvider(SECRET_KEY, VALIDITY_MILLISECONDS, clock);
        final String refreshToken = refreshTokenProvider.createToken(memberId);

        final Clock timeTravelClock = Clock.fixed(now.plusDays(1).minusSeconds(1)
                .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        refreshTokenProvider = new RefreshTokenProvider(SECRET_KEY, VALIDITY_MILLISECONDS, timeTravelClock);

        // when & then
        Assertions.assertThat(refreshTokenProvider.isExpiringSoon(refreshToken)).isFalse();
    }
}