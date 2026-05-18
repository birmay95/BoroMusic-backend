package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TokenNotFoundException extends MusicPlatformException {

    public TokenNotFoundException(final String mes) {
        super(mes);
    }
}
