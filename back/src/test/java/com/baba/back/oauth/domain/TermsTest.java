package com.baba.back.oauth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TermsTest {

    @Test
    void 모든_약관을_조회한다() {
        assertThat(Terms.get()).containsExactly(Terms.TERMS_1, Terms.TERMS_2);
    }
}