package com.baba.back.oauth.service;

import com.baba.back.baby.domain.Babies;
import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.IconColor;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.exception.MemberBadRequestException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BabyRepository babyRepository;
    private final RelationRepository relationRepository;
    private final Picker<IconColor> picker;
    private final IdConstructor idConstructor;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final Clock clock;

    public MemberSignUpResponse signUp(MemberSignUpRequest request, String memberId) {
        validateSignUp(memberId);
        final Member member = saveMember(memberId, request);

        final Babies babies = saveBabies(request);
        saveRelations(babies, member, request.getRelationName());

        final String accessToken = accessTokenProvider.createToken(memberId);
        final String refreshToken = refreshTokenProvider.createToken(memberId);

        return new MemberSignUpResponse(accessToken, refreshToken);
    }

    private void validateSignUp(String memberId) {
        if (memberRepository.existsById(memberId)) {
            throw new MemberBadRequestException(memberId + "는 이미 가입한 멤버입니다.");
        }
    }

    private Member saveMember(String memberId, MemberSignUpRequest request) {
        return memberRepository.save(Member.builder()
                .id(memberId)
                .name(request.getName())
                .introduction("")
                .iconName(request.getIconName())
                .colorPicker(picker)
                .build());
    }

    private Babies saveBabies(MemberSignUpRequest request) {
        return new Babies(request.getBabies().stream()
                .map(babyRequest -> babyRequest.toEntity(idConstructor.createId(), LocalDate.now(clock)))
                .map(babyRepository::save)
                .toList());
    }

    private void saveRelations(Babies babies, Member member, String relationName) {
        babies.getBabies()
                .stream()
                .map(baby -> Relation.builder()
                        .member(member)
                        .baby(baby)
                        .relationName(relationName)
                        .relationGroup(RelationGroup.FAMILY)
                        .build())
                .forEach(relationRepository::save);
    }

    public MemberResponse findMember(String memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));

        return new MemberResponse(
                member.getName(),
                member.getIntroduction(),
                member.getIconName(),
                member.getIconColor()
        );
    }
}
