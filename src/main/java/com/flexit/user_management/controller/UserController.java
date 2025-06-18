package com.flexit.user_management.controller;

import com.flexit.user_management.dto.AuthDtoResponse;
import com.flexit.user_management.dto.UserDto;
import com.flexit.user_management.dto.UserUpdateDto;
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

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam Long userId) {
        userService.logoutUser(userId);
        return ResponseEntity.ok().body("User Logged out successfully");
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserDto> getUserById(@RequestParam String username) {
        return ResponseEntity.ok().body(userService.getUserByUsername(username));
    }

    @PutMapping("/update-user")
    public ResponseEntity<String> updateUserByUsername(@RequestBody UserUpdateDto userDto, @RequestParam String username) {
        userService.updateUser(userDto, username);
        return ResponseEntity.ok().body("User updated successfully");
    }

}