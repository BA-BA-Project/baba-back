package com.baba.back.baby.service;

import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BabyService {

    private final RelationRepository relationRepository;

    public SearchDefaultBabyResponse searchDefaultBaby(String memberId) {
        Relation relation = relationRepository.findByMemberIdAndDefaultRelation(memberId, true)
                .orElseThrow(
                        () -> new RelationNotFoundException(memberId +" 에 해당하는 relation 이 존재하지 않습니다."));

        return new SearchDefaultBabyResponse(relation.getBabyId());
    }

}
