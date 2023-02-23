package com.baba.back.oauth.repository;

import com.baba.back.oauth.domain.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
