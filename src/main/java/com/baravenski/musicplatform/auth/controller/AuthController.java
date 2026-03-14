package com.baravenski.musicplatform.auth.controller;

import com.baravenski.musicplatform.auth.dto.AuthConfirmation;
import com.baravenski.musicplatform.auth.dto.AuthRegister;
import com.baravenski.musicplatform.auth.dto.AuthRequest;
import com.baravenski.musicplatform.auth.dto.AuthResponse;
import com.baravenski.musicplatform.auth.service.AuthService;
import com.baravenski.musicplatform.user.service.UserService;
import com.baravenski.musicplatform.verificationtoken.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/auth")
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

    // TODO check this method later
    @ResponseStatus(OK)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return ResponseEntity.ok(authService.invalidateToken(token));
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
    @GetMapping("/confirm/{userId}")
    public void confirmEmail(
            @PathVariable UUID userId,
            @RequestBody AuthConfirmation authConfirmation
    ) {
        userService.confirmEmail(authConfirmation.token(), userId);
    }
}
