package com.baba.back.oauth.domain.token;

import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.TokenBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Token {

    @Id
    private String id;

    @OneToOne
    @MapsId
    private Member member;

    @Column(name = "token")
    private String value;

    @Builder
    public Token(Member member, String value) {
        validateNull(value);

        this.member = member;
        this.value = value;
    }

    private void validateNull(String token) {
        if (Objects.isNull(token)) {
            throw new TokenBadRequestException("token은 null일 수 없습니다.");
        }
    }

    public void update(String value) {
        this.value = value;
    }
}
