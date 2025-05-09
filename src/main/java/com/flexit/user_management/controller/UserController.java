package com.flexit.user_management.controller;

import com.flexit.user_management.dto.AuthDtoResponse;
import com.flexit.user_management.dto.UserDto;
import com.flexit.user_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flexit/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<AuthDtoResponse> save(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(userService.save(userDto));
    }

    @GetMapping("/login")
    public ResponseEntity<AuthDtoResponse> login(@RequestParam String username, String password) {
        return ResponseEntity.ok().body(userService.validateUserByCredentials(username, password));
    }

    @GetMapping("/token")
    public ResponseEntity<AuthDtoResponse> loginByRefreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok().body(userService.validateUserByToken(refreshToken));
    }

}