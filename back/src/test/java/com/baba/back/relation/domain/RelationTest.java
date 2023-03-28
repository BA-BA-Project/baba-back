package com.baba.back.relation.domain;

import static com.baba.back.fixture.DomainFixture.관계그룹10;
import static com.baba.back.fixture.DomainFixture.관계그룹11;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.멤버3;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RelationTest {

    @Test
    void isFamily_메서드_호출_시_가족_관계이면_true를_반환한다() {
        // given
        final Relation relation = Relation.builder()
                .member(멤버1)
                .relationName("아빠")
                .relationGroup(관계그룹10)
                .build();

        // when & then
        assertThat(relation.isFamily()).isTrue();
    }

    @Test
    void isFamily_메서드_호출_시_가족_관계가_아니면_false를_반환한다() {
        // given
        final Relation relation = Relation.builder()
                .member(멤버3)
                .relationName("이모")
                .relationGroup(관계그룹11)
                .build();

        // when & then
        assertThat(relation.isFamily()).isFalse();
    }

    @Test
    void hasSameRelationGroup_메서드_호출_시_관계_그룹이_동일하면_true를_반환한다() {
        // given
        final Relation relation = Relation.builder()
                .member(멤버1)
                .relationName("아빠")
                .relationGroup(관계그룹10)
                .build();

        // when & then
        assertThat(relation.hasSameRelationGroup(관계그룹10)).isTrue();
    }

    @Test
    void hasSameRelationGroup_메서드_호출_시_관계_그룹이_다르면_false를_반환한다() {
        // given
        final Relation relation = Relation.builder()
                .member(멤버1)
                .relationName("아빠")
                .relationGroup(관계그룹10)
                .build();

        // when & then
        assertThat(relation.hasSameRelationGroup(관계그룹11)).isFalse();
    }
}