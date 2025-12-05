package com.msp.auth_service.service;

import com.msp.auth_service.dto.LoginRequest;
import com.msp.auth_service.dto.LoginResponse;
import com.msp.auth_service.entity.User;
import com.msp.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest req) {
        String username = req.getUsername();
        // In a real application, traceId, spanId, and user context would be managed by a tracing library
        // and potentially added to the MDC (Mapped Diagnostic Context) for automatic inclusion in logs.
        // For this example, we'll log directly.

        Optional<User> ou = userRepository.findByUserName(username);
        if (ou.isEmpty()) {
            log.warn("event:USER_LOGIN_FAILED, user:{}, message:Invalid credentials - user not found", username);
            throw new RuntimeException("Invalid credentials");
        }

        User user = ou.get();

        if (!user.getIsActive()) {
            log.warn("event:USER_LOGIN_FAILED, user:{}, message:User is inactive", username);
            throw new RuntimeException("User is inactive");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getEncryptedPassword())) {
            log.warn("event:USER_LOGIN_FAILED, user:{}, message:Invalid credentials - password mismatch", username);
            throw new RuntimeException("Invalid credentials");
        }

        // TODO: fetch roles from Role and UserRole tables
        String roles = "USER";

        String accessToken = jwtTokenProvider.createToken(user.getUserName(), roles);
        String refreshToken = accessToken; // Placeholder

        log.info("event:USER_LOGIN_SUCCESS, user:{}, message:User logged in successfully", username);
        return new LoginResponse(accessToken, refreshToken, jwtTokenProvider.getExpirationMs());
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // TODO: Implement real refresh token logic
        throw new UnsupportedOperationException("Refresh token not implemented yet");
    }
}
