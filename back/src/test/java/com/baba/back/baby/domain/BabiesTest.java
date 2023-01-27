package com.baba.back.baby.domain;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void 기본_설정된_아기를_조회하는지_확인한다() {
        // given
        List<Baby> babyList = new ArrayList<>();
        babyList.add(new Baby(BABY_ID, BABY_NAME, BIRTHDAY, NOW));
        Babies babies = new Babies(babyList);

        // when
        Baby defaultBaby = babies.getDefaultBaby();

        // then
        assertThat(defaultBaby.getId()).isEqualTo(BABY_ID);
    }

    @Test
    void 아기가_한명일때는_기본_설정되지_않은_아기가_존재하지_않는다() {
        // given
        List<Baby> babyList = new ArrayList<>();
        babyList.add(new Baby(BABY_ID, BABY_NAME, BIRTHDAY, NOW));
        Babies babies = new Babies(babyList);

        // when
        List<Baby> notDefaultBabies = babies.getNotDefaultBabies();

        // then
        assertThat(notDefaultBabies.size()).isZero();
    }

    @Test
    void 아기가_두명이상일때_기본_설정되지_않은_아기가_존재한다() {
        // given
        List<Baby> babyList = new ArrayList<>();
        babyList.add(new Baby(BABY_ID, BABY_NAME, BIRTHDAY, NOW));
        babyList.add(new Baby(BABY_ID, BABY_NAME, BIRTHDAY, NOW));
        Babies babies = new Babies(babyList);

        // when
        List<Baby> notDefaultBabies = babies.getNotDefaultBabies();

        // then
        assertThat(notDefaultBabies.size()).isNotZero();
    }
}