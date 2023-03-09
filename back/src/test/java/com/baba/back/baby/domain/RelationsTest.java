package com.baba.back.baby.domain;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.관계3;
import static com.baba.back.fixture.DomainFixture.관계2;
import static com.baba.back.fixture.DomainFixture.관계4;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.domain.Relations;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RelationsTest {

    @Test
    void 등록된_관계들_중_가족_그룹을_조회할_수_있다() {
        // given
        final Relations relations = new Relations(List.of(관계1, 관계2, 관계3, 관계4));

        // when
        final List<RelationGroup> myFamilyGroup = relations.getMyFamilyGroup();

        // then
        assertThat(myFamilyGroup).containsExactly(관계1.getRelationGroup(), 관계2.getRelationGroup());
    }

    @Test
    void 등록된_관계들_중_가족_그룹이_아닌_그룹을_조회할_수_있다() {
        // given
        final Relations relations = new Relations(List.of(관계1, 관계2, 관계3, 관계4));

        // when
        final List<RelationGroup> othersFamilyGroup = relations.getOthersFamilyGroup();

        // then
        assertThat(othersFamilyGroup).containsExactly(관계3.getRelationGroup(), 관계4.getRelationGroup());
    }

    @Test
    void 등록된_아기가_없는_경우_그룹_조회_시_빈_리스트를_응답한다() {
        // given
        final Relations relations = new Relations(List.of());

        // when & then
        Assertions.assertAll(
                () -> assertThat(relations.getMyFamilyGroup()).isEmpty(),
                () -> assertThat(relations.getMyFamilyGroup()).isEmpty()
        );
    }
}
