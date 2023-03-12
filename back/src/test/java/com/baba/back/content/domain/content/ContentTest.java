package com.baba.back.content.domain.content;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.common.domain.Name;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ContentTest {

    public static final String TITLE = "타이틀";
    public static final LocalDate CONTENT_DATE = LocalDate.of(2023, 1, 27);
    public static final LocalDate NOW = LocalDate.of(2023, 1, 27);
    public static final String CARD_STYLE = CardStyle.CARD_BASIC_1.toString();
    public static final String IMAGE_SOURCE = "1234";


    @Test
    void 컨텐츠를_생성할_수_있다() {
        // when & then
        Assertions.assertThatCode(() -> Content.builder()
                        .title(TITLE)
                        .contentDate(CONTENT_DATE)
                        .now(NOW)
                        .cardStyle(CARD_STYLE)
                        .baby(아기1)
                        .relation(new Name("엄마"))
                        .owner(멤버1)
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
                .owner(멤버1)
                .relation(new Name("엄마"))
                .build();

        // when
        content.updateURL(IMAGE_SOURCE);

        // then
        assertThat(content)
                .extracting(Content::getImageSource)
                .isEqualTo(IMAGE_SOURCE);
    }

    @Test
    void 컨텐츠에_저장된_정보를_조회할_수_있다() {
        // given
        final Name relationName = new Name("엄마");
        Content content = Content.builder()
                .title(TITLE)
                .contentDate(CONTENT_DATE)
                .now(NOW)
                .cardStyle(CARD_STYLE)
                .baby(아기1)
                .owner(멤버1)
                .relation(relationName)
                .build();

        // when & then
        assertAll(
                () -> assertThat(content.getTitle()).isEqualTo(TITLE),
                () -> assertThat(content.getContentDate()).isEqualTo(CONTENT_DATE),
                () -> assertThat(content.getCardStyle()).isEqualTo(CARD_STYLE),
                () -> assertThat(content.getOwnerName()).isEqualTo(멤버1.getName()),
                () -> assertThat(content.getRelationName()).isEqualTo(relationName.getValue()),
                () -> assertThat(content.getImageSource()).isEmpty()
        );
    }
}
