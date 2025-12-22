package com.msp.auth_service.service;

import com.msp.auth_service.entity.RefreshToken;

public interface TokenService {
    RefreshToken createRefreshToken(Integer userId, long ttlMs);
    boolean revokeToken(String token);
    RefreshToken validateAndGet(String token);
    void revokeAllForUser(Integer userId);
}
