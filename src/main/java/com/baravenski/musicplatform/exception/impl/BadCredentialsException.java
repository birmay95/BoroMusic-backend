package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.BAD_CREDENTIALS;

public class BadCredentialsException extends MusicPlatformException {
    public BadCredentialsException(String message) {
        super(BAD_CREDENTIALS.formatted(message));
    }
}
