package com.baba.back.baby.service;

import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BabyService {

    private final RelationRepository relationRepository;
    private final MemberRepository memberRepository;

    public SearchDefaultBabyResponse searchDefaultBaby(String memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));

        final Relation relation = relationRepository.findByMemberAndDefaultRelation(member, true)
                .orElseThrow(() -> new RelationNotFoundException(memberId + " 에 해당하는 relation 이 존재하지 않습니다."));

        return new SearchDefaultBabyResponse(relation.searchBabyId());
    }

}
