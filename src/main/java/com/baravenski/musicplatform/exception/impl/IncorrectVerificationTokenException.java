package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.INCORRECT_VERIFICATION_TOKEN;

@NullMarked
public class IncorrectVerificationTokenException extends MusicPlatformException {

    public IncorrectVerificationTokenException() {
        super(INCORRECT_VERIFICATION_TOKEN);
    }
}
