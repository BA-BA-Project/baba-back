package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠10;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.comment.Comment;
import com.baba.back.content.domain.comment.Tag;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void 댓글에_존재하는_태그를_조회한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(컨텐츠10);
        final Comment savedComment = commentRepository.save(
                Comment.builder().owner(savedMember).content(savedContent).text("z").build());
        final Tag savedTag = tagRepository.save(Tag.builder().tagMember(savedMember).comment(savedComment).build());

        // when
        final Tag result = tagRepository.findByComment(savedComment).orElseThrow();

        // then
        assertThat(result).isEqualTo(savedTag);
    }
}
