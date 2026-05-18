package com.baravenski.musicplatform.auth.service;

import com.baravenski.musicplatform.auth.model.RefreshToken;
import com.baravenski.musicplatform.auth.repository.RefreshTokenRepository;
import com.baravenski.musicplatform.user.model.User;
import com.baravenski.musicplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;

    @Transactional
    public RefreshToken createOrUpdateRefreshToken(UUID userId) {
        User user = userService.findUserById(userId);
        RefreshToken refreshToken = user.getRefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        User user = userService.findUserById(userId);
        if (user.getRefreshToken() != null) {
            refreshTokenRepository.delete(user.getRefreshToken());
        }
    }

    @Scheduled(fixedRate = 43200000)
    @Transactional
    public void cleanUpExpiredTokens() {
        log.info("Running scheduled task to clean up expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
