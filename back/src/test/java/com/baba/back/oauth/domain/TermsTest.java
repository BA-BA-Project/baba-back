package com.baba.back.oauth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.TermsBadRequestException;
import org.junit.jupiter.api.Test;

class TermsTest {

    @Test
    void 약관의_개수가_다르면_false를_반환한다() {
        // given & when
        final boolean sameSize = Terms.isSameSize(Terms.values().length - 1);

        // then
        assertThat(sameSize).isFalse();
    }

    @Test
    void 약관의_개수가_같으면_true를_반환한다() {
        // given & when
        final boolean sameSize = Terms.isSameSize(Terms.values().length);

        // then
        assertThat(sameSize).isTrue();
    }

    @Test
    void N번째_약관과_이름이_다르다면_예외를_던진다() {
        assertThatThrownBy(() -> Terms.isRequiredTermsBy(0, Terms.TERMS_2.getName()))
                .isInstanceOf(TermsBadRequestException.class);
    }

    @Test
    void N번째_약관과_이름이_같다면_약관의_필수여부를_반환한다() {
        // given
        final boolean requiredTerms = Terms.isRequiredTermsBy(0, Terms.TERMS_1.getName());

        assertThat(requiredTerms).isEqualTo(Terms.TERMS_1.isRequired());
    }


}