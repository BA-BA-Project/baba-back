package com.baba.back.fixture;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.invitation.Code;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.comment.Comment;
import com.baba.back.content.domain.comment.Tag;
import com.baba.back.content.domain.content.CardStyle;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.IconName;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DomainFixture {
    public static final LocalDate nowDate = LocalDate.of(2023, 3, 12);
    public static final LocalDateTime nowDateTime = LocalDateTime.now();
    public static final InvitationCode 초대코드정보1 = InvitationCode.builder()
            .code(Code.from((length, chars) -> "AAAAAA"))
            .relationName("이모")
            .now(nowDateTime)
            .build();
    public static final InvitationCode 초대코드정보2 = InvitationCode.builder()
            .code(Code.from((length, chars) -> "AAAAAA"))
            .relationName("고모")
            .now(nowDateTime)
            .build();

    public static final Member 멤버1 = Member.builder()
            .id("member1")
            .name("멤버1")
            .introduction("안녕하세요")
            .iconName(IconName.PROFILE_G_1.toString())
            .iconColor(Color.COLOR_1)
            .build();
    public static final Member 멤버2 = Member.builder()
            .id("member2")
            .name("멤버2")
            .introduction("안녕하세요")
            .iconName(IconName.PROFILE_G_1.toString())
            .iconColor(Color.COLOR_1)
            .build();

    public static final Member 멤버3 = Member.builder()
            .id("member3")
            .name("멤버3")
            .introduction("안녕하세요")
            .iconName(IconName.PROFILE_G_1.toString())
            .iconColor(Color.COLOR_1)
            .build();
    public static final Member 멤버4 = Member.builder()
            .id("member4")
            .name("멤버4")
            .introduction("안녕하세요")
            .iconName(IconName.PROFILE_G_1.toString())
            .iconColor(Color.COLOR_1)
            .build();;
    public static final Token 토큰1 = Token.builder()
            .member(멤버1)
            .value("토큰1")
            .build();

    public static final Baby 아기1 = Baby.builder()
            .id("baby1")
            .name("아기1")
            .birthday(nowDate)
            .now(nowDate)
            .build();
    public static final RelationGroup 관계그룹10 = RelationGroup.builder()
            .baby(아기1)
            .relationGroupName("가족")
            .family(true)
            .groupColor(Color.COLOR_1)
            .build();
    public static final Relation 관계10 = Relation.builder()
            .member(멤버1)
            .relationName("아빠")
            .relationGroup(관계그룹10)
            .build();
    public static final Content 컨텐츠10 = Content.builder()
            .title("제목1")
            .contentDate(nowDate)
            .now(nowDate)
            .cardStyle(CardStyle.CARD_BASIC_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계10.getRelationName())
            .build();
    public static final Like 좋아요10 = Like.builder()
            .member(멤버1)
            .content(컨텐츠10)
            .build();

    public static final Like 좋아요11 = Like.builder()
            .member(멤버2)
            .content(컨텐츠10)
            .build();

    public static final Like 좋아요12 = Like.builder()
            .member(멤버3)
            .content(컨텐츠10)
            .build();

    public static final Like 좋아요13 = Like.builder()
            .member(멤버4)
            .content(컨텐츠10)
            .build();

    public static final Comment 댓글10 = Comment.builder()
            .owner(멤버1)
            .content(컨텐츠10)
            .text("댓글!")
            .build();

    public static final Tag 태그10 = Tag.builder()
            .tagMember(멤버1)
            .comment(댓글10)
            .build();
    public static final Content 컨텐츠11 = Content.builder()
            .title("제목2")
            .contentDate(nowDate.minusDays(1))
            .now(nowDate)
            .cardStyle(CardStyle.CARD_CANDY_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계10.getRelationName())
            .build();
    public static final Content 컨텐츠12 = Content.builder()
            .title("제목3")
            .contentDate(nowDate.minusDays(2))
            .now(nowDate)
            .cardStyle(CardStyle.CARD_CLOUD_2.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계10.getRelationName())
            .build();
    public static final Content 컨텐츠13 = Content.builder()
            .title("제목4")
            .contentDate(nowDate.minusMonths(1))
            .now(nowDate)
            .cardStyle(CardStyle.CARD_CHECK_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계10.getRelationName())
            .build();
    public static final Relation 관계11 = Relation.builder()
            .member(멤버2)
            .relationName("엄마")
            .relationGroup(관계그룹10)
            .build();
    public static final RelationGroup 관계그룹11 = RelationGroup.builder()
            .baby(아기1)
            .relationGroupName("외가")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();
    public static final Relation 관계12 = Relation.builder()
            .member(멤버3)
            .relationName("이모")
            .relationGroup(관계그룹11)
            .build();
    public static final Invitation 초대10 = Invitation.builder()
            .invitationCode(초대코드정보1)
            .relationGroup(관계그룹11)
            .build();

    public static final Baby 아기2 = Baby.builder()
            .id("baby2")
            .name("아기2")
            .birthday(nowDate)
            .now(nowDate)
            .build();
    public static final RelationGroup 관계그룹20 = RelationGroup.builder()
            .baby(아기2)
            .relationGroupName("가족")
            .family(true)
            .groupColor(Color.COLOR_1)
            .build();
    public static final Relation 관계20 = Relation.builder()
            .member(멤버1)
            .relationName("아빠")
            .relationGroup(관계그룹20)
            .build();
    public static final Content 컨텐츠20 = Content.builder()
            .title("제목2")
            .contentDate(nowDate)
            .now(nowDate)
            .cardStyle(CardStyle.CARD_BASIC_1.toString())
            .baby(아기2)
            .owner(멤버1)
            .relation(관계20.getRelationName())
            .build();
    public static final Comment 댓글20 = Comment.builder()
            .owner(멤버1)
            .content(컨텐츠20)
            .text("댓글!")
            .build();
    public static final Comment 댓글21 = Comment.builder()
            .owner(멤버2)
            .content(컨텐츠20)
            .text("댓글!")
            .build();
    public static final Comment 댓글22 = Comment.builder()
            .owner(멤버3)
            .content(컨텐츠20)
            .text("댓글!")
            .build();

    public static final Comment 댓글23 = Comment.builder()
            .owner(멤버1)
            .content(컨텐츠20)
            .text("댓글!")
            .build();

    public static final Tag 태그20 = Tag.builder()
            .tagMember(멤버3)
            .comment(댓글23)
            .build();

    public static final Like 좋아요20 = Like.builder()
            .member(멤버1)
            .content(컨텐츠20)
            .build();
    public static final Like 좋아요21 = Like.builder()
            .member(멤버2)
            .content(컨텐츠20)
            .build();
    public static final Like 좋아요22 = Like.builder()
            .member(멤버3)
            .content(컨텐츠20)
            .build();
    public static final RelationGroup 관계그룹21 = RelationGroup.builder()
            .baby(아기2)
            .relationGroupName("외가")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();
    public static final Relation 관계21 = Relation.builder()
            .member(멤버2)
            .relationName("사촌 형")
            .relationGroup(관계그룹21)
            .build();
    public static final Invitation 초대20 = Invitation.builder()
            .invitationCode(초대코드정보1)
            .relationGroup(관계그룹21)
            .build();
    public static final RelationGroup 관계그룹22 = RelationGroup.builder()
            .baby(아기2)
            .relationGroupName("친가")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();
    public static final Relation 관계22 = Relation.builder()
            .member(멤버3)
            .relationName("친척 형")
            .relationGroup(관계그룹22)
            .build();

    public static final Invitation 초대21 = Invitation.builder()
            .invitationCode(초대코드정보2)
            .relationGroup(관계그룹22)
            .build();

    public static final Baby 아기3 = Baby.builder()
            .id("baby3")
            .name("아기3")
            .birthday(nowDate)
            .now(nowDate)
            .build();
    public static final RelationGroup 관계그룹30 = RelationGroup.builder()
            .baby(아기3)
            .relationGroupName("친구")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();
    public static final Relation 관계30 = Relation.builder()
            .member(멤버1)
            .relationName("아빠친구")
            .relationGroup(관계그룹30)
            .build();
    public static final Baby 아기4 = Baby.builder()
            .id("baby4")
            .name("아기4")
            .birthday(nowDate)
            .now(nowDate)
            .build();
    public static final RelationGroup 관계그룹40 = RelationGroup.builder()
            .baby(아기4)
            .relationGroupName("외가")
            .family(false)
            .groupColor(Color.COLOR_1)
            .build();
    public static final Relation 관계40 = Relation.builder()
            .member(멤버1)
            .relationName("외삼촌")
            .relationGroup(관계그룹40)
            .build();

    public static final Content 수정용_컨텐츠10 = Content.builder()
            .title("제목1")
            .contentDate(nowDate)
            .now(nowDate)
            .cardStyle(CardStyle.CARD_BASIC_1.toString())
            .baby(아기1)
            .owner(멤버1)
            .relation(관계10.getRelationName())
            .build();
}
