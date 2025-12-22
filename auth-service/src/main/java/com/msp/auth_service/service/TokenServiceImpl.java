package com.msp.auth_service.service;

import com.msp.auth_service.entity.RefreshToken;
import com.msp.auth_service.entity.User;
import com.msp.auth_service.repository.RefreshTokenRepository;
import com.msp.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public RefreshToken createRefreshToken(Integer userId, long ttlMs) {
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID();
        Instant expiry = Instant.now().plusMillis(ttlMs);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken rt = RefreshToken.builder()
                .token(token)
                .user(user)
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
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiryDate().isAfter(Instant.now()))
                .orElse(null);
    }

    @Override
    public void revokeAllForUser(Integer userId) {
        refreshTokenRepository.deleteByUser_UserId(userId);
    }
}
