package com.baravenski.musicplatform.auth.controller;

import com.baravenski.musicplatform.auth.dto.AuthConfirmation;
import com.baravenski.musicplatform.auth.dto.AuthRegister;
import com.baravenski.musicplatform.auth.dto.AuthRequest;
import com.baravenski.musicplatform.auth.dto.AuthResponse;
import com.baravenski.musicplatform.auth.dto.TokenRefreshRequest;
import com.baravenski.musicplatform.auth.service.AuthService;
import com.baravenski.musicplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@NullMarked
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @ResponseStatus(OK)
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authService.authorize(authRequest);
    }

    @ResponseStatus(OK)
    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRegister authRegister) {
        return authService.register(authRegister);
    }

    @ResponseStatus(OK)
    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request);
    }

    @ResponseStatus(OK)
    @PostMapping("/logout/{userId}")
    public void logout(@PathVariable UUID userId, @RequestHeader("Authorization") String token) {
        authService.logout(userId, token);
    }

    @ResponseStatus(OK)
    @GetMapping("/verification/{userId}")
    public void verify(@PathVariable UUID userId) {
        authService.verify(userId);
    }

    @ResponseStatus(OK)
    @GetMapping("/check-verification/{userId}")
    public boolean checkEmailVerification(@PathVariable UUID userId) {
        return authService.checkVerification(userId);
    }

    @ResponseStatus(OK)
    @PostMapping("/confirm/{userId}")
    public void confirmEmail(
            @PathVariable UUID userId,
            @RequestBody AuthConfirmation authConfirmation
    ) {
        userService.confirmEmail(authConfirmation.token(), userId);
    }
}
