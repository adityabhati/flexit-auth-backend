package com.flexit.user_management.service;

import com.flexit.user_management.dto.AuthDtoResponse;
import com.flexit.user_management.dto.UserDto;
import com.flexit.user_management.model.User;


public interface UserService {
    AuthDtoResponse save(UserDto userDto);

    User getUserByUsername(String username);

    AuthDtoResponse validateUserByCredentials(String username, String password);

    AuthDtoResponse validateUserByToken(String refreshToken);
}