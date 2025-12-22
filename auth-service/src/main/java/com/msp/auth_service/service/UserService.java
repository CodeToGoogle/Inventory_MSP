package com.msp.auth_service.service;

import com.msp.auth_service.dto.UserDto;

import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto dto, String rawPassword);
    Optional<UserDto> findById(Integer id);
}
