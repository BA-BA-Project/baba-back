package com.baba.back.relation.domain;

import static com.baba.back.fixture.DomainFixture.관계그룹10;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.oauth.domain.member.Color;
import org.junit.jupiter.api.Test;

class RelationGroupTest {

    @Test
    void 관계_그룹의_정보를_조회할_수_있다() {
        // given
        final RelationGroup relationGroup = 관계그룹10;

        // when & then
        assertAll(
                () -> assertThat(relationGroup.getBabyId()).isEqualTo(아기1.getId()),
                () -> assertThat(relationGroup.getBabyName()).isEqualTo(아기1.getName()),
                () -> assertThat(relationGroup.getGroupColor()).isEqualTo(Color.COLOR_1.getValue())
        );
    }

    @Test
    void 아기가_다른_관계_그룹은_다른_그룹과_공유할_수_없다() {
        // given
        final RelationGroup familyRelationGroup = new RelationGroup(아기1, "가족", Color.COLOR_1, true);
        final RelationGroup familyRelationGroup2 = new RelationGroup(아기2, "가족", Color.COLOR_1, true);

        // when
        final boolean result = familyRelationGroup.canShare(familyRelationGroup2);

        // then
        assertThat(result).isFalse();
    }


    @Test
    void 가족_관계_그룹은_다른_그룹과_공유할_수_있다() {
        // given
        final RelationGroup familyRelationGroup = new RelationGroup(아기1, "가족", Color.COLOR_1, true);
        final RelationGroup notFamilyRelationGroup = new RelationGroup(아기1, "가족 아님", Color.COLOR_1, false);

        // when
        final boolean result = familyRelationGroup.canShare(notFamilyRelationGroup);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 가족_관계_그룹은_가족_그룹과_공유할_수_있다() {
        // given
        final RelationGroup familyRelationGroup = new RelationGroup(아기1, "가족", Color.COLOR_1, true);
        final RelationGroup familyRelationGroup2 = new RelationGroup(아기1, "가족", Color.COLOR_1, true);

        // when
        final boolean result = familyRelationGroup.canShare(familyRelationGroup2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 가족_관계_그룹이_아닌_경우_가족_그룹과_공유할_수_있다() {
        // given
        final RelationGroup notFamilyRelationGroup = new RelationGroup(아기1, "가족 아님", Color.COLOR_1, false);
        final RelationGroup familyRelationGroup = new RelationGroup(아기1, "가족", Color.COLOR_1, true);

        // when
        final boolean result = notFamilyRelationGroup.canShare(familyRelationGroup);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 가족_관계_그룹이_아닌_경우에는_같은_관계_그룹과_공유할_수_있다() {
        // given
        final RelationGroup notFamilyRelationGroup = new RelationGroup(아기1, "가족 아님", Color.COLOR_1, false);
        final RelationGroup notFamilyRelationGroup2 = new RelationGroup(아기1, "가족 아님", Color.COLOR_1, false);

        // when
        final boolean result = notFamilyRelationGroup.canShare(notFamilyRelationGroup2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 가족_관계_그룹이_아닌_경우에는_가족이_아닌_다른_관계_그룹과_공유할_수_없다() {
        // given
        final RelationGroup notFamilyRelationGroup = new RelationGroup(아기1, "가족 아님", Color.COLOR_1, false);
        final RelationGroup notFamilyRelationGroup2 = new RelationGroup(아기1, "다른 그룹", Color.COLOR_1, false);

        // when
        final boolean result = notFamilyRelationGroup.canShare(notFamilyRelationGroup2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void hasEqualName_호출_시_관계_그룹명이_같으면_true를_반환한다() {
        // given
        final String relationGroupName = "가족";
        final RelationGroup relationGroup = new RelationGroup(아기1, relationGroupName, Color.COLOR_1, true);

        // when
        final boolean result = relationGroup.hasEqualName(relationGroupName);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void hasEqualName_호출_시_관계_그룹명이_다르면_false를_반환한다() {
        // given
        final String relationGroupName = "가족";
        final String anotherRelationGroupName = "외가";
        final RelationGroup relationGroup = new RelationGroup(아기1, relationGroupName, Color.COLOR_1, true);

        // when
        final boolean result = relationGroup.hasEqualName(anotherRelationGroupName);

        // then
        assertThat(result).isFalse();
    }
}

