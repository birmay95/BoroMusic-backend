package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.INVALID_TOKEN_MESSAGE;

@NullMarked
public class InvalidTokenException extends MusicPlatformException {

    public InvalidTokenException() {
        super(INVALID_TOKEN_MESSAGE);
    }
}
