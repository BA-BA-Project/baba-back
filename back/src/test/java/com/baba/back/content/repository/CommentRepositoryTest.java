package com.baba.back.content.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠10;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.comment.Comment;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CommentRepository commentRepository;


    @Test
    void 게시물에_존재하는_댓글을_조회한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        babyRepository.save(아기1);
        final Content savedContent = contentRepository.save(컨텐츠10);
        final Comment savedComment1 = commentRepository.save(
                Comment.builder().owner(savedMember).content(savedContent).text("1").build());
        final Comment savedComment2 = commentRepository.save(
                Comment.builder().owner(savedMember).content(savedContent).text("2").build());

        // when
        final List<Comment> results = commentRepository.findAllByContent(savedContent);
        // then
        assertThat(results).containsExactly(savedComment1, savedComment2);
    }
}
