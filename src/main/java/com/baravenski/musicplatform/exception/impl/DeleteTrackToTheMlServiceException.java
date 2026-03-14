package com.baravenski.musicplatform.exception.impl;

import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.DELETE_FROM_THE_ML_SERVICE_EXCEPTION_MESSAGE;

@NullMarked
public class DeleteTrackToTheMlServiceException extends RuntimeException {
    public DeleteTrackToTheMlServiceException() {
        super(DELETE_FROM_THE_ML_SERVICE_EXCEPTION_MESSAGE);
    }
}
