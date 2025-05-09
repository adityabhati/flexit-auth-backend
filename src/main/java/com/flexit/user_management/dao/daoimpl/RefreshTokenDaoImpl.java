package com.flexit.user_management.dao.daoimpl;

import com.flexit.user_management.dao.RefreshTokenDao;
import com.flexit.user_management.model.RefreshToken;
import com.flexit.user_management.repository.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

;

@Component
@RequiredArgsConstructor
public class RefreshTokenDaoImpl implements RefreshTokenDao {
    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepo.save(refreshToken);
    }

    @Override
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepo.findByRefreshToken(refreshToken);
    }

    @Override
    public RefreshToken updateInsertRefreshToken(Long userId, String refreshToken, Long expiryDate) {
        return refreshTokenRepo.updateInsertRefreshToken(userId, refreshToken, expiryDate);
    }
}