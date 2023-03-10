package com.baba.back.content.service;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.FileHandler;
import com.baba.back.content.domain.ImageFile;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.domain.content.ContentDate;
import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.content.exception.ContentAuthorizationException;
import com.baba.back.content.exception.ContentBadRequestException;
import com.baba.back.content.exception.ContentNotFountException;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.time.LocalDate;
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
                .build();

        checkDuplication(content.getContentDate(), baby);

        final ImageFile imageFile = new ImageFile(request.getPhoto());
        final String imageSource = fileHandler.upload(imageFile);
        content.updateURL(imageSource);
        final Content savedContent = contentRepository.save(content);

        return savedContent.getId();
    }

    private Member findMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "??? ???????????? ????????? ???????????? ????????????."));
    }

    private Baby findBaby(String babyId) {
        return babyRepository.findById(babyId)
                .orElseThrow(() -> new BabyNotFoundException(babyId + " ??? ???????????? ?????? babyId ?????????."));
    }

    private Relation findRelation(Member member, Baby baby) {
        return relationRepository.findByMemberAndBaby(member, baby)
                .orElseThrow(() -> new RelationNotFoundException(
                        member.getId() + "??? " + baby.getId() + " ????????? ????????? ???????????? ????????????."));
    }

    private void checkAuthorization(Relation relation) {
        if (!relation.isFamily()) {
            throw new ContentAuthorizationException(relation.getId() + " ????????? ?????? ????????? ????????????.");
        }
    }

    private void checkDuplication(ContentDate contentDate, Baby baby) {
        if (contentRepository.existsByContentDateAndBaby(contentDate, baby)) {
            throw new ContentBadRequestException("???????????? ?????? ???????????????.");
        }
    }

    public LikeContentResponse likeContent(String memberId, String babyId, Long contentId) {
        final Member member = findMember(memberId);
        final Baby baby = findBaby(babyId);
        findRelation(member, baby);
        final Content content = findContent(contentId);
        final Like like = findAndUpdateLike(member, content);

        likeRepository.save(like);

        return new LikeContentResponse(!like.isDeleted());
    }

    private Content findContent(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentNotFountException(contentId + " ??? ???????????? ?????? ??????????????????."));
    }

    private Like findAndUpdateLike(Member member, Content content) {
        final Optional<Like> like = likeRepository.findByMemberAndContent(member, content);
        like.ifPresent(Like::updateDeleted);

        return like.orElseGet(
                () -> Like.builder()
                        .member(member)
                        .content(content)
                        .build());
    }
}
