package com.baba.back.oauth.domain.member;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    private String id;

    @Embedded
    private Name name;

    @Embedded
    private Introduction introduction;

    @Embedded
    private Icon icon;

    @Builder
    public Member(String id, String name, String introduction, Color iconColor, String iconName) {
        this.id = id;
        this.name = new Name(name);
        this.introduction = new Introduction(introduction);
        this.icon = Icon.of(iconColor, iconName);
    }

    public String getName() {
        return this.name.getValue();
    }

    public String getIntroduction() {
        return this.introduction.getValue();
    }

    public String getIconColor() {
        return this.icon.getIconColor();
    }

    public String getIconName() {
        return this.icon.getIconName();
    }
}
