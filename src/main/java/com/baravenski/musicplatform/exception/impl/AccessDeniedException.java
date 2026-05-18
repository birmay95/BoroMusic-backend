package com.baravenski.musicplatform.exception.impl;

import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.ACCESS_DENIED;

@NullMarked
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super(ACCESS_DENIED);
    }
}
