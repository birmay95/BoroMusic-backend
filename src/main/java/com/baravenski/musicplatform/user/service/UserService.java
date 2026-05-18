package com.baravenski.musicplatform.user.service;

import com.baravenski.musicplatform.artistrequest.service.ArtistRequestService;
import com.baravenski.musicplatform.auth.dto.AuthRegister;
import com.baravenski.musicplatform.auth.dto.ChangePasswordRequest;
import com.baravenski.musicplatform.core.email.service.EmailService;
import com.baravenski.musicplatform.exception.impl.IncorrectVerificationTokenException;
import com.baravenski.musicplatform.exception.impl.WeakPasswordException;
import com.baravenski.musicplatform.exception.impl.IncorrectPasswordException;
import com.baravenski.musicplatform.exception.impl.UserNotFoundByLoginException;
import com.baravenski.musicplatform.exception.impl.UserNotFoundException;
import com.baravenski.musicplatform.exception.impl.UserWithEmailAlreadyExistsException;
import com.baravenski.musicplatform.exception.impl.UserWithNameAlreadyExistsException;
import com.baravenski.musicplatform.track.service.TrackService;
import com.baravenski.musicplatform.verificationtoken.service.VerificationTokenService;
import com.baravenski.musicplatform.user.dto.UserResponseDto;
import com.baravenski.musicplatform.user.dto.mapper.UserMapper;
import com.baravenski.musicplatform.user.model.User;
import com.baravenski.musicplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@NullMarked
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final TrackService trackService;
    private final ArtistRequestService artistRequestService;

    public static final int MIN_PASSWORD_LENGTH = 6;

    public UserResponseDto getUserById(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);
    }

    @Named("findUserById")
    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void addFavourite(UUID id, UUID trackId) {
        log.info("[USER-FAV] User ID: {} is adding Track ID: {} to Favourites", id, trackId);
        var user = userRepository.findUserWithFavourites(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        var track = trackService.findTrackById(trackId);

        user.getFavourites().add(track);
        log.info("[USER-FAV] Track successfully saved to user's favourite list in DB");
        userRepository.save(user);
    }

    public void removeFavourites(UUID id, UUID trackId) {
        var user = userRepository.findUserWithFavourites(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        var track = trackService.findTrackById(trackId);

        user.getFavourites().remove(track);
        userRepository.save(user);
    }

    public void changePassword(UUID id, ChangePasswordRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void changeEmail(UUID id, String newEmail) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setEmail(newEmail);
        user.setEmailVerified(false);
        userRepository.save(user);
        verificationTokenService.deleteByUserId(id);

        var token = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user, token.getToken());
        log.info("Token {} sent to user {}", token.getToken(), user.getUsername());
    }

    public void deleteUserById(UUID id) {
        artistRequestService.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public User findUserByEmailOrUsername(String login) {
        return userRepository.findByUsername(login)
                .or(() -> userRepository.findByEmail(login))
                .orElseThrow(() -> new UserNotFoundByLoginException(login));
    }

    public void confirmEmail(String token, UUID userId) {
        var verificationToken = verificationTokenService.findByUserId(userId);

        if (verificationToken.getToken().equals(token)) {
            markEmailAsVerified(userId);
            verificationTokenService.deleteByUserId(userId);
        } else {
            throw new IncorrectVerificationTokenException();
        }
    }

    private void markEmailAsVerified(UUID userId) {
        var user = findUserById(userId);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public User saveUserByRegistration(AuthRegister authRegister) {
        if (userRepository.findByEmail(authRegister.getEmail()).isPresent()) {
            throw new UserWithEmailAlreadyExistsException(authRegister.getEmail());
        }
        if (userRepository.findByUsername(authRegister.getUsername()).isPresent()) {
            throw new UserWithNameAlreadyExistsException(authRegister.getUsername());
        }
        if (authRegister.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new WeakPasswordException();
        }
        var passwordEncoded = passwordEncoder.encode(authRegister.getPassword());
        var userToSave = userMapper.toEntity(authRegister, passwordEncoded);

        return userRepository.save(userToSave);
    }
}
