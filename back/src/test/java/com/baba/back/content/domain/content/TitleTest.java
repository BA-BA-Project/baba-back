package com.baba.back.content.domain.content;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.baba.back.content.exception.TitleLengthBadRequestException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class TitleTest {

    public static Stream<Arguments> invalidTitle() {
        return Stream.of(
                Arguments.of("a".repeat(11)),
                Arguments.of("박".repeat(11))
        );
    }

    public static Stream<Arguments> validTitle() {
        return Stream.of(
                Arguments.of("a"),
                Arguments.of("박".repeat(10))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTitle")
    @NullAndEmptySource
    void 유효하지_않은_제목이면_예외를_던진다(String title) {
        assertThatThrownBy(() -> new Title(title))
                .isInstanceOf(TitleLengthBadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("validTitle")
    void 유효한_제목이어야_한다(String title) {
        assertThatCode(() -> new Title(title))
                .doesNotThrowAnyException();
    }

}