package com.baba.back.oauth.domain.token;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Token {

    @Id
    private String id;

    @NotNull
    private String token;

    @Builder
    public Token(String id, String token) {
        this.id = id;
        this.token = token;
    }
}
