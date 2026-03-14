package com.baravenski.musicplatform.exception.impl;

import org.jspecify.annotations.NullMarked;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.VERIFICATION_TOKEN_NOT_FOUND_BY_USER_ID;

@NullMarked
public class VerificationTokenNotFoundByUserIdException extends RuntimeException {

    public VerificationTokenNotFoundByUserIdException(UUID userId) {
        super(VERIFICATION_TOKEN_NOT_FOUND_BY_USER_ID.formatted(userId));
    }
}
