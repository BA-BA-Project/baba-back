package com.baba.back.content.service;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.dto.CreateLikeResponse;
import com.baba.back.content.exception.ContentNotFountException;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final MemberRepository memberRepository;
    private final BabyRepository babyRepository;
    private final RelationRepository relationRepository;
    private final ContentRepository contentRepository;
    private final LikeRepository likeRepository;

    public CreateLikeResponse addLike(String memberId, String babyId, Long contentId) {
        final Member member = findMember(memberId);
        final Baby baby = findBaby(babyId);
        validateRelation(member, baby);
        final Content content = findContent(contentId);

        likeRepository.save(Like.builder()
                .member(member)
                .content(content)
                .build());

        return new CreateLikeResponse(true);
    }

    private Member findMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + " 에 해당하는 멤버를 찾을 수 없습니다."));
    }

    private Baby findBaby(String babyId) {
        return babyRepository.findById(babyId)
                .orElseThrow(() -> new BabyNotFoundException(babyId + " 는 존재하지 않는 아기입니다."));
    }

    private void validateRelation(Member member, Baby baby) {
        relationRepository.findByMemberAndBaby(member, baby)
                .orElseThrow(() -> new RelationNotFoundException(
                        member.getId() + "와 " + baby.getId() + " 사이의 관계가 존재하지 않습니다."));
    }

    private Content findContent(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentNotFountException(contentId + " 는 존재하지 않는 컨텐츠입니다."));
    }
}
