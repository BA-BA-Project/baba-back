package com.baba.back.relation.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.RelationGroup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RelationGroupRepositoryTest {

    @Autowired
    private RelationGroupRepository relationGroupRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Test
    void 아기의_그룹관계중_그룹명이_일치하는_그룹관계를_조회한다() {
        // given
        memberRepository.save(멤버1);
        final Baby savedBaby = babyRepository.save(아기1);
        final RelationGroup savedRelationGroup = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("가족")
                .family(true)
                .build());

        // when
        final RelationGroup relationGroup = relationGroupRepository.findByBabyAndRelationGroupNameValue(savedBaby, "가족")
                .orElseThrow();

        // then
        Assertions.assertThat(relationGroup).isEqualTo(savedRelationGroup);
    }
}