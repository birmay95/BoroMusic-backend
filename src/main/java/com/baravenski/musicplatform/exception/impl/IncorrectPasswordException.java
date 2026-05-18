package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.INCORRECT_PASSWORD;

@NullMarked
public class IncorrectPasswordException extends MusicPlatformException {

    public IncorrectPasswordException() {
        super(INCORRECT_PASSWORD);
    }
}
