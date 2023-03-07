package com.baba.back.baby.domain;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.관계2;
import static com.baba.back.fixture.DomainFixture.관계3;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.relation.domain.RelationGroup;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RelationsTest {

    @Test
    void 등록된_관계들_중_가족_그룹을_조회할_수_있다() {
        // given
        final Relations relations = new Relations(List.of(관계1, 관계2, 관계3));

        // when
        final List<RelationGroup> myFamilyGroup = relations.getMyFamilyGroup();

        // then
        assertThat(myFamilyGroup).containsExactly(관계1.getRelationGroup(), 관계3.getRelationGroup());
    }

    @Test
    void 등록된_관계들_중_가족_그룹이_아닌_그룹을_조회할_수_있다() {
        // given
        final Relations relations = new Relations(List.of(관계1, 관계2));

        // when
        final List<RelationGroup> othersFamilyGroup = relations.getOthersFamilyGroup();

        // then
        assertThat(othersFamilyGroup).containsExactly(관계2.getRelationGroup());
    }
}
