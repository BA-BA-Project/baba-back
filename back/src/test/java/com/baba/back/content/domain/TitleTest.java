package com.baba.back.content.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.baba.back.content.domain.content.Title;
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

    @ParameterizedTest
    @MethodSource("invalidTitle")
    @NullAndEmptySource
    void 제목이_10자_초과면_예외를_던진다(String title) {
        assertThatThrownBy(() -> new Title(title))
                .isInstanceOf(TitleLengthBadRequestException.class);
    }

}