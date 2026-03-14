package com.baravenski.musicplatform.exception;

import com.baravenski.musicplatform.exception.builder.ExceptionResponseBuilder;
import com.baravenski.musicplatform.exception.dto.ExceptionMessageDto;
import com.baravenski.musicplatform.exception.impl.*; // Убедись, что импорты совпадают с твоей структурой
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final ExceptionResponseBuilder responseBuilder;

    @ExceptionHandler({
            UserNotFoundException.class,
            TrackNotFoundException.class,
            ArtistRequestNotFoundException.class,
            ArtistRequestNotFoundByUserIdException.class,
            TokenNotFoundException.class,
            PlaylistNotFoundException.class,
            VerificationTokenNotFoundByUserIdException.class,
    })
    public ResponseEntity<ExceptionMessageDto> handleNotFoundException(
            final Exception exception,
            final WebRequest request
    ) {
        return responseBuilder.build(NOT_FOUND, exception, request, false);
    }

    @ExceptionHandler({
            IncorrectPasswordException.class,
            WeakPasswordException.class,
            IncorrectVerificationTokenException.class
    })
    public ResponseEntity<ExceptionMessageDto> handleBadRequestException(
            final Exception exception,
            final WebRequest request
    ) {
        return responseBuilder.build(BAD_REQUEST, exception, request, false);
    }

    @ExceptionHandler({
            UserWithNameAlreadyExistsException.class,
            UserWithEmailAlreadyExistsException.class
    })
    public ResponseEntity<ExceptionMessageDto> handleConflictException(
            final Exception exception,
            final WebRequest request
    ) {
        return responseBuilder.build(CONFLICT, exception, request, false);
    }

    @ExceptionHandler({
            UploadTrackToTheMlOrAwsServiceException.class,
            DeleteTrackToTheMlServiceException.class,
            RecommendationException.class
    })
    public ResponseEntity<ExceptionMessageDto> handleBadGatewayException(
            final Exception exception,
            final WebRequest request
    ) {
        return responseBuilder.build(BAD_GATEWAY, exception, request, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessageDto> globalExceptionHandler(
            final Exception exception,
            final WebRequest request
    ) {
        return responseBuilder.build(INTERNAL_SERVER_ERROR, exception, request, false);
    }
}