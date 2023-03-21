package com.baba.back.content.domain.comment;

import com.baba.back.common.domain.BaseEntity;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
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
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;


    @Embedded
    private Text text;

    @Builder
    public Comment(Member owner, Content content, String text) {
        this.owner = owner;
        this.content = content;
        this.text = new Text(text);
    }
}
