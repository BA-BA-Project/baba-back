package com.baba.back.oauth.domain.token;

import com.baba.back.oauth.exception.TokenBadRequestException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    private String value;

    @Builder
    public Token(String id, String token) {
        validateNull(token);

        this.id = id;
        this.value = token;
    }

    private void validateNull(String token) {
        if (Objects.isNull(token)) {
            throw new TokenBadRequestException("token은 null일 수 없습니다.");
        }
    }

    public boolean hasEqualToken(String token) {
        return this.value.equals(token);
    }
}
