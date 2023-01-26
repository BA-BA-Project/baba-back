package com.baba.back.baby.domain;

import com.baba.back.baby.exception.BabiesBadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BabiesTest {

    @Test
    void 아기_리스트의_길이가_0이면_예외를_던진다() {
        // given
        List<Baby> babies = new ArrayList<>();

        // when & then
        Assertions.assertThatThrownBy(() -> new Babies(babies))
                .isInstanceOf(BabiesBadRequestException.class);
    }
}