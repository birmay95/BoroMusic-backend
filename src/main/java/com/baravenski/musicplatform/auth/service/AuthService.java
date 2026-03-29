package com.baravenski.musicplatform.auth.service;

import com.baravenski.musicplatform.auth.dto.AuthRegister;
import com.baravenski.musicplatform.auth.dto.AuthRequest;
import com.baravenski.musicplatform.auth.dto.AuthResponse;
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

    public AuthResponse authorize(AuthRequest authRequest) {
        var user = userService.findUserByEmailOrUsername(authRequest.getUsername());

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), authRequest.getPassword())
        );
        return new AuthResponse(JwtUtil.generateToken(authentication), userMapper.toDto(user));
    }

    public AuthResponse register(AuthRegister authRegister) {
        var user = userService.saveUserByRegistration(authRegister);
        var token = verificationTokenService.createVerificationToken(user);
        log.info("Confirmation token for {} creation: {}", authRegister.getUsername(), token.getToken());
        emailService.sendVerificationEmail(user, token.getToken());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRegister.getUsername(), authRegister.getPassword())
        );
        var jwt = JwtUtil.generateToken(authentication);
        return new AuthResponse(jwt, userMapper.toDto(user));
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

    public String invalidateToken(String token) {
        if (token != null && !token.isEmpty()) {
            return "Exit completed successfully";
        } else {
            throw new TokenNotFoundException("Invalid token or the exit has already been completed");
        }
    }

}


