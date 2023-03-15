package com.baba.back.invitation.service;

import com.baba.back.baby.domain.Baby;
import com.baba.back.invitation.domain.Invitation;
import com.baba.back.invitation.domain.InvitationCode;
import com.baba.back.invitation.dto.CreateInviteCodeRequest;
import com.baba.back.invitation.dto.CreateInviteCodeResponse;
import com.baba.back.invitation.exception.RelationGroupNotFoundException;
import com.baba.back.invitation.repository.InvitationRepository;
import com.baba.back.invitation.repository.InvitationCodeRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InvitationService {

    private final MemberRepository memberRepository;
    private final RelationRepository relationRepository;
    private final RelationGroupRepository relationGroupRepository;
    private final InvitationCodeRepository invitationCodeRepository;
    private final InvitationRepository invitationRepository;
    private final Clock clock;

    public CreateInviteCodeResponse createInviteCode(CreateInviteCodeRequest request, String memberId) {
        final Member member = findMember(memberId);
        final List<Relation> relations = findFamilyRelations(member);
        final List<RelationGroup> groups = findRelationGroups(relations, request.getRelationGroup());

        final String inviteCode = InviteCodeGenerator.generate();
        final InvitationCode invitationCode = getInvitationCode(request, inviteCode);
        createInvitation(groups, invitationCode);

        return new CreateInviteCodeResponse(inviteCode);
    }

    private Member findMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));
    }

    private List<Relation> findFamilyRelations(Member member) {
        final List<Relation> relations = relationRepository.findAllByMemberAndRelationGroupFamily(member, true);
        if (relations.isEmpty()) {
            throw new RelationNotFoundException("멤버와 가족 관계인 아기가 존재하지 않습니다.");
        }
        return relations;
    }

    private InvitationCode getInvitationCode(CreateInviteCodeRequest request, String inviteCode) {
        return invitationCodeRepository.save(
                InvitationCode.builder()
                        .inviteCode(inviteCode)
                        .relationName(request.getRelationName())
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
}
