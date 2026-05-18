package com.baravenski.musicplatform.exception;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MusicPlatformException extends RuntimeException {

    public MusicPlatformException(String message) {
        super(message);
    }
}
