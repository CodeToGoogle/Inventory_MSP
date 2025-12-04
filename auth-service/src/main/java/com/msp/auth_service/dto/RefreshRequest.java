package com.msp.auth_service.dto;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RefreshRequest {
    private String refreshToken;
}

