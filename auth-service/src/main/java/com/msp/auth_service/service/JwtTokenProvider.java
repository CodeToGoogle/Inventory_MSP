package com.msp.auth_service.service;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key key;
    private final long validityMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long validityMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityMs = validityMs;
    }

    public String createToken(String username, String rolesCsv) {
        long now = System.currentTimeMillis();
        Date expiry = new Date(now + validityMs);

        String token = Jwts.builder()
                .subject(username)
                .claim("roles", rolesCsv)
                .issuedAt(new Date(now))
                .expiration(expiry)
                .signWith(key)
                .compact();
        log.debug("event:TOKEN_CREATED, user:{}, message:JWT token created successfully", username);
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            log.debug("event:TOKEN_VALIDATION_SUCCESS, message:JWT token validated successfully");
            return true;
        } catch (JwtException ex) {
            log.warn("event:TOKEN_VALIDATION_FAILED, message:Invalid JWT token: {}", ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            log.warn("event:TOKEN_VALIDATION_FAILED, message:JWT token compact of handler are invalid: {}", ex.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            String username = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
            log.debug("event:USERNAME_EXTRACTED, username:{}, message:Username extracted from token", username);
            return username;
        } catch (JwtException ex) {
            log.warn("event:USERNAME_EXTRACTION_FAILED, message:Could not get username from token: {}", ex.getMessage());
            return null; // Or throw a specific exception
        } catch (IllegalArgumentException ex) {
            log.warn("event:USERNAME_EXTRACTION_FAILED, message:JWT token compact of handler are invalid: {}", ex.getMessage());
            return null; // Or throw a specific exception
        }
    }

    public long getExpirationMs() {
        return this.validityMs;
    }
}
