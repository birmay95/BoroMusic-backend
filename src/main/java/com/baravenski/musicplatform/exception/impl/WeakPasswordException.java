package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.WEAK_PASSWORD;

@NullMarked
public class WeakPasswordException extends MusicPlatformException {

    public WeakPasswordException() {
        super(WEAK_PASSWORD);
    }
}
