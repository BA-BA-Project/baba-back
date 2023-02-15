package com.baba.back.oauth.service;

import com.baba.back.oauth.exception.ExpiredTokenAuthenticationException;
import com.baba.back.oauth.exception.InvalidTokenAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

public abstract class TokenProvider {

    private final SecretKey key;
    private final Long validityInMilliseconds;

    public TokenProvider(String secretKey, Long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(String payload) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(payload)
                .setIssuedAt(now)
                .setExpiration(validity)
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenAuthenticationException(String.format("%s 는 유효하지 않은 토큰입니다.", token));
        }
    }
}
