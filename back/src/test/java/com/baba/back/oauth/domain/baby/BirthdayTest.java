package com.baba.back.oauth.domain.baby;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.baba.back.baby.domain.Birthday;
import com.baba.back.baby.exception.BirthdayBadRequestException;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BirthdayTest {

    public static Stream<Arguments> invalidBirthdayAndNow() {
        final LocalDate now = LocalDate.now();
        return Stream.of(
                Arguments.of(now.minusYears(2).minusDays(1), now),
                Arguments.of(now.plusYears(2).plusDays(1), now)
        );
    }

    public static Stream<Arguments> validBirthdayAndNow() {
        final LocalDate now = LocalDate.now();
        return Stream.of(
                Arguments.of(now.minusYears(2), now),
                Arguments.of(now.plusYears(2), now),
                Arguments.of(now, now)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBirthdayAndNow")
    void birthday와_현재시각이_2년보다_많이_차이나면_예외를_던진다(LocalDate birthday, LocalDate now) {
        Assertions.assertThatThrownBy(() -> Birthday.of(birthday, now))
                .isInstanceOf(BirthdayBadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("validBirthdayAndNow")
    void birthday와_현재시각이_2년보다_적어야한다(LocalDate birthday, LocalDate now) {
        assertThatCode(() -> Birthday.of(birthday, now))
                .doesNotThrowAnyException();
    }
}
