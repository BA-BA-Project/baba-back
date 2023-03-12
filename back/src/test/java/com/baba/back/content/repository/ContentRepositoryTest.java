package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠1;
import static com.baba.back.fixture.DomainFixture.컨텐츠2;
import static com.baba.back.fixture.DomainFixture.컨텐츠3;
import static com.baba.back.fixture.DomainFixture.컨텐츠4;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
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
    void 해당_날짜에_이미_컨텐츠를_추가했는지_확인한다() {
        // given
        memberRepository.save(멤버1);
        final Baby baby = babyRepository.save(아기1);
        contentRepository.save(컨텐츠1);

        // when
        final boolean exists = contentRepository.existsByContentDateAndBaby(컨텐츠1.getContentDate(), baby);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 해당_날짜에_아직_컨텐츠가_없는지_확인한다() {
        // given
        memberRepository.save(멤버1);
        final Baby baby = babyRepository.save(아기1);

        // when
        final boolean exists = contentRepository.existsByContentDateAndBaby(컨텐츠1.getContentDate(), baby);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 조회하려는_년_월에_생성된_content들을_조회한다() {
        // given
        final Baby baby = babyRepository.save(아기1);
        memberRepository.save(멤버1);
        contentRepository.save(컨텐츠1);
        contentRepository.save(컨텐츠2);
        contentRepository.save(컨텐츠3);
        contentRepository.save(컨텐츠4);

        final LocalDate now = 컨텐츠1.getContentDate().getValue();

        // when
        final List<Content> contents = contentRepository.findByBabyYearAndMonth(
                baby, now.getYear(),
                now.getMonthValue()
        );

        // then
        assertThat(contents).hasSize(3);
    }

    @Test
    void 조회하려는_년_월에_생성된_content들이_없다면_빈_리스트를_반환한다() {
        // given
        final Baby baby = babyRepository.save(아기1);
        memberRepository.save(멤버1);
        contentRepository.save(컨텐츠1);
        contentRepository.save(컨텐츠2);
        contentRepository.save(컨텐츠3);
        contentRepository.save(컨텐츠4);

        final LocalDate now = LocalDate.now().minusMonths(100);

        // when
        final List<Content> contents = contentRepository.findByBabyYearAndMonth(
                baby, now.getYear(),
                now.getMonthValue()
        );

        // then
        assertThat(contents).isEmpty();
    }
}
