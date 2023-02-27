package com.baba.back.oauth.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider extends TokenProvider {

    public static final int DAYS_TO_REFRESH = 1;

    public RefreshTokenProvider(@Value("${security.jwt.token.refresh.secret-key}") String secretKey,
                                @Value("${security.jwt.token.refresh.expire-length}") Long validityInMilliseconds,
                                Clock clock) {
        super(secretKey, validityInMilliseconds, clock);
    }

    public boolean checkExpiration(String token) {
        final LocalDateTime expirationTime = parseTokenExpiration(token);
        final LocalDateTime noMillisecondsTime = LocalDateTime.now(clock).withNano(0);

        return !noMillisecondsTime.isBefore(expirationTime.minusDays(DAYS_TO_REFRESH));
    }

    private LocalDateTime parseTokenExpiration(String token) {
        return LocalDateTime.ofInstant(parseTokenBody(token).getExpiration().toInstant(), ZoneId.systemDefault());
    }
}
