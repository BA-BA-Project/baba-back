package com.baba.back.oauth.repository;

import static com.baba.back.fixture.DomainFixture.토큰;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    void 멤버와_토큰값쌍이_DB에_존재하면_true를_반환한다() {
        // given
        tokenRepository.save(토큰);

        // when
        final boolean exists = tokenRepository.existsByIdAndToken(토큰.getId(), 토큰.getToken());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 인자로_받은_멤버가_없으면_false를_반환한다() {
        // when
        final boolean exists = tokenRepository.existsByIdAndToken(토큰.getId(), 토큰.getToken());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 멤버와_토큰값쌍이_DB에_존재하지_않으면_false를_반환한다() {
        // given
        final String token = "token";
        tokenRepository.save(토큰);

        // when
        final boolean exists = tokenRepository.existsByIdAndToken(토큰.getId(), token);

        // then
        assertThat(exists).isFalse();
    }
}