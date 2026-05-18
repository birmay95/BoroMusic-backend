package com.baravenski.musicplatform.exception;

import com.baravenski.musicplatform.exception.builder.ExceptionResponseBuilder;
import com.baravenski.musicplatform.exception.dto.ExceptionMessageDto;
import com.baravenski.musicplatform.exception.impl.ArtistRequestNotFoundByUserIdException;
import com.baravenski.musicplatform.exception.impl.ArtistRequestNotFoundException;
import com.baravenski.musicplatform.exception.impl.BadCredentialsException;
import com.baravenski.musicplatform.exception.impl.BruteForceLockException;
import com.baravenski.musicplatform.exception.impl.DeleteTrackToTheMlServiceException;
import com.baravenski.musicplatform.exception.impl.IncorrectPasswordException;
import com.baravenski.musicplatform.exception.impl.IncorrectVerificationTokenException;
import com.baravenski.musicplatform.exception.impl.InvalidTokenException;
import com.baravenski.musicplatform.exception.impl.PlaylistNotFoundException;
import com.baravenski.musicplatform.exception.impl.RecommendationException;
import com.baravenski.musicplatform.exception.impl.TokenNotFoundException;
import com.baravenski.musicplatform.exception.impl.TrackNotFoundException;
import com.baravenski.musicplatform.exception.impl.UploadTrackToTheMlOrAwsServiceException;
import com.baravenski.musicplatform.exception.impl.UserNotFoundException;
import com.baravenski.musicplatform.exception.impl.UserWithEmailAlreadyExistsException;
import com.baravenski.musicplatform.exception.impl.UserWithNameAlreadyExistsException;
import com.baravenski.musicplatform.exception.impl.VerificationTokenNotFoundByUserIdException;
import com.baravenski.musicplatform.exception.impl.WeakPasswordException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.LOCKED;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@NullMarked
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
            IncorrectVerificationTokenException.class,
            BadCredentialsException.class,
            InvalidTokenException.class,
    })
    public ResponseEntity<ExceptionMessageDto> handleBadRequestException(
            final Exception exception,
            final WebRequest request
    ) {
        return responseBuilder.build(BAD_REQUEST, exception, request, false);
    }

    @ExceptionHandler(BruteForceLockException.class)
    public ResponseEntity<ExceptionMessageDto> handleLockedException(
            final Exception exception,
            final WebRequest request
    ) {
        return responseBuilder.build(LOCKED, exception, request, false);
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
