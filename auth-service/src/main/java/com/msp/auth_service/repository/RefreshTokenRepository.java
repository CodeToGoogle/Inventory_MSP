package com.msp.auth_service.repository;

import com.msp.auth_service.entity.RefreshToken;
import com.msp.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
    void deleteByUser_UserId(Integer userId);
}
