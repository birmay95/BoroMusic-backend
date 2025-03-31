package com.example.music_platform.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(final String mes) {super(mes);}
}
