package com.baravenski.musicplatform.auth.service;

import com.baravenski.musicplatform.auth.dto.AuthRegister;
import com.baravenski.musicplatform.auth.dto.AuthRequest;
import com.baravenski.musicplatform.auth.dto.AuthResponse;
import com.baravenski.musicplatform.auth.dto.TokenRefreshRequest;
import com.baravenski.musicplatform.auth.model.RefreshToken;
import com.baravenski.musicplatform.core.email.service.EmailService;
import com.baravenski.musicplatform.exception.impl.IncorrectPasswordException;
import com.baravenski.musicplatform.exception.TokenNotFoundException;
import com.baravenski.musicplatform.user.dto.mapper.UserMapper;
import com.baravenski.musicplatform.user.service.UserService;
import com.baravenski.musicplatform.core.security.util.JwtUtil;
import com.baravenski.musicplatform.verificationtoken.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserMapper userMapper;

    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse authorize(AuthRequest authRequest) {
        var user = userService.findUserByEmailOrUsername(authRequest.getUsername());

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), authRequest.getPassword())
        );
        String accessToken = JwtUtil.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken(), userMapper.toDto(user));
    }

    public AuthResponse register(AuthRegister authRegister) {
        var user = userService.saveUserByRegistration(authRegister);
        var token = verificationTokenService.createVerificationToken(user);
        log.info("Confirmation token for {} creation: {}", authRegister.getUsername(), token.getToken());
        emailService.sendVerificationEmail(user, token.getToken());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRegister.getUsername(), authRegister.getPassword())
        );

        String accessToken = JwtUtil.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getId());
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

    public void logout(String username) {
        var user = userService.findUserByEmailOrUsername(username);
        refreshTokenService.deleteByUserId(user.getId());
    }

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
