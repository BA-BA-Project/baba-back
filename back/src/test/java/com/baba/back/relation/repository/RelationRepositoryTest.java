package com.baba.back.relation.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.util.List;
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

    @Test
    void findByMember메소드_호출시_멤버에_등록된_relation_객체들_반환한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        final Baby savedBaby1 = babyRepository.save(아기1);
        final RelationGroup savedRelationGroup1 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby1)
                .relationGroupName("가족")
                .family(true)
                .build());
        final Relation savedRelation1 = relationRepository.save(Relation.builder()
                .member(savedMember)
                .relationName("아빠")
                .relationGroup(savedRelationGroup1)
                .build());

        final Baby savedBaby2 = babyRepository.save(아기2);
        final RelationGroup savedRelationGroup2 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby2)
                .relationGroupName("친구")
                .family(false)
                .build());

        final Relation savedRelation2 = relationRepository.save(Relation.builder()
                .member(savedMember)
                .relationName("아빠 친구")
                .relationGroup(savedRelationGroup2)
                .build());

        // when
        final List<Relation> relation = relationRepository.findAllByMember(savedMember);

        // then
        Assertions.assertThat(relation).containsExactly(savedRelation1, savedRelation2);
    }
}
