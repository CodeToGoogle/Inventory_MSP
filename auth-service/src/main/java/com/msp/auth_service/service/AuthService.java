package com.msp.auth_service.service;

import com.msp.auth_service.dto.LoginRequest;
import com.msp.auth_service.dto.LoginResponse;
import com.msp.auth_service.dto.UserRegistrationRequest;
import com.msp.auth_service.entity.User;

public interface AuthService {
    User register(UserRegistrationRequest registrationRequest);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse refreshToken(String refreshToken);
}
