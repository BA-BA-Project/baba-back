package com.baba.back.oauth.domain.member;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.exception.IconColorBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ColorTest {

    @Test
    void 아이콘_색을_선택한다() {
        // given
        Picker<Color> picker = colors -> Color.COLOR_1;

        // when
        final Color color = Color.from(picker);

        // then
        Assertions.assertThat(color).isEqualTo(Color.COLOR_1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "aaaaaa"})
    @NullAndEmptySource
    void 유효하지_않은_아이콘_색을_선택하면_예외를_던진다(String color) {
        assertThatThrownBy(() -> Color.from(color))
                .isInstanceOf(IconColorBadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"FFAEBA", "FF8698", "FFE3C8"})
    void 유효한_아이콘_색을_선택한다(String color) {
        assertThatCode(() -> Color.from(color))
                .doesNotThrowAnyException();
    }
}
