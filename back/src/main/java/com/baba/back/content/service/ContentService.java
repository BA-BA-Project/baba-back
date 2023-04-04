package com.baba.back.content.service;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.common.FileHandler;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.comment.Comment;
import com.baba.back.content.domain.comment.Tag;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.domain.content.ImageFile;
import com.baba.back.content.dto.CommentResponse;
import com.baba.back.content.dto.CommentsResponse;
import com.baba.back.content.dto.ContentResponse;
import com.baba.back.content.dto.ContentsResponse;
import com.baba.back.content.dto.CreateCommentRequest;
import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.content.exception.ContentAuthorizationException;
import com.baba.back.content.exception.ContentBadRequestException;
import com.baba.back.content.exception.ContentNotFountException;
import com.baba.back.content.exception.TagBadRequestException;
import com.baba.back.content.repository.CommentRepository;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.content.repository.TagRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final MemberRepository memberRepository;
    private final BabyRepository babyRepository;
    private final RelationRepository relationRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final FileHandler fileHandler;
    private final Clock clock;

    public Long createContent(CreateContentRequest request, String memberId, String babyId) {
        final Member member = findMember(memberId);
        final Baby baby = findBaby(babyId);
        final Relation relation = findRelation(member, baby);
        checkAuthorization(relation);

        final Content content = Content.builder()
                .title(request.getTitle())
                .contentDate(request.getDate())
                .now(LocalDate.now(clock))
                .cardStyle(request.getCardStyle())
                .baby(baby)
                .owner(member)
                .relation(relation.getRelationName())
                .build();

        checkDuplication(baby, content.getContentDate());

        final ImageFile imageFile = new ImageFile(request.getPhoto());
        final String imageSource = fileHandler.upload(imageFile);
        content.updateURL(imageSource);
        final Content savedContent = contentRepository.save(content);

        return savedContent.getId();
    }

    private Member findMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));
    }

    private Baby findBaby(String babyId) {
        return babyRepository.findById(babyId)
                .orElseThrow(() -> new BabyNotFoundException(babyId + " 는 존재하지 않는 babyId 입니다."));
    }

    private Relation findRelation(Member member, Baby baby) {
        return relationRepository.findByMemberAndBaby(member, baby)
                .orElseThrow(() -> new RelationNotFoundException(
                        member.getId() + "와 " + baby.getId() + " 사이의 관계가 존재하지 않습니다."));
    }

    private void checkAuthorization(Relation relation) {
        if (!relation.isFamily()) {
            throw new ContentAuthorizationException(relation.getId() + " 관계는 가족 관계가 아닙니다.");
        }
    }

    private void checkDuplication(Baby baby, LocalDate contentDate) {
        if (contentRepository.existsByBabyAndContentDateValue(baby, contentDate)) {
            throw new ContentBadRequestException("컨텐츠가 이미 존재합니다.");
        }
    }

    public LikeContentResponse likeContent(String memberId, String babyId, Long contentId) {
        final Member member = findMember(memberId);
        final Baby baby = findBaby(babyId);
        findRelation(member, baby);
        final Content content = findContent(contentId);
        validateContentBaby(baby, content);

        final Like like = findAndUpdateLike(member, content);

        likeRepository.save(like);

        return new LikeContentResponse(!like.isDeleted());
    }

    private void validateContentBaby(Baby baby, Content content) {
        if (!content.hasEqualBaby(baby)) {
            throw new ContentBadRequestException(content.getId() + "콘텐츠는 " + baby.getId() + "와 관련이 없습니다.");
        }
    }

    private Content findContent(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentNotFountException(contentId + " 는 존재하지 않는 컨텐츠입니다."));
    }

    private Like findAndUpdateLike(Member member, Content content) {
        final Optional<Like> like = likeRepository.findByContentAndMember(content, member);
        like.ifPresent(Like::updateDeleted);

        return like.orElseGet(
                () -> Like.builder()
                        .member(member)
                        .content(content)
                        .build());
    }

    public ContentsResponse getContents(String memberId, String babyId, int year, int month) {
        final Member member = findMember(memberId);
        final Baby baby = findBaby(babyId);
        findRelation(member, baby);

        List<Content> contents = contentRepository.findByBabyYearAndMonth(baby, year, month);

        return new ContentsResponse(
                contents.stream()
                        .map(
                                content -> new ContentResponse(
                                        content.getId(),
                                        content.getOwnerName(),
                                        content.getRelationName(),
                                        content.getContentDate(),
                                        content.getTitle(),
                                        likeRepository.existsByContentAndMember(content, member),
                                        content.getImageSource(),
                                        content.getCardStyle()
                                ))
                        .sorted()
                        .toList()
        );
    }

    public Long createComment(String memberId, String babyId, Long contentId, CreateCommentRequest request) {
        final Member owner = findMember(memberId);
        final Baby baby = findBaby(babyId);
        final Relation ownerRelation = findRelation(owner, baby);
        final Content content = findContent(contentId);
        validateContentBaby(baby, content);

        final Comment comment = Comment.builder()
                .content(content)
                .owner(owner)
                .text(request.getComment())
                .build();
        commentRepository.save(comment);

        final String tagMemberId = request.getTag();
        if (tagMemberId.isBlank()) {
            return comment.getId();
        }

        final RelationGroup ownerRelationGroup = ownerRelation.getRelationGroup();

        final Member tagMember = findMember(tagMemberId);
        final Relation tagMemberRelation = findRelation(tagMember, baby);
        final RelationGroup tagMemberRelationGroup = tagMemberRelation.getRelationGroup();
        validateRelationGroup(ownerRelationGroup, tagMemberRelationGroup);

        final Tag tag = Tag.builder()
                .comment(comment)
                .tagMember(tagMember)
                .build();
        tagRepository.save(tag);

        return comment.getId();
    }

    private void validateRelationGroup(RelationGroup ownerRelationGroup, RelationGroup tagMemberRelationGroup) {
        if (!ownerRelationGroup.canShare(tagMemberRelationGroup)) {
            throw new TagBadRequestException(
                    String.format("%s에 속한 멤버는 %s에 속한 멤버를 태그할 수 없습니다.",
                            ownerRelationGroup.getRelationGroupName(),
                            tagMemberRelationGroup.getRelationGroupName()
                    )
            );
        }
    }

    public CommentsResponse getComments(String memberId, String babyId, Long contentId) {
        final Member member = findMember(memberId);
        final Content content = findContent(contentId);
        final Baby baby = findBaby(babyId);
        validateContentBaby(baby, content);

        final Relation relation = findRelation(member, baby);
        final RelationGroup relationGroup = relation.getRelationGroup();
        final List<Comment> comments = findSharedComments(content, baby, relationGroup);

        return getCommentsResponse(content, comments);
    }

    private List<Comment> findSharedComments(Content content, Baby baby, RelationGroup relationGroup) {
        return commentRepository.findAllByContent(content)
                .stream()
                .filter(comment -> {
                    final Relation commentMemberRelation = findRelation(comment.getOwner(), baby);
                    final boolean canShareCommentMemberGroup = relationGroup.canShare(
                            commentMemberRelation.getRelationGroup());
                    if (!canShareCommentMemberGroup) {
                        return false;
                    }
                    final Optional<Tag> tag = tagRepository.findByComment(comment);
                    if (tag.isEmpty()) {
                        return true;
                    }

                    final Relation tagMemberRelation = findRelation(tag.get().getTagMember(), baby);
                    return relationGroup.canShare(tagMemberRelation.getRelationGroup());
                })
                .toList();
    }

    private CommentsResponse getCommentsResponse(Content content,
                                                 List<Comment> comments) {
        return new CommentsResponse(
                comments.stream()
                        .map(comment -> {
                                    final Member owner = comment.getOwner();
                                    final Relation relation = findRelation(owner, content.getBaby());

                                    return new CommentResponse(
                                            comment.getId(),
                                            owner.getId(),
                                            owner.getName(),
                                            relation.getRelationName(),
                                            owner.getIconName(),
                                            owner.getIconColor(),
                                            findTagMemberName(comment),
                                            comment.getText(),
                                            comment.getCreatedAt());
                                }
                        )
                        .sorted()
                        .toList()
        );
    }

    private String findTagMemberName(Comment comment) {
        final Optional<Tag> tag = tagRepository.findByComment(comment);
        if (tag.isPresent()) {
            return tag.get().getTagMember().getName();
        }
        return "";
    }
}
