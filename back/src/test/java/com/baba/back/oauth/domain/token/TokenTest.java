package com.baba.back.oauth.domain.token;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.oauth.exception.TokenBadRequestException;
import org.junit.jupiter.api.Test;

class TokenTest {
    @Test
    void token값은_null일수_없다() {
        // given
        final String value = null;

        // when & then
        assertThatThrownBy(() -> Token.builder()
                        .member(멤버1)
                        .value(value)
                        .build())
                .isInstanceOf(TokenBadRequestException.class);
    }

    @Test
    void 토큰_값을_변경한다() {
        // give
        final String oldValue = "oldValue";
        final String newValue = "newValue";
        final Token token = Token.builder()
                .member(멤버1)
                .value(oldValue)
                .build();

        // when
        token.update(newValue);

        // then
        assertThat(token.getValue()).isEqualTo(newValue);

    }
}
