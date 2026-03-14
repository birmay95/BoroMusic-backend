package com.baravenski.musicplatform.verificationtoken.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationTokenDto(
        UUID id,
        String token,
        LocalDateTime expiryDate
) {}