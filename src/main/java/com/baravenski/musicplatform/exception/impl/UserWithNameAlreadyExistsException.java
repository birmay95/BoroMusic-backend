package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.USER_WITH_NAME_ALREADY_EXISTS;

@NullMarked
public class UserWithNameAlreadyExistsException extends MusicPlatformException {

    public UserWithNameAlreadyExistsException(String username) {
        super(USER_WITH_NAME_ALREADY_EXISTS.formatted(username));
    }
}
