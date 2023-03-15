package com.baba.back.fixture;

import com.baba.back.baby.domain.Baby;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.CardStyle;
import com.baba.back.content.domain.content.Content;
import com.baba.back.invitation.domain.Invitation;
import com.baba.back.invitation.domain.InvitationCode;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.IconName;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DomainFixture {
    public static final LocalDate nowDate = LocalDate.of(2023, 3, 12);
    public static final LocalDateTime nowDateTime = LocalDateTime.of(nowDate, LocalTime.NOON);

    public static final Member 멤버1 = Member.builder()
            .id("member1")
            .name("멤버1")
            .introduction("안녕하세요")
            .iconName(IconName.PROFILE_G_1.toString())
            .iconColor(Color.COLOR_1)
            .build();

    public static final Token 토큰 = Token.builder()
            .member(멤버1)
            .value("토큰")
            .build();

    public static final Baby 아기1 = Baby.builder()
            .id("baby1")
            .name("아기1")
            .birthday(nowDate)
            .now(nowDate)
            .build();

    public static final RelationGroup 관계그룹1 = RelationGroup.builder()
            .baby(아기1)
            .relationGroupName("가족")
            .family(true)
            .groupColor(Color.COLOR_1)
            .build();

    public static final Relation 관계1 = Relation.builder()
            .member(멤버1)
            .relationName("아빠")
            .relationGroup(관계그룹1)
            .build();

    public static final Content 컨텐츠1 = Content.builder()
            .title("제목1")
            .contentDate(nowDate)
            .now(nowDate)
            .cardStyle(CardStyle.CARD_BASIC_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계1.getRelationName())
            .build();

    public static final Like 좋아요 = Like.builder()
            .member(멤버1)
            .content(컨텐츠1)
            .build();

    public static final Content 컨텐츠2 = Content.builder()
            .title("제목2")
            .contentDate(nowDate.minusDays(1))
            .now(nowDate)
            .cardStyle(CardStyle.CARD_CANDY_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계1.getRelationName())
            .build();

    public static final Content 컨텐츠3 = Content.builder()
            .title("제목3")
            .contentDate(nowDate.minusDays(2))
            .now(nowDate)
            .cardStyle(CardStyle.CARD_CLOUD_2.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계1.getRelationName())
            .build();

    public static final Content 컨텐츠4 = Content.builder()
            .title("제목4")
            .contentDate(nowDate.minusMonths(1))
            .now(nowDate)
            .cardStyle(CardStyle.CARD_CHECK_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계1.getRelationName())
            .build();

    public static final Baby 아기2 = Baby.builder()
            .id("baby2")
            .name("아기2")
            .birthday(nowDate)
            .now(nowDate)
            .build();

    public static final RelationGroup 관계그룹2 = RelationGroup.builder()
            .baby(아기2)
            .relationGroupName("가족")
            .family(true)
            .groupColor(Color.COLOR_1)
            .build();

    public static final Relation 관계2 = Relation.builder()
            .member(멤버1)
            .relationName("아빠")
            .relationGroup(관계그룹2)
            .build();

    public static final Baby 아기3 = Baby.builder()
            .id("baby3")
            .name("아기3")
            .birthday(nowDate)
            .now(nowDate)
            .build();

    public static final RelationGroup 관계그룹3 = RelationGroup.builder()
            .baby(아기3)
            .relationGroupName("친구")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();

    public static final Relation 관계3 = Relation.builder()
            .member(멤버1)
            .relationName("아빠친구")
            .relationGroup(관계그룹3)
            .build();

    public static final Baby 아기4 = Baby.builder()
            .id("baby4")
            .name("아기4")
            .birthday(nowDate)
            .now(nowDate)
            .build();

    public static final RelationGroup 관계그룹4 = RelationGroup.builder()
            .baby(아기4)
            .relationGroupName("외가")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();

    public static final Relation 관계4 = Relation.builder()
            .member(멤버1)
            .relationName("외삼촌")
            .relationGroup(관계그룹4)
            .build();

    public static final RelationGroup 관계그룹5 = RelationGroup.builder()
            .baby(아기1)
            .relationGroupName("외가")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();

    public static final RelationGroup 관계그룹6 = RelationGroup.builder()
            .baby(아기2)
            .relationGroupName("외가")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();

    public static final InvitationCode 초대코드정보 = InvitationCode.builder()
            .inviteCode("AAAAAA")
            .relationName("이모")
            .now(nowDateTime)
            .build();

    public static final Invitation 초대1 = Invitation.builder()
            .invitationCode(초대코드정보)
            .relationGroup(관계그룹5)
            .build();

    public static final Invitation 초대2 = Invitation.builder()
            .invitationCode(초대코드정보)
            .relationGroup(관계그룹6)
            .build();
}
