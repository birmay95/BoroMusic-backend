package com.baravenski.musicplatform.verificationtoken.service;

import com.baravenski.musicplatform.verificationtoken.dto.mapper.VerificationTokenMapper;
import com.baravenski.musicplatform.exception.impl.IncorrectVerificationTokenException;
import com.baravenski.musicplatform.exception.impl.VerificationTokenNotFoundByUserIdException;
import com.baravenski.musicplatform.user.model.User;
import com.baravenski.musicplatform.verificationtoken.model.VerificationToken;
import com.baravenski.musicplatform.verificationtoken.repository.VerificationTokenRepository;
import com.baravenski.musicplatform.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Data
@AllArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    private final VerificationTokenMapper verificationTokenMapper;

    public VerificationToken createVerificationToken(User user) {
        var verificationToken = verificationTokenMapper.toEntityFromUser(user);
        return tokenRepository.save(verificationToken);
    }

    public void deleteByUserId(UUID userId) {
        tokenRepository.deleteByUserId(userId);
    }

    public VerificationToken findByUserId(UUID userId) {
        return tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new VerificationTokenNotFoundByUserIdException(userId));
    }
}
