package com.msp.auth_service.service;


import com.msp.auth_service.dto.LoginRequest;
import com.msp.auth_service.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest req);
    LoginResponse refreshToken(String refreshToken);
    LoginResponse createUserAndLogin(String username, String password, String userType);
}
