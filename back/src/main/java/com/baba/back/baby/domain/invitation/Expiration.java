package com.baba.back.baby.domain.invitation;

import com.baba.back.baby.exception.ExpirationBadReqeustException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Expiration {
    public static final int EXPIRATION_DAYS = 10;

    @Column(name = "expiration")
    private LocalDateTime value;

    private Expiration(LocalDateTime value) {
        this.value = value;
    }

    public static Expiration from(LocalDateTime now) {
        validateNull(now);
        return new Expiration(now.plusDays(EXPIRATION_DAYS));
    }

    private static void validateNull(LocalDateTime now) {
        if(Objects.isNull(now)) {
            throw new ExpirationBadReqeustException("현재시각은 null일 수 없습니다.");
        }
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(value);
    }
}
