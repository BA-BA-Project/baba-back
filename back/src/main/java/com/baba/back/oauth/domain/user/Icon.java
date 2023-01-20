package com.baba.back.oauth.domain.user;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Icon {

    @Embedded
    private IconColor iconColor;

    @Embedded
    private IconName iconName;

    public static Icon of(String iconColor, String iconName) {
        return new Icon(new IconColor(iconColor), new IconName(iconName));
    }
}
