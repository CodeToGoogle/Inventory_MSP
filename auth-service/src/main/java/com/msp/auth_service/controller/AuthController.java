package com.msp.auth_service.controller;

import com.msp.auth_service.dto.LoginRequest;
import com.msp.auth_service.dto.LoginResponse;
import com.msp.auth_service.dto.RefreshRequest;
import com.msp.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest req) {
        // Basic placeholder - in prod implement refresh token flow
        return ResponseEntity.badRequest().build();
    }
}

