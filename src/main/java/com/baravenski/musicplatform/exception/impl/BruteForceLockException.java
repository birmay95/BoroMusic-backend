package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.BRUTE_FORCE_LOCK;

@NullMarked
public class BruteForceLockException extends MusicPlatformException {

    public BruteForceLockException(long blockTime) {
        super(BRUTE_FORCE_LOCK.formatted(blockTime));
    }
}
