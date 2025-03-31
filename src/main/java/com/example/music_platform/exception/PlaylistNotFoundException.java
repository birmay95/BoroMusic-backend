package com.example.music_platform.exception;

public class PlaylistNotFoundException extends RuntimeException{
    public PlaylistNotFoundException(final String mes) {super(mes);}
}
