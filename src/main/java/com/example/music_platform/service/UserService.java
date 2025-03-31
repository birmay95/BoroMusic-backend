package com.example.music_platform.service;

import com.example.music_platform.exception.IncorrectPasswordException;
import com.example.music_platform.exception.TrackNotFoundException;
import com.example.music_platform.exception.UserNotFoundException;
import com.example.music_platform.model.*;
import com.example.music_platform.repository.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final PasswordEncoder passwordEncoder;
    private final ArtistRequestRepository artistRequestRepository;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final VerificationTokenRepository tokenRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public String addFavourite(Long userId, Long trackId) {
        User user = userRepository.findUserWithFavourites(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Track track = trackRepository.findById(trackId).orElseThrow(() -> new TrackNotFoundException("Track not found"));

        user.getFavourites().add(track);
        userRepository.save(user);

        return "Track added to favourites";
    }

    @Transactional
    public String removeFavourites(Long userId, Long trackId) {
        User user = userRepository.findUserWithFavourites(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Track track = trackRepository.findById(trackId).orElseThrow(() -> new TrackNotFoundException("Track not found"));

        user.getFavourites().remove(track);
        userRepository.save(user);

        return "Track removed from favourites";
    }

    @Transactional
    public Set<Track> getFavourites(Long userId) {
        User user = userRepository.findUserWithFavourites(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getFavourites();
    }

    @Transactional
    public List<Playlist> getPlaylists(Long userId) {
        User user = userRepository.findUserWithPlaylists(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getPlaylists();
    }

    @Transactional
    public String changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IncorrectPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully";
    }

    @Transactional
    public String changeEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setEmail(newEmail);
        user.setEmailVerified(false);
        userRepository.save(user);

        tokenRepository.deleteByUserId(userId);
        tokenRepository.flush();

        VerificationToken token = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user, token.getToken());
        log.info("Token {} sent to user {}", token.getToken(), user.getUsername());

        return "Email changed successfully";
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.getFavourites().clear();

        artistRequestRepository.findByUserId(userId).ifPresent(artistRequestRepository::delete);

        userRepository.delete(user);
    }

}
