package com.baba.back.relation.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.RelationGroup;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RelationGroupRepositoryTest {

    @Autowired
    private RelationGroupRepository relationGroupRepository;

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
        assertThat(relationGroup).isEqualTo(savedRelationGroup);
    }

    @Test
    void findAllByBaby_메서드_호출_시_아기의_그룹관계를_조회한다() {
        // given
        memberRepository.save(멤버1);
        final Baby savedBaby = babyRepository.save(아기1);
        final RelationGroup savedRelationGroup = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("가족")
                .family(true)
                .build());

        final RelationGroup savedRelationGroup2 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("외가")
                .family(false)
                .build());

        // when
        final List<RelationGroup> relationGroups = relationGroupRepository.findAllByBaby(savedBaby);

        // then
        assertThat(relationGroups).containsExactly(savedRelationGroup, savedRelationGroup2);
    }

    @Test
    void findAllByBabyIn_메서드_호출_시_각각의_아기에_해당하는_모든_그룹관계를_조회한다() {
        // given
        memberRepository.save(멤버1);
        final Baby savedBaby = babyRepository.save(아기1);
        final RelationGroup savedRelationGroup = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("가족")
                .family(true)
                .build());

        final RelationGroup savedRelationGroup2 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("외가")
                .family(false)
                .build());
        final Baby savedBaby2 = babyRepository.save(아기2);
        final RelationGroup savedRelationGroup3 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby2)
                .relationGroupName("가족")
                .family(true)
                .build());

        final RelationGroup savedRelationGroup4 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby2)
                .relationGroupName("외가")
                .family(false)
                .build());

        // when
        final List<RelationGroup> relationGroups = relationGroupRepository.findAllByBabyIn(
                List.of(savedBaby, savedBaby2));

        // then
        assertThat(relationGroups).containsExactly(
                savedRelationGroup, savedRelationGroup2, savedRelationGroup3, savedRelationGroup4);
    }
}