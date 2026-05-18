package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.DELETE_FROM_THE_ML_SERVICE_EXCEPTION_MESSAGE;

@NullMarked
public class DeleteTrackToTheMlServiceException extends MusicPlatformException {

    public DeleteTrackToTheMlServiceException() {
        super(DELETE_FROM_THE_ML_SERVICE_EXCEPTION_MESSAGE);
    }
}
