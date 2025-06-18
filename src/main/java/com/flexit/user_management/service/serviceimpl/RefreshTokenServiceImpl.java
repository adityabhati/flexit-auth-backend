package com.flexit.user_management.service.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexit.user_management.dao.RefreshTokenDao;
import com.flexit.user_management.dto.RefreshTokenDto;
import com.flexit.user_management.exception.FlexitCustomException;
import com.flexit.user_management.model.RefreshToken;
import com.flexit.user_management.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenDao refreshTokenDao;
    private final ObjectMapper objectMapper;

    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        try {
            refreshTokenDao.saveRefreshToken(refreshToken);
        } catch (FlexitCustomException e) {
            log.error("Error occurred while saving refresh token {}", e.getDescription());
            throw new FlexitCustomException(e.getHttpStatus(), "Error occurred while saving refresh token", "Error occurred while saving refresh token");
        }
    }

    @Override
    public RefreshTokenDto findByRefreshToken(String refreshToken) {
        RefreshToken token;
        try {
            token = refreshTokenDao.findByRefreshToken(refreshToken);
        } catch (FlexitCustomException e) {
            log.error("Error occurred while fetching refresh token {}", e.getDescription());
            throw new FlexitCustomException(e.getHttpStatus(), "Error occurred while fetching refresh token", "Error occurred while fetching refresh token");
        }
        return objectMapper.convertValue(token, RefreshTokenDto.class);
    }

    @Override
    public void updateInsertRefreshToken(Long userId, String refreshToken, Long expiryDate) {
        RefreshToken token;
        try {
            token = refreshTokenDao.updateInsertRefreshToken(userId, refreshToken, expiryDate);
        } catch (FlexitCustomException e) {
            log.error("Error occurred while updating refresh token {}", e.getDescription());
            throw new FlexitCustomException(e.getHttpStatus(), "Error occurred while updating refresh token", "Error occurred while updating refresh token");
        }
        objectMapper.convertValue(token, RefreshTokenDto.class);
    }

    @Override
    public void deleteRefreshTokenByUserid(Long userId) {
        try {
            refreshTokenDao.deleteRefreshTokenByUserId(userId);
        } catch (FlexitCustomException e) {
            log.error("Error occurred while deleting refresh token {}", e.getDescription());
            throw new FlexitCustomException(e.getHttpStatus(), "Error occurred while deleting refresh token", "Error occurred while deleting refresh token");
        }
    }
}