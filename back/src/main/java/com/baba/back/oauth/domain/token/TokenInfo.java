package com.baba.back.oauth.domain.token;

import com.baba.back.oauth.exception.TokenInfoBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class TokenInfo {
    private String tokenInfo;

    public TokenInfo(String tokenInfo) {
        validateNull(tokenInfo);
        this.tokenInfo = tokenInfo;
    }

    private void validateNull(String tokenInfo) {
        if(Objects.isNull(tokenInfo)) {
            throw new TokenInfoBadRequestException("tokenInfo는 null일 수 없습니다.");
        }
    }
}
