package com.baravenski.musicplatform.exception.impl;

import org.jspecify.annotations.NullMarked;

import static com.baravenski.musicplatform.exception.constant.ExceptionConstants.USER_NOT_FOUND_BY_LOGIN;

@NullMarked
public class UserNotFoundByLoginException extends RuntimeException {

    public UserNotFoundByLoginException(String login) {
        super(USER_NOT_FOUND_BY_LOGIN.formatted(login));
    }
}
