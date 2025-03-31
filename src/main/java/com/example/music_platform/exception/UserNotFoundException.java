package com.example.music_platform.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(final String mes) {super(mes);}
}
