package com.baba.back.oauth.domain.member;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.domain.ColorPicker;
import com.baba.back.oauth.exception.IconColorBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IconColorTest {

    @Test
    void 아이콘_색을_선택한다() {
        // given
        ColorPicker colorPicker = colors -> IconColor.COLOR_1;

        // when
        final IconColor iconColor = IconColor.from(colorPicker);

        // then
        Assertions.assertThat(iconColor).isEqualTo(IconColor.COLOR_1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "aaaaaa"})
    @NullAndEmptySource
    void 유효하지_않은_아이콘_색을_선택하면_예외를_던진다(String color) {
        assertThatThrownBy(() -> IconColor.from(color))
                .isInstanceOf(IconColorBadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"FFAEBA", "FF8698", "FFE3C8"})
    void 유효한_아이콘_색을_선택한다(String color) {
        assertThatCode(() -> IconColor.from(color))
                .doesNotThrowAnyException();
    }
}
