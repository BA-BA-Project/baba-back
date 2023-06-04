package com.baba.back.oauth.domain.member;

import com.baba.back.common.Generated;
import com.baba.back.common.domain.BaseEntity;
import com.baba.back.common.domain.Name;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

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

    public void update(String name, String introduction, Color iconColor, String iconName) {
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

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Member member = (Member) o;
        return Objects.equals(this.getId(), member.getId()) && Objects.equals(this.getName(), member.getName())
                && Objects.equals(this.getIntroduction(), member.getIntroduction()) && Objects.equals(this.getIcon(),
                member.getIcon());
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(id, name, introduction, icon);
    }
}
