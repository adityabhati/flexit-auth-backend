package com.flexit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    private Long id;
    private String refreshToken;
    private UserDto user;
    private Long expiryDate;
}