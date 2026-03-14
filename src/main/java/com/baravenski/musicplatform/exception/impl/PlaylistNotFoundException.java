package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.PLAYLIST_NOT_FOUND;

@NullMarked
public class PlaylistNotFoundException extends MusicPlatformException {
    public PlaylistNotFoundException(UUID id) {
        super(PLAYLIST_NOT_FOUND.formatted(id));
    }
}
