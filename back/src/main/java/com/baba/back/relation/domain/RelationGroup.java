package com.baba.back.relation.domain;

import com.baba.back.baby.domain.Baby;
import com.baba.back.oauth.domain.member.Name;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    private boolean family;

    @Builder
    public RelationGroup(Baby baby, String relationGroupName, boolean family) {
        this.baby = baby;
        this.relationGroupName = new Name(relationGroupName);
        this.family = family;
    }
}
