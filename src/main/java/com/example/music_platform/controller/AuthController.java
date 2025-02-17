package com.example.music_platform.controller;

import com.example.music_platform.dto.AuthRegister;
import com.example.music_platform.dto.AuthRequest;
import com.example.music_platform.dto.AuthResponse;
import com.example.music_platform.model.User;
import com.example.music_platform.service.AuthService;
import com.example.music_platform.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
//@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            return ResponseEntity.ok(authService.authorize(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Неправильное имя пользователя или пароль");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRegister authRegister) {
        try {
            return ResponseEntity.ok(authService.register(authRegister.getUsername(), authRegister.getPassword(), authRegister.getEmail()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Неправильное имя пользователя или пароль");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        // Валидация и деактивация токена (либо помечаем его в черный список, либо просто очищаем у клиента)
        boolean isLoggedOut = authService.invalidateToken(token);

        if (isLoggedOut) {
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or already logged out");
        }
    }

}