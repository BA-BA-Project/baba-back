package com.baba.back.content.domain.content;

import static com.baba.back.fixture.DomainFixture.아기1;

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
    public static final String IMAGE_SOURCE = "1234";


    @Test
    void 컨텐츠를_생성해야_한다() {
        // given
        Baby baby = Baby.builder()
                .id(BABY_ID)
                .name(BABY_NAME)
                .birthday(BIRTHDAY)
                .now(NOW)
                .build();

        // when & then
        Assertions.assertThatCode(() -> Content.builder()
                        .title(TITLE)
                        .contentDate(CONTENT_DATE)
                        .now(NOW)
                        .cardStyle(CARD_STYLE)
                        .baby(baby)
                        .build())
                .doesNotThrowAnyException();
    }

    @Test
    void 이미지가_저장된_url을_변경한다() {
        // given
        Content content = Content.builder()
                .title(TITLE)
                .contentDate(CONTENT_DATE)
                .now(NOW)
                .cardStyle(CARD_STYLE)
                .baby(아기1)
                .build();

        // when
        content.updateURL(IMAGE_SOURCE);

        // then
        Assertions.assertThat(content.getImageSource()).isEqualTo(IMAGE_SOURCE);
    }
}