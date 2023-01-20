package com.baba.back.oauth.domain.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.IconNameBadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IconNameTest {

    @ParameterizedTest
    @ValueSource(strings = {" ", "123", "aaaaaa"})
    @NullAndEmptySource
    void 존재하지않는_아이콘을_선택하면_예외를_던진다(String name) {
        assertThatThrownBy(() -> new IconName(name))
                .isInstanceOf(IconNameBadRequestException.class);
    }
}