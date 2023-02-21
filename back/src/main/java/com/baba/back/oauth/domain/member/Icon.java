package com.baba.back.oauth.domain.member;

import com.baba.back.oauth.domain.Picker;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Icon {

    @Enumerated
    private IconColor iconColor;

    @Enumerated
    private IconName iconName;

    public static Icon of(Picker<IconColor> picker, String iconName) {
        return new Icon(IconColor.from(picker), IconName.from(iconName));
    }
}
