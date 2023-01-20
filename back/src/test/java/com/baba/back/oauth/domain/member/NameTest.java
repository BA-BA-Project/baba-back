package com.baba.back.oauth.domain.member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.NameLengthBadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

    @ParameterizedTest
    @ValueSource(strings = {"박재희박재희박", "abcdefg"})
    @NullAndEmptySource
    void 이름이_1자미만_6자초과이면_예외를_던진다(String name) {
        assertThatThrownBy(() -> new Name(name))
                .isInstanceOf(NameLengthBadRequestException.class);
    }
}
