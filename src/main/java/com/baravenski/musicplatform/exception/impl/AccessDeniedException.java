package com.baravenski.musicplatform.exception.impl;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.ACCESS_DENIED;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super(ACCESS_DENIED);
    }
}
