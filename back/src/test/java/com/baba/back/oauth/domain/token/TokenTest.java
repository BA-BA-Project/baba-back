package com.baba.back.oauth.domain.token;

import static com.baba.back.fixture.DomainFixture.멤버1;

import com.baba.back.oauth.exception.TokenBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenTest {
    @Test
    void token은_null일수_없다() {
        // given
        String invalidToken = null;

        // when & then
        Assertions.assertThatThrownBy(() -> Token.builder()
                        .id(멤버1.getId())
                        .token(invalidToken)
                        .build())
                .isInstanceOf(TokenBadRequestException.class);
    }
}
