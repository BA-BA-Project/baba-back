package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ContentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Test
    void 해당_날짜에_이미_컨텐츠를_추가했다() {
        // given
        memberRepository.save(멤버1);
        final Baby baby = babyRepository.save(아기1);
        contentRepository.save(컨텐츠);

        // when
        final boolean exists = contentRepository.existsByContentDateAndBaby(컨텐츠.getContentDate(), baby);

        // then
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void 해당_날짜에_아직_컨텐츠가_없다() {
        // given
        memberRepository.save(멤버1);
        final Baby baby = babyRepository.save(아기1);

        // when
        final boolean exists = contentRepository.existsByContentDateAndBaby(컨텐츠.getContentDate(), baby);

        // then
        Assertions.assertThat(exists).isFalse();
    }
}