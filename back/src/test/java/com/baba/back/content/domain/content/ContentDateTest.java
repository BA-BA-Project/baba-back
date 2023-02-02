package com.baba.back.content.domain.content;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.content.exception.ContentDateBadRequestException;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ContentDateTest {

    public static Stream<Arguments> invalidContentDateAndNow() {
        final LocalDate now = LocalDate.now();
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(now.plusDays(1), now, now)
        );
    }

    public static Stream<Arguments> invalidContentDateAndBirthday() {
        final LocalDate now = LocalDate.now();
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(now.minusYears(2).minusDays(1), now, now)
        );
    }

    public static Stream<Arguments> validContentDateAndNowAndBirthday() {
        final LocalDate now = LocalDate.now();
        return Stream.of(
                Arguments.of(now, now, now),
                Arguments.of(now.minusYears(2), now, now)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidContentDateAndNow")
    void contentDate는_now보다_미래일수_없다(LocalDate contentDate, LocalDate now, LocalDate baseDate) {
        assertThatThrownBy(() -> ContentDate.of(contentDate, now, baseDate))
                .isInstanceOf(ContentDateBadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidContentDateAndBirthday")
    void contentDate는_birthday의_2년전까지_유효하다(LocalDate contentDate, LocalDate now, LocalDate baseDate) {
        assertThatThrownBy(() -> ContentDate.of(contentDate, now, baseDate))
                .isInstanceOf(ContentDateBadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("validContentDateAndNowAndBirthday")
    void contentDate는_now보다_과거이고_birthday의_2년이내이다(LocalDate contentDate, LocalDate now, LocalDate baseDate) {
        assertThatCode(() -> ContentDate.of(contentDate, now, baseDate))
                .doesNotThrowAnyException();
    }
}