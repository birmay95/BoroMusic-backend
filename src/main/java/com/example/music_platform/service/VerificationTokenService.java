package com.example.music_platform.service;

import com.example.music_platform.exception.VerificationTokenExpiredException;
import com.example.music_platform.model.User;
import com.example.music_platform.model.VerificationToken;
import com.example.music_platform.repository.UserRepository;
import com.example.music_platform.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Data
@AllArgsConstructor
@Service
public class VerificationTokenService {
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public VerificationToken createVerificationToken(User user) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String token = String.valueOf(code);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);
        return verificationToken;
    }

    public String confirmEmail(String token, Long userId) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByUserId(userId);
        if (verificationToken.isPresent() && verificationToken.get().getToken().equals(token)) {
            User user = verificationToken.get().getUser();
            user.setEmailVerified(true);
            userRepository.save(user);
            tokenRepository.deleteById(verificationToken.get().getId());
        } else {
            throw new VerificationTokenExpiredException("Incorrect or expired year");
        }
        return "Email has been successfully confirmed!";
    }
}
