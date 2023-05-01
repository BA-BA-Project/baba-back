package com.baba.back.baby.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BabyTest {

    @Test
    void updateName_메서드_호출_시_아기_이름을_변경한다() {
        // given
        final String babyName = "name";
        final Baby baby = Baby.builder()
                .name(babyName)
                .birthday(LocalDate.now())
                .now(LocalDate.now())
                .build();

        // when
        final String newBabyName = "name2";
        baby.updateName(newBabyName);

        // then
        assertEquals(newBabyName, baby.getName());
    }
}