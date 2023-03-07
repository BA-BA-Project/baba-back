package com.baba.back.oauth.domain.member;

import com.baba.back.oauth.domain.Picker;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
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

    @Enumerated(EnumType.STRING)
    private Color iconColor;

    @Enumerated(EnumType.STRING)
    private IconName iconName;

    public static Icon of(Picker<Color> picker, String iconName) {
        return new Icon(Color.from(picker), IconName.from(iconName));
    }

    public String getIconColor() {
        return this.iconColor.name();
    }

    public String getIconName() {
        return this.iconName.name();
    }
}
