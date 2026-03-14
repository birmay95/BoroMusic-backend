package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.USER_NOT_FOUND;

public class UserNotFoundException extends MusicPlatformException {
    public UserNotFoundException(UUID id) {
        super(USER_NOT_FOUND.formatted(id));
    }
}
