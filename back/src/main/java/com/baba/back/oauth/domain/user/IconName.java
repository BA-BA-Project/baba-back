package com.baba.back.oauth.domain.user;

import com.baba.back.oauth.exception.IconNameBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class IconName {

    private static final Set<String> ICONS = Set.of("icon1", "icon2");

    private String iconName;

    public IconName(String iconName) {
        validateNull(iconName);
        validateIconName(iconName);
        this.iconName = iconName;
    }

    private void validateNull(String iconName) {
        if (Objects.isNull(iconName)) {
            throw new IconNameBadRequestException("아이콘은 null일 수 없습니다.");
        }
    }

    private void validateIconName(String iconName) {
        if (!ICONS.contains(iconName)) {
            throw new IconNameBadRequestException(iconName + "에 해당하는 아이콘이 존재하지 않습니다.");
        }
    }
}
