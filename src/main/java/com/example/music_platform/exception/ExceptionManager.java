package com.example.music_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorMessage> handleUserAlreadyExistsException(
            final IncorrectPasswordException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleUserAlreadyExistsException(
            final UserAlreadyExistsException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ErrorMessage> handleWeakPasswordException(
            final WeakPasswordException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(
            final Exception ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFoundException(
            final UserNotFoundException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(TrackNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleTrackNotFoundException(
            final TrackNotFoundException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleRequestNotFoundException(
            final RequestNotFoundException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleTokenNotFoundException(
            final TokenNotFoundException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlePlaylistNotFoundException(
            final PlaylistNotFoundException ex, final WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    private ResponseEntity<ErrorMessage> createErrorResponse(HttpStatus status, Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                status.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, status);
    }
}
