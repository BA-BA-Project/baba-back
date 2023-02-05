package com.baba.back.content.domain.content;

import com.baba.back.content.exception.CardStyleBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CardStyleTest {

    @ParameterizedTest
    @ValueSource(strings = {"null", "card_basic_2", "card_sky_2"})
    void 유효하지_않은_카드_스타일이면_예외를_던진다(String cardStyle) {
        Assertions.assertThatThrownBy(() -> new CardStyle(cardStyle))
                .isInstanceOf(CardStyleBadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"card_basic_1", "card_sky_1"})
    void 유효한_카드_스타일이어야_한다(String cardStyle) {
        Assertions.assertThatCode(() -> new CardStyle(cardStyle))
                .doesNotThrowAnyException();
    }
}