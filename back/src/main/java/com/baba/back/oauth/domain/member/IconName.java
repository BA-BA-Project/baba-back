package com.baba.back.oauth.domain.member;

import com.baba.back.oauth.exception.IconNameBadRequestException;
import java.util.Arrays;

public enum IconName {
    PROFILE_W_1, PROFILE_W_2, PROFILE_W_3, PROFILE_W_4, PROFILE_W_5,
    PROFILE_M_1, PROFILE_M_2, PROFILE_M_3, PROFILE_M_4, PROFILE_M_5, PROFILE_M_6,
    PROFILE_G_1, PROFILE_G_2, PROFILE_G_3, PROFILE_G_4,
    PROFILE_BABY_1;

    public static IconName from(String name) {
        return Arrays.stream(IconName.values())
                .filter(iconName -> iconName.name().equals(name))
                .findAny()
                .orElseThrow(() -> new IconNameBadRequestException(name + " 는 잘못된 IconName 입니다."));
    }
}
