package com.baba.back.relation.domain;

import com.baba.back.baby.domain.Baby;
import com.baba.back.common.domain.BaseEntity;
import com.baba.back.common.domain.Name;
import com.baba.back.oauth.domain.member.Color;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RelationGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    public String getRelationGroupName() {
        return this.relationGroupName.getValue();
    }

    public void updateRelationGroupName(String relationGroupName) {
        this.relationGroupName = new Name(relationGroupName);
    }

    public boolean canShare(RelationGroup other) {

        final boolean isSameBaby = this.baby.equals(other.baby);
        final boolean canShareGroup = this.family || other.family || this.equals(other);

        return isSameBaby && canShareGroup;
    }

    public boolean hasEqualGroupName(String relationGroupName) {
        return getRelationGroupName().equals(relationGroupName);
    }
}
