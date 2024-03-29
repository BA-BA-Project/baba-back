package com.baba.back.content.domain;

import com.baba.back.common.domain.BaseEntity;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "LIKE_TABLE")
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Content content;

    private boolean deleted;

    @Builder
    public Like(Member member, Content content) {
        this.member = member;
        this.content = content;
        this.deleted = false;
    }

    public void updateDeleted() {
        this.deleted = !this.deleted;
    }
}
