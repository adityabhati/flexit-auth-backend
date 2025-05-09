package com.flexit.user_management.service.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexit.user_management.dao.RefreshTokenDao;
import com.flexit.user_management.dto.RefreshTokenDto;
import com.flexit.user_management.model.RefreshToken;
import com.flexit.user_management.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenDao refreshTokenDao;
    private final ObjectMapper objectMapper;

    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenDao.saveRefreshToken(refreshToken);
    }

    @Override
    public RefreshTokenDto findByRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenDao.findByRefreshToken(refreshToken);
        return objectMapper.convertValue(token, RefreshTokenDto.class);
    }

    @Override
    public RefreshTokenDto updateInsertRefreshToken(Long userId, String refreshToken, Long expiryDate) {
        RefreshToken token = refreshTokenDao.updateInsertRefreshToken(userId, refreshToken, expiryDate);
        return objectMapper.convertValue(token, RefreshTokenDto.class);
    }
}