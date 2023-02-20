package com.baba.back.oauth.domain.member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IconNameTest {

    @ParameterizedTest
    @ValueSource(strings = {" ", "123", "aaaaaa"})
    @EmptySource
    void 존재하지_않는_아이콘을_선택하면_예외를_던진다(String name) {
        assertThatThrownBy(() -> IconName.valueOf(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 유효하지_않은_아이콘을_선택하면_예외를_던진다() {
        assertThatThrownBy(() -> IconName.valueOf(null))
                .isInstanceOf(NullPointerException.class);
    }

}
