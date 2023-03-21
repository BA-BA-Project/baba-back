package com.baba.back.baby.service;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.invitation.Code;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.baby.dto.CreateInviteCodeRequest;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.baby.dto.InviteCodeBabyResponse;
import com.baba.back.baby.dto.SearchInviteCodeResponse;
import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.InvitationCodeNotFoundException;
import com.baba.back.baby.exception.InvitationNotFoundException;
import com.baba.back.baby.exception.RelationGroupNotFoundException;
import com.baba.back.baby.repository.InvitationCodeRepository;
import com.baba.back.baby.repository.InvitationRepository;
import com.baba.back.oauth.domain.member.Member;
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
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BabyService {

    private final MemberRepository memberRepository;
    private final RelationRepository relationRepository;
    private final RelationGroupRepository relationGroupRepository;
    private final InvitationCodeRepository invitationCodeRepository;
    private final InvitationRepository invitationRepository;
    private final CodeGenerator randomCodeGenerator;
    private final Clock clock;

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
        return new BabiesResponse(getBabyResponse(myFamilyGroups), getBabyResponse(othersFamilyGroups));
    }

    private List<BabyResponse> getBabyResponse(List<RelationGroup> groups) {
        return groups.stream()
                .map(group -> new BabyResponse(group.getBabyId(), group.getGroupColor(), group.getBabyName()))
                .sorted()
                .toList();
    }

    public CreateInviteCodeResponse createInviteCode(CreateInviteCodeRequest request, String memberId) {
        final Member member = findMember(memberId);
        final List<Relation> relations = findFamilyRelations(member);
        final List<RelationGroup> groups = findRelationGroups(relations, request.getRelationGroup());

        final Code code = Code.from(randomCodeGenerator);
        final InvitationCode invitationCode = saveInvitationCode(request.getRelationName(), code);
        createInvitation(groups, invitationCode);

        return new CreateInviteCodeResponse(code.getValue());
    }

    private List<Relation> findFamilyRelations(Member member) {
        final List<Relation> relations = relationRepository.findAllByMemberAndRelationGroupFamily(member, true);
        if (relations.isEmpty()) {
            throw new RelationNotFoundException("멤버와 가족 관계인 아기가 존재하지 않습니다.");
        }
        return relations;
    }

    private InvitationCode saveInvitationCode(String relationName, Code code) {
        return invitationCodeRepository.save(
                InvitationCode.builder()
                        .code(code)
                        .relationName(relationName)
                        .now(LocalDateTime.now(clock))
                        .build()
        );
    }

    private List<RelationGroup> findRelationGroups(List<Relation> relations, String relationGroupName) {
        final List<Baby> babies = relations.stream()
                .map(Relation::getRelationGroup)
                .map(RelationGroup::getBaby)
                .toList();

        return babies.stream()
                .map(baby -> relationGroupRepository.findByBabyAndRelationGroupNameValue(baby, relationGroupName)
                        .orElseThrow(() -> new RelationGroupNotFoundException(
                                "관계그룹 {" + relationGroupName + "}가 존재하지 않습니다."))
                )
                .toList();
    }

    private void createInvitation(List<RelationGroup> groups, InvitationCode invitationCode) {
        groups.forEach(relationGroup -> invitationRepository.save(
                Invitation.builder()
                        .invitationCode(invitationCode)
                        .relationGroup(relationGroup)
                        .build())
        );
    }

    public SearchInviteCodeResponse searchInviteCode(String code) {
        final InvitationCode invitationCode = getInvitationCode(code);
        validateInvitationCode(invitationCode);

        final List<Invitation> invitations = getInvitations(invitationCode);

        return new SearchInviteCodeResponse(
                invitations.stream()
                        .map(invitation -> {
                            final RelationGroup relationGroup = invitation.getRelationGroup();
                            final Baby baby = relationGroup.getBaby();

                            return new InviteCodeBabyResponse(baby.getName());
                        })
                        .toList(),
                invitationCode.getRelationName());
    }

    private InvitationCode getInvitationCode(String code) {
        return invitationCodeRepository.findByCodeValue(code)
                .orElseThrow(() -> new InvitationCodeNotFoundException(code + "는 존재하지 않는 초대 코드입니다."));
    }

    private void validateInvitationCode(InvitationCode invitationCode) {
        if (invitationCode.isExpired(LocalDateTime.now(clock))) {
            throw new InvitationCodeBadRequestException("초대 코드가 만료되었습니다.");
        }
    }

    private List<Invitation> getInvitations(InvitationCode invitationCode) {
        final List<Invitation> invitations = invitationRepository.findAllByInvitationCode(invitationCode);
        if (invitations.isEmpty()) {
            throw new InvitationNotFoundException("초대 정보가 존재하지 않습니다.");
        }

        return invitations;
    }
}
