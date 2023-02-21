package com.baba.back.oauth.domain.token;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Token {

    @Id
    private String id;

    @Embedded
    private TokenInfo tokenInfo;
}
