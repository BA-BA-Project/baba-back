package com.baba.back.relation.domain;

import com.baba.back.baby.domain.Baby;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.common.domain.Name;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RelationGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Baby baby;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "relation_group_name"))
    private Name relationGroupName;

    @Enumerated(EnumType.STRING)
    private Color groupColor;

    private boolean family;

    @Builder
    public RelationGroup(Baby baby, String relationGroupName, Color groupColor, boolean family) {
        this.baby = baby;
        this.relationGroupName = new Name(relationGroupName);
        this.groupColor = groupColor;
        this.family = family;
    }

    public String getBabyId() {
        return this.baby.getId();
    }

    public String getBabyName() {
        return this.baby.getName();
    }

    public String getGroupColor() {
        return this.groupColor.getValue();
    }
}
