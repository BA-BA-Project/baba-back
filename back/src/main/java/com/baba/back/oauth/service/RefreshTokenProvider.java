package com.baba.back.oauth.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider extends TokenProvider {

    public static final long MILLIS_TO_REFRESH = 86400000L;

    public RefreshTokenProvider(@Value("${security.jwt.token.refresh.secret-key}") String secretKey,
                                @Value("${security.jwt.token.refresh.expire-length}") Long validityInMilliseconds,
                                Clock clock) {
        super(secretKey, validityInMilliseconds, clock);
    }

    public boolean isExpiringSoon(String token) {
        final LocalDateTime expirationTime = parseTokenExpiration(token);
        final LocalDateTime now = LocalDateTime.now(clock).withNano(0);
        final long between = ChronoUnit.MILLIS.between(now, expirationTime);

        return between <= MILLIS_TO_REFRESH;
    }

    private LocalDateTime parseTokenExpiration(String token) {
        return LocalDateTime.ofInstant(parseTokenBody(token).getExpiration().toInstant(), clock.getZone());
    }
}
