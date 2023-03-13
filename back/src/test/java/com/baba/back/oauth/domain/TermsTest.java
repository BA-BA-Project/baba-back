package com.baba.back.oauth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.TermsBadRequestException;
import org.junit.jupiter.api.Test;

class TermsTest {

    public static final int REQUIRED_TERMS_LENGTH = 2;
    public static final int ALL_TERMS_LENGTH = Terms.values().length;

    @Test
    void 모든_약관의_개수가_다르면_false를_반환한다() {
        // given & when
        final boolean sameSize = Terms.isSizeEqualToAllTerms(REQUIRED_TERMS_LENGTH);

        // then
        assertThat(sameSize).isFalse();
    }

    @Test
    void 모든_약관의_개수가_같으면_true를_반환한다() {
        // given & when
        final boolean sameSize = Terms.isSizeEqualToAllTerms(ALL_TERMS_LENGTH);

        // then
        assertThat(sameSize).isTrue();
    }

    @Test
    void 요청받은_이름의_약관이_존재하지_않으면_예외를_던진다() {
        // given
        final String invalidTermsName = "존재하지 않은 약관";

        // when & then
        assertThatThrownBy(() -> Terms.findByName(invalidTermsName))
                .isInstanceOf(TermsBadRequestException.class);
    }

    @Test
    void 요청받은_이름의_약관이_존재하면_약관을_반환한다() {
        assertThat(Terms.findByName(Terms.TERMS_2.getName())).isEqualTo(Terms.TERMS_2);
    }


}