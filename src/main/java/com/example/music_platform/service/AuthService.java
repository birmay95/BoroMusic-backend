package com.example.music_platform.service;

import com.example.music_platform.dto.AuthResponse;
import com.example.music_platform.model.User;
import com.example.music_platform.repository.UserRepository;
import com.example.music_platform.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthResponse authorize(String username, String password) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return new AuthResponse(JwtUtil.generateToken(authentication), userOptional.get());
    }

    public AuthResponse register(String username, String password, String email) throws AuthenticationException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            throw new IllegalStateException("Пользователь уже существует");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordEncoded = encoder.encode(password);
        User newUser = new User(null, email, username, passwordEncoded, "USER", false);
        userRepository.save(newUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return new AuthResponse(JwtUtil.generateToken(authentication), newUser);
    }

    public boolean invalidateToken(String token) {
        // Логика для аннулирования токена (например, добавление его в черный список)
        //            tokenBlacklist.add(token); // tokenBlacklist — это черный список токенов (например, Set или база данных)
        return token != null;
    }

}


