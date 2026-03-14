package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.FETCH_RECS_EXCEPTION_MESSAGE;

@NullMarked
public class RecommendationException extends MusicPlatformException {
    public RecommendationException() {
        super(FETCH_RECS_EXCEPTION_MESSAGE);
    }
}
