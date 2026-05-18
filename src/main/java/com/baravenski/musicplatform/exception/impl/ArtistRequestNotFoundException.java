package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.ARTIST_REQUEST_NOT_FOUND;

@NullMarked
public class ArtistRequestNotFoundException extends MusicPlatformException {

    public ArtistRequestNotFoundException(UUID id) {
        super(ARTIST_REQUEST_NOT_FOUND.formatted(id));
    }
}
