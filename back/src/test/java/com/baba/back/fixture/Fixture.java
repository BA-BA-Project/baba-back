package com.baba.back.fixture;

import com.baba.back.baby.domain.Baby;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.time.LocalDate;

public class Fixture {

    public static final Member 멤버1 = Member.builder()
            .id("member1")
            .name("멤버1")
            .introduction("안녕하세요")
            .iconName("icon1")
            .colorPicker(colors -> "FFAEBA")
            .build();

    public static final Baby 아기1 = Baby.builder()
            .id("baby1")
            .name("아기1")
            .birthday(LocalDate.now())
            .now(LocalDate.now())
            .build();

    public static final Relation 관계1 = Relation.builder()
            .id(1L)
            .member(멤버1)
            .baby(아기1)
            .relationName("아빠")
            .defaultRelation(true)
            .relationGroup(RelationGroup.FAMILY)
            .build();
}
