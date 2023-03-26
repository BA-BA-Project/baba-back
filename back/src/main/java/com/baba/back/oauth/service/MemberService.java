package com.baba.back.oauth.service;

import com.baba.back.baby.domain.Babies;
import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.domain.invitation.Invitations;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.baby.repository.InvitationRepository;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.dto.MyGroupResponse;
import com.baba.back.oauth.dto.MyGroupMemberResponse;
import com.baba.back.oauth.dto.MyProfileResponse;
import com.baba.back.oauth.dto.SignUpWithBabyResponse;
import com.baba.back.oauth.dto.SignUpWithCodeRequest;
import com.baba.back.oauth.exception.MemberBadRequestException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BabyRepository babyRepository;
    private final RelationGroupRepository relationGroupRepository;
    private final RelationRepository relationRepository;
    private final Picker<Color> picker;
    private final IdConstructor idConstructor;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final TokenRepository tokenRepository;
    private final InvitationRepository invitationRepository;
    private final Clock clock;

    public SignUpWithBabyResponse signUpWithBaby(MemberSignUpRequest request, String memberId) {
        validateSignUp(memberId);
        final Member member = saveMember(memberId, request.getName(), request.getIconName());

        final Babies babies = saveBabies(request);
        saveRelations(babies, member, request.getRelationName());

        final String accessToken = accessTokenProvider.createToken(memberId);
        final String refreshToken = refreshTokenProvider.createToken(memberId);
        saveRefreshToken(member, refreshToken);

        return new SignUpWithBabyResponse(new MemberSignUpResponse(accessToken, refreshToken), babies.getFirstBabyId());
    }

    private void validateSignUp(String memberId) {
        if (memberRepository.existsById(memberId)) {
            throw new MemberBadRequestException(memberId + "는 이미 가입한 멤버입니다.");
        }
    }

    private Member saveMember(String memberId, String name, String iconName) {
        return memberRepository.save(Member.builder()
                .id(memberId)
                .name(name)
                .introduction("")
                .iconName(iconName)
                .iconColor(Color.from(picker))
                .build());
    }

    private Babies saveBabies(MemberSignUpRequest request) {
        return new Babies(request.getBabies().stream()
                .map(babyRequest -> babyRequest.toEntity(idConstructor.createId(), LocalDate.now(clock)))
                .map(babyRepository::save)
                .toList());
    }

    private void saveRelations(Babies babies, Member member, String relationName) {
        final Color groupColor = Color.from(picker);

        babies.getValues()
                .stream()
                .map(baby -> {
                    final RelationGroup relationGroup = saveRelationGroup(baby, groupColor);
                    return Relation.builder()
                            .member(member)
                            .relationName(relationName)
                            .relationGroup(relationGroup)
                            .build();
                })
                .forEach(relationRepository::save);
    }

    private RelationGroup saveRelationGroup(Baby baby, Color groupColor) {
        return relationGroupRepository.save(
                RelationGroup.builder()
                        .baby(baby)
                        .relationGroupName("가족")
                        .groupColor(groupColor)
                        .family(true)
                        .build()
        );
    }

    private void saveRefreshToken(Member member, String refreshToken) {
        tokenRepository.save(Token.builder()
                .member(member)
                .value(refreshToken)
                .build());
    }

    public MemberResponse findMember(String memberId) {
        final Member member = getMember(memberId);

        return new MemberResponse(
                member.getName(),
                member.getIntroduction(),
                member.getIconName(),
                member.getIconColor()
        );
    }

    private Member getMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));
    }

    public SignUpWithBabyResponse signUpWithCode(SignUpWithCodeRequest request, String memberId) {
        validateSignUp(memberId);

        final Invitations invitations = getInvitations(request.getInviteCode());
        final InvitationCode invitationCode = invitations.getUnExpiredInvitationCode(LocalDateTime.now(clock));

        final Member member = saveMember(memberId, request.getName(), request.getIconName());
        final Babies babies = saveRelationsAndGetBabies(invitations.values(), invitationCode.getRelationName(), member);

        final String accessToken = accessTokenProvider.createToken(memberId);
        final String refreshToken = refreshTokenProvider.createToken(memberId);
        saveRefreshToken(member, refreshToken);

        return new SignUpWithBabyResponse(new MemberSignUpResponse(accessToken, refreshToken), babies.getFirstBabyId());
    }

    private Invitations getInvitations(String code) {
        return new Invitations(invitationRepository.findAllByCode(code));
    }

    private Babies saveRelationsAndGetBabies(List<Invitation> invitations, String relationName, Member member) {
        return new Babies(invitations.stream()
                .map(invitation -> {
                    final RelationGroup relationGroup = invitation.getRelationGroup();
                    final Baby baby = relationGroup.getBaby();

                    relationRepository.save(Relation.builder()
                            .relationGroup(relationGroup)
                            .relationName(relationName)
                            .member(member)
                            .build());

                    return baby;
                })
                .toList());
    }

    public MyProfileResponse searchMyGroups(String memberId) {
        final Member member = getMember(memberId);
        final Baby firstBaby = findFirstBaby(member);

        final List<RelationGroup> relationGroups = getRelationGroupsByBaby(firstBaby);
        final List<Relation> relations = getRelationsByRelationGroups(relationGroups);
        final List<MyGroupResponse> groups = getMyGroups(relationGroups, relations);

        return new MyProfileResponse(groups);
    }

    private Baby findFirstBaby(Member member) {
        final Relation relation = relationRepository.findFirstByMemberAndRelationGroupFamily(member, true)
                .orElseThrow(() -> new RelationNotFoundException("멤버와 가족 관계인 아기가 존재하지 않습니다."));
        final RelationGroup relationGroup = relation.getRelationGroup();

        return relationGroup.getBaby();
    }

    private List<RelationGroup> getRelationGroupsByBaby(Baby firstBaby) {
        return relationGroupRepository.findAllByBaby(firstBaby);
    }

    private List<Relation> getRelationsByRelationGroups(List<RelationGroup> relationGroups) {
        return relationRepository.findAllByRelationGroupIn(relationGroups);
    }

    private List<MyGroupResponse> getMyGroups(List<RelationGroup> relationGroups, List<Relation> relations) {
        return relationGroups.stream()
                .map(relationGroup -> {
                    final List<Relation> relationsByGroup = relations.stream()
                            .filter(relation -> relation.hasSameRelationGroup(relationGroup))
                            .toList();

                    return new MyGroupResponse(relationGroup.getRelationGroupName(), relationGroup.isFamily(),
                            getGroupMembers(relationsByGroup));
                }).toList();
    }

    private List<MyGroupMemberResponse> getGroupMembers(List<Relation> relations) {
        return relations.stream()
                .map(relation -> {
                    final Member member = relation.getMember();
                    return new MyGroupMemberResponse(
                            member.getId(),
                            member.getName(),
                            relation.getRelationName(),
                            member.getIconName(),
                            member.getIconColor());
                }).toList();
    }
}
