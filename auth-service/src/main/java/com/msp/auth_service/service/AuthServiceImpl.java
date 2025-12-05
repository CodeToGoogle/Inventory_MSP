package com.msp.auth_service.service;

import com.msp.auth_service.dto.LoginRequest;
import com.msp.auth_service.dto.LoginResponse;
import com.msp.auth_service.entity.User;
import com.msp.auth_service.entity.RefreshToken;
import com.msp.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {



    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final TokenService tokenService;
    private final AuditProducer auditProducer; // Kafka publisher

    @Value("${auth.refresh.ttl:604800000}")
    private long refreshTtlMs;

    @Override
    public LoginResponse login(LoginRequest req) {
        log.info("LOGIN_ATTEMPT username={}", req.getUsername());

        Optional<User> ou = userRepository.findByUserName(req.getUsername());
        if (ou.isEmpty()) {
            log.warn("event:USER_LOGIN_FAILED, user:{}, message:Invalid credentials - user not found", req.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        User user = ou.get();

        if (!user.getIsActive()) {
            log.warn("event:USER_LOGIN_FAILED, user:{}, message:User is inactive", req.getUsername());
            throw new RuntimeException("User is inactive");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getEncryptedPassword())) {
            log.warn("event:USER_LOGIN_FAILED, user:{}, message:Invalid credentials - password mismatch", req.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        // FIX: use Integer directly
        List<String> roles = roleService.getRolesForUser(user.getUserID());
        String rolesCsv = String.join(",", roles);

        String accessToken = jwtTokenProvider.createToken(user.getUserName(), rolesCsv);

        // FIX: create refresh token using Integer, not Long
        RefreshToken rt = tokenService.createRefreshToken(user.getUserID(), refreshTtlMs);

        log.info("LOGIN_SUCCESS username={}, userId={}", user.getUserName(), user.getUserID());
        return new LoginResponse(accessToken, rt.getToken(), jwtTokenProvider.getExpirationMs());
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        RefreshToken rt = tokenService.validateAndGet(refreshToken);
        if (rt == null) {
            log.warn("REFRESH_FAILED invalid_token");
            throw new RuntimeException("Invalid refresh token");
        }

        Optional<User> ou = userRepository.findById(rt.getUserID());
        if (ou.isEmpty()) throw new RuntimeException("User not found");

        User user = ou.get();

        List<String> roles = roleService.getRolesForUser(user.getUserID());
        String rolesCsv = String.join(",", roles);

        String accessToken = jwtTokenProvider.createToken(user.getUserName(), rolesCsv);

        // FIX: rotate token using Integer
        tokenService.revokeToken(rt.getToken());
        RefreshToken newRt = tokenService.createRefreshToken(user.getUserID(), refreshTtlMs);

        log.info("REFRESH_SUCCESS userId={}", user.getUserID());
        return new LoginResponse(accessToken, newRt.getToken(), jwtTokenProvider.getExpirationMs());
    }

    @Override
    public LoginResponse createUserAndLogin(String username, String password, String userType) {
        User u = User.builder()
                .userName(username)
                .encryptedPassword(passwordEncoder.encode(password))
                .userType(User.UserType.valueOf(userType))
                .isActive(true)
                .build();

        User saved = userRepository.save(u);

        // FIX: use Integer everywhere
        auditProducer.publishUserCreated(saved.getUserID(), saved.getUserName());

        List<String> rolesCsvList = roleService.getRolesForUser(saved.getUserID());
        String rolesCsv = String.join(",", rolesCsvList);

        String accessToken = jwtTokenProvider.createToken(saved.getUserName(), rolesCsv);

        RefreshToken rt = tokenService.createRefreshToken(saved.getUserID(), refreshTtlMs);

        log.info("USER_CREATED_AND_LOGGED_IN userId={}", saved.getUserID());
        return new LoginResponse(accessToken, rt.getToken(), jwtTokenProvider.getExpirationMs());
    }
}
