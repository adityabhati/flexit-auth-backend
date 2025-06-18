package com.flexit.user_management.dao;

import com.flexit.user_management.model.RefreshToken;

public interface RefreshTokenDao {
    void saveRefreshToken(RefreshToken refreshToken);

    RefreshToken findByRefreshToken(String refreshToken);

    RefreshToken updateInsertRefreshToken(Long userId, String refreshToken, Long expiryDate);

    void deleteRefreshTokenByUserId(Long userId);
}