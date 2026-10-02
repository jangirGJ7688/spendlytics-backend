package com.ganpat.spendlyticsbackend.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.ganpat.spendlyticsbackend.entity.RefreshToken;
import com.ganpat.spendlyticsbackend.entity.User;
import com.ganpat.spendlyticsbackend.exception.ResourceNotFoundException;
import com.ganpat.spendlyticsbackend.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private static final long REFRESH_TOKEN_DAYS = 30;

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(
            LocalDateTime.now()
            .plusDays(REFRESH_TOKEN_DAYS)
        );
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));

        if(refreshToken.isRevoked()) {
            throw new IllegalArgumentException(
                    "Refresh token has been revoked"
            );
        }

        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String token) {
        RefreshToken oldToken = verifyRefreshToken(token);

        oldToken.setRevoked(true);

        refreshTokenRepository.save(oldToken);

        return createRefreshToken(oldToken.getUser());
    }
}
