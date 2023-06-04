package com.baba.back.baby.domain;

import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.baby.exception.BabiesBadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BabiesTest {

    @Test
    void 아기_리스트의_길이가_0이면_예외를_던진다() {
        // given
        List<Baby> babies = new ArrayList<>();

        // when & then
        assertThatThrownBy(() -> new Babies(babies))
                .isInstanceOf(BabiesBadRequestException.class);
    }

    @Test
    void 제일_처음_저장된_아기의_id를_조회한다() {
        // given
        final Babies babies = new Babies(List.of(아기1, 아기2));

        // when
        final String firstBabyId = babies.getFirstBabyId();

        // then
        assertThat(firstBabyId).isEqualTo(아기1.getId());
    }
}
