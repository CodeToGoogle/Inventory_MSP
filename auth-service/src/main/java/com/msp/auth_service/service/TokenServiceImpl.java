package com.msp.auth_service.service;

import com.msp.auth_service.entity.RefreshToken;
import com.msp.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken createRefreshToken(Integer userId, long ttlMs) {
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID();
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(ttlMs / 1000);
        RefreshToken rt = RefreshToken.builder()
                .token(token)
                .userID(userId)
                .expiryDate(expiry)
                .revoked(false)
                .build();
        return refreshTokenRepository.save(rt);
    }

    @Override
    public boolean revokeToken(String token) {
        Optional<RefreshToken> r = refreshTokenRepository.findByToken(token);
        if (r.isEmpty()) return false;
        RefreshToken rt = r.get();
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);
        return true;
    }

    @Override
    public RefreshToken validateAndGet(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(t -> !t.getRevoked())
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(null);
    }

    @Override
    public void revokeAllForUser(Integer userId) {
        refreshTokenRepository.deleteByUserID(userId);
    }
}
