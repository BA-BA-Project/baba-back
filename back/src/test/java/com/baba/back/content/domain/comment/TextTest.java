package com.baba.back.content.domain.comment;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.content.exception.TextLenthBadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class TextTest {

    @ParameterizedTest
    @NullAndEmptySource
    void 댓글의_길이가_0일때_예외를_던진다(String value) {
        assertThatThrownBy(() -> new Text(value))
                .isInstanceOf(TextLenthBadRequestException.class);
    }

    @Test
    void 댓글의_길이가_250자보다_크면_예외를_던진다() {
        assertThatThrownBy(() -> new Text("a".repeat(251)))
                .isInstanceOf(TextLenthBadRequestException.class);
    }

    @Test
    void 정상적인_댓글을_생성한다() {
        // given
        final String value = "정상적인 댓글";

        // when & then
        assertThatCode(() -> new Text(value))
                .doesNotThrowAnyException();
    }
}
