package com.baba.back.oauth.domain.user;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @Embedded
    private Name name;

    @Embedded
    private Introduction introduction ;

    @Embedded
    private Icon icon;

    @Builder
    public User(String id, String name, String introduction, String iconColor, String iconName) {
        this.id = id;
        this.name = new Name(name);
        this.introduction = new Introduction(introduction);
        this.icon = Icon.of(iconColor, iconName);
    }
}
