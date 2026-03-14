package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.TRACK_NOT_FOUND_BY_ID;
import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.TRACK_NOT_FOUND_BY_NAME;

public class TrackNotFoundException extends MusicPlatformException {

    public TrackNotFoundException(UUID id) {
        super(TRACK_NOT_FOUND_BY_ID.formatted(id));
    }

    public TrackNotFoundException(String fileName) {
        super(TRACK_NOT_FOUND_BY_NAME.formatted(fileName));
    }
}
