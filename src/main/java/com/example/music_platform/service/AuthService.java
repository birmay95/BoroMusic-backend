package com.example.music_platform.service;

import com.example.music_platform.dto.AuthResponse;
import com.example.music_platform.exception.*;
import com.example.music_platform.model.User;
import com.example.music_platform.model.VerificationToken;
import com.example.music_platform.repository.UserRepository;
import com.example.music_platform.repository.VerificationTokenRepository;
import com.example.music_platform.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public AuthResponse authorize(String login, String password) throws AuthenticationException {
        Optional<User> userOptional = userRepository.findWithFavAndPlaylistsByUsername(login);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findWithFavAndPlaylistsByEmail(login);
        }
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("The user was not found");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect password");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password)
        );
        return new AuthResponse(JwtUtil.generateToken(authentication), userOptional.get());
    }

    @Transactional
    public AuthResponse register(String username, String password, String email) throws AuthenticationException {

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("A user with that name already exists");
        }

        userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("The user with this email has already been registered");
        }

        if (password.length() < 6) {
            throw new WeakPasswordException("The password must contain at least 6 characters");
        }

        String passwordEncoded = passwordEncoder.encode(password);

        User newUser = new User(null, email, username, passwordEncoded, "USER", false, null, new HashSet<>(), new ArrayList<>());
        userRepository.save(newUser);

        VerificationToken token = verificationTokenService.createVerificationToken(newUser);
        log.info("Confirmation token for {} creation: {}", username, token);
        emailService.sendVerificationEmail(newUser, token.getToken());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return new AuthResponse(JwtUtil.generateToken(authentication), newUser);
    }

    @Transactional
    public String verify(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        VerificationToken verificationToken = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        tokenRepository.deleteByUserId(userId);
        tokenRepository.flush();

        VerificationToken newVerificationToken = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user, newVerificationToken.getToken());
        log.info("Token {} sent to user {}", newVerificationToken.getToken(), user.getUsername());

        return "New token is successfully created and sent";
    }

    public Boolean checkVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
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


