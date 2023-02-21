package com.baba.back.oauth.domain.member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.IconNameBadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IconNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"123", "aaaaaa"})
    @NullAndEmptySource
    void 유효하지_않는_아이콘을_선택하면_예외를_던진다(String name) {
        assertThatThrownBy(() -> IconName.from(name))
                .isInstanceOf(IconNameBadRequestException.class);
    }
}
