package com.baba.back.fixture;

import com.baba.back.baby.domain.Baby;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.CardStyle;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.IconName;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.time.LocalDate;

public class DomainFixture {

    public static final Member 멤버1 = Member.builder()
            .id("member1")
            .name("멤버1")
            .introduction("안녕하세요")
            .iconName(IconName.PROFILE_G_1.toString())
            .iconColor(Color.COLOR_1)
            .build();

    public static final Baby 아기1 = Baby.builder()
            .id("baby1")
            .name("아기1")
            .birthday(LocalDate.now())
            .now(LocalDate.now())
            .build();

    public static final Baby 아기2 = Baby.builder()
            .id("baby2")
            .name("아기2")
            .birthday(LocalDate.now())
            .now(LocalDate.now())
            .build();

    public static final Baby 아기3 = Baby.builder()
            .id("baby3")
            .name("아기3")
            .birthday(LocalDate.now())
            .now(LocalDate.now())
            .build();

    public static final RelationGroup 관계그룹1 = RelationGroup.builder()
            .baby(아기1)
            .relationGroupName("가족")
            .family(true)
            .build();

    public static final RelationGroup 관계그룹2 = RelationGroup.builder()
            .baby(아기2)
            .relationGroupName("친구")
            .family(false)
            .build();

    public static final RelationGroup 관계그룹3 = RelationGroup.builder()
            .baby(아기3)
            .relationGroupName("가족")
            .family(true)
            .build();

    public static final Relation 관계1 = Relation.builder()
            .member(멤버1)
            .relationName("아빠")
            .relationGroup(관계그룹1)
            .build();

    public static final Relation 관계2 = Relation.builder()
            .member(멤버1)
            .relationName("아빠친구")
            .relationGroup(관계그룹2)
            .build();

    public static final Relation 관계3 = Relation.builder()
            .member(멤버1)
            .relationName("아빠")
            .relationGroup(관계그룹3)
            .build();

    public static final Content 컨텐츠 = Content.builder()
            .title("제목")
            .contentDate(LocalDate.now())
            .now(LocalDate.now())
            .cardStyle(CardStyle.CARD_BASIC_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .build();

    public static final Like 좋아요 = Like.builder()
            .member(멤버1)
            .content(컨텐츠)
            .build();

    public static final Token 토큰 = Token.builder()
            .member(멤버1)
            .value("토큰")
            .build();
}
