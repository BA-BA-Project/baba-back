package com.baba.back.oauth.domain.member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.IconColorBadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IconColorTest {

    @ParameterizedTest
    @ValueSource(strings = {" ", "123", "aaaaaa"})
    @NullAndEmptySource
    void 존재하지않는_색을_선택하면_예외를_던진다(String color) {
        assertThatThrownBy(() -> new IconColor(color))
                .isInstanceOf(IconColorBadRequestException.class);
    }
}
