package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.WEAK_PASSWORD;

public class WeakPasswordException extends MusicPlatformException {
    public WeakPasswordException() {
        super(WEAK_PASSWORD);
    }
}
