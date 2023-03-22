package com.baba.back.baby.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.baby.exception.ExpirationBadReqeustException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ExpirationTest {

    public static final int EXPIRATION_DAYS = 10;

    @Test
    void 현재시각이_null이면_예외를_던진다() {
        assertThatThrownBy(() -> Expiration.from(null))
                .isInstanceOf(ExpirationBadReqeustException.class);
    }

    @Test
    void 현재시각이_null이_아니면_Expiration객체를_생성한다() {
        // given
        final LocalDateTime now = LocalDateTime.now();

        // when & then
        assertThatCode(() -> Expiration.from(now))
                .doesNotThrowAnyException();
    }

    @Test
    void 현재가_만료시각보다_과거이면_만료되지_않았다() {
        // given
        final LocalDateTime now = LocalDateTime.now();

        // when
        final Expiration expiration = Expiration.from(now);

        // then
        assertThat(expiration.isExpired(now.plusDays(EXPIRATION_DAYS))).isFalse();
    }

    @Test
    void 현재가_만료시각보다_미래이면_만료되었다() {
        // given
        final LocalDateTime now = LocalDateTime.now();

        // when
        final Expiration expiration = Expiration.from(now);

        // then
        assertThat(expiration.isExpired(now.plusDays(EXPIRATION_DAYS).plusSeconds(1))).isTrue();
    }
}