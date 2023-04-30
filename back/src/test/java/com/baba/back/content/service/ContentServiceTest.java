package com.baba.back.content.service;

import static com.baba.back.fixture.DomainFixture.관계10;
import static com.baba.back.fixture.DomainFixture.관계11;
import static com.baba.back.fixture.DomainFixture.관계20;
import static com.baba.back.fixture.DomainFixture.관계21;
import static com.baba.back.fixture.DomainFixture.관계22;
import static com.baba.back.fixture.DomainFixture.관계30;
import static com.baba.back.fixture.DomainFixture.댓글10;
import static com.baba.back.fixture.DomainFixture.댓글20;
import static com.baba.back.fixture.DomainFixture.댓글21;
import static com.baba.back.fixture.DomainFixture.댓글22;
import static com.baba.back.fixture.DomainFixture.댓글23;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.멤버2;
import static com.baba.back.fixture.DomainFixture.멤버3;
import static com.baba.back.fixture.DomainFixture.멤버4;
import static com.baba.back.fixture.DomainFixture.수정용_컨텐츠10;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.좋아요10;
import static com.baba.back.fixture.DomainFixture.좋아요11;
import static com.baba.back.fixture.DomainFixture.좋아요12;
import static com.baba.back.fixture.DomainFixture.좋아요13;
import static com.baba.back.fixture.DomainFixture.컨텐츠10;
import static com.baba.back.fixture.DomainFixture.컨텐츠11;
import static com.baba.back.fixture.DomainFixture.컨텐츠20;
import static com.baba.back.fixture.DomainFixture.태그10;
import static com.baba.back.fixture.DomainFixture.태그20;
import static com.baba.back.fixture.RequestFixture.댓글_생성_요청_데이터;
import static com.baba.back.fixture.RequestFixture.컨텐츠_사진_수정_요청_데이터;
import static com.baba.back.fixture.RequestFixture.컨텐츠_생성_요청_데이터;
import static com.baba.back.fixture.RequestFixture.콘텐츠_제목_카드스타일_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.태그_댓글_생성_요청_데이터1;
import static com.baba.back.fixture.RequestFixture.태그_댓글_생성_요청_데이터2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
import com.baba.back.content.dto.CommentResponse;
import com.baba.back.content.dto.CommentsResponse;
import com.baba.back.content.dto.ContentResponse;
import com.baba.back.content.dto.ContentsResponse;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.content.dto.LikesResponse;
import com.baba.back.content.exception.CommentBadRequestException;
import com.baba.back.content.exception.CommentNotFoundException;
import com.baba.back.content.exception.ContentAuthorizationException;
import com.baba.back.content.exception.ContentBadRequestException;
import com.baba.back.content.exception.ContentNotFountException;
import com.baba.back.content.exception.TagBadRequestException;
import com.baba.back.content.repository.CommentRepository;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.content.repository.TagRepository;
import com.baba.back.oauth.dto.MemberResponse;
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
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계30));

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
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
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
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(contentRepository.existsByBabyAndContentDateValue(아기1, LocalDate.now(now))).willReturn(false);
        given(fileHandler.upload(any(ImageFile.class))).willReturn("VALID_IMAGE_SOURCE");
        given(contentRepository.save(any(Content.class))).willReturn(컨텐츠10);

        // when
        final Long contentId = contentService.createContent(컨텐츠_생성_요청_데이터, 멤버1.getId(), 아기1.getId());

        // then
        assertThat(contentId).isEqualTo(컨텐츠10.getId());
    }

    @Test
    void 좋아요_추가_시_멤버가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 좋아요_추가_시_아기가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 좋아요_추가_시_관계가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 좋아요_추가_시_컨텐츠가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isInstanceOf(ContentNotFountException.class);
    }

    @Test
    void 좋아요_추가_시_아기와_콘텐츠가_관련이_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠20.getId())).willReturn(Optional.of(컨텐츠20));

        // when & then
        assertThatThrownBy(() -> contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠20.getId()))
                .isInstanceOf(ContentBadRequestException.class);
    }

    @Test
    void 좋아요가_추가된다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(likeRepository.findByContentAndMember(컨텐츠10, 멤버1)).willReturn(Optional.empty());

        // when
        final LikeContentResponse likeContentResponse = contentService.likeContent(멤버1.getId(), 아기1.getId(),
                컨텐츠10.getId());

        // then
        then(likeRepository).should(times(1)).save(any());

        assertThat(likeContentResponse.isLiked()).isTrue();
    }

    @Test
    void 이미_좋아요가_있을때_좋아요를_누르면_기존의_좋아요가_취소된다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(likeRepository.findByContentAndMember(컨텐츠10, 멤버1)).willReturn(Optional.of(좋아요10));

        // when
        final LikeContentResponse likeContentResponse = contentService.likeContent(멤버1.getId(), 아기1.getId(),
                컨텐츠10.getId());

        // then
        then(likeRepository).should(times(1)).save(any());

        assertThat(likeContentResponse.isLiked()).isFalse();
    }

    @Test
    void 좋아요를_취소하고_다시_좋아요를_누르면_좋아요가_추가된다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(likeRepository.findByContentAndMember(컨텐츠10, 멤버1)).willReturn(Optional.of(좋아요10));

        // when
        contentService.likeContent(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId());
        final LikeContentResponse likeContentResponse = contentService.likeContent(멤버1.getId(), 아기1.getId(),
                컨텐츠10.getId());

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
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findByBabyYearAndMonth(아기1, year, month)).willReturn(List.of(컨텐츠10, 컨텐츠11));
        given(likeRepository.existsByContentAndMember(컨텐츠10, 멤버1)).willReturn(true);
        given(likeRepository.existsByContentAndMember(컨텐츠11, 멤버1)).willReturn(false);

        // when
        final ContentsResponse response = contentService.getContents(멤버1.getId(), 아기1.getId(), year, month);

        // then
        assertAll(
                () -> assertThat(response.album()).hasSize(2),
                () -> assertThat(response.album()).containsExactly(
                        new ContentResponse(
                                컨텐츠11.getId(),
                                컨텐츠11.getOwnerName(),
                                컨텐츠11.getRelationName(),
                                컨텐츠11.getContentDate(),
                                컨텐츠11.getTitle(),
                                false,
                                컨텐츠11.getImageSource(),
                                컨텐츠11.getCardStyle()
                        ),
                        new ContentResponse(
                                컨텐츠10.getId(),
                                컨텐츠10.getOwnerName(),
                                컨텐츠10.getRelationName(),
                                컨텐츠10.getContentDate(),
                                컨텐츠10.getTitle(),
                                true,
                                컨텐츠10.getImageSource(),
                                컨텐츠10.getCardStyle()
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
        assertThatThrownBy(
                () -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터1))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_존재하지_않는_아기의_콘텐츠에_추가하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터1))
                .isInstanceOf(BabyNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_아기와_관계가_없는_멤버가_추가하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터1))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_존재하지_않는_콘텐츠에_추가하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터1))
                .isInstanceOf(ContentNotFountException.class);
    }

    @Test
    void 댓글_추가_시_태그가_존재하지_않으면_생성된_comment_id_를_반환한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글10);

        // when & then
        assertThat(contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 댓글_생성_요청_데이터))
                .isEqualTo(댓글10.getId());
    }

    @Test
    void 댓글_추가_시_태그한_멤버가_존재하지않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글10);
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터1))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_태그한_멤버가_아기와_관계가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글10);
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터1))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 댓글_추가_시_태그한_멤버를_태그할_수_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.of(관계21));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글10);
        given(memberRepository.findById(멤버3.getId())).willReturn(Optional.of(멤버3));
        given(relationRepository.findByMemberAndBaby(멤버3, 아기1)).willReturn(Optional.of(관계22));

        // when & then
        assertThatThrownBy(
                () -> contentService.createComment(멤버2.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터2))
                .isInstanceOf(TagBadRequestException.class);
    }

    @Test
    void 댓글_추가_시_태그된_멤버와_함께_댓글을_추가한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(commentRepository.save(any(Comment.class))).willReturn(댓글10);
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.of(관계11));
        given(tagRepository.save(any(Tag.class))).willReturn(any(Tag.class));

        // when & then
        assertThat(contentService.createComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 태그_댓글_생성_요청_데이터1))
                .isEqualTo(댓글10.getId());
    }

    @Test
    void 성장앨범_댓글_요청_시_존재하지_않는_멤버가_요청하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.getComments(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 성장앨범_댓글_요청_시_존재하지_않는_콘텐츠를_요청하면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.getComments(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isInstanceOf(ContentNotFountException.class);
    }

    @Test
    void 성장앨범_댓글_요청_시_멤버과_아기가_관계가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.getComments(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 성장앨범_댓글_요청_시_가족_그룹의_멤버가_요청했다면_모든_좋아요_댓글을_확인할_수_있다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(commentRepository.findAllByContent(컨텐츠10)).willReturn(List.of(댓글10));
        given(tagRepository.findByComment(댓글10)).willReturn(Optional.empty());

        // when & then
        assertThat(contentService.getComments(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isEqualTo(new CommentsResponse(
                        List.of(new CommentResponse(
                                        댓글10.getId(),
                                        멤버1.getId(),
                                        멤버1.getName(),
                                        관계10.getRelationName(),
                                        멤버1.getIconName(),
                                        멤버1.getIconColor(),
                                        "",
                                        댓글10.getText(),
                                        댓글10.getCreatedAt()
                                )

                        )));
    }

    @Test
    void 성장앨범_댓글_요청_시_모든_태그_댓글을_확인할_수_있다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(commentRepository.findAllByContent(컨텐츠10)).willReturn(List.of(댓글10));
        given(tagRepository.findByComment(댓글10)).willReturn(Optional.of(태그10));

        // when & then
        assertThat(contentService.getComments(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId()))
                .isEqualTo(new CommentsResponse(
                        List.of(new CommentResponse(
                                        댓글10.getId(),
                                        멤버1.getId(),
                                        멤버1.getName(),
                                        관계10.getRelationName(),
                                        멤버1.getIconName(),
                                        멤버1.getIconColor(),
                                        태그10.getTagMember().getName(),
                                        댓글10.getText(),
                                        댓글10.getCreatedAt()
                                )

                        )));
    }

    @Test
    void 성장앨범_댓글_요청_시_가족이_아니라면_가족_그룹과_같은_그룹의_좋아요_댓글_태그_댓글만_확인할_수_있다() {
        // given
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(contentRepository.findById(컨텐츠20.getId())).willReturn(Optional.of(컨텐츠20));
        given(babyRepository.findById(아기2.getId())).willReturn(Optional.of(아기2));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기2)).willReturn(Optional.of(관계20));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기2)).willReturn(Optional.of(관계21));
        given(relationRepository.findByMemberAndBaby(멤버3, 아기2)).willReturn(Optional.of(관계22));
        given(commentRepository.findAllByContent(컨텐츠20)).willReturn(List.of(댓글20, 댓글21, 댓글22, 댓글23));
        given(tagRepository.findByComment(댓글20)).willReturn(Optional.empty());
        given(tagRepository.findByComment(댓글21)).willReturn(Optional.empty());
        given(tagRepository.findByComment(댓글23)).willReturn(Optional.of(태그20));

        // when & then
        assertThat(contentService.getComments(멤버2.getId(), 아기2.getId(), 컨텐츠20.getId()))
                .isEqualTo(new CommentsResponse(
                                List.of(new CommentResponse(
                                                댓글20.getId(),
                                                멤버1.getId(),
                                                멤버1.getName(),
                                                관계20.getRelationName(),
                                                멤버1.getIconName(),
                                                멤버1.getIconColor(),
                                                "",
                                                댓글20.getText(),
                                                댓글20.getCreatedAt()
                                        ),
                                        new CommentResponse(
                                                댓글21.getId(),
                                                멤버2.getId(),
                                                멤버2.getName(),
                                                관계21.getRelationName(),
                                                멤버2.getIconName(),
                                                멤버2.getIconColor(),
                                                "",
                                                댓글21.getText(),
                                                댓글21.getCreatedAt()
                                        )
                                )
                        )
                );
    }

    @Test
    void 좋아요_보기_요청_시_게시물에_등록된_모든_좋아요를_조회한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(likeRepository.findAllByContent(컨텐츠10)).willReturn(List.of(좋아요10, 좋아요11, 좋아요12, 좋아요13));

        // when
        final LikesResponse response = contentService.getLikes(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId());

        // then
        assertAll(
                () -> assertThat(response.likeUsersPreview()).hasSize(3),
                () -> assertThat(response.likeUsers()).containsExactly(
                        new MemberResponse(멤버1.getId(), 멤버1.getName(), 멤버1.getIntroduction(), 멤버1.getIconName(),
                                멤버1.getIconColor()),
                        new MemberResponse(멤버2.getId(), 멤버2.getName(), 멤버2.getIntroduction(), 멤버2.getIconName(),
                                멤버2.getIconColor()),
                        new MemberResponse(멤버3.getId(), 멤버3.getName(), 멤버3.getIntroduction(), 멤버3.getIconName(),
                                멤버3.getIconColor()),
                        new MemberResponse(멤버4.getId(), 멤버4.getName(), 멤버4.getIntroduction(), 멤버4.getIconName(),
                                멤버4.getIconColor())
                ));
    }

    @Test
    void 좋아요_보기_요청_시_게시물에_좋아요가_등록되지_않았다면_빈_리스트를_반환한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(likeRepository.findAllByContent(컨텐츠10)).willReturn(List.of());

        // when
        final LikesResponse response = contentService.getLikes(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId());

        // then
        assertAll(
                () -> assertThat(response.likeUsersPreview()).hasSize(0),
                () -> assertThat(response.likeUsers()).hasSize(0));
    }

    @Test
    void 성장_앨범_제목과_카드_수정을_진행한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(수정용_컨텐츠10.getId())).willReturn(Optional.of(수정용_컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));

        // when
        contentService.updateTitleAndCard(멤버1.getId(), 아기1.getId(), 수정용_컨텐츠10.getId(), 콘텐츠_제목_카드스타일_변경_요청_데이터);

        // then
        assertAll(
                () -> assertThat(수정용_컨텐츠10.getTitle()).isEqualTo(콘텐츠_제목_카드스타일_변경_요청_데이터.title()),
                () -> assertThat(수정용_컨텐츠10.getCardStyle()).isEqualTo(콘텐츠_제목_카드스타일_변경_요청_데이터.cardStyle()));
    }

    @Test
    void 성장_앨범_제목과_카드_수정_시_생성자가_아니라면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(contentRepository.findById(수정용_컨텐츠10.getId())).willReturn(Optional.of(수정용_컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.of(관계11));

        // when
        assertThatThrownBy(() -> contentService.updateTitleAndCard(멤버2.getId(), 아기1.getId(), 수정용_컨텐츠10.getId(),
                콘텐츠_제목_카드스타일_변경_요청_데이터))
                .isInstanceOf(ContentBadRequestException.class);
    }

    @Test
    void 성장_앨범_댓글_삭제_시_댓글의_작성자가_아니면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.of(관계11));
        given(commentRepository.findById(댓글10.getId())).willReturn(Optional.of(댓글10));

        // when & then
        assertThatThrownBy(() -> contentService.deleteComment(멤버2.getId(), 아기1.getId(), 컨텐츠10.getId(), 댓글10.getId()))
                .isInstanceOf(CommentBadRequestException.class);
    }

    @Test
    void 성장_앨범_댓글을_삭제할_수_있다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계11));
        given(commentRepository.findById(댓글10.getId())).willReturn(Optional.of(댓글10));

        // when & then
        assertAll(
                () -> assertThatCode(
                        () -> contentService.deleteComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 댓글10.getId()))
                        .doesNotThrowAnyException(),
                () -> then(commentRepository).should().delete(댓글10)
        );
    }

    @Test
    void 성장_앨범_댓글을_삭제_시_댓글이_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계11));
        given(commentRepository.findById(댓글10.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentService.deleteComment(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 댓글10.getId()))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 성장_앨범_사진_수정_시_성장_앨범의_생성자가_아니라면_예외를_던진다() {
        // given
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(memberRepository.findById(멤버2.getId())).willReturn(Optional.of(멤버2));
        given(relationRepository.findByMemberAndBaby(멤버2, 아기1)).willReturn(Optional.of(관계11));

        // when & then
        assertThatThrownBy(() -> contentService.updatePhoto(멤버2.getId(), 아기1.getId(), 컨텐츠10.getId(), 컨텐츠_사진_수정_요청_데이터))
                .isInstanceOf(ContentBadRequestException.class);
    }

    @Test
    void 성장_앨범_사진을_수정할_수_있다() {
        // given
        given(contentRepository.findById(컨텐츠10.getId())).willReturn(Optional.of(컨텐츠10));
        given(babyRepository.findById(아기1.getId())).willReturn(Optional.of(아기1));
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findByMemberAndBaby(멤버1, 아기1)).willReturn(Optional.of(관계10));
        given(fileHandler.upload(any(ImageFile.class))).willReturn("VALID_IMAGE_SOURCE");

        // when & then
        assertThatCode(() -> contentService.updatePhoto(멤버1.getId(), 아기1.getId(), 컨텐츠10.getId(), 컨텐츠_사진_수정_요청_데이터))
                .doesNotThrowAnyException();
    }
}
