package com.pixelwave.spring_boot.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    String extractSubject(String jwtToken);

    boolean isTokenValid(String jwtToken);

    String generateToken(UserDetails userDetails);

    String generateOpaqueToken();

    boolean isTokenNotExpired(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims,T> claimResolver);

    Claims extractAllClaims(String token);

    String generateRefreshToken(UserDetails userDetails);
}
