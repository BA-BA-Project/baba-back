package com.baba.back.oauth.service;

import static org.mockito.BDDMockito.given;

import java.time.Clock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenProviderTest {

    private static final String SECRET_KEY = "a".repeat(100);
    private static final Long VALIDITY_MILLISECONDS = 86400000L;
    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private Clock clock;

    @Test
    void 만료기간이_하루_이하로_남으면_true를_반환한다() {
        // given
        final String memberId = "memberId";
        final Clock now = Clock.systemDefaultZone();

        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());

        refreshTokenProvider = new RefreshTokenProvider(SECRET_KEY, VALIDITY_MILLISECONDS, clock);

        final String refreshToken = refreshTokenProvider.createToken(memberId);

        // when & then
        Assertions.assertThat(refreshTokenProvider.isExpiringSoon(refreshToken)).isTrue();
    }

    @Test
    void 만료기간이_하루보다_많이_남으면_false를_반환한다() {
        // given
        final String memberId = "memberId";
        final Clock now = Clock.systemDefaultZone();
        final Long twoDaysMilliseconds = 172800000L;

        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());

        refreshTokenProvider = new RefreshTokenProvider(SECRET_KEY, twoDaysMilliseconds, clock);

        final String refreshToken = refreshTokenProvider.createToken(memberId);

        // when & then
        Assertions.assertThat(refreshTokenProvider.isExpiringSoon(refreshToken)).isFalse();
    }
}