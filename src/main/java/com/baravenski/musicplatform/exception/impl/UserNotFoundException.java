package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.USER_NOT_FOUND;

@NullMarked
public class UserNotFoundException extends MusicPlatformException {
    public UserNotFoundException(UUID id) {
        super(USER_NOT_FOUND.formatted(id));
    }
}
