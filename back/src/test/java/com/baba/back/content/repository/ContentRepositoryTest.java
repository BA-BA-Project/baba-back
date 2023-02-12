package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.content.Content;
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
    void 날짜와_아기로_컨텐츠를_조회할_수_있다() {
        // given
        memberRepository.save(멤버1);
        final Baby baby = babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(컨텐츠);

        // when
        final Content foundContent = contentRepository.findByContentDateAndBaby(savedContent.getContentDate(), baby)
                .orElseThrow();

        // then
        Assertions.assertThat(foundContent).isEqualTo(savedContent);
    }
}