package com.baba.back.content.service;

import static com.baba.back.fixture.RequestFixture.컨텐츠_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.Birthday;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.ImageSaver;
import com.baba.back.content.dto.CreateContentResponse;
import com.baba.back.content.exception.ContentAuthorizationException;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    public static final String MEMBER_ID = "1234";
    public static final String BABY_ID = "1234";

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BabyRepository babyRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private ImageSaver imageSaver;

    @InjectMocks
    private ContentService contentService;

    @Test
    void 멤버가_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청, MEMBER_ID, BABY_ID))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 아기가_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.of(new Member()));
        given(babyRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청, MEMBER_ID, BABY_ID))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 관계가_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.of(new Member()));
        given(babyRepository.findById(any())).willReturn(Optional.of(new Baby()));
        given(relationRepository.findByMemberAndBaby(any(), any())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청, MEMBER_ID, BABY_ID))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 아기의_컨텐츠를_생성할_권한이_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(any())).willReturn(Optional.of(new Member()));
        given(babyRepository.findById(any())).willReturn(Optional.of(new Baby()));
        given(relationRepository.findByMemberAndBaby(any(), any())).willReturn(Optional.of(new Relation()));

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청, MEMBER_ID, BABY_ID))
                .isInstanceOf(ContentAuthorizationException.class);
    }

    @Test
    void 새로운_컨텐츠를_만든다() {
        // given
        Baby baby = mock(Baby.class);
        Relation relation = mock(Relation.class);
        given(baby.getBirthday()).willReturn(Birthday.of(컨텐츠_생성_요청.getDate(), 컨텐츠_생성_요청.getDate()));
        given(relation.isFamily()).willReturn(true);

        given(memberRepository.findById(any())).willReturn(Optional.of(new Member()));
        given(babyRepository.findById(any())).willReturn(Optional.of(baby));
        given(relationRepository.findByMemberAndBaby(any(), any())).willReturn(Optional.of(relation));
        given(imageSaver.save(any())).willReturn("VALID_IMAGE_SOURCE");

        // when
        final CreateContentResponse response = contentService.createContent(컨텐츠_생성_요청, MEMBER_ID, BABY_ID);

        // then
        assertThat(response.isSuccess()).isTrue();
    }
}