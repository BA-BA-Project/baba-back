package com.baba.back.relation.repository;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RelationRepositoryTest {

    private static final Member 멤버 = new Member("memberId", "박성우", "안녕하세요", colors -> "FFAEBA", "icon1");
    private static final Baby 아기1 = new Baby("babyId1", "아기", LocalDate.now(), LocalDate.now());
    private static final Baby 아기2 = new Baby("babyId2", "아기2", LocalDate.now(), LocalDate.now());

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Test
    void 멤버의_기본설정된_아기를_조회할_수_있다() {
        // given
        final Member savedMember = memberRepository.save(멤버);
        final Baby savedBaby = babyRepository.save(아기1);
        final Relation relation = new Relation(null, savedMember, savedBaby, "삼촌", RelationGroup.FAMILY, true);

        final Relation savedRelation = relationRepository.save(relation);

        // when
        final Relation findRelation = relationRepository.findByMemberAndDefaultRelation(savedMember, true)
                .orElseThrow();

        // then
        Assertions.assertThat(findRelation).isEqualTo(savedRelation);
    }

    @Test
    void 멤버에_등록된_여러_아기_중_기본설정된_아기를_조회할_수_있다() {
        // given
        final Member savedMember = memberRepository.save(멤버);
        final Baby savedBaby1 = babyRepository.save(아기1);
        final Baby savedBaby2 = babyRepository.save(아기2);
        final Relation relation1 = new Relation(null, savedMember, savedBaby1, "삼촌", RelationGroup.FAMILY, false);
        final Relation relation2 = new Relation(null, savedMember, savedBaby2, "삼촌", RelationGroup.FAMILY, true);

        relationRepository.save(relation1);
        final Relation savedRelation2 = relationRepository.save(relation2);

        // when
        final Relation findRelation = relationRepository.findByMemberAndDefaultRelation(savedMember, true)
                .orElseThrow();

        // then
        Assertions.assertThat(findRelation).isEqualTo(savedRelation2);
    }
}
