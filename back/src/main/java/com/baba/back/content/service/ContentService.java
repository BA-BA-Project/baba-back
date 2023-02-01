package com.baba.back.content.service;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.domain.ImageFile;
import com.baba.back.content.domain.ImageSaver;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.content.dto.CreateContentResponse;
import com.baba.back.content.exception.ContentAuthorizationException;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import java.time.LocalDate;
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
    private final ImageSaver imageSaver;

    public CreateContentResponse createContent(CreateContentRequest request, String memberId, String babyId) {
        // TODO: 멤버를 조회한다
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));

        // TODO: 아기를 조회한다
        final Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new BabyNotFoundException(babyId + " 는 존재하지 않는 babyId 입니다."));

        // TODO: 관계를 조회한다
        final Relation relation = relationRepository.findByMemberAndBaby(member, baby)
                .orElseThrow(() -> new RelationNotFoundException(
                        memberId + "와 " + babyId + " 사이의 관계가 존재하지 않습니다."));

        // TODO: 해당 아기의 컨텐츠를 생성할 수 있는 권한이 있는지 확인한다
        if (!relation.isFamily()) {
            throw new ContentAuthorizationException(memberId + "는 " + babyId + "와 가족 관계가 아닙니다.");
        }

        final Content content = Content.builder()
                .title(request.getTitle())
                .contentDate(request.getDate())
                .now(LocalDate.now())
                .cardStyle(request.getCardStyle())
                .imageSource("")
                .baby(baby)
                .build();

        final ImageFile imageFile = new ImageFile(request.getPhoto());
        final String imageSource = imageSaver.save(imageFile);

        content.updateURL(imageSource);

        contentRepository.save(content);

        return new CreateContentResponse(true);
    }
}