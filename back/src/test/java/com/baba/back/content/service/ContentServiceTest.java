package com.baba.back.content.service;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.관계3;
import static com.baba.back.fixture.DomainFixture.관계5;
import static com.baba.back.fixture.DomainFixture.관계6;
import static com.baba.back.fixture.DomainFixture.댓글1;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.멤버2;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.좋아요;
import static com.baba.back.fixture.DomainFixture.컨텐츠1;
import static com.baba.back.fixture.DomainFixture.컨텐츠2;
import static com.baba.back.fixture.RequestFixture.댓글_생성_요청_데이터;
import static com.baba.back.fixture.RequestFixture.컨텐츠_생성_요청_데이터;
import static com.baba.back.fixture.RequestFixture.태그_댓글_생성_요청_데이터;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.common.FileHandler;
import com.baba.back.content.domain.comment.Comment;
import com.baba.back.content.domain.comment.Tag;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.domain.content.ImageFile;
import com.baba.back.content.dto.ContentResponse;
import com.baba.back.content.dto.ContentsResponse;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.content.exception.ContentAuthorizationException;
import com.baba.back.content.exception.ContentBadRequestException;
import com.baba.back.content.exception.ContentNotFountException;
import com.baba.back.content.exception.TagBadRequestException;
import com.baba.back.content.repository.CommentRepository;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.content.repository.TagRepository;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BabyRepository babyRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private FileHandler fileHandler;

    @Mock
    private Clock clock;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ContentService contentService;


    @Test
    void 콘텐츠_생성_시_멤버가_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청_데이터, 멤버1.getId(), 아기1.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 콘텐츠_생성_시_아기가_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청_데이터, 멤버1.getId(), 아기1.getId()))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 콘텐츠_생성_시_관계가_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청_데이터, 멤버1.getId(), 아기1.getId()))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 콘텐츠_생성_시_아기의_컨텐츠를_생성할_권한이_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계3));

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청_데이터, 멤버1.getId(), 아기1.getId()))
                .isInstanceOf(ContentAuthorizationException.class);
    }

    @Test
    void 콘텐츠_생성_시_해당_날짜에_이미_컨텐츠가_존재하면_예외를_던진다() {
        // given
        final Clock now = Clock.systemDefaultZone();
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(contentRepository.existsByBabyAndContentDateValue(아기1, LocalDate.now(now))).willReturn(true);

        // when & then
        Assertions.assertThatThrownBy(() -> contentService.createContent(컨텐츠_생성_요청_데이터, 멤버1.getId(), 아기1.getId()))
                .isInstanceOf(ContentBadRequestException.class);
    }

    @Test
    void 새로운_컨텐츠를_만든다() {
        // given
        final Clock now = Clock.systemDefaultZone();

        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(contentRepository.existsByBabyAndContentDateValue(아기1, LocalDate.now(now))).willReturn(false);
        given(fileHandler.upload(any(ImageFile.class))).willReturn("VALID_IMAGE_SOURCE");
        given(contentRepository.save(any(Content.class))).willReturn(컨텐츠1);

        // when
        final Long contentId = contentService.createContent(컨텐츠_생성_요청_데이터, 멤버1.getId(), 아기1.getId());

        // then
        assertThat(contentId).isEqualTo(컨텐츠1.getId());
    }

    @Test
    void 좋아요_추가_시_멤버가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 좋아요_추가_시_아기가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId()))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 좋아요_추가_시_관계가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId()))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 좋아요_추가_시_컨텐츠가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId()))
                .isInstanceOf(ContentNotFountException.class);
    }

    @Test
    void 좋아요가_추가된다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(likeRepository.findByContentAndMember(컨텐츠1, 멤버1)).willReturn(Optional.empty());

        // when
        final LikeContentResponse likeContentResponse = contentService.likeContent(멤버1.getId(), 아기1.getId(),
                컨텐츠1.getId());

        // then
        then(likeRepository).should(times(1)).save(any());

        assertThat(likeContentResponse.isLiked()).isTrue();
    }

    @Test
    void 이미_좋아요가_있을때_좋아요를_누르면_기존의_좋아요가_취소된다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(likeRepository.findByContentAndMember(컨텐츠1, 멤버1)).willReturn(Optional.of(좋아요));

        // when
        final LikeContentResponse likeContentResponse = contentService.likeContent(멤버1.getId(), 아기1.getId(),
                컨텐츠1.getId());

        // then
        then(likeRepository).should(times(1)).save(any());

        assertThat(likeContentResponse.isLiked()).isFalse();
    }

    @Test
    void 좋아요를_취소하고_다시_좋아요를_누르면_좋아요가_추가된다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(likeRepository.findByContentAndMember(컨텐츠1, 멤버1)).willReturn(Optional.of(좋아요));

        // when
        contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId());
        final LikeContentResponse likeContentResponse = contentService.likeContent(멤버1.getId(), 아기1.getId(),
                컨텐츠1.getId());

        // then
        then(likeRepository).should(times(2)).save(any());

        assertThat(likeContentResponse.isLiked()).isTrue();
    }

    @Test
    void 원하는_년_월의_성장_앨범을_조회한다() {
        // given
        final int year = 2023;
        final int month = 1;
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findByBabyYearAndMonth(아기1, year, month)).willReturn(List.of(컨텐츠1, 컨텐츠2));
        given(likeRepository.existsByContentAndMember(컨텐츠1, 멤버1)).willReturn(true);
        given(likeRepository.existsByContentAndMember(컨텐츠2, 멤버1)).willReturn(false);

        // when
        final ContentsResponse response = contentService.getContents(멤버1.getId(), 아기1.getId(), year, month);

        // then
        assertAll(
                () -> assertThat(response.album()).hasSize(2),
                () -> assertThat(response.album()).containsExactly(
                        new ContentResponse(
                                컨텐츠2.getId(),
                                컨텐츠2.getOwnerName(),
                                컨텐츠2.getRelationName(),
                                컨텐츠2.getContentDate(),
                                컨텐츠2.getTitle(),
                                false,
                                컨텐츠2.getImageSource(),
                                컨텐츠2.getCardStyle()
                        ),
                        new ContentResponse(
                                컨텐츠1.getId(),
                                컨텐츠1.getOwnerName(),
                                컨텐츠1.getRelationName(),
                                컨텐츠1.getContentDate(),
                                컨텐츠1.getTitle(),
                                true,
                                컨텐츠1.getImageSource(),
                                컨텐츠1.getCardStyle()
                        )
                )
        );
    }

    @Test
    void 없는_멤버가_성장_앨범을_조회_시_예외를_던진다() {
        // given
        final int year = 2023;
        final int month = 1;
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.getContents(멤버1.getId(), 아기1.getId(), year, month))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 없는_아기의_성장_앨범을_조회_시_예외를_던진다() {
        // given
        final int year = 2023;
        final int month = 1;
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.getContents(멤버1.getId(), 아기1.getId(), year, month))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 관계가_없는_아기의_성장_앨범을_조회_시_예외를_던진다() {
        // given
        final int year = 2023;
        final int month = 1;
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.getContents(멤버1.getId(), 아기1.getId(), year, month))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_존재하지_않는_멤버가_추가하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_존재하지_않는_아기의_콘텐츠에_추가하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_아기와_관계가_없는_멤버가_추가하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_존재하지_않는_콘텐츠에_추가하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isInstanceOf(ContentNotFountException.class);
    }

    @Test
    void 댓글_추가_시_태그가_존재하지_않으면_생성된_comment_id_를_반환한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글1);

        // when & then
        assertThat(contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 댓글_생성_요청_데이터))
                .isEqualTo(댓글1.getId());
    }

    @Test
    void 댓글_추가_시_태그한_멤버가_존재하지않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글1);
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_태그한_멤버가_아기와_관계가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글1);
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_태그한_멤버를_태그할_수_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글1);
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.of(관계5));

        // when & then
        assertThatThrownBy(() -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isInstanceOf(TagBadRequestException.class);
    }

    @Test
    void 댓글_추가_시_태그된_멤버와_함께_댓글을_추가한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계1));
        given(contentRepository.findById(컨텐츠1.getId())).willReturn(Optional.of(컨텐츠1));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글1);
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.of(관계6));
        given(tagRepository.save(any(Tag.class))).willReturn(any(Tag.class));

        // when & then
        assertThat(contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠1.getId(), 태그_댓글_생성_요청_데이터))
                .isEqualTo(댓글1.getId());
    }
}
