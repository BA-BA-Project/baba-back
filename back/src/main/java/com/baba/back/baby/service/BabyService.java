package com.baba.back.baby.service;

import com.baba.back.relation.domain.Relations;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BabyService {

    private final RelationRepository relationRepository;
    private final MemberRepository memberRepository;

    public BabiesResponse findBabies(String memberId) {
        final Member member = findMember(memberId);
        final Relations relations = new Relations(relationRepository.findByMember(member));
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
}
