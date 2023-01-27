package com.baba.back.content.domain;

import com.baba.back.content.exception.CardStyleBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class CardStyle {
    private static final List<String> cardStyles = List.of("card_basic_1", "card_sky_1", "card_cloud_1", "card_cloud_2",
            "card_toy_1", "card_candy_1", "card_snowflower_1", "card_snowflower_2", "card_line_1", "card_spring_1",
            "card_check_1", "card_check_2");

    private String cardStyle;

    public CardStyle(String cardStyle) {
        validateNull(cardStyle);
        validateStyle(cardStyle);
        this.cardStyle = cardStyle;
    }

    private void validateNull(String cardStyle) {
        if(Objects.isNull(cardStyle)) {
            throw new CardStyleBadRequestException("cardStyle은 null일 수 없습니다.");
        }
    }

    private void validateStyle(String cardStyle) {
        if(!cardStyles.contains(cardStyle)) {
            throw new CardStyleBadRequestException("{" + cardStyle + "}은 잘못된 cardStyle 입니다.");
        }
    }
}
