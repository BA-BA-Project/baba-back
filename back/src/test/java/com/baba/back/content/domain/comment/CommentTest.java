package com.baba.back.content.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void 댓글을_조회한다() {
        // given
        final String text = "안녕하세요";
        final Comment comment = Comment.builder()
                .text(text)
                .build();

        // when
        final String result = comment.getText();

        // then
        assertThat(result).isEqualTo(text);
    }
}
