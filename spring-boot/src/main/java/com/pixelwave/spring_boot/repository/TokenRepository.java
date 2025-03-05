package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String refreshToken);
}
