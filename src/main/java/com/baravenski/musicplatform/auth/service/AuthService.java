package com.baravenski.musicplatform.auth.service;

import com.baravenski.musicplatform.auth.dto.AuthRegister;
import com.baravenski.musicplatform.auth.dto.AuthRequest;
import com.baravenski.musicplatform.auth.dto.AuthResponse;
import com.baravenski.musicplatform.auth.dto.TokenRefreshRequest;
import com.baravenski.musicplatform.auth.model.RefreshToken;
import com.baravenski.musicplatform.core.email.service.EmailService;
import com.baravenski.musicplatform.exception.impl.BadCredentialsException;
import com.baravenski.musicplatform.exception.impl.BruteForceLockException;
import com.baravenski.musicplatform.exception.impl.TokenNotFoundException;
import com.baravenski.musicplatform.exception.impl.InvalidTokenException;
import com.baravenski.musicplatform.exception.impl.UserNotFoundByLoginException;
import com.baravenski.musicplatform.user.dto.mapper.UserMapper;
import com.baravenski.musicplatform.user.model.User;
import com.baravenski.musicplatform.user.service.UserService;
import com.baravenski.musicplatform.core.security.util.JwtUtil;
import com.baravenski.musicplatform.verificationtoken.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserMapper userMapper;

    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    private final StringRedisTemplate redisTemplate;
    private final TokenBlacklistService tokenBlacklistService;
    private static final int MAX_ATTEMPTS = 5;
    private static final long ATTEMPT_WINDOW_MINUTES = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    public AuthResponse authorize(AuthRequest authRequest) {
        String login = authRequest.getUsername();
        log.info("[AUTH] Attempting login for user: {}", login);

        String lockKey = "login_locked:" + login;
        if (redisTemplate.hasKey(lockKey)) {
            long lockTimeLeft = redisTemplate.getExpire(lockKey, TimeUnit.MINUTES);
            log.warn("[AUTH] Login blocked for {}. Brute-force protection active ({} min left)", login, lockTimeLeft);
            throw new BruteForceLockException(lockTimeLeft);
        }

        User user;
        try {
            user = userService.findUserByEmailOrUsername(authRequest.getUsername());
        } catch (UserNotFoundByLoginException exception) {
            log.warn("[AUTH] Login failed: User {} not found", login);
            registerFailedAttempt(login);
            throw new BadCredentialsException("User not found");
        }
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            log.warn("[AUTH] Login failed: Invalid password for user {}", login);
            registerFailedAttempt(login);
            throw new BadCredentialsException("Wrong password");
        }
        resetAttempts(login);


        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), authRequest.getPassword())
        );
        String accessToken = JwtUtil.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getId());

        log.info("[AUTH] Login successful! Tokens generated for user: {}", login);
        return new AuthResponse(accessToken, refreshToken.getToken(), userMapper.toDto(user));
    }

    private void registerFailedAttempt(String login) {
        String attemptKey = "login_attempts:" + login;
        Long attempts = redisTemplate.opsForValue().increment(attemptKey);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptKey, ATTEMPT_WINDOW_MINUTES, TimeUnit.MINUTES);
        }

        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set("login_locked:" + login, "locked", LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
            redisTemplate.delete(attemptKey);
            throw new BruteForceLockException(LOCK_DURATION_MINUTES);
        }
    }

    private void resetAttempts(String login) {
        redisTemplate.delete("login_attempts:" + login);
    }

    public AuthResponse register(AuthRegister authRegister) {
        log.info("[AUTH] Starting registration process for new user: {}", authRegister.getUsername());
        var user = userService.saveUserByRegistration(authRegister);
        var token = verificationTokenService.createVerificationToken(user);
        log.info("[AUTH] Verification token generated. Triggering EmailService for {}", authRegister.getEmail());
        emailService.sendVerificationEmail(user, token.getToken());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRegister.getUsername(), authRegister.getPassword())
        );

        String accessToken = JwtUtil.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getId());

        log.info("[AUTH] User {} successfully registered and saved to database", user.getUsername());
        return new AuthResponse(accessToken, refreshToken.getToken(), userMapper.toDto(user));
    }

    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = JwtUtil.generateTokenFromUsername(user.getUsername());
                    return new AuthResponse(accessToken, requestRefreshToken, userMapper.toDto(user));
                })
                .orElseThrow(() -> new TokenNotFoundException("Refresh token is not in database!"));
    }

    public void logout(UUID id, String token) {
        String actualToken = token;
        if (token != null && token.startsWith("Bearer ")) {
            actualToken = token.substring(7);
        }

        if (actualToken == null || JwtUtil.isTokenExpired(actualToken) || tokenBlacklistService.isTokenBlacklisted(actualToken)) {
            throw new InvalidTokenException();
        }

        long ttlInMillis = JwtUtil.getExpirationTimeLeft(actualToken);
        tokenBlacklistService.addToBlacklist(actualToken, ttlInMillis);

        refreshTokenService.deleteByUserId(id);
    }

    @Transactional
    public void verify(UUID userId) {
        var user = userService.findUserById(userId);
        verificationTokenService.deleteByUserId(userId);

        var newVerificationToken = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user, newVerificationToken.getToken());
        log.info("Token {} sent to user {}", newVerificationToken.getToken(), user.getUsername());
    }

    public boolean checkVerification(UUID userId) {
        var user = userService.findUserById(userId);
        return user.isEmailVerified();
    }
}
