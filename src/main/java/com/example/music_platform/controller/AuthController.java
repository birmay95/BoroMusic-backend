package com.example.music_platform.controller;

import com.example.music_platform.dto.AuthRegister;
import com.example.music_platform.dto.AuthRequest;
import com.example.music_platform.dto.AuthResponse;
import com.example.music_platform.service.AuthService;
import com.example.music_platform.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationTokenService verificationTokenService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authorize(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRegister authRegister) {
        AuthResponse response = authService.register(authRegister.getUsername(), authRegister.getPassword(), authRegister.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return ResponseEntity.ok(authService.invalidateToken(token));
    }

    @GetMapping("/verification")
    public ResponseEntity<String> verify(@RequestParam Long userId) {
        return ResponseEntity.ok(authService.verify(userId));
    }

    @GetMapping("/check-verification")
    public ResponseEntity<Boolean> checkEmailVerification(@RequestParam Long userId) {
        return ResponseEntity.ok(authService.checkVerification(userId));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token, @RequestParam Long userId) {
        return ResponseEntity.ok(verificationTokenService.confirmEmail(token, userId));
    }
}
