package com.baba.back.fixture;

import com.baba.back.baby.domain.Baby;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.JoinedMember;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.time.LocalDate;

public class DomainFixture {

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

    public static final Baby 아기2 = Baby.builder()
            .id("baby2")
            .name("아기2")
            .birthday(LocalDate.now())
            .now(LocalDate.now())
            .build();

    public static final Relation 관계1 = Relation.builder()
            .member(멤버1)
            .baby(아기1)
            .relationName("아빠")
            .defaultRelation(true)
            .relationGroup(RelationGroup.FAMILY)
            .build();

    public static final Relation 관계2 = Relation.builder()
            .member(멤버1)
            .baby(아기1)
            .relationName("아빠")
            .defaultRelation(true)
            .relationGroup(RelationGroup.FRIENDS)
            .build();

    public static final Content 컨텐츠 = Content.builder()
            .title("제목")
            .contentDate(LocalDate.now())
            .now(LocalDate.now())
            .cardStyle("card_basic_1")
            .baby(아기1)
            .owner(멤버1)
            .build();

    public static final Like 좋아요 = Like.builder()
            .member(멤버1)
            .content(컨텐츠)
            .build();

    public static final JoinedMember 회원가입_안한_유저1 = new JoinedMember(멤버1.getId(), false);
    public static final JoinedMember 이미_회원가입한_유저1 = new JoinedMember(멤버1.getId(), true);
}
