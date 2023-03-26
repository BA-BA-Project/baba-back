package com.baba.back.relation.repository;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.멤버2;
import static com.baba.back.fixture.DomainFixture.멤버3;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static org.assertj.core.api.Assertions.assertThat;

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
    void findByMemberAndBaby_메소드_호출시_멤버와_아기의_relation_객체를_반환한다() {
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
        assertThat(relation).isEqualTo(savedRelation);
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

    @Test
    void findAllByMemberAndFamily_메소드_호출시_멤버의_가족관계를_조회한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        final Baby savedBaby1 = babyRepository.save(아기1);
        final Baby savedBaby2 = babyRepository.save(아기2);
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

        final RelationGroup savedRelationGroup2 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby2)
                .relationGroupName("가족")
                .family(true)
                .build());

        final Relation savedRelation2 = relationRepository.save(Relation.builder()
                .member(savedMember)
                .relationName("아빠")
                .relationGroup(savedRelationGroup2)
                .build());

        // when
        final List<Relation> relations = relationRepository.findAllByMemberAndRelationGroupFamily(savedMember, true);

        // then
        assertThat(relations).containsExactly(savedRelation1, savedRelation2);
    }

    @Test
    void findFirstByMemberAndRelationGroupFamily_메서드_호출_시_멤버의_가족관계중_첫_번째_관계를_조회한다() {
        // given
        final Member savedMember = memberRepository.save(멤버1);
        final Baby savedBaby1 = babyRepository.save(아기1);
        final Baby savedBaby2 = babyRepository.save(아기2);
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

        final RelationGroup savedRelationGroup2 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby2)
                .relationGroupName("가족")
                .family(true)
                .build());

        relationRepository.save(Relation.builder()
                .member(savedMember)
                .relationName("아빠")
                .relationGroup(savedRelationGroup2)
                .build());

        // when
        final Relation firstRelation = relationRepository.findFirstByMemberAndRelationGroupFamily(savedMember, true)
                .orElseThrow();

        // then
        assertThat(firstRelation).isEqualTo(savedRelation1);
    }

    @Test
    void findAllByRelationGroupIn_메서드_호출_시_각각의_관계_그룹에_해당하는_모든_관계를_조회한다() {
        // given
        final Member savedMember1 = memberRepository.save(멤버1);
        final Member savedMember2 = memberRepository.save(멤버2);
        final Member savedMember3 = memberRepository.save(멤버3);
        final Baby savedBaby = babyRepository.save(아기1);

        final RelationGroup savedRelationGroup1 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("가족")
                .family(true)
                .build());
        final RelationGroup savedRelationGroup2 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("외가")
                .family(false)
                .build());

        final Relation savedRelation1 = relationRepository.save(Relation.builder()
                .member(savedMember1)
                .relationName("아빠")
                .relationGroup(savedRelationGroup1)
                .build());
        final Relation savedRelation2 = relationRepository.save(Relation.builder()
                .member(savedMember1)
                .relationName("엄마")
                .relationGroup(savedRelationGroup1)
                .build());
        final Relation savedRelation3 = relationRepository.save(Relation.builder()
                .member(savedMember1)
                .relationName("이모")
                .relationGroup(savedRelationGroup2)
                .build());

        final List<RelationGroup> relationGroups = List.of(savedRelationGroup1, savedRelationGroup2);

        // when
        final List<Relation> relations = relationRepository.findAllByRelationGroupIn(relationGroups);

        // then
        assertThat(relations).containsExactly(savedRelation1, savedRelation2, savedRelation3);
    }
}
