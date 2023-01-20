package com.baba.back.oauth.domain.user;


import com.baba.back.oauth.exception.IntroductionLengthBadRequestException;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

class IntroductionTest {

    public static Stream<Arguments> invalidIntroduce() {
        return Stream.of(
                Arguments.of("a".repeat(101)),
                Arguments.of("박".repeat(101))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidIntroduce")
    @NullSource
    void 내_소개가_100자초과이면_예외를_던진다(String introduction) {
        Assertions.assertThatThrownBy(() -> new Introduction(introduction))
                .isInstanceOf(IntroductionLengthBadRequestException.class);
    }
}