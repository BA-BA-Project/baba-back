package com.baba.back.common.domain;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.컨텐츠;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.content.domain.Like;
import com.baba.back.content.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BaseEntityTest {

    @Autowired
    private LikeRepository likeRepository;

    @Test
    void 생성시간과_업데이트시간을_확인한다() {
        // given
        final Like like = Like.builder()
                .member(멤버1)
                .content(컨텐츠)
                .build();

        likeRepository.save(like);

        // when & then
        assertThat(like.getCreatedDate()).isNotNull();
        assertThat(like.getUpdatedDate()).isNotNull();
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