package com.baba.back.oauth.domain.member;

import com.baba.back.oauth.domain.ColorPicker;
import com.baba.back.oauth.exception.IconColorBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class IconColor {
    private static final List<String> COLORS
            = List.of("FFAEBA", "FF8698", "FFE3C8", "FFD2A7", "FFD400", "9ED883", "30BE9B", "81E0D5", "5BD2FF",
            "97BEFF",
            "98A2FF", "BFA1FF");

    private String iconColor;

    private IconColor(String iconColor) {
        validateNull(iconColor);
        validateIconColor(iconColor);
        this.iconColor = iconColor;
    }

    public static IconColor from(ColorPicker<String> colorPicker) {
        String color = colorPicker.pick(COLORS);
        return new IconColor(color);
    }

    private void validateNull(String iconColor) {
        if (Objects.isNull(iconColor)) {
            throw new IconColorBadRequestException("색은 null일 수 없습니다.");
        }
    }

    private void validateIconColor(String iconColor) {
        if (!COLORS.contains(iconColor)) {
            throw new IconColorBadRequestException(iconColor + "는 선택할 수 없는 색입니다.");
        }
    }
}
