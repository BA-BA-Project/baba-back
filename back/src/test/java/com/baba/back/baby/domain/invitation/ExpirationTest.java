package com.baba.back.baby.domain.invitation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.baby.exception.ExpirationBadReqeustException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ExpirationTest {

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
}