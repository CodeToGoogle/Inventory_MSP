package com.msp.auth_service.service;

import com.msp.auth_service.dto.LoginRequest;
import com.msp.auth_service.dto.LoginResponse;
import com.msp.auth_service.dto.UserRegistrationRequest;
import com.msp.auth_service.entity.User;
import com.msp.auth_service.entity.UserType;
import com.msp.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditProducer auditProducer;

    @Override
    public User register(UserRegistrationRequest registrationRequest) {
        User user = new User();
        user.setUserName(registrationRequest.getUsername());
        user.setEncryptedPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setUserType(UserType.valueOf(registrationRequest.getUserType()));
        user.setIsActive(true); // Explicitly set default value
        User savedUser = userRepository.save(user);
        auditProducer.publishUserCreated(savedUser.getUserId(), savedUser.getUserName());
        return savedUser;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getEncryptedPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // For simplicity, roles are not implemented yet
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserName(), "");
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserName());

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        // For simplicity, roles are not implemented yet
        String newAccessToken = jwtTokenProvider.createAccessToken(username, "");
        return new LoginResponse(newAccessToken, refreshToken);
    }
}
