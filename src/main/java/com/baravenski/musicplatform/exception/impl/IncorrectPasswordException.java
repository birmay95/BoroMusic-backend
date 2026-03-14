package com.baravenski.musicplatform.exception.impl;

import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.INCORRECT_PASSWORD;

@NullMarked
public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super(INCORRECT_PASSWORD);
    }
}
