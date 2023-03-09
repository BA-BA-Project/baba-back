package com.baba.back.relation.domain;

import static com.baba.back.fixture.DomainFixture.관계그룹1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.oauth.domain.member.Color;
import org.junit.jupiter.api.Test;

class RelationGroupTest {

    @Test
    void 관계_그룹의_정보를_조회할_수_있다() {
        // given
        final RelationGroup relationGroup = 관계그룹1;

        // when & then
        assertAll(
                () -> assertThat(relationGroup.getBabyId()).isEqualTo(아기1.getId()),
                () -> assertThat(relationGroup.getBabyName()).isEqualTo(아기1.getName()),
                () -> assertThat(relationGroup.getGroupColor()).isEqualTo(Color.COLOR_1.getValue())
        );
    }
}
