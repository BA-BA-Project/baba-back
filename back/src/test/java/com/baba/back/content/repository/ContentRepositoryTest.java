package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠10;
import static com.baba.back.fixture.DomainFixture.컨텐츠11;
import static com.baba.back.fixture.DomainFixture.컨텐츠12;
import static com.baba.back.fixture.DomainFixture.컨텐츠13;
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
        contentRepository.save(컨텐츠10);

        // when
        final boolean exists = contentRepository.existsByBabyAndContentDateValue(baby, 컨텐츠10.getContentDate());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 해당_날짜에_아직_컨텐츠가_없는지_확인한다() {
        // given
        memberRepository.save(멤버1);
        final Baby baby = babyRepository.save(아기1);

        // when
        final boolean exists = contentRepository.existsByBabyAndContentDateValue(baby, 컨텐츠10.getContentDate());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 조회하려는_년_월에_생성된_content들을_조회한다() {
        // given
        final Baby baby = babyRepository.save(아기1);
        memberRepository.save(멤버1);
        contentRepository.save(컨텐츠10);
        contentRepository.save(컨텐츠11);
        contentRepository.save(컨텐츠12);
        contentRepository.save(컨텐츠13);

        final LocalDate now = 컨텐츠10.getContentDate();

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
        contentRepository.save(컨텐츠10);
        contentRepository.save(컨텐츠11);
        contentRepository.save(컨텐츠12);
        contentRepository.save(컨텐츠13);

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
