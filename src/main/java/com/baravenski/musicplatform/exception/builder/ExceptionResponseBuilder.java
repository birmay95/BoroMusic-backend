package com.baravenski.musicplatform.exception.builder;

import com.baravenski.musicplatform.exception.dto.ExceptionMessageDto;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

@Component
@NullMarked
public class ExceptionResponseBuilder {

    public ResponseEntity<ExceptionMessageDto> build(
            HttpStatus httpStatus,
            Exception exception,
            WebRequest request,
            boolean includeRequestDetails
    ) {
        var now = LocalDateTime.now();
        var formattedTime = BASIC_ISO_DATE.format(now);

        var messageDto = new ExceptionMessageDto(
                httpStatus.value(),
                formattedTime,
                exception.getMessage(),
                request.getDescription(includeRequestDetails)
        );

        return new ResponseEntity<>(messageDto, httpStatus);
    }
}
