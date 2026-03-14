package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.ARTIST_REQUEST_NOT_FOUND;

public class ArtistRequestNotFoundException extends MusicPlatformException {
    public ArtistRequestNotFoundException(UUID id) {
        super(ARTIST_REQUEST_NOT_FOUND.formatted(id));
    }
}
