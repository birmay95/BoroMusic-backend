package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.ARTIST_REQUEST_NOT_FOUND_BY_USER_ID;

public class ArtistRequestNotFoundByUserIdException extends MusicPlatformException {
    public ArtistRequestNotFoundByUserIdException(UUID userId) {
        super(ARTIST_REQUEST_NOT_FOUND_BY_USER_ID.formatted(userId));
    }
}
