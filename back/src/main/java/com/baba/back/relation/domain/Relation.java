package com.baba.back.relation.domain;

import com.baba.back.baby.domain.Baby;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.member.Name;
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
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Baby baby;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "relation_name"))
    private Name relationName;

    @Enumerated(EnumType.STRING)
    private RelationGroup relationGroup;

    private boolean defaultRelation;

    @Builder
    public Relation(Long id, Member member, Baby baby, String relationName, RelationGroup relationGroup,
                    boolean defaultRelation) {
        this.id = id;
        this.member = member;
        this.baby = baby;
        this.relationName = new Name(relationName);
        this.relationGroup = relationGroup;
        this.defaultRelation = defaultRelation;
    }
}
