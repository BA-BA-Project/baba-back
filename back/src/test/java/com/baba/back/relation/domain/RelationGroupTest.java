package com.baba.back.relation.domain;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RelationGroupTest {

    public static Stream<Arguments> invalidRelationGroup() {
        return Stream.of(
                Arguments.of(RelationGroup.FATHERS),
                Arguments.of(RelationGroup.FRIENDS),
                Arguments.of(RelationGroup.MOTHERS)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRelationGroup")
    void 가족관계가_아니다(RelationGroup relationGroup) {
        Assertions.assertThat(relationGroup.isFamily()).isFalse();
    }

    @Test
    void 가족관계이다() {
        // given
        RelationGroup relationGroup = RelationGroup.FAMILY;

        // when & then
        Assertions.assertThat(relationGroup.isFamily()).isTrue();
    }
}