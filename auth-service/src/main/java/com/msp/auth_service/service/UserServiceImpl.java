package com.msp.auth_service.service;

import com.msp.auth_service.dto.UserDto;
import com.msp.auth_service.entity.User;
import com.msp.auth_service.entity.UserType;
import com.msp.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto dto, String rawPassword) {
        User user = User.builder()
                .userName(dto.getUserName())
                .encryptedPassword(passwordEncoder.encode(rawPassword))
                .userType(UserType.valueOf(dto.getUserType()))
                .isActive(dto.getIsActive())
                .build();

        User saved = userRepository.save(user);

        return new UserDto(
                saved.getUserId(),
                saved.getUserName(),
                saved.getUserType().name(),
                saved.getIsActive()
        );
    }

    @Override
    public Optional<UserDto> findById(Integer id) {
        return userRepository.findById(id)
                .map(u -> new UserDto(u.getUserId(), u.getUserName(), u.getUserType().name(), u.getIsActive()));
    }
}
