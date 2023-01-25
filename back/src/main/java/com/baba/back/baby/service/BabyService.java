package com.baba.back.baby.service;

import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.relation.domain.DefaultRelation;
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
        Relation relation = relationRepository.findByMemberIdAndDefaultRelation(memberId, DefaultRelation.DEFAULT)
                .orElseThrow(
                        () -> new RelationNotFoundException(String.format("%s 는 Relation 테이블에 존재하지 않습니다.", memberId)));

        return new SearchDefaultBabyResponse(relation.getBaby().getId());
    }

}
