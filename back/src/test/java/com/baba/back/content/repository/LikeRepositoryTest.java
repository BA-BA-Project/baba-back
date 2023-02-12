package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LikeRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Test
    void 멤버와_컨텐츠로_좋아요를_조회한다() {
        // given
        final LocalDate now = LocalDate.now();
        final Member savedMember = memberRepository.save(멤버1);
        final Baby savedBaby = babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(
                new Content("제목", now, now, "card_basic_1", savedBaby, savedMember));
        final Like savedLike = likeRepository.save(new Like(savedMember, savedContent));

        // when
        final Like findLike = likeRepository.findByMemberAndContent(savedMember, savedContent).orElseThrow();

        // then
        assertThat(savedLike).isEqualTo(findLike);

    }
}