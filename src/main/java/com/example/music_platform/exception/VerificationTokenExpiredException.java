package com.example.music_platform.exception;

public class VerificationTokenExpiredException extends RuntimeException{
    public VerificationTokenExpiredException(final String mes) {super(mes);}
}
