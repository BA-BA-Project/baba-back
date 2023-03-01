package com.baba.back.oauth.service;

import com.baba.back.oauth.exception.ExpiredTokenAuthenticationException;
import com.baba.back.oauth.exception.InvalidTokenAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;

public abstract class TokenProvider {

    protected final SecretKey key;
    protected final Long validityInMilliseconds;
    protected final Clock clock;

    protected TokenProvider(String secretKey, Long validityInMilliseconds, Clock clock) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
        this.clock = clock;
    }

    public String createToken(String payload) {
        final LocalDateTime now = LocalDateTime.now(clock);
        final LocalDateTime validity = now.plusSeconds(TimeUnit.MILLISECONDS.toSeconds(validityInMilliseconds));

        return Jwts.builder()
                .setSubject(payload)
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(validity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenAuthenticationException(String.format("%s 은 만료된 토큰입니다. ", token));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenAuthenticationException(String.format("%s 은 유효하지 않은 토큰입니다.", token));
        }
    }

    public String parseToken(String token) {
        return parseTokenBody(token).getSubject();
    }

    protected Claims parseTokenBody(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenAuthenticationException(String.format("%s 는 유효하지 않은 토큰입니다.", token));
        }
    }
}
