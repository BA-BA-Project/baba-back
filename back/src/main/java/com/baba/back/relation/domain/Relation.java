package com.baba.back.relation.domain;

import com.baba.back.common.domain.Name;
import com.baba.back.oauth.domain.member.Member;
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
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "relation_name"))
    private Name relationName;

    @ManyToOne(fetch = FetchType.LAZY)
    private RelationGroup relationGroup;

    @Builder
    public Relation(Member member, String relationName, RelationGroup relationGroup) {
        this.member = member;
        this.relationName = new Name(relationName);
        this.relationGroup = relationGroup;
    }

    public boolean isFamily() {
        return relationGroup.isFamily();
    }
}
