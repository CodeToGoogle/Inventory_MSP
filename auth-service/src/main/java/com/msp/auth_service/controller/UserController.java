package com.msp.auth_service.controller;


import com.msp.auth_service.dto.UserDto;
import com.msp.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto, @RequestParam String password) {
        UserDto created = userService.createUser(dto, password);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer id) {
        return userService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public ResponseEntity<?> pending() {
        // Provide small payload for gateway aggregation
        return ResponseEntity.ok(java.util.Map.of("pending_user_approvals", 0));
    }
}

