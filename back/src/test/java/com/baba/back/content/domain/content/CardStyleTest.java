package com.baba.back.content.domain.content;

import com.baba.back.content.exception.CardStyleBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CardStyleTest {

    @ParameterizedTest
    @ValueSource(strings = {"null", "card_invalid_2", "card_zzz_2"})
    void 유효하지_않은_카드_스타일이면_예외를_던진다(String cardStyle) {
        Assertions.assertThatThrownBy(() -> CardStyle.from(cardStyle))
                .isInstanceOf(CardStyleBadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CARD_BASIC_1", "CARD_SKY_1"})
    void 유효한_카드_스타일이어야_한다(String cardStyle) {
        Assertions.assertThatCode(() -> CardStyle.from(cardStyle))
                .doesNotThrowAnyException();
    }
}
