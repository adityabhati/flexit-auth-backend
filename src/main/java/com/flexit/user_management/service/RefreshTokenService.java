package com.flexit.user_management.service;

import com.flexit.user_management.dto.RefreshTokenDto;
import com.flexit.user_management.model.RefreshToken;

public interface RefreshTokenService {
    void saveRefreshToken(RefreshToken refreshToken);

    RefreshTokenDto findByRefreshToken(String refreshToken);

    void updateInsertRefreshToken(Long userId, String refreshToken, Long expiryDate);

    void deleteRefreshTokenByUserid(Long userId);
}