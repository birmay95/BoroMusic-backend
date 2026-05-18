package com.baravenski.musicplatform.verificationtoken.dto;

import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;
import java.util.UUID;

@NullMarked
public record VerificationTokenDto(
        UUID id,
        String token,
        LocalDateTime expiryDate
) {
}
