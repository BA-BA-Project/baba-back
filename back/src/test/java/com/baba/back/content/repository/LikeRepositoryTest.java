package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠10;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import java.util.List;
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
        final Member savedMember = memberRepository.save(멤버1);
        babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(컨텐츠10);
        final Like savedLike = likeRepository.save(new Like(savedMember, savedContent));

        // when
        final Like findLike = likeRepository.findByContentAndMember(savedContent, savedMember).orElseThrow();

        // then
        assertThat(savedLike).isEqualTo(findLike);
    }

    @Test
    void 멤버가_컨텐츠에_좋아요를_눌렀는지_확인한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(컨텐츠10);
        likeRepository.save(new Like(savedMember, savedContent));

        // when
        final boolean result = likeRepository.findByContentAndMember(savedContent, savedMember)
                .stream()
                .anyMatch(like -> !like.isDeleted());
        // then
        assertThat(result).isTrue();
    }

    @Test
    void 멤버가_컨텐츠에_좋아요를_안_눌렀는지_확인한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(컨텐츠10);

        // when
        final boolean result = likeRepository.findByContentAndMember(savedContent, savedMember)
                .stream()
                .anyMatch(like -> !like.isDeleted());

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 성장_앨범에_존재하는_좋아요를_모두_조회한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(컨텐츠10);
        final Like savedLike = likeRepository.save(Like.builder().member(savedMember).content(savedContent).build());

        // when
        final List<Like> result = likeRepository.findAllByContent(savedContent);

        // then
        assertThat(result).isEqualTo(List.of(savedLike));
    }
}
