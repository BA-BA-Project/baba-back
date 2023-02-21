package com.baba.back.oauth.domain.token;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.TokenInfoBadRequestException;
import org.junit.jupiter.api.Test;

class TokenInfoTest {
    @Test
    void tokenInfo는_null일_수_없다() {
        assertThatThrownBy(() -> new TokenInfo(null))
                .isInstanceOf(TokenInfoBadRequestException.class);
    }
}