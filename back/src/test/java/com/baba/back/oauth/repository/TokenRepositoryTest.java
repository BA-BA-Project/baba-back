package com.baba.back.oauth.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.토큰;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TokenRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    void 멤버와_토큰값쌍이_DB에_존재하면_true를_반환한다() {
        // given
        final Member member = memberRepository.save(멤버1);
        final String value = "token";

        tokenRepository.save(Token.builder()
                .member(member)
                .value(value)
                .build());

        // when
        final boolean exists = tokenRepository.existsByMemberAndValue(member, value);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 인자로_받은_멤버가_없으면_false를_반환한다() {
        // when
        final boolean exists = tokenRepository.existsByMemberAndValue(토큰.getMember(), 토큰.getValue());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 멤버와_토큰값쌍이_DB에_존재하지_않으면_false를_반환한다() {
        // given
        final Member member = memberRepository.save(멤버1);
        final String value = "validToken";
        final String invalidValue = "invalidToken";

        tokenRepository.save(Token.builder()
                .member(member)
                .value(value)
                .build());

        // when
        final boolean exists = tokenRepository.existsByMemberAndValue(member, invalidValue);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 멤버에_대한_refreshToken이_저장되어있는지_확인한다() {
        // given
        final String value = "validToken";

        final Member member = memberRepository.save(멤버1);
        final Token token = tokenRepository.save(
                Token.builder()
                        .member(member)
                        .value(value)
                        .build()
        );

        // when
        final Token savedToken = tokenRepository.findByMember(member).orElseThrow();

        // then
        Assertions.assertThat(token).isEqualTo(savedToken);
    }
}
