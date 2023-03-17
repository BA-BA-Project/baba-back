package com.baba.back.content.domain.comment;

import com.baba.back.common.domain.BaseEntity;
import com.baba.back.oauth.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @OneToOne(fetch = FetchType.LAZY)
    private Member tagMember;

    @Builder
    public Tag(Comment comment, Member tagMember) {
        this.comment = comment;
        this.tagMember = tagMember;
    }
}
