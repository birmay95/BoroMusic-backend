package com.example.music_platform.exception;

public class TrackNotFoundException extends RuntimeException {
    public TrackNotFoundException(final String mes) {super(mes);}
}
