package com.baba.back.content.domain.content;

import com.baba.back.baby.domain.Baby;
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

    @Builder
    public Content(String title, LocalDate contentDate, LocalDate now, String cardStyle, Baby baby) {
        this.title = new Title(title);
        this.contentDate = ContentDate.of(contentDate, now, baby.getBirthday());
        this.cardStyle = new CardStyle(cardStyle);
        this.imageSource = new ImageSource("");
        this.baby = baby;
    }

    public Boolean hasEqualBaby(Baby baby) {
        return this.baby.getId().equals(baby.getId());
    }

    public void updateURL(String imageSource) {
        this.imageSource = new ImageSource(imageSource);
    }
}
