package com.baravenski.musicplatform.exception.impl;

import com.baravenski.musicplatform.exception.MusicPlatformException;
import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.UPLOAD_TO_THE_ML_OR_AWS_SERVICE_EXCEPTION_MESSAGE;

@NullMarked
public class UploadTrackToTheMlOrAwsServiceException extends MusicPlatformException {

    public UploadTrackToTheMlOrAwsServiceException() {
        super(UPLOAD_TO_THE_ML_OR_AWS_SERVICE_EXCEPTION_MESSAGE);
    }
}
