package com.baba.back.content.domain.content;

import com.baba.back.baby.domain.Baby;
import java.time.LocalDate;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ContentTest {

    public static final String TITLE = "타이틀";
    public static final LocalDate CONTENT_DATE = LocalDate.of(2023, 1, 27);
    public static final LocalDate NOW = LocalDate.of(2023, 1, 27);
    public static final LocalDate BIRTHDAY = LocalDate.of(2023, 2, 28);
    public static final String CARD_STYLE = "card_basic_1";
    public static final String BABY_ID = UUID.randomUUID().toString();
    public static final String BABY_NAME = "앙쥬";


    @Test
    void 컨텐츠를_생성한다() {
        // given
        Baby baby = Baby.builder()
                .id(BABY_ID)
                .name(BABY_NAME)
                .birthday(BIRTHDAY)
                .now(NOW)
                .build();

        // when
        Content content = Content.builder()
                .title(TITLE)
                .contentDate(CONTENT_DATE)
                .now(NOW)
                .cardStyle(CARD_STYLE)
                .baby(baby)
                .build();

        // then
        Assertions.assertThat(content.hasEqualBaby(baby)).isTrue();
    }
}