package com.baba.back.content.domain.content;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.content.exception.ContentDateBadRequestException;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ContentDateTest {
    private final static LocalDate now = LocalDate.now();

    public static Stream<Arguments> nullLocalDate() {
        return Stream.of(
                Arguments.of(null, now, now),
                Arguments.of(now, null, now),
                Arguments.of(now, now, null)
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
    @MethodSource("nullLocalDate")
    void null이_존재하면_contentDate를_생성할_수_없다(LocalDate contentDate, LocalDate now, LocalDate baseDate) {
        assertThatThrownBy(() -> ContentDate.of(contentDate, now, baseDate))
                .isInstanceOf(ContentDateBadRequestException.class);
    }

    @Test
    void contentDate는_now보다_미래일수_없다() {
        // given
        final LocalDate contentDate = now.plusDays(1);

        // when & then
        assertThatThrownBy(() -> ContentDate.of(contentDate, now, now))
                .isInstanceOf(ContentDateBadRequestException.class);
    }

    @Test
    void contentDate는_birthday의_2년전까지_유효하다() {
        // given
        final LocalDate contentDate = now.minusYears(2).minusDays(1);

        // when & then
        assertThatThrownBy(() -> ContentDate.of(contentDate, now, now))
                .isInstanceOf(ContentDateBadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("validContentDateAndNowAndBirthday")
    void contentDate는_now와_같거나_과거이고_birthday의_2년이내이다(LocalDate contentDate, LocalDate now, LocalDate baseDate) {
        assertThatCode(() -> ContentDate.of(contentDate, now, baseDate))
                .doesNotThrowAnyException();
    }
}