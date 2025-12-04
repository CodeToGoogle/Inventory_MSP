package com.msp.auth_service.dto;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserDto {
    private Integer userID;
    private String userName;
    private String userType;
    private Boolean isActive;
}
