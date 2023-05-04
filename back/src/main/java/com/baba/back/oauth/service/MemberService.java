package com.baba.back.oauth.service;

import com.baba.back.baby.domain.Babies;
import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.domain.invitation.Invitations;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.exception.RelationGroupNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.baby.repository.InvitationRepository;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.BabyProfileResponse;
import com.baba.back.oauth.dto.CreateGroupRequest;
import com.baba.back.oauth.dto.FamilyGroupResponse;
import com.baba.back.oauth.dto.GroupMemberResponse;
import com.baba.back.oauth.dto.GroupResponse;
import com.baba.back.oauth.dto.GroupResponseWithFamily;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.dto.MemberUpdateRequest;
import com.baba.back.oauth.dto.MyProfileResponse;
import com.baba.back.oauth.dto.SignUpWithBabyResponse;
import com.baba.back.oauth.dto.SignUpWithCodeRequest;
import com.baba.back.oauth.dto.UpdateGroupMemberRequest;
import com.baba.back.oauth.dto.UpdateGroupRequest;
import com.baba.back.oauth.exception.MemberBadRequestException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.domain.Relations;
import com.baba.back.relation.exception.RelationGroupBadRequestException;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
        final Member member = getFirstMember(memberId);

        return new MemberResponse(
                memberId,
                member.getName(),
                member.getIntroduction(),
                member.getIconName(),
                member.getIconColor()
        );
    }

    private Member getFirstMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));
    }

    public void updateMember(String memberId, MemberUpdateRequest request) {
        final Member member = getFirstMember(memberId);
        member.update(request.getName(), request.getIntroduction(), Color.from(request.getIconColor()),
                request.getIconName());

        memberRepository.save(member);
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

    public void createGroup(String memberId, CreateGroupRequest request) {
        final Member member = getFirstMember(memberId);
        final List<Baby> myBabies = getMyBabies(member);
        final List<RelationGroup> relationGroups = getRelationGroupsByBabies(myBabies);

        validateRelationGroup(relationGroups, request.getRelationGroup());

        saveRelationGroups(request, myBabies);
    }

    private List<Baby> getMyBabies(Member member) {
        final Relations relations = new Relations(relationRepository.findAllByMember(member));
        final List<RelationGroup> myFamilyGroup = relations.getMyFamilyGroup();

        final List<Baby> babies = myFamilyGroup.stream()
                .map(RelationGroup::getBaby)
                .toList();

        if (babies.isEmpty()) {
            throw new RelationNotFoundException("가족 관계인 아기가 존재하지 않습니다.");
        }

        return babies;
    }

    private List<RelationGroup> getRelationGroupsByBabies(List<Baby> babies) {
        return relationGroupRepository.findAllByBabyIn(babies);
    }

    private void validateRelationGroup(List<RelationGroup> relationGroups, String relationGroupName) {
        relationGroups.forEach(relationGroup -> {
            if (relationGroup.hasEqualGroupName(relationGroupName)) {
                throw new RelationGroupBadRequestException("이미 존재하는 그룹 이름입니다.");
            }
        });
    }

    private void saveRelationGroups(CreateGroupRequest request, List<Baby> myBabies) {
        myBabies.forEach(baby -> relationGroupRepository.save(
                RelationGroup.builder()
                        .baby(baby)
                        .relationGroupName(request.getRelationGroup())
                        .groupColor(Color.from(request.getIconColor()))
                        .family(false)
                        .build()));
    }

    public MyProfileResponse searchMyGroups(String memberId) {
        final Member member = getFirstMember(memberId);
        final Baby firstBaby = findFirstBaby(member);

        final List<RelationGroup> relationGroups = getRelationGroupsByBaby(firstBaby);
        final List<Relation> relations = getRelationsByRelationGroups(relationGroups);
        final List<GroupResponseWithFamily> groups = getMyGroups(relations);

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

    private List<GroupResponseWithFamily> getMyGroups(List<Relation> relations) {
        return relations.stream()
                .collect(Collectors.groupingBy(Relation::getRelationGroup))
                .entrySet()
                .stream()
                .map(entry -> {
                    final RelationGroup relationGroup = entry.getKey();
                    final List<Relation> relationsByGroup = entry.getValue();

                    return new GroupResponseWithFamily(relationGroup.getRelationGroupName(), relationGroup.isFamily(),
                            getGroupMembers(relationsByGroup));
                })
                .toList();
    }

    private List<GroupMemberResponse> getGroupMembers(List<Relation> relations) {
        return relations.stream()
                .map(relation -> {
                    final Member member = relation.getMember();
                    return new GroupMemberResponse(
                            member.getId(),
                            member.getName(),
                            relation.getRelationName(),
                            member.getIconName(),
                            member.getIconColor());
                }).toList();
    }

    public BabyProfileResponse searchBabyGroups(String memberId, String babyId) {
        final Member member = getFirstMember(memberId);
        final Baby baby = getBaby(babyId);

        final RelationGroup memberRelationGroup = getMemberRelationGroup(member, baby);
        final RelationGroup familyRelationGroup = getFamilyRelationGroup(baby);

        final FamilyGroupResponse familyGroupResponse = getFamilyGroup(familyRelationGroup);

        if (memberRelationGroup.isFamily()) {
            return new BabyProfileResponse(familyGroupResponse, null);
        }

        final GroupResponse groupResponse = getGroupResponse(memberRelationGroup);

        return new BabyProfileResponse(familyGroupResponse, groupResponse);
    }

    private Baby getBaby(String babyId) {
        return babyRepository.findById(babyId)
                .orElseThrow(() -> new BabyNotFoundException(babyId + "에 해당하는 아기가 존재하지 않습니다."));
    }

    private RelationGroup getMemberRelationGroup(Member member, Baby baby) {
        return relationRepository.findByMemberAndBaby(member, baby)
                .orElseThrow(() -> new RelationNotFoundException("멤버와 아기의 관계가 존재하지 않습니다."))
                .getRelationGroup();
    }

    private RelationGroup getFamilyRelationGroup(Baby baby) {
        return relationGroupRepository.findByBabyAndFamily(baby, true)
                .orElseThrow(() -> new RelationGroupNotFoundException("아기의 가족 그룹이 존재하지 않습니다."));
    }

    private FamilyGroupResponse getFamilyGroup(RelationGroup relationGroup) {
        final List<BabyResponse> babyResponses = getBabies(relationGroup);

        final List<GroupMemberResponse> groupMembers = getGroupMembersByRelationGroup(relationGroup);

        return new FamilyGroupResponse(relationGroup.getRelationGroupName(), babyResponses, groupMembers);
    }

    private List<GroupMemberResponse> getGroupMembersByRelationGroup(RelationGroup relationGroup) {
        final List<Relation> relations = relationRepository.findAllByRelationGroup(relationGroup);
        return getGroupMembers(relations);
    }

    private List<BabyResponse> getBabies(RelationGroup relationGroup) {
        final Member familyMember = getFirstMember(relationGroup);
        final List<Relation> relations = relationRepository.findAllByMember(familyMember);

        return relations.stream()
                .map(relation -> {
                    final RelationGroup group = relation.getRelationGroup();

                    return new BabyResponse(group.getBabyId(), group.getGroupColor(), group.getBabyName());
                })
                .toList();
    }

    private Member getFirstMember(RelationGroup relationGroup) {
        return relationRepository.findFirstByRelationGroup(relationGroup)
                .orElseThrow(() -> new RelationNotFoundException("아기와의 관계가 존재하지 않습니다."))
                .getMember();
    }

    private GroupResponse getGroupResponse(RelationGroup relationGroup) {
        final List<GroupMemberResponse> groupMembers = getGroupMembersByRelationGroup(relationGroup);

        return new GroupResponse(relationGroup.getRelationGroupName(), groupMembers);
    }

    public void updateGroup(String memberId, String groupName, UpdateGroupRequest request) {
        final Member member = getFirstMember(memberId);
        final Baby firstBaby = findFirstBaby(member);

        final List<RelationGroup> relationGroups = findGroupByGroupName(groupName, firstBaby);

        updateGroupNames(relationGroups, request.getRelationGroup());
    }

    private List<RelationGroup> findGroupByGroupName(String groupName, Baby firstBaby) {
        final List<RelationGroup> relationGroups = getRelationGroupsByBaby(firstBaby);
        final List<RelationGroup> groupsByName = relationGroups.stream()
                .filter(relationGroup -> relationGroup.hasEqualGroupName(groupName))
                .toList();

        if (groupsByName.isEmpty()) {
            throw new RelationGroupNotFoundException("{" + groupName + "} 그룹이 존재하지 않습니다.");
        }

        return groupsByName;
    }

    void updateGroupNames(List<RelationGroup> relationGroups, String newGroupName) {
        relationGroups.forEach(relationGroup -> relationGroup.updateRelationGroupName(newGroupName));
    }

    public void updateGroupMember(String memberId, String groupMemberId, UpdateGroupMemberRequest request) {
        final Member member = getFirstMember(memberId);
        final Member groupMember = getFirstMember(groupMemberId);
        final Baby firstBaby = findFirstBaby(member);

        final List<RelationGroup> relationGroups = getRelationGroupsByBaby(firstBaby);
        final List<Relation> relationsByMember = getRelationsByMember(relationGroups, groupMember);

        updateRelationNames(relationsByMember, request.getRelationName());
    }

    private List<Relation> getRelationsByMember(List<RelationGroup> relationsGroups, Member groupMember) {
        final List<Relation> relations = getRelationsByRelationGroups(relationsGroups);
        
        final List<Relation> relationsByMember = relations.stream()
                .filter(relation -> relation.hasMember(groupMember))
                .toList();

        if (relationsByMember.isEmpty()) {
            throw new RelationNotFoundException("{" + groupMember.getId() + "}는 그룹의 멤버가 아닙니다.");
        }

        return relationsByMember;
    }

    private void updateRelationNames(List<Relation> relations, String relationName) {
        relations.forEach(relation -> relation.updateRelationName(relationName));
    }
}
