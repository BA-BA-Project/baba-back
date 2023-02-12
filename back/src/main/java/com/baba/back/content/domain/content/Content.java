package com.baba.back.content.domain.content;

import com.baba.back.baby.domain.Baby;
import com.baba.back.oauth.domain.member.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @Embedded
    private CardStyle cardStyle;

    @Embedded
    private ImageSource imageSource;

    @ManyToOne(fetch = FetchType.LAZY)
    private Baby baby;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Content(String title, LocalDate contentDate, LocalDate now, String cardStyle, Baby baby, Member member) {
        this.title = new Title(title);
        this.contentDate = ContentDate.of(contentDate, now, baby.getBirthday());
        this.cardStyle = new CardStyle(cardStyle);
        this.imageSource = new ImageSource("");
        this.baby = baby;
        this.member = member;
    }

    public void updateURL(String imageSource) {
        this.imageSource = new ImageSource(imageSource);
    }
}
