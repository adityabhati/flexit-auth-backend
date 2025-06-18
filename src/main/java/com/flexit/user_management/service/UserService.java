package com.flexit.user_management.service;

import com.flexit.user_management.dto.AuthDtoResponse;
import com.flexit.user_management.dto.UserDto;
import com.flexit.user_management.dto.UserUpdateDto;


public interface UserService {
    AuthDtoResponse save(UserDto userDto);

    UserDto getUserByUsername(String username);

    AuthDtoResponse validateUserByCredentials(String username, String password);

    AuthDtoResponse validateUserByToken(String refreshToken);

    void logoutUser(Long userId);

    void updateUser(UserUpdateDto userDto, String username);
}