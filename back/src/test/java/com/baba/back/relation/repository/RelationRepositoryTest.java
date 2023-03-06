package com.baba.back.relation.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RelationRepositoryTest {

    @Autowired
    private RelationGroupRepository relationGroupRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Test
    void findByMemberAndBaby메소드_호출시_멤버와_아기의_relation_객체를_반환한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        final Baby savedBaby = babyRepository.save(아기1);
        final RelationGroup savedRelationGroup = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("가족")
                .family(true)
                .build());

        final Relation savedRelation = relationRepository.save(Relation.builder()
                .member(savedMember)
                .relationName("아빠")
                .relationGroup(savedRelationGroup)
                .build());

        // when
        final Relation relation = relationRepository.findByMemberAndBaby(savedMember, savedBaby).orElseThrow();

        // then
        Assertions.assertThat(relation).isEqualTo(savedRelation);
    }
}
