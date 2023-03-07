package com.baba.back.invitation.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.invitation.exception.ExpirationBadReqeustException;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExpirationTest {

    public static Stream<Arguments> invalidNowAndValue() {
        final LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(null, now),
                Arguments.of(now, null),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNowAndValue")
    void 만료시간_혹은_현재시간이_null이면_예외를_던진다(LocalDateTime now, LocalDateTime value) {
        assertThatThrownBy(() -> Expiration.of(now, value))
                .isInstanceOf(ExpirationBadReqeustException.class);
    }

    @Test
    void 만료시간이_현재시간의_과거이면_예외를_던진다() {
        // given
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime value = now.minusSeconds(1);

        // when & then
        assertThatThrownBy(() -> Expiration.of(now, value))
                .isInstanceOf(ExpirationBadReqeustException.class);
    }

    @Test
    void 만료시간이_현재시간보다_미래라면_Expiration객체를_생성한다() {
        // given
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime value = now.plusSeconds(1);

        // when & then
        assertThatCode(() -> Expiration.of(now, value))
                .doesNotThrowAnyException();
    }
}