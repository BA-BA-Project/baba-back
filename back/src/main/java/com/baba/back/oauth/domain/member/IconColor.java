package com.baba.back.oauth.domain.member;

import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.exception.IconColorBadRequestException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IconColor {
    COLOR_1("FFAEBA"), COLOR_2("FF8698"), COLOR_3("FFE3C8"), COLOR_4("FFD2A7"), COLOR_5("FFD400"), COLOR_6("9ED883"),
    COLOR_7("30BE9B"), COLOR_8("81E0D5"), COLOR_9("5BD2FF"), COLOR_10("97BEFF"), COLOR_11("98A2FF"), COLOR_12("BFA1FF");

    private final String value;

    public static IconColor from(String color) {
        return Arrays.stream(IconColor.values())
                .filter(iconColor -> iconColor.value.equals(color))
                .findAny()
                .orElseThrow(() -> new IconColorBadRequestException(color + " 는 잘못된 IconColor 입니다."));
    }

    public static IconColor from(Picker<IconColor> picker) {
        return picker.pick(List.of(IconColor.values()));
    }
}
