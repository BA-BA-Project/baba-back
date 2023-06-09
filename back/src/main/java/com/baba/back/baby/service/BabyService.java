package com.baba.back.baby.service;

import com.baba.back.baby.domain.Babies;
import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.domain.invitation.Code;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.domain.invitation.Invitations;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.CreateBabyRequest;
import com.baba.back.baby.dto.CreateInviteCodeRequest;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.baby.dto.InviteCodeBabyResponse;
import com.baba.back.baby.dto.InviteCodeRequest;
import com.baba.back.baby.dto.IsMyBabyResponse;
import com.baba.back.baby.dto.SearchInviteCodeResponse;
import com.baba.back.baby.exception.BabyBadRequestException;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.RelationBadRequestException;
import com.baba.back.baby.exception.RelationGroupNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.baby.repository.InvitationRepository;
import com.baba.back.common.Generated;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberAuthorizationException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.domain.Relations;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BabyService {

    private final IdConstructor idConstructor;
    private final Picker<Color> picker;
    private final MemberRepository memberRepository;
    private final BabyRepository babyRepository;
    private final RelationRepository relationRepository;
    private final RelationGroupRepository relationGroupRepository;
    private final InvitationRepository invitationRepository;
    private final CodeGenerator randomCodeGenerator;
    private final Clock clock;

    /**
     * 아기를 생성한다. 자신의 아기가 없다면 아기를 생성하고 관계를 생성한 후 응답을 마무리한다. 자신의 아기가 있다면 동일한 이름의 아기가 있는지 확인한다
     *
     * @param memberId 멤버 아이디
     * @param request  아기 생성 요청
     * @return 아기 아이디
     */
    public String createBaby(String memberId, CreateBabyRequest request) {
        final Member member = findMember(memberId);
        final Baby baby = saveBaby(request);
        final List<Relation> relations = relationRepository.findAllByMemberAndRelationGroupFamily(member, true);

        if (relations.isEmpty()) {
            final RelationGroup relationGroup = saveRelationGroup(baby, "가족", Color.from(picker), true);
            saveRelation(member, request.getRelationName(), relationGroup);

            return baby.getId();
        }

        final Babies babies = getMyBabies(request.getName(), relations);

        final List<RelationGroup> relationGroups = getRelationGroupsByBaby(babies.getFirstBaby());
        final List<Relation> relationsByBaby = getRelationsByRelationGroups(relationGroups);

        relationsByBaby.forEach(relation -> {
            final RelationGroup relationGroup = saveRelationGroup(baby, relation.getRelationGroupName(),
                    Color.from(relation.getRelationGroupColor()), relation.isFamily());
            saveRelation(relation.getMember(), relation.getRelationName(), relationGroup);
        });

        return baby.getId();
    }

    private Baby saveBaby(CreateBabyRequest request) {
        return babyRepository.save(request.toEntity(idConstructor.createId(), LocalDate.now(clock)));
    }

    private RelationGroup saveRelationGroup(Baby baby, String relationGroupName, Color color, boolean isFamily) {
        return relationGroupRepository.save(RelationGroup.builder()
                .baby(baby)
                .relationGroupName(relationGroupName)
                .groupColor(color)
                .family(isFamily)
                .build());
    }

    private void saveRelation(Member member, String relationName, RelationGroup relationGroup) {
        relationRepository.save(Relation.builder()
                .member(member)
                .relationName(relationName)
                .relationGroup(relationGroup)
                .build());
    }

    private Babies getMyBabies(String babyName, List<Relation> relations) {
        return new Babies(relations.stream()
                .map(relation -> {
                    final Baby baby = relation.getBaby();
                    if (baby.equalsByName(babyName)) {
                        throw new BabyBadRequestException("{" + babyName + "} 는 이미 존재하는 아기 이름입니다.");
                    }

                    return baby;
                })
                .toList());
    }

    private List<RelationGroup> getRelationGroupsByBaby(Baby firstBaby) {
        return relationGroupRepository.findAllByBaby(firstBaby);
    }

    private List<Relation> getRelationsByRelationGroups(List<RelationGroup> relationGroups) {
        return relationRepository.findAllByRelationGroupIn(relationGroups);
    }

    public BabiesResponse findBabies(String memberId) {
        final Member member = findMember(memberId);
        final Relations relations = new Relations(relationRepository.findAllByMember(member));
        final List<RelationGroup> myFamilyGroups = relations.getMyFamilyGroup();
        final List<RelationGroup> othersFamilyGroups = relations.getOthersFamilyGroup();

        return getBabiesResponse(myFamilyGroups, othersFamilyGroups);
    }

    private Member findMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));
    }

    private BabiesResponse getBabiesResponse(List<RelationGroup> myFamilyGroups,
                                             List<RelationGroup> othersFamilyGroups) {
        return new BabiesResponse(getBabyResponse(myFamilyGroups, true), getBabyResponse(othersFamilyGroups, false));
    }

    private List<IsMyBabyResponse> getBabyResponse(List<RelationGroup> groups, boolean isMyBaby) {
        return groups.stream()
                .map(group -> new IsMyBabyResponse(group.getBabyId(), group.getGroupColor(), group.getBabyName(),
                        isMyBaby))
                .sorted()
                .toList();
    }

    public void updateBabyName(String memberId, String babyId, String babyName) {
        final Member member = findMember(memberId);
        final Baby baby = findBaby(babyId);
        final Relation relation = findRelation(member, baby);
        checkAuthorization(relation);

        baby.updateName(babyName);
        babyRepository.save(baby);
    }

    private Baby findBaby(String babyId) {
        return babyRepository.findById(babyId)
                .orElseThrow(() -> new BabyNotFoundException(babyId + "에 해당하는 아기가 존재하지 않습니다."));
    }

    private Relation findRelation(Member member, Baby baby) {
        return relationRepository.findByMemberAndBaby(member, baby)
                .orElseThrow(() -> new RelationNotFoundException(
                        member.getId() + "와 " + baby.getId() + " 사이의 관계가 존재하지 않습니다."));
    }

    private void checkAuthorization(Relation relation) {
        if (!relation.isFamily()) {
            throw new MemberAuthorizationException(relation.getId() + " 관계는 가족 관계가 아닙니다.");
        }
    }

    public CreateInviteCodeResponse createInviteCode(CreateInviteCodeRequest request, String memberId) {
        final Member member = findMember(memberId);
        final List<Relation> relations = findFamilyRelations(member);
        final List<RelationGroup> groups = findRelationGroups(relations, request.getRelationGroup());

        final Code code = Code.from(randomCodeGenerator);
        final InvitationCode invitationCode = InvitationCode.builder()
                .code(code)
                .relationName(request.getRelationName())
                .now(LocalDateTime.now(clock))
                .build();

        saveInvitation(groups, invitationCode);

        return new CreateInviteCodeResponse(code.getValue());
    }

    private List<Relation> findFamilyRelations(Member member) {
        final List<Relation> relations = relationRepository.findAllByMemberAndRelationGroupFamily(member, true);
        if (relations.isEmpty()) {
            throw new RelationNotFoundException("멤버와 가족 관계인 아기가 존재하지 않습니다.");
        }
        return relations;
    }

    private List<RelationGroup> findRelationGroups(List<Relation> relations, String relationGroupName) {
        final List<Baby> babies = relations.stream()
                .map(Relation::getRelationGroup)
                .map(RelationGroup::getBaby)
                .toList();

        return babies.stream()
                .map(baby -> relationGroupRepository.findByBabyAndRelationGroupNameValue(baby, relationGroupName)
                        .orElseThrow(() -> new RelationGroupNotFoundException(
                                "관계그룹 {" + relationGroupName + "}가 존재하지 않습니다.")))
                .toList();
    }

    private void saveInvitation(List<RelationGroup> groups, InvitationCode invitationCode) {
        groups.forEach(relationGroup -> {
            final Optional<Invitation> optionalInvitation = invitationRepository.findByRelationGroupAndRelationName(
                    relationGroup, invitationCode.getRelationName());

            optionalInvitation.ifPresent(invitation -> invitation.updateCode(invitationCode.getCode()));

            final Invitation invitation = optionalInvitation.orElseGet(() -> Invitation.builder()
                    .invitationCode(invitationCode)
                    .relationGroup(relationGroup)
                    .build());

            invitationRepository.save(invitation);
        });
    }

    public SearchInviteCodeResponse searchInviteCode(String code) {
        final Invitations invitations = new Invitations(invitationRepository.findAllByCode(code));
        final InvitationCode invitationCode = invitations.getUnExpiredInvitationCode(LocalDateTime.now(clock));

        return new SearchInviteCodeResponse(
                getInviteCodeBabyResponses(invitations),
                invitationCode.getRelationName(),
                invitations.getRelationGroupName());
    }

    private List<InviteCodeBabyResponse> getInviteCodeBabyResponses(Invitations invitations) {
        return invitations.values().stream()
                .map(invitation -> {
                    final RelationGroup relationGroup = invitation.getRelationGroup();
                    final Baby baby = relationGroup.getBaby();

                    return new InviteCodeBabyResponse(baby.getName());
                })
                .toList();
    }

    public void addBabyWithCode(InviteCodeRequest request, String memberId) {
        final Member member = findMember(memberId);

        final Invitations invitations = getInvitations(request.getInviteCode());
        final InvitationCode invitationCode = invitations.getUnExpiredInvitationCode(LocalDateTime.now(clock));

        validateInvitationCode(invitations.values());
        validateRelations(invitations.values(), member);
        saveRelations(invitations.values(), invitationCode.getRelationName(), member);
    }

    private void validateInvitationCode(List<Invitation> invitations) {
        invitations.forEach(invitation -> {
            final RelationGroup relationGroup = invitation.getRelationGroup();

            if (relationGroup.isFamily()) {
                throw new InvitationCodeBadRequestException("가족 관계의 멤버는 회원가입으로만 등록할 수 있습니다.");
            }
        });
    }

    private Invitations getInvitations(String code) {
        return new Invitations(invitationRepository.findAllByCode(code));
    }

    private void validateRelations(List<Invitation> invitations, Member member) {
        final List<Relation> relations = relationRepository.findAllByMember(member);
        final List<RelationGroup> relationGroups = relations.stream()
                .map(Relation::getRelationGroup)
                .toList();

        invitations.forEach(invitation -> {
            if (relationGroups.contains(invitation.getRelationGroup())) {
                throw new RelationBadRequestException("이미 존재하는 관계입니다.");
            }
        });
    }

    private void saveRelations(List<Invitation> invitations, String relationName, Member member) {
        invitations.forEach(invitation -> relationRepository.save(Relation.builder()
                .relationGroup(invitation.getRelationGroup())
                .relationName(relationName)
                .member(member)
                .build())
        );
    }

    @Generated
    public void deleteBaby(String memberId, String babyId) {
        final Member member = findMember(memberId);
        final Baby baby = findBaby(babyId);
        final Relation relation = findRelation(member, baby);

        if (relation.isFamily()) {
            babyRepository.delete(baby);
            return;
        }

        relationRepository.delete(relation);
    }
}
