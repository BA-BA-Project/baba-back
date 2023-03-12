package com.baba.back.content.domain.content;

import com.baba.back.baby.domain.Baby;
import com.baba.back.common.domain.Name;
import com.baba.back.oauth.domain.member.Member;
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
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Embedded
    private ContentDate contentDate;

    @Enumerated(EnumType.STRING)
    private CardStyle cardStyle;

    @Embedded
    private ImageSource imageSource;

    @ManyToOne(fetch = FetchType.LAZY)
    private Baby baby;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "relation"))
    private Name relation;

    @Builder
    public Content(String title, LocalDate contentDate, LocalDate now, String cardStyle, Baby baby, Member owner, Name relation) {
        this.title = new Title(title);
        this.contentDate = ContentDate.of(contentDate, now, baby.getBirthday());
        this.cardStyle = CardStyle.from(cardStyle);
        this.imageSource = new ImageSource("");
        this.baby = baby;
        this.owner = owner;
        this.relation = relation;
    }

    public void updateURL(String imageSource) {
        this.imageSource = new ImageSource(imageSource);
    }
}
