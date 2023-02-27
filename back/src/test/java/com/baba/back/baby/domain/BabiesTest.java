package com.baba.back.baby.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.baby.exception.BabiesBadRequestException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BabiesTest {
    public static final String BABY_ID = "1234";
    public static final String BABY_NAME = "앙쥬";
    private static final LocalDate NOW = LocalDate.now();
    private static final LocalDate BIRTHDAY = LocalDate.of(2023, 7, 7);

    @Test
    void 아기_리스트의_길이가_0이면_예외를_던진다() {
        // given
        List<Baby> babies = new ArrayList<>();

        // when & then
        assertThatThrownBy(() -> new Babies(babies))
                .isInstanceOf(BabiesBadRequestException.class);
    }
}