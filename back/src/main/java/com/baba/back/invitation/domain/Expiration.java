package com.baba.back.invitation.domain;

import com.baba.back.invitation.exception.ExpirationBadReqeustException;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Expiration {
    private LocalDateTime value;

    private Expiration(LocalDateTime value) {
        this.value = value;
    }

    public static Expiration of(LocalDateTime now, LocalDateTime value) {
        validateNull(now, value);
        validateExpiration(now, value);
        return new Expiration(value);
    }

    private static void validateNull(LocalDateTime now, LocalDateTime value) {
        if(Objects.isNull(now) || Objects.isNull(value)) {
            throw new ExpirationBadReqeustException("만료시간은 null일 수 없습니다.");
        }
    }

    private static void validateExpiration(LocalDateTime now, LocalDateTime value) {
        if(now.isAfter(value)) {
            throw new ExpirationBadReqeustException("만료시간은 현재보다 미래여야 합니다.");
        }
    }
}
