package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.USER_NOT_FOUND_BY_LOGIN;

@NullMarked
public class UserNotFoundByLoginException extends MusicPlatformException {

    public UserNotFoundByLoginException(String login) {
        super(USER_NOT_FOUND_BY_LOGIN.formatted(login));
    }
}
