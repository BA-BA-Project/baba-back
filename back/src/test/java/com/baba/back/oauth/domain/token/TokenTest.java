package com.baba.back.oauth.domain.token;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.토큰;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void 같은_토큰인지_확인한다() {
        assertThat(토큰.hasEqualToken(토큰.getValue())).isTrue();
    }

    @Test
    void 다른_토큰인지_확인한다() {
        // given
        final String token = "token";

        // when & then
        assertThat(토큰.hasEqualToken(token)).isFalse();
    }
}
