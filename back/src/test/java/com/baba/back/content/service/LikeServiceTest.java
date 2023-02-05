package com.baba.back.content.service;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.dto.AddLikeResponse;
import com.baba.back.content.exception.ContentNotFountException;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BabyRepository babyRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void 멤버가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> likeService.addLike(멤버1.getId(), 아기1.getId(), 컨텐츠.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 아기가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> likeService.addLike(멤버1.getId(), 아기1.getId(), 컨텐츠.getId()))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 관계가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(any())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> likeService.addLike(멤버1.getId(), 아기1.getId(), 컨텐츠.getId()))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 컨텐츠가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(any())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(any(), any())).willReturn(Optional.of(관계1));
        given(contentRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> likeService.addLike(멤버1.getId(), 아기1.getId(), 컨텐츠.getId()))
                .isInstanceOf(ContentNotFountException.class);
    }

    @Test
    void 좋아요가_추가된다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(any())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(any(), any())).willReturn(Optional.of(관계1));
        given(contentRepository.findById(any())).willReturn(Optional.of(컨텐츠));

        // when
        final AddLikeResponse addLikeResponse = likeService.addLike(멤버1.getId(), 아기1.getId(), 컨텐츠.getId());

        // then
        then(likeRepository).should(times(1)).save(any());

        assertThat(addLikeResponse.liked()).isTrue();
    }
}