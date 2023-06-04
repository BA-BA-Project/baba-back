package com.baba.back.content.domain.content;

import com.baba.back.content.exception.CardStyleBadRequestException;
import java.util.Arrays;

public enum CardStyle {
    CARD_BASIC_1, CARD_SKY_1, CARD_CLOUD_1, CARD_CLOUD_2, CARD_TOY_1, CARD_CANDY_1, CARD_SNOWFLOWER_1, CARD_SNOWFLOWER_2,
    CARD_LINE_1, CARD_SPRING_1, CARD_CHECK_1, CARD_CHECK_2;

    public static CardStyle from(String cardStyle) {
        return Arrays.stream(CardStyle.values())
                .filter(style -> style.name().equals(cardStyle))
                .findAny()
                .orElseThrow(() -> new CardStyleBadRequestException("{" + cardStyle + "}은 잘못된 cardStyle 입니다."));
    }
}
