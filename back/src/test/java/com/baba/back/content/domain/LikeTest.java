package com.baba.back.content.domain;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LikeTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Test
    void 생성시간과_업데이트시간을_확인한다() {
        // given
        final LocalDateTime now = LocalDateTime.now();
        final LocalDate today = now.toLocalDate();
        final Member savedMember = memberRepository.save(멤버1);
        final Baby savedBaby = babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(
                new Content("제목", today, today, "CARD_BASIC_1", savedBaby, savedMember));

        // when
        final Like like = likeRepository.save(new Like(savedMember, savedContent));

        // then
        Assertions.assertAll(
                () -> assertThat(like.getCreatedAt()).isAfter(now),
                () -> assertThat(like.getUpdatedAt()).isAfter(now)
        );
    }

    @Test
    void deleted가_바뀌는지_확인한다() {
        // given
        final Like like = Like.builder()
                .member(멤버1)
                .content(컨텐츠)
                .build();

        // when
        like.updateDeleted();

        // then
        assertThat(like.isDeleted()).isTrue();
    }
}
