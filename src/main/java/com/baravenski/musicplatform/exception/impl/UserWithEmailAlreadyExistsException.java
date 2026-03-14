package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.USER_WITH_EMAIL_ALREADY_EXISTS;

@NullMarked
public class UserWithEmailAlreadyExistsException extends MusicPlatformException {

    public UserWithEmailAlreadyExistsException(String email) {
        super(USER_WITH_EMAIL_ALREADY_EXISTS.formatted(email));
    }
}
