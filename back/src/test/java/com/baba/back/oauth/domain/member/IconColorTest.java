package com.baba.back.oauth.domain.member;

import com.baba.back.oauth.domain.ColorPicker;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class IconColorTest {

    @Test
    void 아이콘_색을_선택한다() {
        // given
        final String color = "FFAEBA";
        ColorPicker<String> colorPicker = (List<String> colors) -> color;

        // when
        final IconColor iconColor = IconColor.from(colorPicker);

        // then
        Assertions.assertThat(iconColor.getIconColor()).isEqualTo(color);
    }
}
